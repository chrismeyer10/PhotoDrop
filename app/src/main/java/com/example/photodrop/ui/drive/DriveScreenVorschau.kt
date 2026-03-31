package com.example.photodrop.ui.drive

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.photodrop.ui.drive.api.DriveOrdner
import com.example.photodrop.ui.drive.api.DriveOrdnerDatei
import com.example.photodrop.ui.drive.zustand.DriveZustand
import com.example.photodrop.ui.theme.PhotoDropTheme

// Preview-Funktionen fuer den DriveScreen — ausgelagert wegen Dateilaenge.

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Nicht verbunden")
@Composable
private fun DriveInhaltNichtVerbundenVorschau() {
    PhotoDropTheme {
        DriveInhalt(zustand = DriveZustand.NichtVerbunden, onVerbinden = {}, onZuruecksetzen = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Verbindet")
@Composable
private fun DriveInhaltVerbindetVorschau() {
    PhotoDropTheme {
        DriveInhalt(zustand = DriveZustand.Verbindet, onVerbinden = {}, onZuruecksetzen = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Verbunden")
@Composable
private fun DriveInhaltVerbundenVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            zustand = DriveZustand.Verbunden("max@gmail.com", "abc123"),
            onVerbinden = {}, onZuruecksetzen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Inhalt geladen Root")
@Composable
private fun DriveInhaltGeladenRootVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            zustand = DriveZustand.InhaltGeladen(
                kontoName = "max@gmail.com",
                ordnerId = "root",
                dateien = listOf(
                    DriveOrdnerDatei("1", "Rechnungen", "application/vnd.google-apps.folder", null, null),
                    DriveOrdnerDatei("2", "Fotos 2026", "application/vnd.google-apps.folder", null, null)
                )
            ),
            navigationsStack = emptyList(),
            aktiverOrdner = DriveOrdner("1", "Rechnungen"),
            onVerbinden = {}, onZuruecksetzen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Inhalt geladen Unterordner")
@Composable
private fun DriveInhaltGeladenUnterordnerVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            zustand = DriveZustand.InhaltGeladen(
                kontoName = "max@gmail.com",
                ordnerId = "abc",
                dateien = listOf(
                    DriveOrdnerDatei("3", "foto.jpg", "image/jpeg", 1_200_000, "2026-03-21")
                ),
                ordnerName = "Rechnungen"
            ),
            navigationsStack = listOf(DriveOrdner("1", "Rechnungen")),
            onVerbinden = {}, onZuruecksetzen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Fehler")
@Composable
private fun DriveInhaltFehlerVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            zustand = DriveZustand.Fehler("Anmeldung fehlgeschlagen: 12500"),
            onVerbinden = {}, onZuruecksetzen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Ordner auswaehlen")
@Composable
private fun DriveInhaltOrdnerAuswaehlenVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            zustand = DriveZustand.OrdnerAuswaehlen(
                "max@gmail.com", "token",
                listOf(DriveOrdner("id1", "PhotoDrop")), null
            ),
            onVerbinden = {}, onZuruecksetzen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Ordner benennen")
@Composable
private fun DriveInhaltOrdnerBenennenVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            zustand = DriveZustand.OrdnerBenennen("max@gmail.com", "token"),
            onVerbinden = {}, onZuruecksetzen = {}
        )
    }
}
