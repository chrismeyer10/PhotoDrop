package com.example.photodrop.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.KartenFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextHell

// Inhalt der linken Seitenleiste.
// Zeigt alle Navigationsziele als klickbare Einträge an.
@Composable
fun NavigationsLeisteInhalt(
    aktuelleRoute: String,
    onZielAusgewaehlt: (NavigationsZiel) -> Unit,
    modifier: Modifier = Modifier
) {
    val ziele = listOf(NavigationsZiel.FotoAufnahme, NavigationsZiel.Dokument, NavigationsZiel.GoogleDrive)

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(OberflächenFarbe)
            .padding(top = 56.dp, bottom = 24.dp)
    ) {
        Text(
            text = "PhotoDrop",
            style = MaterialTheme.typography.headlineSmall,
            color = AkzentFarbe,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = KartenFarbe)
        Spacer(modifier = Modifier.height(8.dp))

        ziele.forEach { ziel ->
            NavigationsEintrag(
                ziel = ziel,
                istAktiv = ziel.route == aktuelleRoute,
                onClick = { onZielAusgewaehlt(ziel) }
            )
        }
    }
}

// Einzelner Eintrag in der Seitenleiste mit Icon und Bezeichnung.
// Aktiver Eintrag wird farbig hervorgehoben.
@Composable
private fun NavigationsEintrag(
    ziel: NavigationsZiel,
    istAktiv: Boolean,
    onClick: () -> Unit
) {
    val hintergrund = if (istAktiv) AkzentFarbe.copy(alpha = 0.15f) else Color.Transparent
    val textFarbe = if (istAktiv) AkzentFarbe else TextHell

    Row(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick)
            .background(hintergrund)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ziel.icon,
            contentDescription = null,
            tint = textFarbe,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = ziel.titel,
            color = textFarbe,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF141414, name = "Seitenleiste")
@Composable
private fun NavigationsLeisteVorschau() {
    PhotoDropTheme {
        NavigationsLeisteInhalt(
            aktuelleRoute = NavigationsZiel.FotoAufnahme.route,
            onZielAusgewaehlt = {}
        )
    }
}
