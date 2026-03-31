package com.example.photodrop.ui.einstellungen

// Wandelt eine API-Fehlermeldung in einen nutzerfreundlichen Text um.
// Zeigt die rohe API-Meldung an, damit der Nutzer den genauen Fehlergrund sieht.
fun verbindungsFehlerText(rohMeldung: String): String {
    val klein = rohMeldung.lowercase()
    return when {
        "401" in klein || "unauthorized" in klein || "authentication" in klein ->
            "API-Schluessel ungueltig [401].\n\n" +
            "Wichtig: Ein Claude Pro oder Max Abo (claude.ai) gibt KEINEN API-Zugang — " +
            "das sind zwei getrennte Produkte von Anthropic. Bitte unter " +
            "console.anthropic.com ein Konto anlegen, Zahlungsmethode hinterlegen " +
            "und einen API-Key generieren.\n\nAPI: ${rohMeldung.take(200)}"
        "403" in klein || "forbidden" in klein || "permission" in klein ->
            "Kein API-Zugang [403].\n\n" +
            "Wichtig: Ein Claude Pro oder Max Abo (claude.ai) gibt KEINEN API-Zugang — " +
            "das sind zwei getrennte Produkte von Anthropic. Bitte unter " +
            "console.anthropic.com ein Konto anlegen, Zahlungsmethode hinterlegen " +
            "und einen API-Key generieren.\n\nAPI: ${rohMeldung.take(200)}"
        "credit balance" in klein || "billing" in klein ->
            "Kontoguthaben erschoepft.\n\n" +
            "Hinweis: Ein Claude.ai-Abo gibt KEINEN API-Zugriff. " +
            "Bitte unter console.anthropic.com ein Konto anlegen und Credits kaufen.\n\n" +
            "API: $rohMeldung"
        "rate limit" in klein || "429" in klein ->
            "Zu viele Anfragen. Bitte kurz warten.\n\nAPI: ${rohMeldung.take(200)}"
        "network" in klein || "connect" in klein ->
            "Keine Internetverbindung. Bitte Netzwerk pruefen.\n\nAPI: ${rohMeldung.take(200)}"
        else -> rohMeldung.take(300)
    }
}
