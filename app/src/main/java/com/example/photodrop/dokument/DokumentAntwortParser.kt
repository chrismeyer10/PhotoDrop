package com.example.photodrop.dokument

import android.graphics.Bitmap
import android.net.Uri
import com.example.photodrop.agent.AgentResult
import org.json.JSONObject

// Verarbeitet die KI-Antwort und gibt den passenden Zustand zurueck.
fun ergebnisVerarbeiten(ergebnis: AgentResult, uri: Uri, vorschau: Bitmap?): DokumentZustand {
    return when (ergebnis) {
        is AgentResult.Error -> DokumentZustand.AnalyseFehler(
            meldung = fehlerUebersetzen(ergebnis.message),
            uri = uri,
            vorschau = vorschau
        )
        is AgentResult.Success -> jsonAntwortParsieren(ergebnis.text, uri, vorschau)
    }
}

// Parsed die JSON-Antwort der KI in einen VorschlagBereit-Zustand.
private fun jsonAntwortParsieren(text: String, uri: Uri, vorschau: Bitmap?): DokumentZustand {
    return try {
        val bereinigt = jsonExtrahieren(text)
        val json = JSONObject(bereinigt)
        val unterordner = json.optString("unterordner", "Dokumente")
        val drivePfad = json.optString("drive_pfad", unterordner)
        DokumentZustand.VorschlagBereit(
            uri = uri,
            vorschau = vorschau,
            dateiname = json.getString("dateiname"),
            unterordner = unterordner,
            begruendung = json.optString("begruendung", ""),
            drivePfad = drivePfad
        )
    } catch (_: Exception) {
        DokumentZustand.Fehler("KI-Antwort konnte nicht verarbeitet werden")
    }
}

// Extrahiert das JSON-Objekt aus dem Text (robust gegenueber Markdown-Wrapping).
private fun jsonExtrahieren(text: String): String {
    val bereinigt = text.trim()
    val start = bereinigt.indexOf('{')
    val ende = bereinigt.lastIndexOf('}')
    return if (start >= 0 && ende > start) bereinigt.substring(start, ende + 1) else bereinigt
}
