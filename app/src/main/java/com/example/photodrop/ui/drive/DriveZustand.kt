package com.example.photodrop.ui.drive

// Mögliche Zustände der Google Drive Verbindung.
sealed interface DriveZustand {
    object NichtVerbunden : DriveZustand
    object Verbindet : DriveZustand

    // Warten auf Ordnername-Eingabe nach erfolgreicher Anmeldung.
    data class OrdnerBenennen(val kontoName: String, val token: String) : DriveZustand

    // Ordner verbunden — zeigt Bereit-Animation, Inhalt wird parallel geladen.
    data class Verbunden(val kontoName: String, val ordnerId: String, val token: String = "") : DriveZustand

    // Ordnerinhalt wurde geladen und ist bereit zur Anzeige.
    data class InhaltGeladen(
        val kontoName: String,
        val ordnerId: String,
        val dateien: List<DriveOrdnerDatei>
    ) : DriveZustand

    data class Fehler(val meldung: String) : DriveZustand
}
