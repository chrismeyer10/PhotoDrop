package com.example.photodrop.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.KartenFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Permanente Schnellaktionsleiste am unteren Bildschirmrand.
// Zeigt gewaehlten Ordner sowie Buttons fuer Kamera und Ordner-Auswahl.
@Composable
fun SchnellAktionsLeiste(
    aktuellerOrdnerName: String?,
    onFotoMachen: () -> Unit = {},
    onOrdnerWaehlen: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(OberflächenFarbe)
    ) {
        HorizontalDivider(color = KartenFarbe, thickness = 1.dp)
        OrdnerAnzeige(aktuellerOrdnerName)
        SchnellButtons(onFotoMachen, onOrdnerWaehlen)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// Zeigt den aktuell gewaehlten Ordner oder einen Hinweis wenn keiner gewaehlt ist.
@Composable
private fun OrdnerAnzeige(ordnerName: String?) {
    val text = ordnerName ?: "Kein Ordner gewaehlt"
    val farbe = if (ordnerName != null) AkzentFarbe else TextGedaempft
    Text(
        text = text,
        color = farbe,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}

// Zeigt Kamera- und Ordner-Buttons nebeneinander.
@Composable
private fun SchnellButtons(onFotoMachen: () -> Unit, onOrdnerWaehlen: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onFotoMachen,
            modifier = Modifier.weight(1f).height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
        ) {
            Icon(Icons.Filled.CameraAlt, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Foto machen")
        }
        OutlinedButton(
            onClick = onOrdnerWaehlen,
            modifier = Modifier.weight(1f).height(48.dp)
        ) {
            Icon(Icons.Filled.FolderOpen, null, tint = TextHell, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Ordner waehlen", color = TextHell)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Mit Ordner")
@Composable
private fun SchnellAktionsLeisteVorschau() {
    PhotoDropTheme {
        SchnellAktionsLeiste(aktuellerOrdnerName = "Fotos/2026")
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Ohne Ordner")
@Composable
private fun SchnellAktionsLeisteOhneOrdnerVorschau() {
    PhotoDropTheme {
        SchnellAktionsLeiste(aktuellerOrdnerName = null)
    }
}
