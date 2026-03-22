package com.example.photodrop.ui.drive

import android.content.SharedPreferences

// Verwaltet die gespeicherten Drive-Ordner-Einstellungen in SharedPreferences.
class OrdnerEinstellungen(private val einstellungen: SharedPreferences) {

    private val SCHLUESSEL_NAME = "ordner_name"
    private val SCHLUESSEL_ID = "ordner_id"

    // Gespeicherter Ordnername — null wenn noch keiner gewaehlt.
    val ordnerName: String? get() = einstellungen.getString(SCHLUESSEL_NAME, null)

    // Gespeicherte Ordner-ID — null wenn noch keiner gewaehlt.
    val ordnerId: String? get() = einstellungen.getString(SCHLUESSEL_ID, null)

    // Speichert den ausgewaehlten Ordner.
    fun speichern(name: String, id: String) {
        einstellungen.edit()
            .putString(SCHLUESSEL_NAME, name)
            .putString(SCHLUESSEL_ID, id)
            .apply()
    }

    // Loescht den gespeicherten Ordner.
    fun loeschen() {
        einstellungen.edit()
            .remove(SCHLUESSEL_NAME)
            .remove(SCHLUESSEL_ID)
            .apply()
    }
}
