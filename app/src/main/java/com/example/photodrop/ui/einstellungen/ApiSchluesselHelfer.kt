package com.example.photodrop.ui.einstellungen

import android.content.Context
import com.example.photodrop.BuildConfig

// Ermittelt den aktiven API-Schluessel: gespeicherter Key hat Vorrang vor BuildConfig.
object ApiSchluesselHelfer {

    // Gibt den aktiven Schluessel zurueck oder null wenn keiner verfuegbar.
    fun aktivenSchluesselHolen(context: Context): String? {
        val gespeicherter = ApiSchluesselSpeicher.lesen(context)
        if (!gespeicherter.isNullOrBlank()) return gespeicherter

        val buildConfig = BuildConfig.ANTHROPIC_API_KEY
        return if (buildConfig.isNotBlank()) buildConfig else null
    }

    // Prueft ob ein API-Schluessel verfuegbar ist.
    fun istVerfuegbar(context: Context): Boolean {
        return !aktivenSchluesselHolen(context).isNullOrBlank()
    }
}
