package com.example.photodrop.ui.drive

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.photodrop.ui.drive.api.DriveOrdnerDatei
import com.example.photodrop.ui.drive.zustand.DriveZustand
import com.example.photodrop.ui.theme.PhotoDropTheme

// Preview-Funktionen fuer den DriveScreen — ausgelagert wegen Dateilaenge.

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Nicht verbunden")
@Composable
private fun DriveInhaltNichtVerbundenVorschau() {
    PhotoDropTheme {
        DriveInhalt(DriveZustand.NichtVerbunden, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Verbindet")
@Composable
private fun DriveInhaltVerbindetVorschau() {
    PhotoDropTheme {
        DriveInhalt(DriveZustand.Verbindet, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Verbunden")
@Composable
private fun DriveInhaltVerbundenVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            DriveZustand.Verbunden("max@gmail.com", "abc123"),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Inhalt geladen")
@Composable
private fun DriveInhaltGeladenVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            DriveZustand.InhaltGeladen(
                "max@gmail.com", "abc123",
                listOf(DriveOrdnerDatei("1", "foto.jpg", "image/jpeg", 1_200_000, "2026-03-21"))
            ), {}, {}, {}, {}, {}, {}, {}, {}, {}, {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Fehler")
@Composable
private fun DriveInhaltFehlerVorschau() {
    PhotoDropTheme {
        DriveInhalt(
            DriveZustand.Fehler("Anmeldung fehlgeschlagen: 12500"),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}
        )
    }
}
