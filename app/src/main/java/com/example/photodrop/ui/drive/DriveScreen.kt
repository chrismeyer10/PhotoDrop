package com.example.photodrop.ui.drive

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.AppHintergrund
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextHell

// Stateful: Verbindet den ViewModel mit dem UI.
@Composable
fun DriveScreen(
    viewModel: DriveViewModel = viewModel(),
    onMenuOeffnen: () -> Unit = {}
) {
    val zustand by viewModel.zustand.collectAsState()

    val anmeldeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { ergebnis ->
        viewModel.anmeldeErgebnisVerarbeiten(ergebnis.data)
    }

    DriveInhalt(
        zustand = zustand,
        onVerbinden = { anmeldeLauncher.launch(viewModel.anmeldeIntentErstellen()) },
        onZuruecksetzen = viewModel::zuruecksetzen,
        onMenuOeffnen = onMenuOeffnen
    )
}

// Stateless: Zeigt den aktuellen Drive-Verbindungsstatus.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriveInhalt(
    zustand: DriveZustand,
    onVerbinden: () -> Unit,
    onZuruecksetzen: () -> Unit,
    onMenuOeffnen: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Google Drive", color = TextHell) },
                navigationIcon = {
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
        containerColor = AppHintergrund
    ) { innenAbstand ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innenAbstand),
            contentAlignment = Alignment.Center
        ) {
            when (zustand) {
                is DriveZustand.NichtVerbunden -> NichtVerbundenInhalt(onVerbinden)
                is DriveZustand.Verbindet -> LadeInhalt()
                is DriveZustand.Verbunden -> VerbundenInhalt(zustand)
                is DriveZustand.Fehler -> FehlerInhalt(zustand.meldung, onZuruecksetzen)
            }
        }
    }
}

// Zeigt den Verbinden-Button wenn noch keine Verbindung besteht.
@Composable
private fun NichtVerbundenInhalt(onVerbinden: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CloudSync,
            contentDescription = null,
            tint = TextGedaempft,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Mit Google Drive verbinden",
            color = TextHell,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Fotos werden automatisch im\nOrdner \"PhotoDrop\" gespeichert.",
            color = TextGedaempft,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onVerbinden,
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
        ) {
            Text("Verbinden")
        }
    }
}

// Zeigt einen Ladeindikator während der Anmeldung.
@Composable
private fun LadeInhalt() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = AkzentFarbe)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Verbinde...", color = TextGedaempft)
    }
}

// Zeigt Konto und Ordnerbestätigung nach erfolgreicher Verbindung.
@Composable
private fun VerbundenInhalt(zustand: DriveZustand.Verbunden) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = AkzentFarbe,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Verbunden", color = TextHell)
        Spacer(modifier = Modifier.height(8.dp))
        Text(zustand.kontoName, color = TextGedaempft)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ordner \"PhotoDrop\" bereit",
            color = AkzentFarbe
        )
    }
}

// Zeigt eine Fehlermeldung mit Retry-Button.
@Composable
private fun FehlerInhalt(meldung: String, onZuruecksetzen: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Fehler",
            color = TextHell
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = meldung,
            color = TextGedaempft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = onZuruecksetzen) {
            Text("Erneut versuchen", color = AkzentFarbe)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Nicht verbunden")
@Composable
private fun DriveInhaltNichtVerbundenVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            zustand = DriveZustand.NichtVerbunden,
            onVerbinden = {},
            onZuruecksetzen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Verbindet")
@Composable
private fun DriveInhaltVerbindetVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            zustand = DriveZustand.Verbindet,
            onVerbinden = {},
            onZuruecksetzen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Verbunden")
@Composable
private fun DriveInhaltVerbundenVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            zustand = DriveZustand.Verbunden(
                kontoName = "max@gmail.com",
                ordnerId = "abc123"
            ),
            onVerbinden = {},
            onZuruecksetzen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Fehler")
@Composable
private fun DriveInhaltFehlerVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            zustand = DriveZustand.Fehler("Anmeldung fehlgeschlagen: 12500"),
            onVerbinden = {},
            onZuruecksetzen = {}
        )
    }
}
