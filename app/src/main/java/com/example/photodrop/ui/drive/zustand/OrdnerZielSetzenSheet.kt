package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.drive.api.DriveOrdner
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.KartenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// BottomSheet mit Optionen fuer einen langen Druck auf einen Ordner.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdnerZielSetzenSheet(
    ordner: DriveOrdner,
    onAlsZielSetzen: (DriveOrdner) -> Unit,
    onOeffnen: (DriveOrdner) -> Unit,
    onSchliessen: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onSchliessen,
        sheetState = sheetState
    ) {
        SheetInhalt(
            ordner = ordner,
            onAlsZielSetzen = { onAlsZielSetzen(ordner); onSchliessen() },
            onOeffnen = { onOeffnen(ordner); onSchliessen() }
        )
    }
}

// Inhalt des BottomSheets mit Ordner-Aktionen.
@Composable
private fun SheetInhalt(
    ordner: DriveOrdner,
    onAlsZielSetzen: () -> Unit,
    onOeffnen: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            ordner.name,
            color = TextHell,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider(color = KartenFarbe)
        Spacer(Modifier.height(8.dp))
        AktionZeile(
            label = "Als Upload-Ziel setzen",
            icon = Icons.Filled.Upload,
            onClick = onAlsZielSetzen
        )
        AktionZeile(
            label = "Ordner oeffnen",
            icon = Icons.Filled.FolderOpen,
            onClick = onOeffnen
        )
        Spacer(Modifier.height(16.dp))
    }
}

// Einzelne Aktionszeile im BottomSheet.
@Composable
private fun AktionZeile(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(icon, contentDescription = null, tint = AkzentFarbe, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, color = TextHell)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun OrdnerZielSetzenSheetVorschau() {
    PhotoDropTheme {
        SheetInhalt(
            ordner = DriveOrdner("1", "Rechnungen 2026"),
            onAlsZielSetzen = {},
            onOeffnen = {}
        )
    }
}
