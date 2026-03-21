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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Mögliche Zustände der Drive-Verbindung.
sealed interface DriveZustand {
    object NichtVerbunden : DriveZustand
    object Verbindet : DriveZustand
    data class Verbunden(val kontoName: String, val ordnerId: String) : DriveZustand
    data class Fehler(val meldung: String) : DriveZustand
}

// Verwaltet die Google Drive Verbindung.
// Erstellt den Sign-In Intent und verarbeitet das Ergebnis.
class DriveViewModel(application: Application) : AndroidViewModel(application) {

    private val _zustand = MutableStateFlow<DriveZustand>(DriveZustand.NichtVerbunden)
    val zustand: StateFlow<DriveZustand> = _zustand

    // Erstellt den Google Sign-In Intent mit Drive-Berechtigung.
    fun anmeldeIntentErstellen(): Intent {
        val optionen = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/drive.file"))
            .build()
        val client = GoogleSignIn.getClient(getApplication(), optionen)
        return client.signInIntent
    }

    // Verarbeitet das Ergebnis des Sign-In Intents.
    // Holt den OAuth-Token und erstellt den PhotoDrop-Ordner.
    fun anmeldeErgebnisVerarbeiten(daten: Intent?) {
        _zustand.value = DriveZustand.Verbindet
        viewModelScope.launch {
            try {
                val konto: GoogleSignInAccount = withContext(Dispatchers.IO) {
                    GoogleSignIn.getSignedInAccountFromIntent(daten)
                        .getResult(ApiException::class.java)
                }
                val token: String = withContext(Dispatchers.IO) {
                    GoogleAuthUtil.getToken(
                        getApplication(),
                        konto.account!!,
                        "oauth2:https://www.googleapis.com/auth/drive.file"
                    )
                }
                val ordnerId = DriveVerbindung.ordnerSicherstellen(token)
                if (ordnerId != null) {
                    _zustand.value = DriveZustand.Verbunden(
                        kontoName = konto.email ?: konto.displayName ?: "",
                        ordnerId = ordnerId
                    )
                } else {
                    _zustand.value = DriveZustand.Fehler("Ordner konnte nicht erstellt werden.")
                }
            } catch (e: ApiException) {
                _zustand.value = DriveZustand.Fehler("Anmeldung fehlgeschlagen: ${e.statusCode}")
            } catch (e: Exception) {
                _zustand.value = DriveZustand.Fehler(e.message ?: "Unbekannter Fehler")
            }
        }
    }

    // Setzt den Zustand zurück damit der Nutzer es erneut versuchen kann.
    fun zuruecksetzen() {
        _zustand.value = DriveZustand.NichtVerbunden
    }
}
