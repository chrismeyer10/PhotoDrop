package com.example.photodrop.ui.einstellungen

import android.content.Context
import android.content.SharedPreferences

// Speichert und liest den benutzerdefinierten Anthropic API-Schluessel.
object ApiSchluesselSpeicher {

    private const val PREFS_NAME = "api_einstellungen"
    private const val SCHLUESSEL_KEY = "anthropic_api_key"

    // Speichert den API-Schluessel in SharedPreferences.
    fun speichern(context: Context, schluessel: String) {
        prefs(context).edit().putString(SCHLUESSEL_KEY, schluessel).apply()
    }

    // Liest den gespeicherten API-Schluessel oder gibt null zurueck.
    fun lesen(context: Context): String? {
        val wert = prefs(context).getString(SCHLUESSEL_KEY, null)
        return if (wert.isNullOrBlank()) null else wert
    }

    // Loescht den gespeicherten API-Schluessel.
    fun loeschen(context: Context) {
        prefs(context).edit().remove(SCHLUESSEL_KEY).apply()
    }

    // Gibt den SharedPreferences-Zugriff zurueck.
    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
