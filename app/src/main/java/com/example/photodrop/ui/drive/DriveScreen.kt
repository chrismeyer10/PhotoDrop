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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photodrop.ui.theme.AppHintergrund
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
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
        onZuruecksetzen = viewModel::zuruecksetzen,
        onAbmelden = viewModel::abmelden,
        onMenuOeffnen = onMenuOeffnen
    )
}

// Stateless: Zeigt den aktuellen Drive-Verbindungsstatus mit AnimatedContent-Übergängen.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriveInhalt(
    zustand: DriveZustand,
    onVerbinden: () -> Unit,
    onOrdnerAuswaehlen: (DriveOrdner) -> Unit = {},
    onNeuenOrdnerErstellen: () -> Unit = {},
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
                    val istVerbunden = zustand is DriveZustand.Verbunden
                            || zustand is DriveZustand.InhaltGeladen
                    if (istVerbunden) {
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
                    onZuruecksetzen = onZuruecksetzen
                )
            }
        }
    }
}

// Wählt den passenden Inhalt für den aktuellen Zustand aus.
@Composable
private fun ZustandInhaltAuswaehlen(
    zustand: DriveZustand,
    onVerbinden: () -> Unit,
    onOrdnerAuswaehlen: (DriveOrdner) -> Unit,
    onNeuenOrdnerErstellen: () -> Unit,
    onOrdnerBestaetigen: (String) -> Unit,
    onZuruecksetzen: () -> Unit
) {
    when (zustand) {
        is DriveZustand.NichtVerbunden -> NichtVerbundenInhalt(onVerbinden)
        is DriveZustand.Verbindet -> LadeInhalt()
        is DriveZustand.OrdnerLaden -> LadeInhalt()
        is DriveZustand.OrdnerAuswaehlen -> OrdnerAuswaehlenInhalt(
            zustand = zustand,
            onAuswaehlen = onOrdnerAuswaehlen,
            onNeuErstellen = onNeuenOrdnerErstellen
        )
        is DriveZustand.OrdnerBenennen -> OrdnerBenennenInhalt(
            kontoName = zustand.kontoName,
            onBestaetigen = onOrdnerBestaetigen
        )
        is DriveZustand.Verbunden -> VerbundenAnimiertInhalt(zustand)
        is DriveZustand.InhaltGeladen -> OrdnerInhaltInhalt(zustand)
        is DriveZustand.Fehler -> FehlerInhalt(zustand.meldung, onZuruecksetzen)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Nicht verbunden")
@Composable
private fun DriveInhaltNichtVerbundenVorschau() {
    PhotoDropTheme { DriveInhalt(DriveZustand.NichtVerbunden, {}, {}, {}, {}, {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Verbindet")
@Composable
private fun DriveInhaltVerbindetVorschau() {
    PhotoDropTheme { DriveInhalt(DriveZustand.Verbindet, {}, {}, {}, {}, {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Ordner laden")
@Composable
private fun DriveInhaltOrdnerLadenVorschau() {
    PhotoDropTheme {
        DriveInhalt(DriveZustand.OrdnerLaden("max@gmail.com", "token"), {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Ordner auswählen")
@Composable
private fun DriveInhaltOrdnerAuswaehlenVorschau() {
    val gespeichert = DriveOrdner("id1", "PhotoDrop")
    PhotoDropTheme {
        DriveInhalt(
            DriveZustand.OrdnerAuswaehlen(
                "max@gmail.com", "token",
                listOf(gespeichert, DriveOrdner("id2", "Dokumente")), gespeichert
            ), {}, {}, {}, {}, {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Ordner benennen")
@Composable
private fun DriveInhaltOrdnerBenennenVorschau() {
    PhotoDropTheme {
        DriveInhalt(DriveZustand.OrdnerBenennen("max@gmail.com", "token"), {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Verbunden")
@Composable
private fun DriveInhaltVerbundenVorschau() {
    PhotoDropTheme {
        DriveInhalt(DriveZustand.Verbunden("max@gmail.com", "abc123"), {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Inhalt geladen")
@Composable
private fun DriveInhaltGeladenVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            DriveZustand.InhaltGeladen(
                "max@gmail.com", "abc123",
                listOf(
                    DriveOrdnerDatei("1", "foto_001.jpg", "image/jpeg", 1_200_000, "2026-03-21"),
                    DriveOrdnerDatei("2", "Sicherung", "application/vnd.google-apps.folder", null, null)
                )
            ), {}, {}, {}, {}, {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Fehler")
@Composable
private fun DriveInhaltFehlerVorschau() {
    PhotoDropTheme {
        DriveInhalt(DriveZustand.Fehler("Anmeldung fehlgeschlagen: 12500"), {}, {}, {}, {}, {})
    }
}
