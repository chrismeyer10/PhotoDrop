package com.example.photodrop.ui.navigation

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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

// Dialog zur Ordnerauswahl nach einem Schnellfoto.
// Zeigt alle verfuegbaren Ordner, markiert den zuletzt verwendeten.
@Composable
fun OrdnerAuswahlDialog(
    verfuegbareOrdner: List<DriveOrdner>,
    aktiverOrdner: DriveOrdner?,
    onOrdnerGewaehlt: (DriveOrdner) -> Unit,
    onAbbrechen: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onAbbrechen,
        title = { Text("In welchen Ordner?", color = TextHell, fontWeight = FontWeight.SemiBold) },
        text = {
            OrdnerListeInhalt(
                verfuegbareOrdner = verfuegbareOrdner,
                aktiverOrdner = aktiverOrdner,
                onOrdnerGewaehlt = onOrdnerGewaehlt
            )
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onAbbrechen) {
                Text("Abbrechen", color = TextGedaempft)
            }
        }
    )
}

// Scrollbare Liste der Ordner-Eintraege.
@Composable
private fun OrdnerListeInhalt(
    verfuegbareOrdner: List<DriveOrdner>,
    aktiverOrdner: DriveOrdner?,
    onOrdnerGewaehlt: (DriveOrdner) -> Unit
) {
    if (verfuegbareOrdner.isEmpty()) {
        Text("Keine Ordner gefunden.", color = TextGedaempft)
        return
    }
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        aktiverOrdner?.let { letzter ->
            OrdnerZeile(ordner = letzter, istAktiv = true, onClick = { onOrdnerGewaehlt(letzter) })
            Spacer(Modifier.height(4.dp))
        }
        verfuegbareOrdner
            .filter { it.id != aktiverOrdner?.id }
            .forEach { ordner ->
                OrdnerZeile(ordner = ordner, istAktiv = false, onClick = { onOrdnerGewaehlt(ordner) })
                Spacer(Modifier.height(4.dp))
            }
    }
}

// Einzelne Ordner-Zeile im Dialog.
@Composable
private fun OrdnerZeile(
    ordner: DriveOrdner,
    istAktiv: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (istAktiv) OberflächenFarbe else KartenFarbe
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Folder, null, tint = AkzentFarbe, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (istAktiv) {
                    Text("Zuletzt verwendet", color = AkzentFarbe, fontSize = 11.sp)
                }
                Text(ordner.name, color = TextHell, fontSize = 14.sp)
            }
            if (istAktiv) {
                Icon(Icons.Filled.CheckCircle, null, tint = AkzentFarbe, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun OrdnerAuswahlDialogVorschau() {
    val aktiv = DriveOrdner("1", "Fotos 2026")
    PhotoDropTheme {
        OrdnerAuswahlDialog(
            verfuegbareOrdner = listOf(aktiv, DriveOrdner("2", "Dokumente"), DriveOrdner("3", "Archiv")),
            aktiverOrdner = aktiv,
            onOrdnerGewaehlt = {},
            onAbbrechen = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Kein aktiver Ordner")
@Composable
private fun OrdnerAuswahlDialogLeerVorschau() {
    PhotoDropTheme {
        OrdnerAuswahlDialog(
            verfuegbareOrdner = listOf(DriveOrdner("1", "Fotos"), DriveOrdner("2", "Dokumente")),
            aktiverOrdner = null,
            onOrdnerGewaehlt = {},
            onAbbrechen = {}
        )
    }
}
