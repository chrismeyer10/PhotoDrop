package com.example.photodrop.ui.foto.analyse

// Moegliche Zustaende der KI-Foto-Analyse.
sealed interface FotoAnalyseZustand {
    // Noch keine Analyse gestartet.
    object Bereit : FotoAnalyseZustand

    // Analyse laeuft gerade.
    object Laeuft : FotoAnalyseZustand

    // Analyse erfolgreich abgeschlossen.
    data class Ergebnis(val text: String) : FotoAnalyseZustand

    // Analyse fehlgeschlagen.
    data class Fehler(val meldung: String) : FotoAnalyseZustand
}
