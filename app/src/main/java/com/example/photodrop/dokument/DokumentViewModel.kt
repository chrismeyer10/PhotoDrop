package com.example.photodrop.dokument

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.photodrop.BuildConfig
import com.example.photodrop.agent.AgentService
import com.example.photodrop.ui.drive.api.DriveVerbindung
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Steuert den Dokument-Archiv-Flow: Laden, Analysieren, Hochladen.
class DokumentViewModel(application: Application) : AndroidViewModel(application) {

    private val verlauf = DokumentVerlauf(
        application.getSharedPreferences("dokument_verlauf", 0)
    )
    private val agentService = AgentService(BuildConfig.ANTHROPIC_API_KEY)

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

    // Startet die KI-Analyse des geladenen Dokuments.
    fun analysieren() {
        val bytes = aktuelleBildBytes ?: return
        _zustand.value = DokumentZustand.Analysiert
        aktuellerJob = viewModelScope.launch {
            val ergebnis = agentService.run(
                prompt = "Analysiere dieses Dokument und schlage einen Dateinamen und Unterordner vor.",
                systemPrompt = systemPromptBauen(verlauf),
                bild = bytes,
                bildMimeType = "image/jpeg"
            )
            _zustand.value = ergebnisVerarbeiten(ergebnis, aktuelleUri ?: Uri.EMPTY, aktuelleVorschau)
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
}
