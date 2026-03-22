package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft

// Zeigt einen Ladeindikator waehrend der Anmeldung.
@Composable
fun LadeInhalt() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = AkzentFarbe)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Verbinde...", color = TextGedaempft)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Laedt")
@Composable
private fun LadeInhaltVorschau() {
    PhotoDropTheme { LadeInhalt() }
}
