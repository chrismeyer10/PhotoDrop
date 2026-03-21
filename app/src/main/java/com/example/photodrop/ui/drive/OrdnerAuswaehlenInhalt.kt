package com.example.photodrop.ui.drive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.KartenFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Zeigt die Liste der Drive-Ordner zur Auswahl oder Erstellung.
@Composable
fun OrdnerAuswaehlenInhalt(
    zustand: DriveZustand.OrdnerAuswaehlen,
    onAuswaehlen: (DriveOrdner) -> Unit,
    onNeuErstellen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        OrdnerAuswaehlenKopf(zustand.kontoName)
        Spacer(Modifier.height(16.dp))
        zustand.gespeicherterOrdner?.let { gespeichert ->
            ZuletztVerwendetKarte(gespeichert, onAuswaehlen)
            Spacer(Modifier.height(8.dp))
        }
        OrdnerListeInhalt(zustand.ordner, zustand.gespeicherterOrdner, onAuswaehlen)
        Spacer(Modifier.height(16.dp))
        NeuenOrdnerButton(onNeuErstellen)
        Spacer(Modifier.height(24.dp))
    }
}

// Kopfzeile mit Titel und Kontoname.
@Composable
private fun OrdnerAuswaehlenKopf(kontoName: String) {
    Text("Drive-Ordner wählen", color = TextHell, fontSize = 18.sp, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(4.dp))
    Text(kontoName, color = TextGedaempft, fontSize = 13.sp)
}

// Hebt den zuletzt verwendeten Ordner oben hervor.
@Composable
private fun ZuletztVerwendetKarte(ordner: DriveOrdner, onAuswaehlen: (DriveOrdner) -> Unit) {
    OrdnerKarte(
        ordner = ordner,
        hervorgehoben = true,
        onAuswaehlen = onAuswaehlen
    )
}

// Liste aller Ordner — bereits gespeicherter wird oben übersprungen wenn schon als Karte gezeigt.
@Composable
private fun OrdnerListeInhalt(
    ordner: List<DriveOrdner>,
    gespeichert: DriveOrdner?,
    onAuswaehlen: (DriveOrdner) -> Unit
) {
    ordner.filter { it.id != gespeichert?.id }.forEach { eintrag ->
        OrdnerKarte(ordner = eintrag, hervorgehoben = false, onAuswaehlen = onAuswaehlen)
        Spacer(Modifier.height(8.dp))
    }
}

// Einzelne Ordner-Karte mit Auswählen-Button.
@Composable
private fun OrdnerKarte(
    ordner: DriveOrdner,
    hervorgehoben: Boolean,
    onAuswaehlen: (DriveOrdner) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (hervorgehoben) OberflächenFarbe else KartenFarbe
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Folder, null, tint = AkzentFarbe, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (hervorgehoben) {
                    Text("Zuletzt verwendet", color = AkzentFarbe, fontSize = 11.sp)
                }
                Text(ordner.name, color = TextHell, fontSize = 15.sp)
            }
            OutlinedButton(
                onClick = { onAuswaehlen(ordner) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AkzentFarbe)
            ) {
                Text("Wählen")
            }
        }
    }
}

// Button zum Erstellen eines neuen Ordners.
@Composable
private fun NeuenOrdnerButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Filled.AddCircle, null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("Neuen Ordner erstellen")
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Mit Vorschlag")
@Composable
private fun OrdnerAuswaehlenInhaltMitVorschlagVorschau() {
    val gespeichert = DriveOrdner("id1", "PhotoDrop")
    val ordner = listOf(
        gespeichert,
        DriveOrdner("id2", "Urlaub 2026"),
        DriveOrdner("id3", "Dokumente")
    )
    PhotoDropTheme {
        OrdnerAuswaehlenInhalt(
            zustand = DriveZustand.OrdnerAuswaehlen("max@gmail.com", "token", ordner, gespeichert),
            onAuswaehlen = {},
            onNeuErstellen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Ohne Vorschlag")
@Composable
private fun OrdnerAuswaehlenInhaltOhneVorschlagVorschau() {
    val ordner = listOf(
        DriveOrdner("id2", "Urlaub 2026"),
        DriveOrdner("id3", "Dokumente")
    )
    PhotoDropTheme {
        OrdnerAuswaehlenInhalt(
            zustand = DriveZustand.OrdnerAuswaehlen("max@gmail.com", "token", ordner, null),
            onAuswaehlen = {},
            onNeuErstellen = {}
        )
    }
}
