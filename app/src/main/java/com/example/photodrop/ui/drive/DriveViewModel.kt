package com.example.photodrop.ui.drive

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.photodrop.ui.drive.anmeldung.DriveAnmeldung
import com.example.photodrop.ui.drive.api.DriveOrdner
import com.example.photodrop.ui.drive.api.DriveVerbindung
import com.example.photodrop.ui.drive.zustand.DriveZustand
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Verwaltet die Google Drive Verbindung und den Zustandsfluss.
class DriveViewModel(application: Application) : AndroidViewModel(application) {

    private val ordnerPrefs = OrdnerEinstellungen(
        application.getSharedPreferences("drive_prefs", 0)
    )

    private val _zustand = MutableStateFlow<DriveZustand>(DriveZustand.NichtVerbunden)
    val zustand: StateFlow<DriveZustand> = _zustand

    // Zustand des Schnell-Foto-Uploads (null = kein aktiver Upload).
    private val _schnellUploadZustand = MutableStateFlow<SchnellUploadZustand?>(null)
    val schnellUploadZustand: StateFlow<SchnellUploadZustand?> = _schnellUploadZustand

    // Aktueller Lade-Job, damit er bei Abbruch gecancelt werden kann.
    private var ladeJob: Job? = null

    // Gespeicherter Ordnername fuer andere ViewModels.
    val ordnerName: String? get() = ordnerPrefs.ordnerName

    init { automatischVerbinden() }

    // Prueft ob bereits ein Konto angemeldet ist.
    private fun automatischVerbinden() {
        val konto = DriveAnmeldung.letztesKontoHolen(getApplication()) ?: return
        _zustand.value = DriveZustand.Verbindet
        ladeJob = viewModelScope.launch {
            val name = ordnerPrefs.ordnerName
            val id = ordnerPrefs.ordnerId
            if (name != null && id != null) {
                tokenHolenUndVerbinden(konto, name, id)
            } else {
                ordnerListeAnzeigen(konto)
            }
        }
    }

    // Laedt die Ordnerliste zur Auswahl.
    private suspend fun ordnerListeAnzeigen(konto: GoogleSignInAccount) {
        try {
            val token = DriveAnmeldung.tokenHolen(getApplication(), konto)
            val kontoName = konto.email ?: konto.displayName ?: ""
            _zustand.value = DriveZustand.OrdnerLaden(kontoName, token)
            val ordner = DriveVerbindung.ordnerListeLaden(token)
            val gespeichert = ordner.find { it.id == ordnerPrefs.ordnerId }
            _zustand.value = if (ordner.isEmpty()) {
                DriveZustand.OrdnerBenennen(kontoName, token, hatVorhandeneListe = false)
            } else {
                DriveZustand.OrdnerAuswaehlen(kontoName, token, ordner, gespeichert)
            }
        } catch (e: Exception) {
            _zustand.value = DriveZustand.NichtVerbunden
        }
    }

    // Stellt die Verbindung mit gespeichertem Ordner her.
    private suspend fun tokenHolenUndVerbinden(
        konto: GoogleSignInAccount, name: String, id: String
    ) {
        try {
            val token = DriveAnmeldung.tokenHolen(getApplication(), konto)
            val kontoName = konto.email ?: konto.displayName ?: ""
            _zustand.value = DriveZustand.Verbunden(kontoName, id, token)
            ordnerInhaltLaden(token, kontoName, id)
        } catch (e: Exception) {
            _zustand.value = DriveZustand.Fehler(e.message ?: "Unbekannter Fehler")
        }
    }

    // Laedt den Ordnerinhalt mit Mindest-Anzeigezeit.
    private suspend fun ordnerInhaltLaden(
        token: String, kontoName: String, ordnerId: String
    ) {
        val startZeit = System.currentTimeMillis()
        val dateien = DriveVerbindung.ordnerInhaltLaden(token, ordnerId)
        val vergangen = System.currentTimeMillis() - startZeit
        if (vergangen < MINDEST_ANZEIGEZEIT_MS) delay(MINDEST_ANZEIGEZEIT_MS - vergangen)
        _zustand.value = DriveZustand.InhaltGeladen(kontoName, ordnerId, dateien, token)
    }

