package com.example.photodrop.ui.foto

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.photodrop.ui.theme.PhotoDropTheme

// Preview-Funktionen fuer den FotoAufnahmeScreen — ausgelagert wegen Dateilaenge.

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Leerzustand")
@Composable
private fun FotoAufnahmeInhaltLeerVorschau() {
    PhotoDropTheme {
        FotoAufnahmeInhalt(fotos = emptyList(), onFotoAufnehmen = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Mit Fotos")
@Composable
private fun FotoAufnahmeInhaltMitFotosVorschau() {
    PhotoDropTheme {
        FotoAufnahmeInhalt(fotos = List(4) { Uri.EMPTY }, onFotoAufnehmen = {})
    }
}
