package com.example.photodrop.dokument

import android.graphics.Bitmap
import android.net.Uri

// Moegliche Zustaende des Dokument-Archiv-Flows.
sealed interface DokumentZustand {

    // Startbereit — kein Dokument geladen.
    object Bereit : DokumentZustand

    // Dokument geladen — kostenlose Analyse laeuft im Hintergrund.
    data class Geladen(
        val uri: Uri,
        val vorschau: Bitmap?,
        val dateiname: String = "",
        val drivePfad: String = "",
        val analysiertLaeuft: Boolean = true
    ) : DokumentZustand

    // KI analysiert das Dokument (KI-Analyse laeuft).
    object Analysiert : DokumentZustand

    // KI-Vorschlag liegt vor — User kann anpassen.
    data class VorschlagBereit(
        val uri: Uri,
        val vorschau: Bitmap?,
        val dateiname: String,
        val unterordner: String,
        val begruendung: String,
        // Vollstaendiger Drive-Pfad, z.B. "Rechnungen/2024" (optional, KI-Vorschlag).
        val drivePfad: String = ""
    ) : DokumentZustand

    // Datei wird hochgeladen.
    object LaeadtHoch : DokumentZustand

    // Upload erfolgreich abgeschlossen.
    data class Fertig(val dateiname: String, val unterordner: String) : DokumentZustand

    // KI-Analyse fehlgeschlagen — Dokument kann trotzdem gespeichert werden.
    data class AnalyseFehler(
        val meldung: String,
        val uri: Uri,
        val vorschau: Bitmap?
    ) : DokumentZustand

    // Manuelles Benennen — User gibt Dateiname und Unterordner selbst ein.
    data class ManuellBenennen(val uri: Uri, val vorschau: Bitmap?) : DokumentZustand

    // Fehlerzustand mit Meldung.
    data class Fehler(val meldung: String) : DokumentZustand
}
