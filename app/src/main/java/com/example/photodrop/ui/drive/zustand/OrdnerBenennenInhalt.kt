package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

// Formular zur Eingabe des Google Drive Ordnernamens.
@Composable
fun OrdnerBenennenInhalt(
    kontoName: String,
    onBestaetigen: (name: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var eingabe by rememberSaveable { mutableStateOf("PhotoDrop") }

    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.CreateNewFolder, null, tint = AkzentFarbe, modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Drive-Ordner waehlen", color = TextHell, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(4.dp))
        Text(kontoName, color = TextGedaempft, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = eingabe,
            onValueChange = { eingabe = it },
            label = { Text("Ordnername", color = TextGedaempft) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AkzentFarbe,
                unfocusedBorderColor = TextGedaempft,
                focusedTextColor = TextHell,
                unfocusedTextColor = TextHell,
                cursorColor = AkzentFarbe,
                focusedContainerColor = OberflächenFarbe,
                unfocusedContainerColor = OberflächenFarbe
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Alle Fotos werden automatisch in\ndiesem Ordner in Google Drive gespeichert.",
            color = TextGedaempft, textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onBestaetigen(eingabe.trim()) },
            enabled = eingabe.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
        ) { Text("Ordner erstellen") }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Ordner benennen")
@Composable
private fun OrdnerBenennenInhaltVorschau() {
    PhotoDropTheme { OrdnerBenennenInhalt(kontoName = "max@gmail.com", onBestaetigen = {}) }
}
