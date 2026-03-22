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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme

// Zeigt zwei grosse Buttons: Fotografieren und Datei auswaehlen.
@Composable
fun DokumentBereitInhalt(onFotografieren: () -> Unit = {}, onDateiAuswaehlen: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onFotografieren,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
        ) {
            Icon(Icons.Filled.CameraAlt, null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.size(12.dp))
            Text("Dokument fotografieren")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onDateiAuswaehlen,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
        ) {
            Icon(Icons.Filled.UploadFile, null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.size(12.dp))
            Text("Datei auswaehlen")
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Bereit")
@Composable
private fun DokumentBereitInhaltVorschau() {
    PhotoDropTheme { DokumentBereitInhalt() }
}
