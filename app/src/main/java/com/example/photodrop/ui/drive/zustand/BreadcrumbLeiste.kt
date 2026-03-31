package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.drive.api.DriveOrdner
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Breadcrumb-Navigationsleiste mit Zurueck-Button und Pfad-Anzeige.
// Zeigt "Drive / Ordner1 / Ordner2" entsprechend dem Navigations-Stack.
@Composable
fun BreadcrumbLeiste(
    navigationsStack: List<DriveOrdner>,
    onZurueck: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (navigationsStack.isNotEmpty()) {
            IconButton(onClick = onZurueck, modifier = Modifier.size(40.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurueck",
                    tint = AkzentFarbe
                )
            }
        }
        BreadcrumbPfad(navigationsStack = navigationsStack)
    }
}

// Zeigt den Breadcrumb-Pfad als Text mit Trennzeichen.
@Composable
private fun BreadcrumbPfad(navigationsStack: List<DriveOrdner>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Drive",
            style = MaterialTheme.typography.bodySmall,
            color = if (navigationsStack.isEmpty()) TextHell else TextGedaempft
        )
        navigationsStack.forEachIndexed { index, ordner ->
            Icon(
                Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = null,
                tint = TextGedaempft,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = ordner.name,
                style = MaterialTheme.typography.bodySmall,
                color = if (index == navigationsStack.lastIndex) TextHell else TextGedaempft,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Root")
@Composable
private fun BreadcrumbLeisteRootVorschau() {
    PhotoDropTheme {
        BreadcrumbLeiste(navigationsStack = emptyList(), onZurueck = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "In Unterordner")
@Composable
private fun BreadcrumbLeisteUnterordnerVorschau() {
    PhotoDropTheme {
        BreadcrumbLeiste(
            navigationsStack = listOf(
                DriveOrdner("1", "Rechnungen"),
                DriveOrdner("2", "2026")
            ),
            onZurueck = {}
        )
    }
}
