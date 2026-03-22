package com.example.photodrop.agent

// Moegliche Ergebnisse eines Agent-Laufs.
sealed class AgentResult {
    // Erfolgreiche Ausfuehrung mit Textantwort.
    data class Success(val text: String) : AgentResult()

    // Fehler bei der Ausfuehrung.
    data class Error(val message: String, val cause: Throwable) : AgentResult()
}
