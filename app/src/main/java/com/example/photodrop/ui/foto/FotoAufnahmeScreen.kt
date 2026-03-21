package com.example.photodrop.ui.foto

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.AppHintergrund
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextHell

// Stateful: Verbindet den ViewModel mit dem UI.
// Holt die Fotoliste und die Kamera-Aktion und gibt sie nach unten weiter.
@Composable
fun FotoAufnahmeScreen(
    viewModel: FotoViewModel = viewModel(),
    onMenuOeffnen: () -> Unit = {}
) {
    val fotos by viewModel.fotos.collectAsState()
    val fotoAktion = kameraAktionErstellen { viewModel.fotoHinzufuegen(it) }
    FotoAufnahmeInhalt(
        fotos = fotos,
        onFotoAufnehmen = fotoAktion,
        onMenuOeffnen = onMenuOeffnen
    )
}

// Stateless: Zeigt die Fotoliste mit TopAppBar und Kamera-Button.
// Weiß nichts vom ViewModel — bekommt alles was es braucht von außen.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoAufnahmeInhalt(
    fotos: List<Uri>,
    onFotoAufnehmen: () -> Unit,
    onMenuOeffnen: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fotos", color = TextHell) },
                navigationIcon = {
                    // Hamburger-Button öffnet die linke Seitenleiste
                    IconButton(onClick = onMenuOeffnen) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menü öffnen",
                            tint = TextHell
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = OberflächenFarbe
                )
            )
        },
        containerColor = AppHintergrund,
        floatingActionButton = { KameraAusloeser(onClick = onFotoAufnehmen) },
        floatingActionButtonPosition = FabPosition.Center
    ) { innenAbstand ->
        FotoListe(
            fotos = fotos,
            modifier = Modifier
                .fillMaxSize()
                .padding(innenAbstand)
        )
    }
}

// Runder Kamera-Button unten in der Mitte.
// Wenn man drückt, wird onFotoAufnehmen ausgelöst.
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
