package com.example.photodrop.dokument

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextHell

// Zeigt Optionen fuer das geladene Dokument: Analysieren oder Zurueck.
@Composable
fun DokumentGeladenInhalt(onAnalysieren: () -> Unit = {}, onZurueck: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dokument geladen", color = TextHell)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAnalysieren,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
        ) { Text("Mit KI analysieren") }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onZurueck,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OberflächenFarbe)
        ) { Text("Zurueck", color = TextHell) }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Geladen")
@Composable
private fun DokumentGeladenInhaltVorschau() {
    PhotoDropTheme { DokumentGeladenInhalt() }
}
