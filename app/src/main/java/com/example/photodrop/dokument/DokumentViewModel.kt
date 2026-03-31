package com.example.photodrop.dokument

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.photodrop.agent.KiAnbieter
import com.example.photodrop.agent.KiDienstFabrik
import com.example.photodrop.ui.drive.api.DriveVerbindung
import com.example.photodrop.ui.einstellungen.ApiSchluesselHelfer
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Steuert den Dokument-Archiv-Flow: Laden, Analysieren, Hochladen.
class DokumentViewModel(application: Application) : AndroidViewModel(application) {

    private val verlauf = DokumentVerlauf(
        application.getSharedPreferences("dokument_verlauf", 0)
    )

    private val _zustand = MutableStateFlow<DokumentZustand>(DokumentZustand.Bereit)
    val zustand: StateFlow<DokumentZustand> = _zustand

    private var aktuellerJob: Job? = null
    private var aktuelleBildBytes: ByteArray? = null
    private var aktuelleMimeType: String? = null
    private var aktuelleUri: Uri? = null
    private var aktuelleVorschau: Bitmap? = null

    // Setzt ein Dokument (von Kamera oder File Picker).
    fun dokumentSetzen(uri: Uri, context: Context) {
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val istPdf = mimeType == "application/pdf"
        aktuelleMimeType = mimeType
        aktuelleUri = uri

        viewModelScope.launch {
            aktuelleVorschau = if (istPdf) DokumentLeser.pdfVorschauErstellen(uri, context)
            else DokumentLeser.vorschauErstellen(uri, context)
            aktuelleBildBytes = if (istPdf) DokumentLeser.pdfErsteSeiteAlsBild(uri, context)
            else DokumentLeser.bildAlsBytes(uri, context)
            _zustand.value = DokumentZustand.Geladen(uri, aktuelleVorschau)
        }
    }

    // Startet die KI-Analyse mit dem in den Einstellungen gewaehlten Anbieter.
    fun analysieren() {
        val bytes = aktuelleBildBytes ?: return
        val app: Application = getApplication()
        val anbieter = ApiSchluesselHelfer.aktuellenAnbieterHolen(app)

        if (anbieter == KiAnbieter.OcrKostenlos) {
            ocrAnalysieren()
            return
        }

        val schluessel = ApiSchluesselHelfer.schluesselFuerAnbieter(app, anbieter)
        if (schluessel == null) {
            _zustand.value = DokumentZustand.AnalyseFehler(
                meldung = "Kein API-Schluessel fuer ${anbieter.anzeigeName} gespeichert. " +
                    "Bitte in Einstellungen eintragen.",
                uri = aktuelleUri ?: Uri.EMPTY,
                vorschau = aktuelleVorschau
            )
            return
        }

        val dienst = KiDienstFabrik.erstellen(anbieter, schluessel) ?: return
        _zustand.value = DokumentZustand.Analysiert
        aktuellerJob = viewModelScope.launch {
            val ergebnis = dienst.analysieren(
                prompt = "Analysiere dieses Dokument und schlage einen Dateinamen und Unterordner vor.",
                systemPrompt = systemPromptBauen(verlauf),
                bild = bytes,
                bildMimeType = "image/jpeg"
            )
            _zustand.value = ergebnisVerarbeiten(ergebnis, aktuelleUri ?: Uri.EMPTY, aktuelleVorschau)
        }
    }

    // Startet kostenlose OCR-Analyse mit ML Kit (kein API-Key noetig).
    fun ocrAnalysieren() {
        val uri = aktuelleUri ?: return
        _zustand.value = DokumentZustand.Analysiert
        aktuellerJob = viewModelScope.launch {
            try {
                val text = MlKitTextErkennung.textErkennen(uri, getApplication())
                val dateiname = dateinameAusOcrText(text)
                _zustand.value = DokumentZustand.VorschlagBereit(
                    uri = uri,
                    vorschau = aktuelleVorschau,
                    dateiname = dateiname,
                    unterordner = "Dokumente",
                    begruendung = "Aus Texterkennung (OCR) — bitte Dateinamen pruefen."
                )
            } catch (e: Exception) {
                _zustand.value = DokumentZustand.ManuellBenennen(uri, aktuelleVorschau)
            }
        }
    }

    // Wechselt zum manuellen Benennungs-Zustand nach Analyse-Fehler.
    fun trotzdemSpeichern() {
        _zustand.value = DokumentZustand.ManuellBenennen(
            uri = aktuelleUri ?: Uri.EMPTY,
            vorschau = aktuelleVorschau
        )
    }

    // Laedt das Dokument in Google Drive hoch.
    fun hochladen(dateiname: String, unterordner: String, token: String, ordnerId: String) {
        _zustand.value = DokumentZustand.LaeadtHoch
        aktuellerJob = viewModelScope.launch {
            try {
                val uri = aktuelleUri ?: throw Exception("Keine Datei geladen")
                val inhalt = DokumentLeser.bildAlsBytes(uri, getApplication()) ?: throw Exception("Datei nicht lesbar")
                val unterordnerId = DriveVerbindung.unterordnerSicherstellen(token, ordnerId, unterordner)
                DriveVerbindung.dateiHochladen(token, unterordnerId, dateiname, aktuelleMimeType ?: "image/jpeg", inhalt)
                verlauf.eintragHinzufuegen(VerlaufEintrag(dateiname, dateiname, unterordner))
                _zustand.value = DokumentZustand.Fertig(dateiname, unterordner)
            } catch (e: Exception) {
                _zustand.value = DokumentZustand.Fehler(e.message ?: "Upload fehlgeschlagen")
            }
        }
    }

    // Setzt den Zustand zurueck auf Bereit.
    fun zuruecksetzen() {
        aktuellerJob?.cancel()
        aktuellerJob = null
        aktuelleBildBytes = null
        aktuelleUri = null
        aktuelleVorschau = null
        _zustand.value = DokumentZustand.Bereit
    }

    // Erstellt einen sinnvollen Dateinamen aus dem OCR-Text.
    private fun dateinameAusOcrText(text: String): String {
        val zeilen = text.lines()
            .map { it.trim() }
            .filter { it.length >= 5 && it.any { c -> c.isLetter() } }

        val aussagekraeftig = zeilen.firstOrNull { zeile ->
            val woerter = zeile.split(Regex("\\s+"))
                .filter { it.length >= 3 && it.any { c -> c.isLetter() } }
            woerter.size >= 2
        } ?: zeilen.firstOrNull() ?: "Dokument"

        return aussagekraeftig
            .replace(Regex("[^A-Za-z0-9äöüÄÖÜß _-]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(40)
            .trimEnd()
            .ifBlank { "Dokument" }
    }
}
