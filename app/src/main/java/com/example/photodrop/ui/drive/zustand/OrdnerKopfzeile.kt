package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Kopfzeile mit Ordner-Icon, Kontoname, Dateianzahl und Wechsel-Button.
@Composable
fun OrdnerKopfzeile(
    kontoName: String,
    dateiAnzahl: Int,
    onOrdnerWechseln: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Folder,
                contentDescription = null,
                tint = AkzentFarbe,
                modifier = Modifier.size(28.dp).padding(end = 0.dp)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    "Drive-Ordner",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextHell
                )
                Text(
                    "$kontoName  |  $dateiAnzahl Dateien",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGedaempft
                )
            }
        }
        IconButton(onClick = onOrdnerWechseln) {
            Icon(Icons.Filled.SwapHoriz, "Ordner wechseln", tint = AkzentFarbe)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun OrdnerKopfzeileVorschau() {
    PhotoDropTheme {
        OrdnerKopfzeile(kontoName = "max@gmail.com", dateiAnzahl = 12)
    }
}
