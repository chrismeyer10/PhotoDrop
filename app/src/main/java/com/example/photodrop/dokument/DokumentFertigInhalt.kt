package com.example.photodrop.dokument

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell
import kotlinx.coroutines.delay

// Dauer der Einblend-Animation in Millisekunden.
private const val EINBLEND_DAUER_MS = 400

// Wartezeit nach der Einblend-Animation, bevor ausgeblendet wird.
private const val WARTE_DAUER_MS = 1600L

// Dauer der Ausblend-Animation in Millisekunden.
private const val AUSBLEND_DAUER_MS = 500

// Bestaetigung nach erfolgreichem Upload — blendet sich automatisch aus und ruft onWeiter.
@Composable
fun DokumentFertigInhalt(
    dateiname: String,
    unterordner: String,
    onWeiter: () -> Unit = {}
) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }

    // Einblenden, kurz anzeigen, dann ausblenden und weiterleiten.
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(EINBLEND_DAUER_MS))
        scale.animateTo(1f, tween(EINBLEND_DAUER_MS))
        delay(WARTE_DAUER_MS)
        alpha.animateTo(0f, tween(AUSBLEND_DAUER_MS))
        onWeiter()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
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
        Text("in $unterordner", color = TextGedaempft)
        Spacer(modifier = Modifier.height(40.dp))
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
            unterordner = "Rechnungen/2026"
        )
    }
}
