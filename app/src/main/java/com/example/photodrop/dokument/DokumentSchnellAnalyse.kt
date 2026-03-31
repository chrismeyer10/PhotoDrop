package com.example.photodrop.dokument

import android.content.Context
import android.net.Uri
import java.time.LocalDate

// Fuehrt eine kostenlose Analyse eines Dokuments durch (ohne KI, ohne API-Key).
object DokumentSchnellAnalyse {

    // Analysiert das Dokument und gibt Dateiname- und Drive-Pfad-Vorschlag zurueck.
    suspend fun analysieren(uri: Uri, context: Context): SchnellAnalyseErgebnis {
        val dateiname = dateinameErmitteln(uri, context)
        val ocrText = ocrTextHolen(uri, context)
        val drivePfad = drivePfadErmitteln(dateiname, ocrText)
        return SchnellAnalyseErgebnis(dateiname, drivePfad)
    }

    // Ermittelt den bereinigten Dateinamen: PDF-Metadaten > Dateiname > Fallback.
    private fun dateinameErmitteln(uri: Uri, context: Context): String {
        val ausMetadaten = pdfTitelAusMetadaten(uri, context)
        if (!ausMetadaten.isNullOrBlank()) return ausMetadaten.trim().take(60)
        val ausdateiname = dateinameAusUri(uri, context)
        if (!ausdateiname.isNullOrBlank()) return ausdateiname
        return "Dokument_${LocalDate.now()}"
    }

    // Liest den Anzeigenamen aus PDF-Metadaten und bereinigt ihn.
    // Gibt null zurueck wenn der Name leer oder nicht lesbar ist.
    private fun pdfTitelAusMetadaten(uri: Uri, context: Context): String? {
        val mimeType = context.contentResolver.getType(uri)
        if (mimeType != "application/pdf") return null
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val titelIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && titelIndex >= 0) {
                    val anzeigename = cursor.getString(titelIndex) ?: return null
                    // Extension entfernen und nur als Tipp-Wert nutzen
                    bereinigen(anzeigename).ifBlank { null }
                } else null
            }
        } catch (_: Exception) {
            null
        }
    }

    // Liest den Anzeigenamen der Datei und bereinigt ihn.
    private fun dateinameAusUri(uri: Uri, context: Context): String? {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && index >= 0) bereinigen(cursor.getString(index)) else null
            }
        } catch (_: Exception) {
            null
        }
    }

    // Bereinigt einen Dateinamen: Extension entfernen, Sonderzeichen zu Leerzeichen.
    private fun bereinigen(name: String): String {
        val ohneExtension = name.substringBeforeLast(".")
        return ohneExtension
            .replace(Regex("[_\\-]+"), " ")
            .replace(Regex("[^A-Za-z0-9äöüÄÖÜß ]+"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(60)
            .ifBlank { "Dokument" }
    }

    // Versucht OCR-Text aus dem Dokument zu lesen (schlaegt lautlos fehl).
    private suspend fun ocrTextHolen(uri: Uri, context: Context): String {
        return try {
            MlKitTextErkennung.textErkennen(uri, context)
        } catch (_: Exception) {
            ""
        }
    }

    // Ermittelt den Drive-Pfad basierend auf Dateiname und OCR-Text.
    fun drivePfadErmitteln(dateiname: String, ocrText: String = ""): String {
        val kombiniert = (dateiname + " " + ocrText).lowercase()
        val jahr = LocalDate.now().year
        return when {
            schluesselerkennung(kombiniert, listOf("rechnung", "invoice", "beleg", "quittung")) ->
                "Rechnungen/$jahr"
            schluesselerkennung(kombiniert, listOf("vertrag", "contract", "vereinbarung")) ->
                "Vertraege/$jahr"
            schluesselerkennung(kombiniert, listOf("kontoauszug", "ueberweisung", "konto")) ->
                "Finanzen/$jahr"
            else -> "Dokumente/$jahr"
        }
    }

    // Prueft ob mindestens ein Schluesselwort im Text vorkommt.
    private fun schluesselerkennung(text: String, schluessel: List<String>): Boolean =
        schluessel.any { text.contains(it) }
}

// Ergebnis der kostenlosen Schnellanalyse.
data class SchnellAnalyseErgebnis(
    val dateiname: String,
    val drivePfad: String
)
