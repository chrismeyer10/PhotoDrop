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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Bestaetigung nach erfolgreichem Upload.
@Composable
fun DokumentFertigInhalt(
    dateiname: String,
    unterordner: String,
    onWeiter: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = AkzentFarbe,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Gespeichert", style = MaterialTheme.typography.headlineSmall, color = TextHell)
        Spacer(modifier = Modifier.height(8.dp))
        Text(dateiname, color = TextHell)
        Text("in $unterordner", color = TextGedaempft)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onWeiter,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
        ) { Text("Weiteres Dokument") }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Fertig")
@Composable
private fun DokumentFertigInhaltVorschau() {
    PhotoDropTheme {
        DokumentFertigInhalt(
            dateiname = "Rechnung_Amazon_2026-03.pdf",
            unterordner = "Rechnungen"
        )
    }
}
