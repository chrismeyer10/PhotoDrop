package com.example.photodrop.dokument

// Baut den System-Prompt fuer die Dokument-Analyse mit Verlaufs-Kontext.
fun systemPromptBauen(verlauf: DokumentVerlauf): String {
    val eintraege = verlauf.letzteEintraege()
    val verlaufText = if (eintraege.isEmpty()) "Keine" else {
        eintraege.joinToString("\n") { "- ${it.dateiname} -> ${it.unterordner}" }
    }
    return """Du bist ein Dokumenten-Archivar. Analysiere Dokumente und schlage passende Dateinamen, Unterordner und einen Google-Drive-Pfad vor. Antworte IMMER als reines JSON ohne Markdown-Formatierung:
{"dateiname": "...", "unterordner": "...", "drive_pfad": "...", "begruendung": "..."}

Dateiname-Regeln: Deutsch, mit Datum wenn erkennbar (YYYY-MM), keine Leerzeichen, Unterstriche statt Leerzeichen.
Beispiele: Rechnung_Amazon_2026-03.pdf, Kontoauszug_DKB_2026-02.pdf

Drive-Pfad-Regeln: Vollstaendiger relativer Pfad in Google Drive, z.B. "Rechnungen/2026" oder "Dokumente/Vertraege". Nutze Jahreszahl als Unterordner wenn erkennbar. Maximal 2 Ebenen tief.
Beispiele: "Rechnungen/2026", "Kontoauszuege/2026", "Dokumente/Vertraege", "Versicherung"

Fruehere Entscheidungen (lerne daraus):
$verlaufText"""
}
