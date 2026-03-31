package com.example.photodrop.ui.einstellungen

import android.content.Context
import android.content.SharedPreferences
import com.example.photodrop.agent.KiAnbieter

// Speichert und liest den gewaehlten KI-Anbieter und zugehoerige API-Keys.
object KiAnbieterSpeicher {

    private const val PREFS_NAME = "ki_anbieter_einstellungen"
    private const val KEY_ANBIETER = "ki_anbieter_modell_id"
    private const val KEY_OPENAI = "openai_api_key"

    // Gibt den aktuell gespeicherten Anbieter zurueck (Standard: OcrKostenlos).
    fun anbieterLesen(context: Context): KiAnbieter {
        val id = prefs(context).getString(KEY_ANBIETER, KiAnbieter.OcrKostenlos.modellId)
            ?: KiAnbieter.OcrKostenlos.modellId
        return KiAnbieter.vonModellId(id)
    }

    // Speichert den gewaehlten Anbieter.
    fun anbieterSpeichern(context: Context, anbieter: KiAnbieter) {
        prefs(context).edit().putString(KEY_ANBIETER, anbieter.modellId).apply()
    }

    // Liest den gespeicherten OpenAI API-Key oder null.
    fun openAiKeyLesen(context: Context): String? {
        val wert = prefs(context).getString(KEY_OPENAI, null)
        return if (wert.isNullOrBlank()) null else wert
    }

    // Speichert den OpenAI API-Key.
    fun openAiKeySpeichern(context: Context, schluessel: String) {
        prefs(context).edit().putString(KEY_OPENAI, schluessel.trim()).apply()
    }

    // Loescht den OpenAI API-Key.
    fun openAiKeyLoeschen(context: Context) {
        prefs(context).edit().remove(KEY_OPENAI).apply()
    }

    // Gibt den SharedPreferences-Zugriff zurueck.
    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
