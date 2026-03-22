package com.example.photodrop.ui.foto.analyse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Zeigt das Ergebnis der KI-Foto-Analyse in einem Dialog an.
@Composable
fun FotoAnalyseDialog(
    zustand: FotoAnalyseZustand,
    onSchliessen: () -> Unit
) {
    val buttonText = if (zustand is FotoAnalyseZustand.Laeuft) "Abbrechen" else "Schliessen"
    AlertDialog(
        onDismissRequest = onSchliessen,
        containerColor = OberflächenFarbe,
        title = { Text("KI-Analyse", color = TextHell) },
        text = { FotoAnalyseInhalt(zustand) },
        confirmButton = {
            TextButton(onClick = onSchliessen) {
                Text(buttonText, color = AkzentFarbe)
            }
        }
    )
}

// Zeigt den passenden Inhalt je nach Analyse-Zustand.
@Composable
private fun FotoAnalyseInhalt(zustand: FotoAnalyseZustand) {
    when (zustand) {
        is FotoAnalyseZustand.Bereit -> Text("Analyse wird gestartet...", color = TextGedaempft)
        is FotoAnalyseZustand.Laeuft -> AnalyseLadeAnzeige()
        is FotoAnalyseZustand.Ergebnis -> Text(zustand.text, color = TextHell)
        is FotoAnalyseZustand.Fehler -> Text(zustand.meldung, color = AkzentFarbe)
    }
}

// Ladeindikator waehrend die Analyse laeuft.
@Composable
private fun AnalyseLadeAnzeige() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = AkzentFarbe)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Foto wird analysiert...", color = TextGedaempft)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Laeuft")
@Composable
private fun FotoAnalyseDialogLaeuftVorschau() {
    PhotoDropTheme {
        FotoAnalyseDialog(zustand = FotoAnalyseZustand.Laeuft, onSchliessen = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Ergebnis")
@Composable
private fun FotoAnalyseDialogErgebnisVorschau() {
    PhotoDropTheme {
        FotoAnalyseDialog(
            zustand = FotoAnalyseZustand.Ergebnis("Ein Landschaftsfoto mit Bergen."),
            onSchliessen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Fehler")
@Composable
private fun FotoAnalyseDialogFehlerVorschau() {
    PhotoDropTheme {
        FotoAnalyseDialog(
            zustand = FotoAnalyseZustand.Fehler("Kein API-Key konfiguriert."),
            onSchliessen = {}
        )
    }
}
