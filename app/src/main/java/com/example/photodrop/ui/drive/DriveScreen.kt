package com.example.photodrop.ui.drive

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photodrop.ui.drive.api.DriveOrdner
import com.example.photodrop.ui.drive.zustand.DriveZustand
import com.example.photodrop.ui.drive.zustand.ZustandInhaltAuswaehlen
import com.example.photodrop.ui.theme.AppHintergrund
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Stateful: Verbindet den ViewModel mit dem Drive-UI.
@Composable
fun DriveScreen(
    viewModel: DriveViewModel = viewModel(),
    onMenuOeffnen: () -> Unit = {}
) {
    val zustand by viewModel.zustand.collectAsState()
    val anmeldeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.anmeldeErgebnisVerarbeiten(it.data) }

    DriveInhalt(
        zustand = zustand,
        onVerbinden = { anmeldeLauncher.launch(viewModel.anmeldeIntentErstellen()) },
        onOrdnerAuswaehlen = viewModel::ordnerAuswaehlen,
        onNeuenOrdnerErstellen = viewModel::neuenOrdnerErstellen,
        onOrdnerBestaetigen = viewModel::ordnerBestaetigen,
        onOrdnerBenennenAbbrechen = viewModel::ordnerBenennenAbbrechen,
        onZuruecksetzen = viewModel::fehlerBeheben,
        onAbmelden = viewModel::abmelden,
        onMenuOeffnen = onMenuOeffnen,
        onLadeAbbrechen = viewModel::ladeAbbrechen,
        onOrdnerWechseln = viewModel::ordnerWechseln
    )
}

// Stateless: Zeigt den aktuellen Drive-Verbindungsstatus mit Animationen.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriveInhalt(
    zustand: DriveZustand,
    onVerbinden: () -> Unit,
    onOrdnerAuswaehlen: (DriveOrdner) -> Unit = {},
    onNeuenOrdnerErstellen: () -> Unit = {},
    onOrdnerBestaetigen: (String) -> Unit = {},
    onOrdnerBenennenAbbrechen: () -> Unit = {},
    onZuruecksetzen: () -> Unit,
    onAbmelden: () -> Unit = {},
    onMenuOeffnen: () -> Unit = {},
    onLadeAbbrechen: () -> Unit = {},
    onOrdnerWechseln: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Google Drive", color = TextHell) },
                navigationIcon = {
                    IconButton(onClick = onMenuOeffnen) {
                        Icon(Icons.Filled.Menu, "Menue oeffnen", tint = TextHell)
                    }
                },
                actions = {
                    // Abmelden-Button fuer alle angemeldeten Zustaende anzeigen.
                    val istAngemeldet = zustand is DriveZustand.Verbunden
                            || zustand is DriveZustand.InhaltGeladen
                            || zustand is DriveZustand.OrdnerLaden
                            || zustand is DriveZustand.OrdnerAuswaehlen
                            || zustand is DriveZustand.OrdnerBenennen
                    if (istAngemeldet) {
                        IconButton(onClick = onAbmelden) {
                            Icon(Icons.AutoMirrored.Filled.Logout, "Abmelden", tint = TextGedaempft)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = OberflächenFarbe
                )
            )
        },
        containerColor = AppHintergrund
    ) { innenAbstand ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innenAbstand),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = zustand,
                transitionSpec = {
                    (fadeIn() + slideInVertically { it / 4 }) togetherWith
                            (fadeOut() + slideOutVertically { -it / 4 })
                },
                label = "DriveZustandAnimation"
            ) { aktuellerZustand ->
                ZustandInhaltAuswaehlen(
                    zustand = aktuellerZustand,
                    onVerbinden = onVerbinden,
                    onOrdnerAuswaehlen = onOrdnerAuswaehlen,
                    onNeuenOrdnerErstellen = onNeuenOrdnerErstellen,
                    onOrdnerBestaetigen = onOrdnerBestaetigen,
                    onOrdnerBenennenAbbrechen = onOrdnerBenennenAbbrechen,
                    onZuruecksetzen = onZuruecksetzen,
                    onLadeAbbrechen = onLadeAbbrechen,
                    onOrdnerWechseln = onOrdnerWechseln
                )
            }
        }
    }
}
