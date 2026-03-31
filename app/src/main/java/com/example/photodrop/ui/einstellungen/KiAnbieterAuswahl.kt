package com.example.photodrop.ui.einstellungen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.agent.KiAnbieter
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Zeigt eine Liste von KI-Anbietern als Radiobuttons zur Auswahl.
@Composable
fun KiAnbieterAuswahl(
    gewaehlterAnbieter: KiAnbieter,
    onAnbieterGewaehlt: (KiAnbieter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            "KI-Anbieter waehlen",
            style = MaterialTheme.typography.titleMedium,
            color = TextHell
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Bestimmt welche KI fuer die Dokumentanalyse verwendet wird.",
            style = MaterialTheme.typography.bodySmall,
            color = TextGedaempft
        )
        Spacer(modifier = Modifier.height(12.dp))
        KiAnbieter.alle.forEach { anbieter ->
            AnbieterZeile(
                anbieter = anbieter,
                istGewaehlt = anbieter.modellId == gewaehlterAnbieter.modellId,
                onGewaehlt = { onAnbieterGewaehlt(anbieter) }
            )
        }
    }
}

// Einzelne Zeile fuer einen KI-Anbieter mit Radiobutton und Name.
@Composable
private fun AnbieterZeile(
    anbieter: KiAnbieter,
    istGewaehlt: Boolean,
    onGewaehlt: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onGewaehlt)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = istGewaehlt,
            onClick = onGewaehlt,
            colors = RadioButtonDefaults.colors(selectedColor = AkzentFarbe)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(anbieter.anzeigeName, color = TextHell)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "KI-Auswahl OCR")
@Composable
private fun KiAnbieterAuswahlOcrVorschau() {
    PhotoDropTheme {
        KiAnbieterAuswahl(
            gewaehlterAnbieter = KiAnbieter.OcrKostenlos,
            onAnbieterGewaehlt = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "KI-Auswahl Claude")
@Composable
private fun KiAnbieterAuswahlClaudeVorschau() {
    PhotoDropTheme {
        KiAnbieterAuswahl(
            gewaehlterAnbieter = KiAnbieter.Claude,
            onAnbieterGewaehlt = {}
        )
    }
}
