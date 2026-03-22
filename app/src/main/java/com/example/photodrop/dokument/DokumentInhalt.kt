package com.example.photodrop.dokument

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.photodrop.dokument.vorschlag.VorschlagInhalt
import com.example.photodrop.dokument.vorschlag.VorschlagLadeInhalt
import com.example.photodrop.ui.theme.AppHintergrund
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextHell

// Stateless: Zeigt den Dokument-Archiv-Screen je nach aktuellem Zustand.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DokumentInhalt(
    zustand: DokumentZustand,
    onFotografieren: () -> Unit = {},
    onDateiAuswaehlen: () -> Unit = {},
    onAnalysieren: () -> Unit = {},
    onHochladen: (String, String) -> Unit = { _, _ -> },
    onZuruecksetzen: () -> Unit = {},
    onMenuOeffnen: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dokument", color = TextHell) },
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
        containerColor = AppHintergrund
    ) { innenAbstand ->
        Column(modifier = Modifier.padding(innenAbstand).fillMaxSize()) {
            ZustandsWeiche(zustand, onFotografieren, onDateiAuswaehlen, onAnalysieren, onHochladen, onZuruecksetzen)
        }
    }
}

// Delegiert an die passende Inhalt-Komponente je nach Zustand.
@Composable
private fun ZustandsWeiche(
    zustand: DokumentZustand,
    onFotografieren: () -> Unit,
    onDateiAuswaehlen: () -> Unit,
    onAnalysieren: () -> Unit,
    onHochladen: (String, String) -> Unit,
    onZuruecksetzen: () -> Unit
) {
    when (zustand) {
        is DokumentZustand.Bereit -> DokumentBereitInhalt(onFotografieren, onDateiAuswaehlen)
        is DokumentZustand.Geladen -> DokumentGeladenInhalt(onAnalysieren, onZuruecksetzen)
        is DokumentZustand.Analysiert -> VorschlagLadeInhalt()
        is DokumentZustand.VorschlagBereit -> VorschlagInhalt(zustand, onHochladen, onZuruecksetzen)
        is DokumentZustand.LaeadtHoch -> VorschlagLadeInhalt(text = "Wird hochgeladen...")
        is DokumentZustand.Fertig -> DokumentFertigInhalt(zustand.dateiname, zustand.unterordner, onZuruecksetzen)
        is DokumentZustand.Fehler -> DokumentFehlerInhalt(zustand.meldung, onZuruecksetzen)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Bereit")
@Composable
private fun DokumentInhaltBereitVorschau() {
    PhotoDropTheme { DokumentInhalt(zustand = DokumentZustand.Bereit) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Fehler")
@Composable
private fun DokumentInhaltFehlerVorschau() {
    PhotoDropTheme { DokumentInhalt(zustand = DokumentZustand.Fehler("Verbindung fehlgeschlagen")) }
}
