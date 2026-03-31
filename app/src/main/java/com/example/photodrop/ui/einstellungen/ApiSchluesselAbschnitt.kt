package com.example.photodrop.ui.einstellungen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.KartenFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Zeigt ein API-Key-Eingabefeld mit Speichern- und Loeschen-Button.
@Composable
fun ApiSchluesselAbschnitt(
    titel: String,
    hinweis: String,
    platzhalter: String,
    schluessel: String,
    istGespeichert: Boolean,
    testLaeuft: Boolean,
    onSchluesselAendern: (String) -> Unit,
    onSpeichern: () -> Unit,
    onLoeschen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(titel, style = MaterialTheme.typography.titleMedium, color = TextHell)
        Spacer(modifier = Modifier.height(4.dp))
        Text(hinweis, style = MaterialTheme.typography.bodySmall, color = TextGedaempft)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = schluessel,
            onValueChange = onSchluesselAendern,
            label = { Text("API-Schluessel", color = TextGedaempft) },
            placeholder = { Text(platzhalter, color = TextGedaempft) },
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
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onSpeichern,
            enabled = schluessel.isNotBlank() && !testLaeuft,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
        ) {
            Text(if (testLaeuft) "Verbindung wird geprueft..." else "Speichern & testen")
        }
        if (istGespeichert) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onLoeschen,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = OberflächenFarbe)
            ) { Text("Schluessel loeschen", color = TextHell) }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "API-Abschnitt leer")
@Composable
private fun ApiSchluesselAbschnittLeerVorschau() {
    PhotoDropTheme {
        ApiSchluesselAbschnitt(
            titel = "Anthropic API-Schluessel",
            hinweis = "Fuer Claude-Analyse benoetigt.",
            platzhalter = "sk-ant-...",
            schluessel = "",
            istGespeichert = false,
            testLaeuft = false,
            onSchluesselAendern = {},
            onSpeichern = {},
            onLoeschen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "API-Abschnitt gespeichert")
@Composable
private fun ApiSchluesselAbschnittGespeichertVorschau() {
    PhotoDropTheme {
        ApiSchluesselAbschnitt(
            titel = "OpenAI API-Schluessel",
            hinweis = "Fuer GPT-4o-mini-Analyse benoetigt.",
            platzhalter = "sk-...",
            schluessel = "sk-gespeichert",
            istGespeichert = true,
            testLaeuft = false,
            onSchluesselAendern = {},
            onSpeichern = {},
            onLoeschen = {}
        )
    }
}
