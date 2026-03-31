package com.example.photodrop.ui.einstellungen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.agent.KiAnbieter
import com.example.photodrop.ui.theme.KartenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme

// Scrollbarer Inhaltsbereich mit Anbieter-Auswahl und passendem API-Key-Abschnitt.
@Composable
fun EinstellungenScrollInhalt(
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        KiAnbieterAuswahl(
            gewaehlterAnbieter = gewaehlterAnbieter,
            onAnbieterGewaehlt = onAnbieterGewaehlt
        )
        if (gewaehlterAnbieter.benoetigtApiKey) {
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = KartenFarbe)
            Spacer(modifier = Modifier.height(24.dp))
            ApiKeyAbschnittFuerAnbieter(
                anbieter = gewaehlterAnbieter,
                anthropicKey = anthropicKey,
                openAiKey = openAiKey,
                istAnthropicGespeichert = istAnthropicGespeichert,
                istOpenAiGespeichert = istOpenAiGespeichert,
                testLaeuft = testLaeuft,
                onAnthropicKeyAendern = onAnthropicKeyAendern,
                onOpenAiKeyAendern = onOpenAiKeyAendern,
                onAnthropicSpeichern = onAnthropicSpeichern,
                onAnthropicLoeschen = onAnthropicLoeschen,
                onOpenAiSpeichern = onOpenAiSpeichern,
                onOpenAiLoeschen = onOpenAiLoeschen
            )
        }
    }
}

// Zeigt den passenden API-Key-Abschnitt je nach gewaehltem Anbieter.
@Composable
private fun ApiKeyAbschnittFuerAnbieter(
    anbieter: KiAnbieter,
    anthropicKey: String,
    openAiKey: String,
    istAnthropicGespeichert: Boolean,
    istOpenAiGespeichert: Boolean,
    testLaeuft: Boolean,
    onAnthropicKeyAendern: (String) -> Unit,
    onOpenAiKeyAendern: (String) -> Unit,
    onAnthropicSpeichern: () -> Unit,
    onAnthropicLoeschen: () -> Unit,
    onOpenAiSpeichern: () -> Unit,
    onOpenAiLoeschen: () -> Unit
) {
    when (anbieter) {
        KiAnbieter.Claude -> ApiSchluesselAbschnitt(
            titel = "Anthropic API-Schluessel",
            hinweis = "Benoetigt fuer Claude. Erhaltlich unter console.anthropic.com.",
            platzhalter = "sk-ant-...",
            schluessel = anthropicKey,
            istGespeichert = istAnthropicGespeichert,
            testLaeuft = testLaeuft,
            onSchluesselAendern = onAnthropicKeyAendern,
            onSpeichern = onAnthropicSpeichern,
            onLoeschen = onAnthropicLoeschen
        )
        KiAnbieter.GptMini -> ApiSchluesselAbschnitt(
            titel = "OpenAI API-Schluessel",
            hinweis = "Benoetigt fuer GPT-4o-mini. Erhaltlich unter platform.openai.com.",
            platzhalter = "sk-...",
            schluessel = openAiKey,
            istGespeichert = istOpenAiGespeichert,
            testLaeuft = testLaeuft,
            onSchluesselAendern = onOpenAiKeyAendern,
            onSpeichern = onOpenAiSpeichern,
            onLoeschen = onOpenAiLoeschen
        )
        else -> {}
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Scroll OCR")
@Composable
private fun EinstellungenScrollInhaltOcrVorschau() {
    PhotoDropTheme {
        EinstellungenScrollInhalt(
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
