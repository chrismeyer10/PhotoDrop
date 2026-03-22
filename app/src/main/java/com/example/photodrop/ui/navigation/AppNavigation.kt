package com.example.photodrop.ui.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.photodrop.dokument.DokumentScreen
import com.example.photodrop.ui.drive.DriveScreen
import com.example.photodrop.ui.drive.DriveViewModel
import com.example.photodrop.ui.foto.FotoAufnahmeScreen
import kotlinx.coroutines.launch

// Haupt-Navigation der App.
// Verwaltet die linke Seitenleiste und den sichtbaren Screen.
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val schubladenZustand = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ViewModel auf Activity-Ebene — ueberlebt Navigation zwischen Screens
    val driveViewModel: DriveViewModel = viewModel()

    val aktuellerEintrag by navController.currentBackStackEntryAsState()
    val aktuelleRoute = aktuellerEintrag?.destination?.route
        ?: NavigationsZiel.FotoAufnahme.route

    // Linke Seitenleiste umschließt den gesamten Inhalt der App
    ModalNavigationDrawer(
        drawerState = schubladenZustand,
        drawerContent = {
            NavigationsLeisteInhalt(
                aktuelleRoute = aktuelleRoute,
                onZielAusgewaehlt = { ziel ->
                    scope.launch { schubladenZustand.close() }
                    navController.navigate(ziel.route) {
                        launchSingleTop = true
                        popUpTo(NavigationsZiel.FotoAufnahme.route) { inclusive = false }
                    }
                }
            )
        }
    ) {
        // Alle Screens werden hier über Routen gesteuert
        NavHost(
            navController = navController,
            startDestination = NavigationsZiel.FotoAufnahme.route
        ) {
            composable(NavigationsZiel.FotoAufnahme.route) {
                FotoAufnahmeScreen(
                    driveViewModel = driveViewModel,
                    onMenuOeffnen = { scope.launch { schubladenZustand.open() } },
                    onZuDrive = {
                        navController.navigate(NavigationsZiel.GoogleDrive.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(NavigationsZiel.Dokument.route) {
                DokumentScreen(
                    driveViewModel = driveViewModel,
                    onMenuOeffnen = { scope.launch { schubladenZustand.open() } }
                )
            }
            composable(NavigationsZiel.GoogleDrive.route) {
                DriveScreen(
                    viewModel = driveViewModel,
                    onMenuOeffnen = { scope.launch { schubladenZustand.open() } }
                )
            }
        }
    }
}
