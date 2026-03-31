package com.example.photodrop.ui.einstellungen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.photodrop.agent.KiAnbieter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Haelt alle Einstellungs-Zustaende und vermittelt zwischen UI und Speicher.
class EinstellungenViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application

    private val _gewahlterAnbieter = MutableStateFlow(
        KiAnbieterSpeicher.anbieterLesen(app)
    )
    val gewahlterAnbieter: StateFlow<KiAnbieter> = _gewahlterAnbieter

    private val _anthropicKey = MutableStateFlow(
        ApiSchluesselSpeicher.lesen(app) ?: ""
    )
    val anthropicKey: StateFlow<String> = _anthropicKey

    private val _openAiKey = MutableStateFlow(
        KiAnbieterSpeicher.openAiKeyLesen(app) ?: ""
    )
    val openAiKey: StateFlow<String> = _openAiKey

    // Wird nach erfolgreichem Verbindungstest gesetzt.
    private val _testErgebnis = MutableStateFlow<Pair<Boolean, String>?>(null)
    val testErgebnis: StateFlow<Pair<Boolean, String>?> = _testErgebnis

    private val _testLaeuft = MutableStateFlow(false)
    val testLaeuft: StateFlow<Boolean> = _testLaeuft

    // Wechselt den gewaehlten KI-Anbieter und speichert ihn.
    fun anbieterWaehlen(anbieter: KiAnbieter) {
        _gewahlterAnbieter.value = anbieter
        KiAnbieterSpeicher.anbieterSpeichern(app, anbieter)
    }

    // Aktualisiert den Anthropic-Key-Text (noch nicht gespeichert).
    fun anthropicKeyAendern(wert: String) { _anthropicKey.value = wert }

    // Aktualisiert den OpenAI-Key-Text (noch nicht gespeichert).
    fun openAiKeyAendern(wert: String) { _openAiKey.value = wert }

    // Speichert den Anthropic-Key dauerhaft.
    fun anthropicKeySpeichern() {
        ApiSchluesselSpeicher.speichern(app, _anthropicKey.value)
    }

    // Loescht den gespeicherten Anthropic-Key.
    fun anthropicKeyLoeschen() {
        ApiSchluesselSpeicher.loeschen(app)
        _anthropicKey.value = ""
    }

    // Speichert den OpenAI-Key dauerhaft.
    fun openAiKeySpeichern() {
        KiAnbieterSpeicher.openAiKeySpeichern(app, _openAiKey.value)
    }

    // Loescht den gespeicherten OpenAI-Key.
    fun openAiKeyLoeschen() {
        KiAnbieterSpeicher.openAiKeyLoeschen(app)
        _openAiKey.value = ""
    }

    // Setzt den Verbindungstest-Laufstatus.
    fun testStarten() { _testLaeuft.value = true }

    // Beendet den Verbindungstest und speichert das Ergebnis.
    fun testAbschliessen(erfolg: Boolean, meldung: String) {
        _testLaeuft.value = false
        _testErgebnis.value = Pair(erfolg, meldung)
    }

    // Schliesst den Test-Ergebnis-Dialog.
    fun testErgebnisSchliessen() { _testErgebnis.value = null }

    // Prueft ob der Anthropic-Key gespeichert ist.
    fun istAnthropicKeyGespeichert(): Boolean =
        !ApiSchluesselSpeicher.lesen(app).isNullOrBlank()

    // Prueft ob der OpenAI-Key gespeichert ist.
    fun istOpenAiKeyGespeichert(): Boolean =
        !KiAnbieterSpeicher.openAiKeyLesen(app).isNullOrBlank()
}
