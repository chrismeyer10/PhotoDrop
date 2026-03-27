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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

// Zeigt einen Analyse-Fehler mit Option, das Dokument trotzdem in Drive zu speichern.
@Composable
fun DokumentAnalyseFehlerInhalt(
    meldung: String,
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
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Das Dokument kann trotzdem in Google Drive gespeichert werden.",
            color = TextGedaempft,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onTrotzdemSpeichern,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
        ) { Text("Trotzdem in Drive speichern") }
        Spacer(modifier = Modifier.height(12.dp))
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
            meldung = "Die KI-Analyse ist voruebergehend nicht verfuegbar."
        )
    }
}
