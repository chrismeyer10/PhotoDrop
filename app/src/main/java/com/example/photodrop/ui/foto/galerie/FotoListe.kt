package com.example.photodrop.ui.foto.galerie

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.PhotoDropTheme

// Zeigt alle Fotos in einem 2-spaltigen Raster.
// Ist die Liste leer, erscheint stattdessen der Leerzustand.
@Composable
fun FotoListe(
    fotos: List<Uri>,
    onFotoKlick: (Uri) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (fotos.isEmpty()) {
        FotoListeLeerzustand(modifier)
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        items(fotos) { uri -> FotoKarte(uri = uri, onKlick = onFotoKlick) }
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
