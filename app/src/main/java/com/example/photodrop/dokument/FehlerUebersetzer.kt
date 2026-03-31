package com.example.photodrop.dokument

// Wandelt technische Fehlermeldungen in benutzerfreundliche deutsche Texte um.
fun fehlerUebersetzen(rohMeldung: String): String {
    val klein = rohMeldung.lowercase()
    return when {
        "credit balance" in klein || "billing" in klein ->
            "Die KI-Analyse ist voruebergehend nicht verfuegbar (Kontoguthaben erschoepft)."
        "rate limit" in klein || "429" in klein ->
            "Zu viele Anfragen. Bitte versuche es in einer Minute erneut."
        "timeout" in klein || "timed out" in klein ->
            "Die Verbindung zur KI hat zu lange gedauert. Bitte erneut versuchen."
        "network" in klein || "connect" in klein || "unreachable" in klein ->
            "Keine Internetverbindung. Bitte Netzwerk pruefen."
        "401" in klein || "unauthorized" in klein || "authentication" in klein ->
            "API-Schluessel ungueltig [401].\n\n" +
            "Hinweis: Ein Claude Pro oder Max Abo (claude.ai) gibt keinen API-Zugang. " +
            "Bitte einen separaten API-Key unter console.anthropic.com erstellen."
        "403" in klein || "forbidden" in klein || "permission" in klein ->
            "Kein API-Zugang [403].\n\n" +
            "Hinweis: Ein Claude Pro oder Max Abo (claude.ai) gibt keinen API-Zugang. " +
            "Bitte einen separaten API-Key unter console.anthropic.com erstellen."
        "invalid_request" in klein || "400" in klein ->
            "Die KI-Analyse ist voruebergehend nicht verfuegbar."
        "500" in klein || "server" in klein ->
            "Der KI-Dienst hat einen Serverfehler. Bitte spaeter erneut versuchen."
        else ->
            "Ein unerwarteter Fehler ist aufgetreten: ${rohMeldung.take(100)}"
    }
}
