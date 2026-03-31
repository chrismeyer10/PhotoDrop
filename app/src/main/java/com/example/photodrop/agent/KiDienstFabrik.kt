package com.example.photodrop.agent

// Erstellt den passenden KiDienst fuer einen gegebenen Anbieter und API-Key.
object KiDienstFabrik {

    // Gibt den passenden Dienst zurueck oder null wenn kein Key vorhanden.
    fun erstellen(anbieter: KiAnbieter, apiKey: String?): KiDienst? {
        if (!anbieter.benoetigtApiKey) return null
        if (apiKey.isNullOrBlank()) return null
        return when (anbieter) {
            KiAnbieter.Claude -> AgentService(apiKey)
            KiAnbieter.GptMini -> GptMiniService(apiKey)
            else -> null
        }
    }
}