    // Bricht den aktuellen Ladevorgang ab.
    fun ladeAbbrechen() {
        ladeJob?.cancel()
        ladeJob = null
        _zustand.value = DriveZustand.NichtVerbunden
    }

    // Wechselt zum Neuen-Ordner-Formular.
    fun neuenOrdnerErstellen() {
        val aktuell = _zustand.value as? DriveZustand.OrdnerAuswaehlen ?: return
        _zustand.value = DriveZustand.OrdnerBenennen(
            aktuell.kontoName, aktuell.token, hatVorhandeneListe = true
        )
    }

    // Wechselt vom Ordner-Benennen zurueck — je nach Kontext.
    fun ordnerBenennenAbbrechen() {
        val aktuell = _zustand.value as? DriveZustand.OrdnerBenennen ?: return
        if (!aktuell.hatVorhandeneListe) {
            DriveAnmeldung.abmelden(getApplication()) {}
            _zustand.value = DriveZustand.NichtVerbunden
        } else {
            _zustand.value = DriveZustand.Verbindet
            ladeJob = viewModelScope.launch {
                val konto = DriveAnmeldung.letztesKontoHolen(getApplication()) ?: run {
                    _zustand.value = DriveZustand.NichtVerbunden
                    return@launch
                }
                ordnerListeAnzeigen(konto)
            }
        }
    }

    // Waehlt einen bestehenden Ordner aus.
    fun ordnerAuswaehlen(ordner: DriveOrdner) {
        val aktuell = _zustand.value as? DriveZustand.OrdnerAuswaehlen ?: return
        ordnerPrefs.speichern(ordner.name, ordner.id)
        _zustand.value = DriveZustand.Verbunden(aktuell.kontoName, ordner.id, aktuell.token)
        viewModelScope.launch {
            ordnerInhaltLaden(aktuell.token, aktuell.kontoName, ordner.id)
        }
    }

    // Erstellt einen neuen Ordner in Drive.
    fun ordnerBestaetigen(name: String) {
        val aktuell = _zustand.value as? DriveZustand.OrdnerBenennen ?: return
        _zustand.value = DriveZustand.Verbindet
        ladeJob = viewModelScope.launch {
            val ordnerId = DriveVerbindung.ordnerSicherstellen(aktuell.token, name)
            if (ordnerId != null) {
                ordnerPrefs.speichern(name, ordnerId)
                _zustand.value = DriveZustand.Verbunden(
                    aktuell.kontoName, ordnerId, aktuell.token
                )
                ordnerInhaltLaden(aktuell.token, aktuell.kontoName, ordnerId)
            } else {
                _zustand.value = DriveZustand.Fehler(
                    "Ordner konnte nicht erstellt werden.",
                    aktuell.kontoName, aktuell.token
                )
            }
        }
    }

    // Wechselt den verbundenen Ordner — laedt Ordnerliste neu.
    fun ordnerWechseln() {
        ordnerPrefs.loeschen()
        val konto = DriveAnmeldung.letztesKontoHolen(getApplication()) ?: run {
            _zustand.value = DriveZustand.NichtVerbunden
            return
        }
        _zustand.value = DriveZustand.Verbindet
        ladeJob = viewModelScope.launch { ordnerListeAnzeigen(konto) }
    }

    // Erstellt den Sign-In Intent.
    fun anmeldeIntentErstellen(): Intent = DriveAnmeldung.intentErstellen(getApplication())

