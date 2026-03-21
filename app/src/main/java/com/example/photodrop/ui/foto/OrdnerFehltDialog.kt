package com.example.photodrop.ui.foto

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Dialog der erscheint wenn kein Drive-Ordner gesetzt ist und der Nutzer ein Foto aufnehmen will.
// Leitet den Nutzer zur Drive-Seite zum Erstellen des Ordners weiter.
@Composable
fun OrdnerFehltDialog(
    onZuDrive: () -> Unit,
    onAbbrechen: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onAbbrechen,
        containerColor = OberflächenFarbe,
        title = {
            Text(text = "Kein Drive-Ordner", color = TextHell)
        },
        text = {
            Text(
                text = "Bevor du Fotos aufnehmen kannst, " +
                    "verbinde Google Drive und wähle einen Ordner " +
                    "für die automatische Speicherung.",
                color = TextGedaempft
            )
        },
        confirmButton = {
            Button(
                onClick = onZuDrive,
                colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
            ) {
                Text("Zu Drive")
            }
        },
        dismissButton = {
            TextButton(onClick = onAbbrechen) {
                Text("Abbrechen", color = TextGedaempft)
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Ordner fehlt Dialog")
@Composable
private fun OrdnerFehltDialogVorschau() {
    PhotoDropTheme {
        OrdnerFehltDialog(
            onZuDrive = {},
            onAbbrechen = {}
        )
    }
}
