package com.example.photodrop.dokument

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft

// Zeigt die Vorschau eines Dokuments als grosses Bild oder einen Platzhalter.
@Composable
fun DokumentVorschauBild(vorschau: Bitmap?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(OberflächenFarbe),
        contentAlignment = Alignment.Center
    ) {
        if (vorschau != null) {
            Image(
                bitmap = vorschau.asImageBitmap(),
                contentDescription = "Dokumentvorschau",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                "Vorschau nicht verfuegbar",
                color = TextGedaempft,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Vorschau Platzhalter")
@Composable
private fun DokumentVorschauBildVorschau() {
    PhotoDropTheme { DokumentVorschauBild(vorschau = null) }
}
