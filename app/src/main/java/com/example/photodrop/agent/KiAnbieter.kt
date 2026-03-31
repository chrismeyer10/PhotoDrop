package com.example.photodrop.agent

// Verfuegbare KI-Anbieter mit Anzeigename, Modell-ID und Typ.
sealed class KiAnbieter(
    val anzeigeName: String,
    val modellId: String,
    val benoetigtApiKey: Boolean
) {
    // Anthropic Claude — leistungsstark, eigener API-Key noetig.
    object Claude : KiAnbieter("Claude (Anthropic)", "claude-sonnet-4-20250514", true)

    // OpenAI GPT-4o-mini — guenstig, eigener API-Key noetig.
    object GptMini : KiAnbieter("GPT-4o-mini (OpenAI)", "gpt-4o-mini", true)

    // Kostenlose OCR-Texterkennung via ML Kit — kein API-Key noetig.
    object OcrKostenlos : KiAnbieter("Kostenlose OCR (kein API-Key)", "mlkit_ocr", false)

    companion object {
        // Alle verfuegbaren Anbieter fuer die Auswahl-UI.
        val alle = listOf(OcrKostenlos, Claude, GptMini)

        // Findet einen Anbieter anhand der Modell-ID.
        fun vonModellId(id: String): KiAnbieter {
            return alle.firstOrNull { it.modellId == id } ?: OcrKostenlos
        }
    }
}
