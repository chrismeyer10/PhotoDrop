package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.drive.api.DriveOrdnerDatei
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Dialog mit Aktionen fuer eine ausgewaehlte Datei (Info, Oeffnen, Schliessen).
@Composable
fun DateiAktionsDialog(
    datei: DriveOrdnerDatei,
    onSchliessen: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onSchliessen,
        icon = {
            val icon = when {
                datei.istOrdner -> Icons.Filled.Folder
                datei.istBild -> Icons.Filled.Image
                else -> Icons.Filled.Description
            }
            Icon(icon, contentDescription = null, tint = AkzentFarbe, modifier = Modifier.size(36.dp))
        },
        title = {
            Text(datei.name, color = TextHell, style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column(horizontalAlignment = Alignment.Start) {
                DateiInfoZeile("Typ", datei.mimeType.substringAfterLast("/"))
                if (datei.groesseFormatiert.isNotEmpty()) {
                    DateiInfoZeile("Groesse", datei.groesseFormatiert)
                }
                datei.geaendertAm?.take(10)?.let { DateiInfoZeile("Geaendert", it) }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Oeffnen via Google Drive App oder Browser.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGedaempft
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSchliessen) { Text("Schliessen", color = AkzentFarbe) }
        }
    )
}

// Eine einzelne Info-Zeile mit Label und Wert.
@Composable
private fun DateiInfoZeile(label: String, wert: String) {
    Text(
        "$label: $wert",
        style = MaterialTheme.typography.bodySmall,
        color = TextGedaempft,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun DateiAktionsDialogVorschau() {
    PhotoDropTheme {
        DateiAktionsDialog(
            datei = DriveOrdnerDatei("1", "Rechnung Amazon 2026.pdf", "application/pdf", 1_234_567, "2026-03-21T10:00:00Z")
        )
    }
}
