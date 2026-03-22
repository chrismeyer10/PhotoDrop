package com.example.photodrop.ui.drive.zustand

import com.example.photodrop.ui.drive.api.DriveOrdner
import com.example.photodrop.ui.drive.api.DriveOrdnerDatei

// Moegliche Zustaende der Google Drive Verbindung.
sealed interface DriveZustand {
    object NichtVerbunden : DriveZustand
    object Verbindet : DriveZustand

    // Ordnerliste wird von Drive geladen.
    data class OrdnerLaden(val kontoName: String, val token: String) : DriveZustand

    // Ordnerliste geladen — User kann auswaehlen oder neu erstellen.
    data class OrdnerAuswaehlen(
        val kontoName: String,
        val token: String,
        val ordner: List<DriveOrdner>,
        val gespeicherterOrdner: DriveOrdner?
    ) : DriveZustand

    // Warten auf Ordnername-Eingabe fuer neuen Ordner.
    data class OrdnerBenennen(val kontoName: String, val token: String) : DriveZustand

    // Ordner verbunden — zeigt Bereit-Animation.
    data class Verbunden(val kontoName: String, val ordnerId: String, val token: String = "") : DriveZustand

    // Ordnerinhalt wurde geladen und ist bereit zur Anzeige.
    data class InhaltGeladen(
        val kontoName: String,
        val ordnerId: String,
        val dateien: List<DriveOrdnerDatei>
    ) : DriveZustand

    data class Fehler(val meldung: String) : DriveZustand
}
