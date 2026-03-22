package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Zeigt den Verbinden-Button wenn noch keine Verbindung besteht.
@Composable
fun NichtVerbundenInhalt(onVerbinden: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.CloudSync, null, tint = TextGedaempft, modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Mit Google Drive verbinden", color = TextHell, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Fotos werden automatisch in deinem\ngewaehlten Ordner gespeichert.",
            color = TextGedaempft, textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onVerbinden, colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)) {
            Text("Verbinden")
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Nicht verbunden")
@Composable
private fun NichtVerbundenInhaltVorschau() {
    PhotoDropTheme { NichtVerbundenInhalt(onVerbinden = {}) }
}
