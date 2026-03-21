package com.example.photodrop.ui.drive

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Verwaltet die Google Drive Verbindung und den gespeicherten Ordnernamen.
class DriveViewModel(application: Application) : AndroidViewModel(application) {

    private val einstellungen = application.getSharedPreferences("drive_prefs", 0)
    private val SCHLUESSEL_ORDNER = "ordner_name"

    private val _zustand = MutableStateFlow<DriveZustand>(DriveZustand.NichtVerbunden)
    val zustand: StateFlow<DriveZustand> = _zustand

    // Gespeicherter Ordnername — null wenn noch kein Ordner angelegt wurde.
    val ordnerName: String? get() = einstellungen.getString(SCHLUESSEL_ORDNER, null)

    init { automatischVerbinden() }

    // Prueft ob bereits ein Konto angemeldet ist und verbindet automatisch.
    private fun automatischVerbinden() {
        val konto = GoogleSignIn.getLastSignedInAccount(getApplication()) ?: return
        val gespeicherterName = ordnerName ?: return verbindetMitNamensfrage(konto)
        _zustand.value = DriveZustand.Verbindet
        viewModelScope.launch { tokenHolenUndVerbinden(konto, gespeicherterName) }
    }

    // Holt den Token und zeigt den Ordnername-Dialog (kein gespeicherter Name vorhanden).
    private fun verbindetMitNamensfrage(konto: GoogleSignInAccount) {
        _zustand.value = DriveZustand.Verbindet
        viewModelScope.launch {
            try {
                val token = tokenHolen(konto)
                _zustand.value = DriveZustand.OrdnerBenennen(
                    kontoName = konto.email ?: konto.displayName ?: "",
                    token = token
                )
            } catch (e: Exception) {
                _zustand.value = DriveZustand.NichtVerbunden
            }
        }
    }

    // Holt den Token und stellt die Drive-Verbindung mit gespeichertem Ordnernamen her.
    private suspend fun tokenHolenUndVerbinden(konto: GoogleSignInAccount, name: String) {
        try {
            val token = tokenHolen(konto)
            verbindungAufbauen(token, konto.email ?: konto.displayName ?: "", name)
        } catch (e: Exception) {
            _zustand.value = DriveZustand.Fehler(e.message ?: "Unbekannter Fehler")
        }
    }

    // Holt den OAuth2-Token für das gegebene Konto.
    private suspend fun tokenHolen(konto: GoogleSignInAccount): String =
        withContext(Dispatchers.IO) {
            GoogleAuthUtil.getToken(
                getApplication(), konto.account!!,
                "oauth2:https://www.googleapis.com/auth/drive.file"
            )
        }

    // Baut die Drive-Verbindung auf und startet parallel das Laden des Ordnerinhalts.
    private suspend fun verbindungAufbauen(token: String, kontoName: String, name: String) {
        val ordnerId = DriveVerbindung.ordnerSicherstellen(token, name)
        if (ordnerId != null) {
            _zustand.value = DriveZustand.Verbunden(kontoName, ordnerId, token)
            ordnerInhaltLaden(token, kontoName, ordnerId)
        } else {
            _zustand.value = DriveZustand.Fehler("Ordner konnte nicht erstellt werden.")
        }
    }

    // Lädt den Ordnerinhalt — wartet mindestens 2.5s damit die Bereit-Animation sichtbar ist.
    private suspend fun ordnerInhaltLaden(token: String, kontoName: String, ordnerId: String) {
        val startZeit = System.currentTimeMillis()
        val dateien = DriveVerbindung.ordnerInhaltLaden(token, ordnerId)
        val vergangen = System.currentTimeMillis() - startZeit
        if (vergangen < MINDEST_ANZEIGEZEIT_MS) delay(MINDEST_ANZEIGEZEIT_MS - vergangen)
        _zustand.value = DriveZustand.InhaltGeladen(kontoName, ordnerId, dateien)
    }

    companion object {
        // Mindestzeit die der Verbunden-Screen sichtbar bleibt (2.5 Sekunden).
        private const val MINDEST_ANZEIGEZEIT_MS = 2_500L
    }

    // Aktualisiert den Ordnerinhalt wenn bereits verbunden (z.B. nach neuem Foto).
    fun inhaltAktualisieren() {
        val aktuell = _zustand.value as? DriveZustand.InhaltGeladen ?: return
        viewModelScope.launch {
            val konto = GoogleSignIn.getLastSignedInAccount(getApplication()) ?: return@launch
            val token = tokenHolen(konto)
            ordnerInhaltLaden(token, aktuell.kontoName, aktuell.ordnerId)
        }
    }

    // Erstellt den Google Sign-In Intent mit Drive-Berechtigung.
    fun anmeldeIntentErstellen(): Intent {
        val optionen = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/drive.file"))
            .build()
        return GoogleSignIn.getClient(getApplication(), optionen).signInIntent
    }

    // Verarbeitet das Ergebnis des Sign-In Intents.
    fun anmeldeErgebnisVerarbeiten(daten: Intent?) {
        _zustand.value = DriveZustand.Verbindet
        viewModelScope.launch {
            try {
                val konto: GoogleSignInAccount = withContext(Dispatchers.IO) {
                    GoogleSignIn.getSignedInAccountFromIntent(daten)
                        .getResult(ApiException::class.java)
                }
                val token = tokenHolen(konto)
                val name = ordnerName
                if (name != null) {
                    verbindungAufbauen(token, konto.email ?: konto.displayName ?: "", name)
                } else {
                    _zustand.value = DriveZustand.OrdnerBenennen(
                        kontoName = konto.email ?: konto.displayName ?: "", token = token
                    )
                }
            } catch (e: ApiException) {
                _zustand.value = DriveZustand.Fehler("Anmeldung fehlgeschlagen: ${e.statusCode}")
            } catch (e: Exception) {
                _zustand.value = DriveZustand.Fehler(e.message ?: "Unbekannter Fehler")
            }
        }
    }

    // Speichert den Ordnernamen und erstellt den Ordner in Drive.
    fun ordnerBestaetigen(name: String) {
        val aktuell = _zustand.value as? DriveZustand.OrdnerBenennen ?: return
        einstellungen.edit().putString(SCHLUESSEL_ORDNER, name).apply()
        _zustand.value = DriveZustand.Verbindet
        viewModelScope.launch { verbindungAufbauen(aktuell.token, aktuell.kontoName, name) }
    }

    // Setzt den Zustand zurück damit der Nutzer es erneut versuchen kann.
    fun zuruecksetzen() { _zustand.value = DriveZustand.NichtVerbunden }

    // Meldet den Nutzer von Google Drive ab und löscht den Ordnernamen.
    fun abmelden() {
        einstellungen.edit().remove(SCHLUESSEL_ORDNER).apply()
        val optionen = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().requestScopes(Scope("https://www.googleapis.com/auth/drive.file"))
            .build()
        GoogleSignIn.getClient(getApplication(), optionen)
            .signOut().addOnCompleteListener { _zustand.value = DriveZustand.NichtVerbunden }
    }
}
