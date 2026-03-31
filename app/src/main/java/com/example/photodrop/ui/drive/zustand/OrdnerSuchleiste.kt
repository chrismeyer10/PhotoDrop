package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Suchleiste zum Filtern der Dateiliste.
@Composable
fun OrdnerSuchleiste(
    suchtext: String,
    onSuchtextAendern: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = suchtext,
        onValueChange = onSuchtextAendern,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Dateien suchen...", color = TextGedaempft) },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = null, tint = TextGedaempft)
        },
        trailingIcon = {
            if (suchtext.isNotBlank()) {
                IconButton(onClick = { onSuchtextAendern("") }) {
                    Icon(Icons.Filled.Clear, "Suche leeren", tint = TextGedaempft)
                }
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextHell,
            unfocusedTextColor = TextHell,
            focusedBorderColor = AkzentFarbe,
            unfocusedBorderColor = TextGedaempft,
            cursorColor = AkzentFarbe
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun OrdnerSuchleisteVorschau() {
    PhotoDropTheme { OrdnerSuchleiste(suchtext = "Rechnung", onSuchtextAendern = {}) }
}
