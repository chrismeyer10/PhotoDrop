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
    private val SCHLUESSEL_ORDNER_NAME = "ordner_name"
    private val SCHLUESSEL_ORDNER_ID = "ordner_id"

    private val _zustand = MutableStateFlow<DriveZustand>(DriveZustand.NichtVerbunden)
    val zustand: StateFlow<DriveZustand> = _zustand

    // Gespeicherter Ordnername — null wenn noch kein Ordner gewählt wurde.
    val ordnerName: String? get() = einstellungen.getString(SCHLUESSEL_ORDNER_NAME, null)

    // Gespeicherte Ordner-ID — null wenn noch kein Ordner gewählt wurde.
    private val gespeicherteOrdnerId: String? get() = einstellungen.getString(SCHLUESSEL_ORDNER_ID, null)

    init { automatischVerbinden() }

    // Prueft ob bereits ein Konto angemeldet ist und verbindet automatisch.
    private fun automatischVerbinden() {
        val konto = GoogleSignIn.getLastSignedInAccount(getApplication()) ?: return
        val name = ordnerName
        val id = gespeicherteOrdnerId
        _zustand.value = DriveZustand.Verbindet
        viewModelScope.launch {
            if (name != null && id != null) {
                tokenHolenUndVerbinden(konto, name, id)
            } else {
                ordnerListeAnzeigen(konto)
            }
        }
    }

    // Holt den Token und lädt die Ordnerliste zur Auswahl.
    private suspend fun ordnerListeAnzeigen(konto: GoogleSignInAccount) {
        try {
            val token = tokenHolen(konto)
            val kontoName = konto.email ?: konto.displayName ?: ""
            _zustand.value = DriveZustand.OrdnerLaden(kontoName, token)
            val ordner = DriveVerbindung.ordnerListeLaden(token)
            val gespeichert = gespeichertenOrdnerFinden(ordner)
            if (ordner.isEmpty()) {
                _zustand.value = DriveZustand.OrdnerBenennen(kontoName, token)
            } else {
                _zustand.value = DriveZustand.OrdnerAuswaehlen(kontoName, token, ordner, gespeichert)
            }
        } catch (e: Exception) {
            _zustand.value = DriveZustand.NichtVerbunden
        }
    }

    // Sucht den gespeicherten Ordner in der geladenen Liste.
    private fun gespeichertenOrdnerFinden(ordner: List<DriveOrdner>): DriveOrdner? {
        val id = gespeicherteOrdnerId ?: return null
        return ordner.find { it.id == id }
    }

    // Holt den Token und stellt die Drive-Verbindung mit gespeicherter Ordner-ID her.
    private suspend fun tokenHolenUndVerbinden(konto: GoogleSignInAccount, name: String, id: String) {
        try {
            val token = tokenHolen(konto)
            val kontoName = konto.email ?: konto.displayName ?: ""
            _zustand.value = DriveZustand.Verbunden(kontoName, id, token)
            ordnerInhaltLaden(token, kontoName, id)
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

    // Wechselt zum "Neuen Ordner erstellen"-Formular aus der Auswahlliste.
    fun neuenOrdnerErstellen() {
        val aktuell = _zustand.value as? DriveZustand.OrdnerAuswaehlen ?: return
        _zustand.value = DriveZustand.OrdnerBenennen(aktuell.kontoName, aktuell.token)
    }

    // Wählt einen bestehenden Ordner aus, speichert ihn und startet die Verbindung.
    fun ordnerAuswaehlen(ordner: DriveOrdner) {
        val aktuell = _zustand.value as? DriveZustand.OrdnerAuswaehlen ?: return
        einstellungen.edit()
            .putString(SCHLUESSEL_ORDNER_NAME, ordner.name)
            .putString(SCHLUESSEL_ORDNER_ID, ordner.id)
            .apply()
        _zustand.value = DriveZustand.Verbunden(aktuell.kontoName, ordner.id, aktuell.token)
        viewModelScope.launch { ordnerInhaltLaden(aktuell.token, aktuell.kontoName, ordner.id) }
    }

    // Speichert den neuen Ordnernamen, erstellt den Ordner in Drive und verbindet.
    fun ordnerBestaetigen(name: String) {
        val aktuell = _zustand.value as? DriveZustand.OrdnerBenennen ?: return
        _zustand.value = DriveZustand.Verbindet
        viewModelScope.launch {
            val ordnerId = DriveVerbindung.ordnerSicherstellen(aktuell.token, name)
            if (ordnerId != null) {
                einstellungen.edit()
                    .putString(SCHLUESSEL_ORDNER_NAME, name)
                    .putString(SCHLUESSEL_ORDNER_ID, ordnerId)
                    .apply()
                _zustand.value = DriveZustand.Verbunden(aktuell.kontoName, ordnerId, aktuell.token)
                ordnerInhaltLaden(aktuell.token, aktuell.kontoName, ordnerId)
            } else {
                _zustand.value = DriveZustand.Fehler("Ordner konnte nicht erstellt werden.")
            }
        }
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
                ordnerListeAnzeigen(konto)
            } catch (e: ApiException) {
                _zustand.value = DriveZustand.Fehler("Anmeldung fehlgeschlagen: ${e.statusCode}")
            } catch (e: Exception) {
                _zustand.value = DriveZustand.Fehler(e.message ?: "Unbekannter Fehler")
            }
        }
    }

    // Setzt den Zustand zurück damit der Nutzer es erneut versuchen kann.
    fun zuruecksetzen() { _zustand.value = DriveZustand.NichtVerbunden }

    // Meldet den Nutzer von Google Drive ab und löscht den gespeicherten Ordner.
    fun abmelden() {
        einstellungen.edit()
            .remove(SCHLUESSEL_ORDNER_NAME)
            .remove(SCHLUESSEL_ORDNER_ID)
            .apply()
        val optionen = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().requestScopes(Scope("https://www.googleapis.com/auth/drive.file"))
            .build()
        GoogleSignIn.getClient(getApplication(), optionen)
            .signOut().addOnCompleteListener { _zustand.value = DriveZustand.NichtVerbunden }
    }
}
