package com.example.photodrop.ui.drive

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photodrop.ui.theme.AppHintergrund
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Stateful: Verbindet den ViewModel mit dem UI.
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
        onOrdnerBestaetigen = viewModel::ordnerBestaetigen,
        onZuruecksetzen = viewModel::zuruecksetzen,
        onAbmelden = viewModel::abmelden,
        onMenuOeffnen = onMenuOeffnen
    )
}

// Stateless: Zeigt den aktuellen Drive-Verbindungsstatus.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriveInhalt(
    zustand: DriveZustand,
    onVerbinden: () -> Unit,
    onOrdnerBestaetigen: (String) -> Unit = {},
    onZuruecksetzen: () -> Unit,
    onAbmelden: () -> Unit = {},
    onMenuOeffnen: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Google Drive", color = TextHell) },
                navigationIcon = {
                    IconButton(onClick = onMenuOeffnen) {
                        Icon(Icons.Filled.Menu, "Menü öffnen", tint = TextHell)
                    }
                },
                actions = {
                    if (zustand is DriveZustand.Verbunden) {
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
            when (zustand) {
                is DriveZustand.NichtVerbunden -> NichtVerbundenInhalt(onVerbinden)
                is DriveZustand.Verbindet -> LadeInhalt()
                is DriveZustand.OrdnerBenennen -> OrdnerBenennenInhalt(
                    kontoName = zustand.kontoName,
                    onBestaetigen = onOrdnerBestaetigen
                )
                is DriveZustand.Verbunden -> VerbundenInhalt(zustand)
                is DriveZustand.Fehler -> FehlerInhalt(zustand.meldung, onZuruecksetzen)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Nicht verbunden")
@Composable
private fun DriveInhaltNichtVerbundenVorschau() {
    PhotoDropTheme { DriveInhalt(DriveZustand.NichtVerbunden, {}, {}, {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Verbindet")
@Composable
private fun DriveInhaltVerbindetVorschau() {
    PhotoDropTheme { DriveInhalt(DriveZustand.Verbindet, {}, {}, {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Ordner benennen")
@Composable
private fun DriveInhaltOrdnerBenennenVorschau() {
    PhotoDropTheme {
        DriveInhalt(DriveZustand.OrdnerBenennen("max@gmail.com", "token"), {}, {}, {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Verbunden")
@Composable
private fun DriveInhaltVerbundenVorschau() {
    PhotoDropTheme {
        DriveInhalt(DriveZustand.Verbunden("max@gmail.com", "abc123"), {}, {}, {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Fehler")
@Composable
private fun DriveInhaltFehlerVorschau() {
    PhotoDropTheme {
        DriveInhalt(DriveZustand.Fehler("Anmeldung fehlgeschlagen: 12500"), {}, {}, {})
    }
}
