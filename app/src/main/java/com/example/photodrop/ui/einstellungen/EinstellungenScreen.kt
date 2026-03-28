package com.example.photodrop.ui.einstellungen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

// Stateful: Verbindet den ApiSchluesselSpeicher mit der UI.
@Composable
fun EinstellungenScreen(onMenuOeffnen: () -> Unit = {}) {
    val context = LocalContext.current
    val gespeicherterKey = ApiSchluesselSpeicher.lesen(context)
    var schluessel by remember { mutableStateOf(gespeicherterKey ?: "") }
    var istGespeichert by remember { mutableStateOf(gespeicherterKey != null) }

    EinstellungenInhalt(
        schluessel = schluessel,
        istGespeichert = istGespeichert,
        onSchluesselAendern = { schluessel = it },
        onSpeichern = {
            ApiSchluesselSpeicher.speichern(context, schluessel)
            istGespeichert = true
        },
        onLoeschen = {
            ApiSchluesselSpeicher.loeschen(context)
            schluessel = ""
            istGespeichert = false
        },
        onMenuOeffnen = onMenuOeffnen
    )
}
