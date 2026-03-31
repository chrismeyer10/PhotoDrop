package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.drive.api.DriveOrdnerDatei
import com.example.photodrop.ui.theme.PhotoDropTheme

// Zeigt den Ordnerinhalt mit Suchleiste, Datei-Karten und Aktionen.
@Composable
fun OrdnerInhaltInhalt(
    zustand: DriveZustand.InhaltGeladen,
    onOrdnerWechseln: () -> Unit = {}
) {
    var suchtext by remember { mutableStateOf("") }
    var ausgewaehlteDatei by remember { mutableStateOf<DriveOrdnerDatei?>(null) }

    val gefilterteDateien = zustand.dateien.filter { datei ->
        suchtext.isBlank() || datei.name.contains(suchtext, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OrdnerKopfzeile(
            kontoName = zustand.kontoName,
            dateiAnzahl = zustand.dateien.size,
            onOrdnerWechseln = onOrdnerWechseln
        )
        OrdnerSuchleiste(
            suchtext = suchtext,
            onSuchtextAendern = { suchtext = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        )
        if (gefilterteDateien.isEmpty()) {
            OrdnerLeerzustand(hatSuchfilter = suchtext.isNotBlank())
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(gefilterteDateien, key = { it.id }) { datei ->
                    DateiKarte(
                        datei = datei,
                        onKlick = { ausgewaehlteDatei = datei },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    ausgewaehlteDatei?.let { datei ->
        DateiAktionsDialog(
            datei = datei,
            onSchliessen = { ausgewaehlteDatei = null }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Mit Dateien")
@Composable
private fun OrdnerInhaltInhaltVorschau() {
    PhotoDropTheme {
        OrdnerInhaltInhalt(
            DriveZustand.InhaltGeladen(
                "max@gmail.com", "abc",
                listOf(
                    DriveOrdnerDatei("1", "Rechnung Amazon 2026.pdf", "application/pdf", 1_234_567, "2026-03-21T10:00:00Z"),
                    DriveOrdnerDatei("2", "Kontoauszug Maerz.pdf", "application/pdf", 987_654, "2026-03-15T08:30:00Z"),
                    DriveOrdnerDatei("3", "foto_001.jpg", "image/jpeg", 2_345_678, "2026-03-10T14:00:00Z"),
                    DriveOrdnerDatei("4", "Sicherung", "application/vnd.google-apps.folder", null, null)
                )
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Leer")
@Composable
private fun OrdnerInhaltLeerVorschau() {
    PhotoDropTheme {
        OrdnerInhaltInhalt(DriveZustand.InhaltGeladen("max@gmail.com", "abc", emptyList()))
    }
}
