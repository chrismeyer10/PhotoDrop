package com.example.photodrop.agent

// Gemeinsame Schnittstelle fuer KI-Dienste (Anthropic, OpenAI).
interface KiDienst {

    // Fuehrt eine Analyse mit optionalem Bild aus.
    suspend fun analysieren(
        prompt: String,
        systemPrompt: String? = null,
        bild: ByteArray? = null,
        bildMimeType: String? = null
    ): AgentResult
}
