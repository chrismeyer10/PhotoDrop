package com.example.photodrop.ui.foto

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photodrop.ui.drive.DriveViewModel
import com.example.photodrop.ui.foto.analyse.FotoAnalyseDialog
import com.example.photodrop.ui.foto.analyse.FotoAnalyseViewModel
import com.example.photodrop.ui.foto.galerie.FotoListe
import com.example.photodrop.ui.foto.kamera.KameraAusloeser
import com.example.photodrop.ui.foto.kamera.kameraAktionErstellen
import com.example.photodrop.ui.theme.AppHintergrund
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.TextHell

// Stateful: Verbindet den ViewModel mit dem UI.
// Prueft ob ein Drive-Ordner gesetzt ist bevor ein Foto gemacht werden kann.
@Composable
fun FotoAufnahmeScreen(
    viewModel: FotoViewModel = viewModel(),
    driveViewModel: DriveViewModel = viewModel(),
    analyseViewModel: FotoAnalyseViewModel = viewModel(),
    onMenuOeffnen: () -> Unit = {},
    onZuDrive: () -> Unit = {}
) {
    val fotos by viewModel.fotos.collectAsState()
    val analyseZustand by analyseViewModel.zustand.collectAsState()
    var zeigeOrdnerDialog by remember { mutableStateOf(false) }
    var zeigeAnalyseDialog by remember { mutableStateOf(false) }

    val fotoAktion = kameraAktionErstellen { viewModel.fotoHinzufuegen(it) }

    FotoAufnahmeInhalt(
        fotos = fotos,
        onFotoAufnehmen = {
            if (driveViewModel.ordnerName != null) {
                fotoAktion()
            } else {
                zeigeOrdnerDialog = true
            }
        },
        onFotoKlick = { uri ->
            analyseViewModel.analysieren(uri)
            zeigeAnalyseDialog = true
        },
        onMenuOeffnen = onMenuOeffnen
    )

    if (zeigeOrdnerDialog) {
        OrdnerFehltDialog(
            onZuDrive = {
                zeigeOrdnerDialog = false
                onZuDrive()
            },
            onAbbrechen = { zeigeOrdnerDialog = false }
        )
    }

    if (zeigeAnalyseDialog) {
        FotoAnalyseDialog(
            zustand = analyseZustand,
            onSchliessen = {
                zeigeAnalyseDialog = false
                analyseViewModel.zuruecksetzen()
            }
        )
    }
}

// Stateless: Zeigt die Fotoliste mit TopAppBar und Kamera-Button.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoAufnahmeInhalt(
    fotos: List<Uri>,
    onFotoAufnehmen: () -> Unit,
    onFotoKlick: (Uri) -> Unit = {},
    onMenuOeffnen: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fotos", color = TextHell) },
                navigationIcon = {
                    IconButton(onClick = onMenuOeffnen) {
                        Icon(Icons.Filled.Menu, "Menue oeffnen", tint = TextHell)
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
            onFotoKlick = onFotoKlick,
            modifier = Modifier
                .fillMaxSize()
                .padding(innenAbstand)
        )
    }
}
