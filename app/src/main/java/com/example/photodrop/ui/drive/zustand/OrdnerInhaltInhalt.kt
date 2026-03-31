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
import com.example.photodrop.ui.drive.api.DriveOrdner
import com.example.photodrop.ui.drive.api.DriveOrdnerDatei
import com.example.photodrop.ui.theme.PhotoDropTheme

// Zeigt den Ordnerinhalt mit Breadcrumb, Navigation und Datei-Karten.
@Composable
fun OrdnerInhaltInhalt(
    zustand: DriveZustand.InhaltGeladen,
    navigationsStack: List<DriveOrdner> = emptyList(),
    aktiverOrdner: DriveOrdner? = null,
    onOrdnerOeffnen: (DriveOrdner) -> Unit = {},
    onOrdnerAlsZielSetzen: (DriveOrdner) -> Unit = {},
    onZurueckNavigieren: () -> Unit = {},
    onOrdnerWechseln: () -> Unit = {}
) {
    var suchtext by remember { mutableStateOf("") }
    var ausgewaehlteDatei by remember { mutableStateOf<DriveOrdnerDatei?>(null) }
    var ordnerFuerSheet by remember { mutableStateOf<DriveOrdner?>(null) }

    val gefilterteDateien = zustand.dateien.filter { datei ->
        suchtext.isBlank() || datei.name.contains(suchtext, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BreadcrumbLeiste(
            navigationsStack = navigationsStack,
            onZurueck = onZurueckNavigieren,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
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
            DateiListe(
                dateien = gefilterteDateien,
                aktiverOrdner = aktiverOrdner,
                onDateiKlick = { ausgewaehlteDatei = it },
                onOrdnerOeffnen = onOrdnerOeffnen,
                onOrdnerLangDruck = { ordnerFuerSheet = it }
            )
        }
    }

    ausgewaehlteDatei?.let { datei ->
        DateiAktionsDialog(datei = datei, onSchliessen = { ausgewaehlteDatei = null })
    }

    ordnerFuerSheet?.let { ordner ->
        OrdnerZielSetzenSheet(
            ordner = ordner,
            onAlsZielSetzen = onOrdnerAlsZielSetzen,
            onOeffnen = onOrdnerOeffnen,
            onSchliessen = { ordnerFuerSheet = null }
        )
    }
}

// Liste aller Dateien und Ordner mit passender Darstellung.
@Composable
private fun DateiListe(
    dateien: List<DriveOrdnerDatei>,
    aktiverOrdner: DriveOrdner?,
    onDateiKlick: (DriveOrdnerDatei) -> Unit,
    onOrdnerOeffnen: (DriveOrdner) -> Unit,
    onOrdnerLangDruck: (DriveOrdner) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(dateien, key = { it.id }) { datei ->
            val pad = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            if (datei.istOrdner) {
                val ordner = DriveOrdner(id = datei.id, name = datei.name)
                OrdnerNavigationsKarte(
                    ordner = ordner,
                    istAktivesUploadZiel = ordner.id == aktiverOrdner?.id,
                    onOeffnen = onOrdnerOeffnen,
                    onAlsZielSetzen = onOrdnerLangDruck,
                    modifier = pad
                )
            } else {
                DateiKarte(
                    datei = datei,
                    onKlick = { onDateiKlick(datei) },
                    modifier = pad
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Mit Inhalt")
@Composable
private fun OrdnerInhaltInhaltVorschau() {
    PhotoDropTheme {
        OrdnerInhaltInhalt(
            zustand = DriveZustand.InhaltGeladen(
                kontoName = "max@gmail.com",
                ordnerId = "root",
                dateien = listOf(
                    DriveOrdnerDatei("1", "Rechnungen", "application/vnd.google-apps.folder", null, null),
                    DriveOrdnerDatei("2", "Fotos 2026", "application/vnd.google-apps.folder", null, null),
                    DriveOrdnerDatei("3", "foto_001.jpg", "image/jpeg", 2_345_678, "2026-03-10T14:00:00Z")
                )
            ),
            aktiverOrdner = DriveOrdner("1", "Rechnungen"),
            navigationsStack = emptyList()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "In Unterordner")
@Composable
private fun OrdnerInhaltInhaltUnterordnerVorschau() {
    PhotoDropTheme {
        OrdnerInhaltInhalt(
            zustand = DriveZustand.InhaltGeladen(
                kontoName = "max@gmail.com",
                ordnerId = "abc",
                dateien = listOf(
                    DriveOrdnerDatei("4", "Rechnung Amazon.pdf", "application/pdf", 1_234_567, "2026-03-21T10:00:00Z")
                ),
                ordnerName = "Rechnungen"
            ),
            navigationsStack = listOf(DriveOrdner("1", "Rechnungen"))
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Leer")
@Composable
private fun OrdnerInhaltLeerVorschau() {
    PhotoDropTheme {
        OrdnerInhaltInhalt(
            zustand = DriveZustand.InhaltGeladen("max@gmail.com", "abc", emptyList())
        )
    }
}