    // Verarbeitet das Sign-In Ergebnis.
    fun anmeldeErgebnisVerarbeiten(daten: Intent?) {
        _zustand.value = DriveZustand.Verbindet
        ladeJob = viewModelScope.launch {
            try {
                val konto = withContext(Dispatchers.IO) {
                    GoogleSignIn.getSignedInAccountFromIntent(daten)
                        .getResult(ApiException::class.java)
                }
                ordnerListeAnzeigen(konto)
            } catch (e: ApiException) {
                _zustand.value = DriveZustand.Fehler(
                    "Anmeldung fehlgeschlagen: ${e.statusCode}"
                )
            } catch (e: Exception) {
                _zustand.value = DriveZustand.Fehler(
                    e.message ?: "Unbekannter Fehler"
                )
            }
        }
    }

    // Versucht den Fehler zu beheben — mit Retry wenn Token vorhanden.
    fun fehlerBeheben() {
        val aktuell = _zustand.value as? DriveZustand.Fehler ?: return
        val token = aktuell.token
        val kontoName = aktuell.kontoName
        if (token != null && kontoName != null) {
            _zustand.value = DriveZustand.Verbindet
            ladeJob = viewModelScope.launch {
                val konto = DriveAnmeldung.letztesKontoHolen(getApplication()) ?: run {
                    _zustand.value = DriveZustand.NichtVerbunden
                    return@launch
                }
                ordnerListeAnzeigen(konto)
            }
        } else {
            _zustand.value = DriveZustand.NichtVerbunden
        }
    }

    // Laedt den Ordnerinhalt neu — wird nach einem Dokument-Upload aufgerufen.
    fun inhaltNeuLaden() {
        val aktuell = _zustand.value
        val (token, kontoName, ordnerId) = when (aktuell) {
            is DriveZustand.InhaltGeladen -> Triple(aktuell.token, aktuell.kontoName, aktuell.ordnerId)
            is DriveZustand.Verbunden -> Triple(aktuell.token, aktuell.kontoName, aktuell.ordnerId)
            else -> return
        }
        ladeJob = viewModelScope.launch { ordnerInhaltLaden(token, kontoName, ordnerId) }
    }

    // Laedt ein Foto direkt in den verbundenen Ordner hoch (Schnellmenue-Funktion).
    fun fotoSchnellHochladen(fotoUri: Uri, context: Context) {
        val aktuell = _zustand.value
        val token = when (aktuell) {
            is DriveZustand.InhaltGeladen -> aktuell.token
            is DriveZustand.Verbunden -> aktuell.token
            else -> null
        }
        val ordnerId = when (aktuell) {
            is DriveZustand.InhaltGeladen -> aktuell.ordnerId
            is DriveZustand.Verbunden -> aktuell.ordnerId
            else -> null
        }
        val ordnerNameAktuell = ordnerPrefs.ordnerName ?: "Drive"

        if (token == null || ordnerId == null) {
            _schnellUploadZustand.value = SchnellUploadZustand.Fehler("Kein Drive-Ordner verbunden.")
            return
        }

        _schnellUploadZustand.value = SchnellUploadZustand.Laedt
        viewModelScope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(fotoUri)?.readBytes()
                } ?: throw Exception("Foto nicht lesbar")
                val dateiname = "foto_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
                DriveVerbindung.dateiHochladen(token, ordnerId, dateiname, "image/jpeg", bytes)
                _schnellUploadZustand.value = SchnellUploadZustand.Fertig(dateiname, ordnerNameAktuell)
                inhaltNeuLaden()
            } catch (e: Exception) {
                _schnellUploadZustand.value = SchnellUploadZustand.Fehler(
                    e.message ?: "Upload fehlgeschlagen"
                )
            }
        }
    }

    // Setzt den Schnell-Upload-Zustand zurueck.
    fun schnellUploadZuruecksetzen() {
        _schnellUploadZustand.value = null
    }

    // Setzt den Zustand zurueck.
    fun zuruecksetzen() { _zustand.value = DriveZustand.NichtVerbunden }

    // Meldet ab und loescht die Einstellungen.
    fun abmelden() {
        ordnerPrefs.loeschen()
        DriveAnmeldung.abmelden(getApplication()) {
            _zustand.value = DriveZustand.NichtVerbunden
        }
    }

    companion object {
        private const val MINDEST_ANZEIGEZEIT_MS = 2_500L
    }
}
