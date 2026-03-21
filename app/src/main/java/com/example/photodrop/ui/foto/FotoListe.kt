package com.example.photodrop.ui.foto

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft

@Composable
fun FotoListe(fotos: List<Uri>, modifier: Modifier = Modifier) {
    if (fotos.isEmpty()) {
        Leerzustand(modifier)
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        items(fotos) { uri -> FotoKarte(uri = uri) }
    }
}

@Composable
private fun Leerzustand(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.CameraAlt,
            contentDescription = null,
            tint = TextGedaempft,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text("Noch keine Fotos", color = TextGedaempft, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tippe unten um loszulegen", color = TextGedaempft.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Leerzustand")
@Composable
private fun FotoListeLeerVorschau() {
    PhotoDropTheme {
        FotoListe(fotos = emptyList(), modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Mit Fotos")
@Composable
private fun FotoListeMitFotosVorschau() {
    PhotoDropTheme {
        FotoListe(
            fotos = List(4) { Uri.EMPTY },
            modifier = Modifier.fillMaxSize()
        )
    }
}
