package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.photodrop.ui.drive.api.DriveOrdner
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.KartenFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Karte fuer einen Ordner in der Drive-Navigationsansicht.
// Antippen = In Ordner navigieren. Langer Druck = Als Upload-Ziel setzen.
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrdnerNavigationsKarte(
    ordner: DriveOrdner,
    istAktivesUploadZiel: Boolean,
    onOeffnen: (DriveOrdner) -> Unit,
    onAlsZielSetzen: (DriveOrdner) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onOeffnen(ordner) },
                onLongClick = { onAlsZielSetzen(ordner) }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (istAktivesUploadZiel) OberflächenFarbe else KartenFarbe
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Folder,
                contentDescription = null,
                tint = AkzentFarbe,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (istAktivesUploadZiel) {
                    Text("Upload-Ziel", color = AkzentFarbe, fontSize = 11.sp)
                }
                Text(ordner.name, color = TextHell, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Lang druecken = Als Ziel setzen",
                    color = TextGedaempft,
                    fontSize = 11.sp
                )
            }
            if (istAktivesUploadZiel) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Aktives Ziel",
                    tint = AkzentFarbe,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(4.dp))
            }
            Icon(
                Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = "Oeffnen",
                tint = TextGedaempft,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Normal")
@Composable
private fun OrdnerNavigationsKarteVorschau() {
    PhotoDropTheme {
        OrdnerNavigationsKarte(
            ordner = DriveOrdner("1", "Rechnungen 2026"),
            istAktivesUploadZiel = false,
            onOeffnen = {},
            onAlsZielSetzen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Aktives Ziel")
@Composable
private fun OrdnerNavigationsKarteAktivVorschau() {
    PhotoDropTheme {
        OrdnerNavigationsKarte(
            ordner = DriveOrdner("2", "Fotos 2026"),
            istAktivesUploadZiel = true,
            onOeffnen = {},
            onAlsZielSetzen = {}
        )
    }
}
