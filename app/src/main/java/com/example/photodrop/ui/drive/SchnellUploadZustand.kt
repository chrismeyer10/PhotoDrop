package com.example.photodrop.ui.drive

// Zustand des Schnell-Foto-Uploads aus dem unteren Schnellmenue.
sealed interface SchnellUploadZustand {

    // Upload laeuft gerade.
    object Laedt : SchnellUploadZustand

    // Upload erfolgreich abgeschlossen.
    data class Fertig(val dateiname: String, val ordnerName: String) : SchnellUploadZustand

    // Upload fehlgeschlagen.
    data class Fehler(val meldung: String) : SchnellUploadZustand
}
