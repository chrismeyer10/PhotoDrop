package com.example.photodrop.ui.einstellungen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.AppHintergrund
import com.example.photodrop.ui.theme.KartenFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Stateless: Zeigt die Einstellungen fuer den API-Schluessel.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EinstellungenInhalt(
    schluessel: String,
    istGespeichert: Boolean,
    onSchluesselAendern: (String) -> Unit = {},
    onSpeichern: () -> Unit = {},
    onLoeschen: () -> Unit = {},
    onMenuOeffnen: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Einstellungen", color = TextHell) },
                navigationIcon = {
                    IconButton(onClick = onMenuOeffnen) {
                        Icon(Icons.Filled.Menu, "Menue oeffnen", tint = TextHell)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = OberflächenFarbe
                )
            )
        },
        containerColor = AppHintergrund
    ) { innenAbstand ->
        EinstellungenFormular(
            schluessel = schluessel,
            istGespeichert = istGespeichert,
            onSchluesselAendern = onSchluesselAendern,
            onSpeichern = onSpeichern,
            onLoeschen = onLoeschen,
            modifier = Modifier.padding(innenAbstand)
        )
    }
}

// Formular fuer API-Schluessel-Eingabe mit Speichern/Loeschen-Buttons.
@Composable
private fun EinstellungenFormular(
    schluessel: String,
    istGespeichert: Boolean,
    onSchluesselAendern: (String) -> Unit,
    onSpeichern: () -> Unit,
    onLoeschen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp)
    ) {
        Text("Anthropic API-Schluessel", style = MaterialTheme.typography.titleMedium, color = TextHell)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Eigenen Schluessel eingeben fuer KI-Dokumentanalyse. " +
                "Ohne Schluessel wird kostenlose OCR-Texterkennung verwendet.",
            color = TextGedaempft,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = schluessel,
            onValueChange = onSchluesselAendern,
            label = { Text("API-Schluessel", color = TextGedaempft) },
            placeholder = { Text("sk-ant-...", color = TextGedaempft) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextHell,
                unfocusedTextColor = TextHell,
                focusedBorderColor = AkzentFarbe,
                unfocusedBorderColor = KartenFarbe,
                cursorColor = AkzentFarbe
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        SchluesselButtons(schluessel, istGespeichert, onSpeichern, onLoeschen)
    }
}

// Speichern- und Loeschen-Buttons fuer den API-Schluessel.
@Composable
private fun SchluesselButtons(
    schluessel: String,
    istGespeichert: Boolean,
    onSpeichern: () -> Unit,
    onLoeschen: () -> Unit
) {
    Button(
        onClick = onSpeichern,
        enabled = schluessel.isNotBlank(),
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
    ) { Text("Speichern") }

    if (istGespeichert) {
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onLoeschen,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OberflächenFarbe)
        ) { Text("Schluessel loeschen", color = TextHell) }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Einstellungen leer")
@Composable
private fun EinstellungenInhaltLeerVorschau() {
    PhotoDropTheme { EinstellungenInhalt(schluessel = "", istGespeichert = false) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Einstellungen gespeichert")
@Composable
private fun EinstellungenInhaltGespeichertVorschau() {
    PhotoDropTheme { EinstellungenInhalt(schluessel = "sk-ant-xxx", istGespeichert = true) }
}
