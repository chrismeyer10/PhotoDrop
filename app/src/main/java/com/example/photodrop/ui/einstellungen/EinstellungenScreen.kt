package com.example.photodrop.ui.einstellungen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.photodrop.agent.AgentResult
import com.example.photodrop.agent.AgentService
import kotlinx.coroutines.launch

// Stateful: Verbindet den ApiSchluesselSpeicher mit der UI inkl. Verbindungstest.
@Composable
fun EinstellungenScreen(onMenuOeffnen: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gespeicherterKey = ApiSchluesselSpeicher.lesen(context)
    var schluessel by remember { mutableStateOf(gespeicherterKey ?: "") }
    var istGespeichert by remember { mutableStateOf(gespeicherterKey != null) }
    var testLaeuft by remember { mutableStateOf(false) }
    var testMeldung by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    testMeldung?.let { (erfolg, meldung) ->
        AlertDialog(
            onDismissRequest = { testMeldung = null },
            title = { Text(if (erfolg) "Verbindung erfolgreich" else "Verbindungsfehler") },
            text = { Text(meldung) },
            confirmButton = { Button(onClick = { testMeldung = null }) { Text("OK") } }
        )
    }

    EinstellungenInhalt(
        schluessel = schluessel,
        istGespeichert = istGespeichert,
        testLaeuft = testLaeuft,
        onSchluesselAendern = { schluessel = it },
        onSpeichern = {
            ApiSchluesselSpeicher.speichern(context, schluessel)
            istGespeichert = true
            testLaeuft = true
            scope.launch {
                val ergebnis = AgentService(schluessel.trim()).run(
                    prompt = "Antworte mit genau einem Wort: OK",
                    systemPrompt = "Sei sehr kurz."
                )
                testLaeuft = false
                testMeldung = when (ergebnis) {
                    is AgentResult.Success -> true to "API-Schluessel gespeichert und Verbindung erfolgreich hergestellt."
                    is AgentResult.Error -> false to verbindungsFehlerText(ergebnis.message)
                }
            }
        },
        onLoeschen = {
            ApiSchluesselSpeicher.loeschen(context)
            schluessel = ""
            istGespeichert = false
        },
        onMenuOeffnen = onMenuOeffnen
    )
}

// Wandelt eine Fehlermeldung in einen nutzerfreundlichen Text um.
private fun verbindungsFehlerText(rohMeldung: String): String {
    val klein = rohMeldung.lowercase()
    return when {
        "401" in klein || "unauthorized" in klein || "authentication" in klein ->
            "Schluessel ungueltig. Bitte pruefen ob der Key korrekt kopiert wurde."
        "credit balance" in klein || "billing" in klein ->
            "Kontoguthaben erschoepft. Bitte Anthropic-Konto pruefen."
        "rate limit" in klein || "429" in klein ->
            "Zu viele Anfragen. Bitte kurz warten und erneut versuchen."
        "network" in klein || "connect" in klein ->
            "Keine Internetverbindung. Bitte Netzwerk pruefen."
        else -> "Fehler: ${rohMeldung.take(120)}"
    }
}
