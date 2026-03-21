package com.example.photodrop.ui.foto

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.AppHintergrund
import com.example.photodrop.ui.theme.PhotoDropTheme

@Composable
fun FotoAufnahmeScreen(viewModel: FotoViewModel = viewModel()) {
    val fotos by viewModel.fotos.collectAsState()
    val fotoAktion = kameraAktionErstellen { viewModel.fotoHinzufuegen(it) }
    FotoAufnahmeInhalt(fotos = fotos, onFotoAufnehmen = fotoAktion)
}

@Composable
fun FotoAufnahmeInhalt(fotos: List<Uri>, onFotoAufnehmen: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppHintergrund)
    ) {
        FotoListe(
            fotos = fotos,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp)
        )
        KameraAusloeser(
            onClick = onFotoAufnehmen,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        )
    }
}

@Composable
private fun KameraAusloeser(onClick: () -> Unit, modifier: Modifier = Modifier) {
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
