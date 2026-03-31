package com.example.photodrop.ui.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.AppHintergrund
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell
import kotlinx.coroutines.delay

// Zeitkonstanten fuer die Einblend-Animation des Gespeichert-Screens.
private const val EINBLEND_MS = 350
private const val WARTE_MS = 1800L
private const val AUSBLEND_MS = 450

// Zeigt kurz eine Bestaetigung nach erfolgreichem Foto-Schnellspeichern.
// Blendet sich automatisch aus und ruft danach onFertig auf.
@Composable
fun SchnellAktionsFotoGespeichertInhalt(
    dateiname: String,
    ordnerName: String,
    onFertig: () -> Unit = {}
) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.85f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(EINBLEND_MS))
        scale.animateTo(1f, tween(EINBLEND_MS))
        delay(WARTE_MS)
        alpha.animateTo(0f, tween(AUSBLEND_MS))
        onFertig()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppHintergrund)
            .alpha(alpha.value)
            .scale(scale.value),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = AkzentFarbe,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text("Gespeichert!", style = MaterialTheme.typography.headlineMedium, color = TextHell)
        Spacer(modifier = Modifier.height(10.dp))
        Text(dateiname, color = TextHell, style = MaterialTheme.typography.bodyLarge)
        Text("in $ordnerName", color = TextGedaempft)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Gespeichert")
@Composable
private fun SchnellAktionsFotoGespeichertVorschau() {
    PhotoDropTheme {
        SchnellAktionsFotoGespeichertInhalt(
            dateiname = "foto_20260401_142301.jpg",
            ordnerName = "Fotos/2026"
        )
    }
}
