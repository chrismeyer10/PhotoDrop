package com.example.photodrop.dokument.vorschlag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextHell

// Zeigt einen Lade-Spinner mit optionalem Text.
@Composable
fun VorschlagLadeInhalt(text: String = "Dokument wird analysiert...") {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = AkzentFarbe)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = text, color = TextHell)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Laden")
@Composable
private fun VorschlagLadeInhaltVorschau() {
    PhotoDropTheme { VorschlagLadeInhalt() }
}
