package com.example.photodrop.ui.einstellungen

import android.content.Context
import com.example.photodrop.BuildConfig
import com.example.photodrop.agent.KiAnbieter

// Ermittelt den aktiven API-Schluessel und Anbieter basierend auf den Einstellungen.
object ApiSchluesselHelfer {

    // Gibt den aktiven Anthropic-Schluessel zurueck: gespeicherter Key hat Vorrang vor BuildConfig.
    fun aktivenSchluesselHolen(context: Context): String? {
        val gespeicherter = ApiSchluesselSpeicher.lesen(context)
        if (!gespeicherter.isNullOrBlank()) return gespeicherter

        val buildConfig = BuildConfig.ANTHROPIC_API_KEY
        return if (buildConfig.isNotBlank()) buildConfig else null
    }

    // Gibt den aktiven API-Schluessel fuer den gewaehlten Anbieter zurueck.
    fun schluesselFuerAnbieter(context: Context, anbieter: KiAnbieter): String? {
        return when (anbieter) {
            KiAnbieter.Claude -> aktivenSchluesselHolen(context)
            KiAnbieter.GptMini -> KiAnbieterSpeicher.openAiKeyLesen(context)
            else -> null
        }
    }

    // Prueft ob ein API-Schluessel fuer den gewaehlten Anbieter verfuegbar ist.
    fun istVerfuegbar(context: Context): Boolean {
        val anbieter = KiAnbieterSpeicher.anbieterLesen(context)
        if (!anbieter.benoetigtApiKey) return true
        return !schluesselFuerAnbieter(context, anbieter).isNullOrBlank()
    }

    // Gibt den aktuell gewaehlten KI-Anbieter zurueck.
    fun aktuellenAnbieterHolen(context: Context): KiAnbieter {
        return KiAnbieterSpeicher.anbieterLesen(context)
    }
}
