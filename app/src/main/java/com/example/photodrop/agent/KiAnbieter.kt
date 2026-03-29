package com.example.photodrop.agent

// Verfuegbare KI-Anbieter und Modelle fuer die Dokumentanalyse.
sealed class KiAnbieter(val anzeigeName: String, val modellId: String) {

    // Anthropic Claude — leistungsstark, kostenpflichtig.
    object Claude : KiAnbieter("Claude (Anthropic)", "claude-sonnet-4-20250514")

    // OpenAI GPT-4o-mini — guenstig, gut fuer Bildanalyse.
    object GptMini : KiAnbieter("GPT-4o-mini (OpenAI)", "gpt-4o-mini")

    companion object {
        // Alle verfuegbaren Anbieter.
        val alle = listOf(Claude, GptMini)

        // Findet einen Anbieter anhand der Modell-ID.
        fun vonModellId(id: String): KiAnbieter {
            return alle.firstOrNull { it.modellId == id } ?: Claude
        }
    }
}
