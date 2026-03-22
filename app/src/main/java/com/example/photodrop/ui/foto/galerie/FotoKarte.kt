package com.example.photodrop.ui.foto.galerie

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.KartenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme

// Zeigt ein einzelnes Foto als quadratische Karte mit abgerundeten Ecken.
// Bei Klick wird onKlick mit der URI aufgerufen.
@Composable
fun FotoKarte(
    uri: Uri,
    onKlick: (Uri) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onKlick(uri) },
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = KartenFarbe)
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Aufgenommenes Foto",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun FotoKarteVorschau() {
    PhotoDropTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier.aspectRatio(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = KartenFarbe)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AkzentFarbe.copy(alpha = 0.3f))
                )
            }
        }
    }
}
