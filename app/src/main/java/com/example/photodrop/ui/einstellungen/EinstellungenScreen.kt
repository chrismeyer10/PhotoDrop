package com.example.photodrop.ui.einstellungen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photodrop.agent.AgentResult
import com.example.photodrop.agent.KiAnbieter
import com.example.photodrop.agent.KiDienstFabrik
import kotlinx.coroutines.launch

// Stateful: Verbindet das EinstellungenViewModel mit der UI inkl. Verbindungstest.
@Composable
fun EinstellungenScreen(
    viewModel: EinstellungenViewModel = viewModel(),
    onMenuOeffnen: () -> Unit = {}
) {
    val gewaehlterAnbieter by viewModel.gewahlterAnbieter.collectAsState()
    val anthropicKey by viewModel.anthropicKey.collectAsState()
    val openAiKey by viewModel.openAiKey.collectAsState()
    val testLaeuft by viewModel.testLaeuft.collectAsState()
    val testErgebnis by viewModel.testErgebnis.collectAsState()
    val scope = rememberCoroutineScope()

    // Zeigt den Claude-API-Hinweis-Dialog wenn Claude gewaehlt und kein Key gespeichert.
    var claudeHinweisZeigen by remember { mutableStateOf(false) }

    if (claudeHinweisZeigen) {
        ClaudeApiHinweisDialog(onSchliessen = { claudeHinweisZeigen = false })
    }

    testErgebnis?.let { (erfolg, meldung) ->
        AlertDialog(
            onDismissRequest = { viewModel.testErgebnisSchliessen() },
            title = { Text(if (erfolg) "Verbindung erfolgreich" else "Verbindungsfehler") },
            text = { Text(meldung) },
            confirmButton = {
                Button(onClick = { viewModel.testErgebnisSchliessen() }) { Text("OK") }
            }
        )
    }

    EinstellungenInhalt(
        gewaehlterAnbieter = gewaehlterAnbieter,
        anthropicKey = anthropicKey,
        openAiKey = openAiKey,
        istAnthropicGespeichert = viewModel.istAnthropicKeyGespeichert(),
        istOpenAiGespeichert = viewModel.istOpenAiKeyGespeichert(),
        testLaeuft = testLaeuft,
        onAnbieterGewaehlt = { anbieter ->
            viewModel.anbieterWaehlen(anbieter)
            // Hinweis zeigen wenn Claude gewaehlt und noch kein API-Key gespeichert.
            if (anbieter == KiAnbieter.Claude && !viewModel.istAnthropicKeyGespeichert()) {
                claudeHinweisZeigen = true
            }
        },
        onAnthropicKeyAendern = { viewModel.anthropicKeyAendern(it) },
        onOpenAiKeyAendern = { viewModel.openAiKeyAendern(it) },
        onAnthropicSpeichern = {
            viewModel.anthropicKeySpeichern()
            scope.launch {
                verbindungTesten(anthropicKey.trim(), KiAnbieter.Claude, viewModel)
            }
        },
        onAnthropicLoeschen = { viewModel.anthropicKeyLoeschen() },
        onOpenAiSpeichern = {
            viewModel.openAiKeySpeichern()
            scope.launch {
                verbindungTesten(openAiKey.trim(), KiAnbieter.GptMini, viewModel)
            }
        },
        onOpenAiLoeschen = { viewModel.openAiKeyLoeschen() },
        onMenuOeffnen = onMenuOeffnen
    )
}

// Fuehrt einen schnellen API-Verbindungstest durch und aktualisiert das ViewModel.
private suspend fun verbindungTesten(
    schluessel: String,
    anbieter: KiAnbieter,
    viewModel: EinstellungenViewModel
) {
    viewModel.testStarten()
    val dienst = KiDienstFabrik.erstellen(anbieter, schluessel)
    if (dienst == null) {
        viewModel.testAbschliessen(false, "Dienst konnte nicht erstellt werden.")
        return
    }
    val ergebnis = dienst.analysieren(
        prompt = "Antworte mit genau einem Wort: OK",
        systemPrompt = "Sei sehr kurz."
    )
    when (ergebnis) {
        is AgentResult.Success -> viewModel.testAbschliessen(
            true, "Verbindung zu ${anbieter.anzeigeName} erfolgreich."
        )
        is AgentResult.Error -> viewModel.testAbschliessen(
            false, verbindungsFehlerText(ergebnis.message)
        )
    }
}
