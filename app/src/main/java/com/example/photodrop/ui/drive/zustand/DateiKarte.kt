package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
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
import com.example.photodrop.ui.drive.api.DriveOrdnerDatei
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Einzelne Datei-Karte mit Icon, Name, Metadaten und Klick-Aktion.
@Composable
fun DateiKarte(
    datei: DriveOrdnerDatei,
    onKlick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onKlick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = OberflächenFarbe)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DateiIcon(datei = datei)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = datei.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextHell,
                    maxLines = 2
                )
                val meta = buildString {
                    if (datei.groesseFormatiert.isNotEmpty()) append(datei.groesseFormatiert)
                    datei.geaendertAm?.take(10)?.let {
                        if (isNotEmpty()) append("  |  ")
                        append(it)
                    }
                }
                if (meta.isNotBlank()) {
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGedaempft
                    )
                }
            }
        }
    }
}

// Icon passend zum Dateityp.
@Composable
private fun DateiIcon(datei: DriveOrdnerDatei) {
    val (icon, farbe) = when {
        datei.istOrdner -> Pair(Icons.Filled.Folder, AkzentFarbe)
        datei.istBild -> Pair(Icons.Filled.Image, TextHell)
        else -> Pair(Icons.Filled.Description, TextGedaempft)
    }
    Icon(icon, contentDescription = null, tint = farbe, modifier = Modifier.size(32.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "PDF")
@Composable
private fun DateiKartePdfVorschau() {
    PhotoDropTheme {
        DateiKarte(
            datei = DriveOrdnerDatei("1", "Rechnung Amazon 2026.pdf", "application/pdf", 1_234_567, "2026-03-21T10:00:00Z")
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Ordner")
@Composable
private fun DateiKarteOrdnerVorschau() {
    PhotoDropTheme {
        DateiKarte(
            datei = DriveOrdnerDatei("2", "Rechnungen 2026", "application/vnd.google-apps.folder", null, null)
        )
    }
}
