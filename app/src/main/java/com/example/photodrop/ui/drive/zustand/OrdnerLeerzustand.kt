package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft

// Leerzustand der Dateiliste — unterscheidet zwischen leerem Ordner und keinen Suchergebnissen.
@Composable
fun OrdnerLeerzustand(
    hatSuchfilter: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (hatSuchfilter) Icons.Filled.SearchOff else Icons.Filled.FolderOff,
            contentDescription = null,
            tint = TextGedaempft,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hatSuchfilter) "Keine Treffer" else "Ordner ist leer",
            style = MaterialTheme.typography.titleMedium,
            color = TextGedaempft
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (hatSuchfilter)
                "Kein Dateiname enthaelt diesen Suchbegriff."
            else
                "Lade dein erstes Dokument hoch.",
            style = MaterialTheme.typography.bodySmall,
            color = TextGedaempft
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Leer")
@Composable
private fun OrdnerLeerzustandVorschau() {
    PhotoDropTheme { OrdnerLeerzustand(hatSuchfilter = false) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Keine Suchergebnisse")
@Composable
private fun OrdnerLeerzustandSucheVorschau() {
    PhotoDropTheme { OrdnerLeerzustand(hatSuchfilter = true) }
}
