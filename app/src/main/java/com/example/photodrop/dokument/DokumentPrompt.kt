package com.example.photodrop.dokument

// Baut den System-Prompt fuer die Dokument-Analyse mit Verlaufs-Kontext.
fun systemPromptBauen(verlauf: DokumentVerlauf): String {
    val eintraege = verlauf.letzteEintraege()
    val verlaufText = if (eintraege.isEmpty()) "Keine" else {
        eintraege.joinToString("\n") { "- ${it.dateiname} -> ${it.unterordner}" }
    }
    return """Du bist ein Dokumenten-Archivar. Analysiere Dokumente und schlage passende Dateinamen und Unterordner vor. Antworte IMMER als reines JSON ohne Markdown-Formatierung:
{"dateiname": "...", "unterordner": "...", "begruendung": "..."}

Dateiname-Regeln: Deutsch, mit Datum wenn erkennbar (YYYY-MM), keine Leerzeichen, Unterstriche statt Leerzeichen.
Beispiele: Rechnung_Amazon_2026-03.pdf, Kontoauszug_DKB_2026-02.pdf

Fruehere Entscheidungen (lerne daraus):
$verlaufText"""
}
