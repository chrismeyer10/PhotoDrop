package com.example.photodrop.dokument

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Zeigt einen Analyse-Fehler mit Optionen: OCR, Einstellungen, manuelles Speichern.
@Composable
fun DokumentAnalyseFehlerInhalt(
    meldung: String,
    onOcrAnalysieren: () -> Unit = {},
    onEinstellungenOeffnen: () -> Unit = {},
    onTrotzdemSpeichern: () -> Unit = {},
    onZurueck: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.CloudUpload,
            contentDescription = null,
            tint = AkzentFarbe,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Analyse nicht moeglich",
            style = MaterialTheme.typography.headlineSmall,
            color = TextHell
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(meldung, color = TextGedaempft, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onOcrAnalysieren,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
        ) {
            Icon(Icons.Filled.DocumentScanner, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text("Kostenlos analysieren (OCR)")
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = onEinstellungenOeffnen,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Filled.Settings, null, tint = TextHell, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text("Eigenen API-Schluessel eingeben", color = TextHell)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onTrotzdemSpeichern,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OberflächenFarbe)
        ) { Text("Trotzdem in Drive speichern", color = TextHell) }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onZurueck,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OberflächenFarbe)
        ) { Text("Zurueck", color = TextHell) }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "AnalyseFehler")
@Composable
private fun DokumentAnalyseFehlerInhaltVorschau() {
    PhotoDropTheme {
        DokumentAnalyseFehlerInhalt(
            meldung = "Kontoguthaben erschoepft. Bitte API-Schluessel pruefen."
        )
    }
}
