package com.example.photodrop.ui.drive

// Mögliche Zustände der Google Drive Verbindung.
sealed interface DriveZustand {
    object NichtVerbunden : DriveZustand
    object Verbindet : DriveZustand

    // Warten auf Ordnername-Eingabe nach erfolgreicher Anmeldung.
    data class OrdnerBenennen(val kontoName: String, val token: String) : DriveZustand
    data class Verbunden(val kontoName: String, val ordnerId: String) : DriveZustand
    data class Fehler(val meldung: String) : DriveZustand
}
