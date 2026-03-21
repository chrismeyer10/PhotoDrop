package com.example.photodrop.ui.drive

// Mögliche Zustände der Google Drive Verbindung.
sealed interface DriveZustand {
    object NichtVerbunden : DriveZustand
    object Verbindet : DriveZustand

    // Ordnerliste wird von Drive geladen (Ladeindikator).
    data class OrdnerLaden(val kontoName: String, val token: String) : DriveZustand

    // Ordnerliste geladen — User kann einen Ordner auswählen oder neu erstellen.
    data class OrdnerAuswaehlen(
        val kontoName: String,
        val token: String,
        val ordner: List<DriveOrdner>,
        val gespeicherterOrdner: DriveOrdner?
    ) : DriveZustand

    // Warten auf Ordnername-Eingabe für neuen Ordner.
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
