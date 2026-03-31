package com.example.photodrop.ui.einstellungen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.photodrop.agent.KiAnbieter
import com.example.photodrop.ui.theme.AppHintergrund
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextHell

// Stateless: Zeigt die Einstellungen mit Scaffold und TopBar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EinstellungenInhalt(
    gewaehlterAnbieter: KiAnbieter,
    anthropicKey: String,
    openAiKey: String,
    istAnthropicGespeichert: Boolean,
    istOpenAiGespeichert: Boolean,
    testLaeuft: Boolean,
    onAnbieterGewaehlt: (KiAnbieter) -> Unit,
    onAnthropicKeyAendern: (String) -> Unit,
    onOpenAiKeyAendern: (String) -> Unit,
    onAnthropicSpeichern: () -> Unit,
    onAnthropicLoeschen: () -> Unit,
    onOpenAiSpeichern: () -> Unit,
    onOpenAiLoeschen: () -> Unit,
    onMenuOeffnen: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Einstellungen", color = TextHell) },
                navigationIcon = {
                    IconButton(onClick = onMenuOeffnen) {
                        Icon(Icons.Filled.Menu, "Menue oeffnen", tint = TextHell)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = OberflächenFarbe
                )
            )
        },
        containerColor = AppHintergrund
    ) { innenAbstand ->
        EinstellungenScrollInhalt(
            gewaehlterAnbieter = gewaehlterAnbieter,
            anthropicKey = anthropicKey,
            openAiKey = openAiKey,
            istAnthropicGespeichert = istAnthropicGespeichert,
            istOpenAiGespeichert = istOpenAiGespeichert,
            testLaeuft = testLaeuft,
            onAnbieterGewaehlt = onAnbieterGewaehlt,
            onAnthropicKeyAendern = onAnthropicKeyAendern,
            onOpenAiKeyAendern = onOpenAiKeyAendern,
            onAnthropicSpeichern = onAnthropicSpeichern,
            onAnthropicLoeschen = onAnthropicLoeschen,
            onOpenAiSpeichern = onOpenAiSpeichern,
            onOpenAiLoeschen = onOpenAiLoeschen,
            modifier = Modifier.padding(innenAbstand)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Einstellungen OCR")
@Composable
private fun EinstellungenInhaltOcrVorschau() {
    PhotoDropTheme {
        EinstellungenInhalt(
            gewaehlterAnbieter = KiAnbieter.OcrKostenlos,
            anthropicKey = "", openAiKey = "",
            istAnthropicGespeichert = false, istOpenAiGespeichert = false,
            testLaeuft = false,
            onAnbieterGewaehlt = {}, onAnthropicKeyAendern = {}, onOpenAiKeyAendern = {},
            onAnthropicSpeichern = {}, onAnthropicLoeschen = {},
            onOpenAiSpeichern = {}, onOpenAiLoeschen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Einstellungen Claude")
@Composable
private fun EinstellungenInhaltClaudeVorschau() {
    PhotoDropTheme {
        EinstellungenInhalt(
            gewaehlterAnbieter = KiAnbieter.Claude,
            anthropicKey = "sk-ant-xxx", openAiKey = "",
            istAnthropicGespeichert = true, istOpenAiGespeichert = false,
            testLaeuft = false,
            onAnbieterGewaehlt = {}, onAnthropicKeyAendern = {}, onOpenAiKeyAendern = {},
            onAnthropicSpeichern = {}, onAnthropicLoeschen = {},
            onOpenAiSpeichern = {}, onOpenAiLoeschen = {}
        )
    }
}
