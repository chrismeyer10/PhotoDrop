package com.example.photodrop.ui.drive

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.photodrop.ui.drive.api.DriveOrdner
import com.example.photodrop.ui.drive.api.DriveVerbindung
import com.example.photodrop.ui.drive.zustand.DriveZustand
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Verwaltet die Google Drive Verbindung, den Navigationspfad und den aktiven Upload-Ordner.
class DriveViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = OrdnerEinstellungen(application.getSharedPreferences("drive_prefs", 0))
    private val upload = DriveUploadLogik()

    private val _zustand = MutableStateFlow<DriveZustand>(DriveZustand.NichtVerbunden)
    val zustand: StateFlow<DriveZustand> = _zustand

    private val _navigationsStack = MutableStateFlow<List<DriveOrdner>>(emptyList())
    val navigationsStack: StateFlow<List<DriveOrdner>> = _navigationsStack.asStateFlow()

    private val _aktiverOrdner = MutableStateFlow<DriveOrdner?>(null)
    val aktiverOrdner: StateFlow<DriveOrdner?> = _aktiverOrdner.asStateFlow()

    private val _schnellUploadZustand = MutableStateFlow<SchnellUploadZustand?>(null)
    val schnellUploadZustand: StateFlow<SchnellUploadZustand?> = _schnellUploadZustand

    // Steuert ob der Ordner-Auswahl-Dialog nach dem Foto angezeigt wird.
    private val _zeigeOrdnerAuswahlDialog = MutableStateFlow(false)
    val zeigeOrdnerAuswahlDialog: StateFlow<Boolean> = _zeigeOrdnerAuswahlDialog.asStateFlow()

    private var wartendesFotoUri: Uri? = null
    private var ladeJob: Job? = null

    // Fallback-Accessor fuer den Ordnernamen (Kompatibilitaet).
    val ordnerName: String? get() = _aktiverOrdner.value?.name ?: prefs.ordnerName

    private val verbindung = DriveVerbindungsLogik(application, prefs)

    init {
        aktivenOrdnerAusPrefsLaden()
        automatischVerbinden()
    }

    // Laedt den zuletzt gespeicherten aktiven Ordner aus SharedPreferences.
    private fun aktivenOrdnerAusPrefsLaden() {
        val name = prefs.ordnerName
        val id = prefs.ordnerId
        if (name != null && id != null) _aktiverOrdner.value = DriveOrdner(id, name)
    }

    // Prueft ob bereits ein Konto angemeldet ist und verbindet automatisch.
    private fun automatischVerbinden() {
        val konto = verbindung.letztesKontoHolen() ?: return
        _zustand.value = DriveZustand.Verbindet
        ladeJob = viewModelScope.launch {
            val name = prefs.ordnerName
            val id = prefs.ordnerId
            if (name != null && id != null) tokenHolenUndVerbinden(konto, name, id)
            else ordnerListeAnzeigen(konto)
        }
    }

    // Laedt die Root-Ordnerliste zur Auswahl.
    private suspend fun ordnerListeAnzeigen(
        konto: com.google.android.gms.auth.api.signin.GoogleSignInAccount
    ) {
        try {
            val token = verbindung.tokenHolen(konto)
            val kontoName = konto.email ?: konto.displayName ?: ""
            _zustand.value = DriveZustand.OrdnerLaden(kontoName, token)
            val ordner = DriveVerbindung.ordnerListeLaden(token)
            _zustand.value = if (ordner.isEmpty()) {
                DriveZustand.OrdnerBenennen(kontoName, token, hatVorhandeneListe = false)
            } else {
                DriveZustand.OrdnerAuswaehlen(kontoName, token, ordner, verbindung.gespeichertenOrdnerFinden(ordner))
            }
        } catch (e: Exception) {
            _zustand.value = DriveZustand.NichtVerbunden
        }
    }

    // Stellt die Verbindung mit gespeichertem Ordner her.
    private suspend fun tokenHolenUndVerbinden(
        konto: com.google.android.gms.auth.api.signin.GoogleSignInAccount,
        name: String, id: String
    ) {
        try {
            val token = verbindung.tokenHolen(konto)
            val kontoName = konto.email ?: konto.displayName ?: ""
            _zustand.value = DriveZustand.Verbunden(kontoName, id, token)
            rootInhaltLaden(token, kontoName)
        } catch (e: Exception) {
            _zustand.value = DriveZustand.Fehler(e.message ?: "Unbekannter Fehler")
        }
    }

    // Laedt alle Root-Ordner fuer die Drive-Navigationsansicht.
    private suspend fun rootInhaltLaden(token: String, kontoName: String) {
        _navigationsStack.value = emptyList()
        _zustand.value = DriveZustand.InhaltGeladen(
            kontoName = kontoName,
            ordnerId = "root",
            dateien = verbindung.rootOrdnerAlsDateien(token),
            token = token
        )
    }

    // Laedt den Inhalt eines Ordners und aktualisiert den Zustand.
    private suspend fun ordnerInhaltLaden(
        token: String, kontoName: String, ordnerId: String, ordnerName: String? = null
    ) {
        _zustand.value = DriveZustand.InhaltGeladen(
            kontoName = kontoName,
            ordnerId = ordnerId,
            dateien = upload.ordnerInhaltLaden(token, ordnerId),
            token = token,
            ordnerName = ordnerName
        )
    }

    // Navigiert in einen Unterordner und laedt dessen Inhalt.
    fun inOrdnerNavigieren(ordner: DriveOrdner) {
        val aktuell = _zustand.value as? DriveZustand.InhaltGeladen ?: return
        _navigationsStack.value = _navigationsStack.value + ordner
        ladeJob = viewModelScope.launch {
            ordnerInhaltLaden(aktuell.token, aktuell.kontoName, ordner.id, ordner.name)
        }
    }

    // Navigiert eine Ebene zurueck (Breadcrumb).
    fun zurueckNavigieren() {
        val aktuell = _zustand.value as? DriveZustand.InhaltGeladen ?: return
        val stack = _navigationsStack.value
        if (stack.isEmpty()) return
        val neuerStack = stack.dropLast(1)
        _navigationsStack.value = neuerStack
        ladeJob = viewModelScope.launch {
            if (neuerStack.isEmpty()) rootInhaltLaden(aktuell.token, aktuell.kontoName)
            else {
                val eltern = neuerStack.last()
                ordnerInhaltLaden(aktuell.token, aktuell.kontoName, eltern.id, eltern.name)
            }
        }
    }

    // Setzt den aktiven Upload-Ordner und speichert ihn persistent.
    fun aktivenOrdnerSetzen(ordner: DriveOrdner) {
        _aktiverOrdner.value = ordner
        prefs.speichern(ordner.name, ordner.id)
    }

    // Bricht den aktuellen Ladevorgang ab.
    fun ladeAbbrechen() {
        ladeJob?.cancel(); ladeJob = null
        _zustand.value = DriveZustand.NichtVerbunden
    }

    // Wechselt zum Neuen-Ordner-Formular.
    fun neuenOrdnerErstellen() {
        val s = _zustand.value as? DriveZustand.OrdnerAuswaehlen ?: return
        _zustand.value = DriveZustand.OrdnerBenennen(s.kontoName, s.token, hatVorhandeneListe = true)
    }

    // Wechselt vom Ordner-Benennen zurueck — je nach Kontext.
    fun ordnerBenennenAbbrechen() {
        val s = _zustand.value as? DriveZustand.OrdnerBenennen ?: return
        if (!s.hatVorhandeneListe) {
            verbindung.abmelden {}
        } else {
            _zustand.value = DriveZustand.Verbindet
            ladeJob = viewModelScope.launch {
                verbindung.letztesKontoHolen()?.let { ordnerListeAnzeigen(it) }
                    ?: run { _zustand.value = DriveZustand.NichtVerbunden }
            }
        }
    }

    // Waehlt einen bestehenden Ordner aus der OrdnerAuswaehlen-Ansicht aus.
    fun ordnerAuswaehlen(ordner: DriveOrdner) {
        val s = _zustand.value as? DriveZustand.OrdnerAuswaehlen ?: return
        prefs.speichern(ordner.name, ordner.id)
        _aktiverOrdner.value = ordner
        _zustand.value = DriveZustand.Verbunden(s.kontoName, ordner.id, s.token)
        ladeJob = viewModelScope.launch { rootInhaltLaden(s.token, s.kontoName) }
    }

    // Erstellt einen neuen Ordner in Drive und verbindet ihn.
    fun ordnerBestaetigen(name: String) {
        val s = _zustand.value as? DriveZustand.OrdnerBenennen ?: return
        _zustand.value = DriveZustand.Verbindet
        ladeJob = viewModelScope.launch {
            val id = upload.ordnerBestaetigen(s.token, name)
            if (id != null) {
                prefs.speichern(name, id)
                _aktiverOrdner.value = DriveOrdner(id, name)
                _zustand.value = DriveZustand.Verbunden(s.kontoName, id, s.token)
                rootInhaltLaden(s.token, s.kontoName)
            } else {
                _zustand.value = DriveZustand.Fehler("Ordner konnte nicht erstellt werden.", s.kontoName, s.token)
            }
        }
    }

    // Wechselt den verbundenen Ordner — laedt Ordnerliste neu.
    fun ordnerWechseln() {
        prefs.loeschen()
        val konto = verbindung.letztesKontoHolen() ?: run {
            _zustand.value = DriveZustand.NichtVerbunden; return
        }
        _zustand.value = DriveZustand.Verbindet
        ladeJob = viewModelScope.launch { ordnerListeAnzeigen(konto) }
    }

    // Erstellt den Sign-In Intent.
    fun anmeldeIntentErstellen(): Intent = verbindung.anmeldeIntentErstellen()

    // Verarbeitet das Sign-In Ergebnis.
    fun anmeldeErgebnisVerarbeiten(daten: Intent?) {
        _zustand.value = DriveZustand.Verbindet
        ladeJob = viewModelScope.launch {
            try {
                val konto = withContext(Dispatchers.IO) {
                    GoogleSignIn.getSignedInAccountFromIntent(daten).getResult(ApiException::class.java)
                }
                ordnerListeAnzeigen(konto)
            } catch (e: ApiException) {
                _zustand.value = DriveZustand.Fehler("Anmeldung fehlgeschlagen: ${e.statusCode}")
            } catch (e: Exception) {
                _zustand.value = DriveZustand.Fehler(e.message ?: "Unbekannter Fehler")
            }
        }
    }

    // Versucht den Fehler zu beheben — mit Retry wenn Token vorhanden.
    fun fehlerBeheben() {
        val s = _zustand.value as? DriveZustand.Fehler ?: return
        if (s.token != null && s.kontoName != null) {
            _zustand.value = DriveZustand.Verbindet
            ladeJob = viewModelScope.launch {
                verbindung.letztesKontoHolen()?.let { ordnerListeAnzeigen(it) }
                    ?: run { _zustand.value = DriveZustand.NichtVerbunden }
            }
        } else {
            _zustand.value = DriveZustand.NichtVerbunden
        }
    }

    // Laedt den Ordnerinhalt neu — nach einem Dokument-Upload.
    fun inhaltNeuLaden() {
        val s = _zustand.value
        val (token, kontoName, ordnerId, oName) = when (s) {
            is DriveZustand.InhaltGeladen -> listOf(s.token, s.kontoName, s.ordnerId, s.ordnerName)
            is DriveZustand.Verbunden -> listOf(s.token, s.kontoName, s.ordnerId, null)
            else -> return
        }
        ladeJob = viewModelScope.launch {
            ordnerInhaltLaden(token as String, kontoName as String, ordnerId as String, oName as String?)
        }
    }

    // Speichert das Foto-URI und zeigt den Ordner-Auswahl-Dialog.
    fun fotoFuerUploadVormerken(fotoUri: Uri) {
        wartendesFotoUri = fotoUri
        _zeigeOrdnerAuswahlDialog.value = true
    }

    // Schliesst den Ordner-Auswahl-Dialog ohne Upload.
    fun ordnerAuswahlAbbrechen() {
        wartendesFotoUri = null
        _zeigeOrdnerAuswahlDialog.value = false
    }

    // Waehlt Ordner aus dem Dialog und startet den Upload.
    fun ordnerFuerFotoAuswaehlenUndHochladen(ordner: DriveOrdner, context: Context) {
        aktivenOrdnerSetzen(ordner)
        _zeigeOrdnerAuswahlDialog.value = false
        val uri = wartendesFotoUri ?: return
        wartendesFotoUri = null
        fotoSchnellHochladen(uri, context)
    }

    // Laedt ein Foto in den aktiven Ordner hoch (Schnellmenue-Funktion).
    fun fotoSchnellHochladen(fotoUri: Uri, context: Context) {
        val token = tokenAusZustand() ?: run {
            _schnellUploadZustand.value = SchnellUploadZustand.Fehler("Kein Drive-Ordner verbunden.")
            return
        }
        val zielId = _aktiverOrdner.value?.id ?: prefs.ordnerId ?: run {
            _schnellUploadZustand.value = SchnellUploadZustand.Fehler("Kein Upload-Ordner gewaehlt.")
            return
        }
        val zielName = _aktiverOrdner.value?.name ?: prefs.ordnerName ?: "Drive"
        _schnellUploadZustand.value = SchnellUploadZustand.Laedt
        viewModelScope.launch {
            try {
                val dateiname = upload.fotoHochladen(fotoUri, context, token, zielId)
                _schnellUploadZustand.value = SchnellUploadZustand.Fertig(dateiname, zielName)
                inhaltNeuLaden()
            } catch (e: Exception) {
                _schnellUploadZustand.value = SchnellUploadZustand.Fehler(e.message ?: "Upload fehlgeschlagen")
            }
        }
    }

    // Setzt den Schnell-Upload-Zustand zurueck.
    fun schnellUploadZuruecksetzen() { _schnellUploadZustand.value = null }

    // Setzt den Zustand zurueck auf NichtVerbunden.
    fun zuruecksetzen() { _zustand.value = DriveZustand.NichtVerbunden }

    // Meldet ab und loescht alle gespeicherten Einstellungen.
    fun abmelden() {
        _aktiverOrdner.value = null
        _navigationsStack.value = emptyList()
        verbindung.abmelden {}
    }

    // Gibt alle Root-Ordner aus dem aktuellen Zustand zurueck (fuer den Dialog).
    fun rootOrdnerAusZustand(): List<DriveOrdner> {
        val s = _zustand.value as? DriveZustand.InhaltGeladen ?: return emptyList()
        if (s.ordnerName != null) return emptyList()
        return s.dateien.filter { it.istOrdner }.map { DriveOrdner(it.id, it.name) }
    }

    // Extrahiert das Token aus dem aktuellen Zustand.
    private fun tokenAusZustand(): String? = when (val s = _zustand.value) {
        is DriveZustand.InhaltGeladen -> s.token
        is DriveZustand.Verbunden -> s.token
        else -> null
    }
}
