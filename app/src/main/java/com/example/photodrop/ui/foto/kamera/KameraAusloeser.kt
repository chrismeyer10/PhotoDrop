package com.example.photodrop.ui.foto.kamera

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme

// Runder Kamera-Button unten in der Mitte des Foto-Screens.
@Composable
fun KameraAusloeser(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(72.dp),
        shape = CircleShape,
        containerColor = AkzentFarbe,
        contentColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = "Foto aufnehmen",
            modifier = Modifier.size(34.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Kamera-Ausloeser")
@Composable
private fun KameraAusloeserVorschau() {
    PhotoDropTheme {
        KameraAusloeser(onClick = {})
    }
}
