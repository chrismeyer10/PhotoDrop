package com.example.photodrop.ui.navigation

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.photodrop.dokument.DokumentScreen
import com.example.photodrop.ui.drive.DriveScreen
import com.example.photodrop.ui.drive.DriveViewModel
import com.example.photodrop.ui.drive.SchnellUploadZustand
import com.example.photodrop.ui.einstellungen.EinstellungenScreen
import com.example.photodrop.ui.foto.kamera.dateiUriErstellen
import com.example.photodrop.ui.foto.kamera.hatKameraErlaubnis
import kotlinx.coroutines.launch

// Haupt-Navigation der App.
// Verwaltet die linke Seitenleiste, den sichtbaren Screen und die Schnellaktionsleiste.
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val schubladenZustand = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // ViewModel auf Activity-Ebene — ueberlebt Navigation zwischen Screens
    val driveViewModel: DriveViewModel = viewModel()
    val schnellUploadZustand by driveViewModel.schnellUploadZustand.collectAsState()
    val zeigeOrdnerDialog by driveViewModel.zeigeOrdnerAuswahlDialog.collectAsState()
    val aktiverOrdner by driveViewModel.aktiverOrdner.collectAsState()

    val aktuellerEintrag by navController.currentBackStackEntryAsState()
    val aktuelleRoute = aktuellerEintrag?.destination?.route
        ?: NavigationsZiel.Archiv.route

    // Kamera URI fuer den Schnell-Upload-Flow
    var kameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher fuer Foto aufnehmen — zeigt danach Ordner-Auswahl-Dialog
    val schnellKameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { erfolg ->
        if (erfolg) kameraUri?.let { driveViewModel.fotoFuerUploadVormerken(it) }
    }

    // Launcher fuer Kamera-Erlaubnis
    val kameraErlaubnisLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { erlaubt ->
        if (erlaubt) {
            val uri = dateiUriErstellen(context)
            kameraUri = uri
            schnellKameraLauncher.launch(uri)
        }
    }

    // Fehler-Snackbar anzeigen wenn Schnell-Upload fehlschlaegt.
    LaunchedEffect(schnellUploadZustand) {
        val zustand = schnellUploadZustand
        if (zustand is SchnellUploadZustand.Fehler) {
            snackbarHostState.showSnackbar(zustand.meldung)
            driveViewModel.schnellUploadZuruecksetzen()
        }
    }

    // Linke Seitenleiste umschliesst den gesamten Inhalt der App
    ModalNavigationDrawer(
        drawerState = schubladenZustand,
        drawerContent = {
            NavigationsLeisteInhalt(
                aktuelleRoute = aktuelleRoute,
                onZielAusgewaehlt = { ziel ->
                    scope.launch { schubladenZustand.close() }
                    navController.navigate(ziel.route) {
                        launchSingleTop = true
                        popUpTo(NavigationsZiel.Archiv.route) { inclusive = false }
                    }
                }
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                SchnellAktionsLeiste(
                    aktuellerOrdnerName = aktiverOrdner?.name ?: driveViewModel.ordnerName,
                    onFotoMachen = {
                        if (hatKameraErlaubnis(context)) {
                            val uri = dateiUriErstellen(context)
                            kameraUri = uri
                            schnellKameraLauncher.launch(uri)
                        } else {
                            kameraErlaubnisLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    onOrdnerWaehlen = {
                        scope.launch { schubladenZustand.close() }
                        navController.navigate(NavigationsZiel.GoogleDrive.route) {
                            launchSingleTop = true
                            popUpTo(NavigationsZiel.Archiv.route) { inclusive = false }
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innenAbstand ->
            Box(modifier = Modifier.fillMaxSize().padding(innenAbstand)) {
                NavHost(
                    navController = navController,
                    startDestination = NavigationsZiel.Archiv.route
                ) {
                    composable(NavigationsZiel.Archiv.route) {
                        DokumentScreen(
                            driveViewModel = driveViewModel,
                            onMenuOeffnen = { scope.launch { schubladenZustand.open() } },
                            onEinstellungenOeffnen = {
                                navController.navigate(NavigationsZiel.Einstellungen.route) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(NavigationsZiel.GoogleDrive.route) {
                        DriveScreen(
                            viewModel = driveViewModel,
                            onMenuOeffnen = { scope.launch { schubladenZustand.open() } }
                        )
                    }
                    composable(NavigationsZiel.Einstellungen.route) {
                        EinstellungenScreen(
                            onMenuOeffnen = { scope.launch { schubladenZustand.open() } }
                        )
                    }
                }

                // Gespeichert-Overlay nach erfolgreichem Schnell-Upload
                val aktuellUpload = schnellUploadZustand
                if (aktuellUpload is SchnellUploadZustand.Fertig) {
                    SchnellAktionsFotoGespeichertInhalt(
                        dateiname = aktuellUpload.dateiname,
                        ordnerName = aktuellUpload.ordnerName,
                        onFertig = { driveViewModel.schnellUploadZuruecksetzen() }
                    )
                }
            }

            // Ordner-Auswahl-Dialog nach Schnellfoto
            if (zeigeOrdnerDialog) {
                val verfuegbareOrdner = driveViewModel.rootOrdnerAusZustand()
                OrdnerAuswahlDialog(
                    verfuegbareOrdner = verfuegbareOrdner,
                    aktiverOrdner = aktiverOrdner,
                    onOrdnerGewaehlt = { ordner ->
                        driveViewModel.ordnerFuerFotoAuswaehlenUndHochladen(ordner, context)
                    },
                    onAbbrechen = { driveViewModel.ordnerAuswahlAbbrechen() }
                )
            }
        }
    }
}
