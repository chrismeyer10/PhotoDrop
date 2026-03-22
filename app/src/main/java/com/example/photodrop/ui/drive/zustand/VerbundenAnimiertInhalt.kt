package com.example.photodrop.ui.drive.zustand

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Zeigt die "Ordner bereit"-Bestaetigung mit pulsierender Animation.
@Composable
fun VerbundenAnimiertInhalt(zustand: DriveZustand.Verbunden) {
    val puls = rememberInfiniteTransition(label = "PulsAnimation")
    val skalierung by puls.animateFloat(
        initialValue = 1f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IconPuls"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.CheckCircle, null,
            tint = AkzentFarbe,
            modifier = Modifier.size(72.dp).scale(skalierung)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Verbunden", color = TextHell)
        Spacer(modifier = Modifier.height(8.dp))
        Text(zustand.kontoName, color = TextGedaempft)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Ordner bereit", color = AkzentFarbe)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Verbunden animiert")
@Composable
private fun VerbundenAnimiertInhaltVorschau() {
    PhotoDropTheme {
        VerbundenAnimiertInhalt(DriveZustand.Verbunden("max@gmail.com", "id123"))
    }
}
