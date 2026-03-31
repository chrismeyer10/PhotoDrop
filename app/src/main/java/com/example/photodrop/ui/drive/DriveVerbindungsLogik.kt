package com.example.photodrop.ui.drive

import android.app.Application
import android.content.Intent
import com.example.photodrop.ui.drive.anmeldung.DriveAnmeldung
import com.example.photodrop.ui.drive.api.DriveOrdner
import com.example.photodrop.ui.drive.api.DriveOrdnerDatei
import com.example.photodrop.ui.drive.api.DriveVerbindung
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

// Kapselt die Verbindungs- und Authentifizierungs-Logik fuer Drive.
class DriveVerbindungsLogik(
    private val application: Application,
    private val prefs: OrdnerEinstellungen
) {
    // Erstellt den Sign-In Intent fuer die Anmeldemaske.
    fun anmeldeIntentErstellen(): Intent = DriveAnmeldung.intentErstellen(application)

    // Meldet den Nutzer ab und loescht gespeicherte Einstellungen.
    fun abmelden(onFertig: () -> Unit) {
        prefs.loeschen()
        DriveAnmeldung.abmelden(application, onFertig)
    }

    // Gibt das letzte angemeldete Konto zurueck.
    fun letztesKontoHolen() = DriveAnmeldung.letztesKontoHolen(application)

    // Holt das Access-Token fuer ein angemeldetes Konto.
    suspend fun tokenHolen(konto: GoogleSignInAccount): String =
        DriveAnmeldung.tokenHolen(application, konto)

    // Laedt alle Root-Ordner und gibt sie als DateiListe zurueck.
    suspend fun rootOrdnerAlsDateien(token: String): List<DriveOrdnerDatei> =
        DriveVerbindung.ordnerListeLaden(token).map {
            DriveOrdnerDatei(
                id = it.id, name = it.name,
                mimeType = "application/vnd.google-apps.folder",
                groesse = null, geaendertAm = null
            )
        }

    // Laedt alle Root-Ordner als DriveOrdner-Liste.
    suspend fun rootOrdnerLaden(token: String): List<DriveOrdner> =
        DriveVerbindung.ordnerListeLaden(token)

    // Findet den gespeicherten Ordner in der Ordnerliste.
    fun gespeichertenOrdnerFinden(ordner: List<DriveOrdner>): DriveOrdner? =
        ordner.find { it.id == prefs.ordnerId }
}
