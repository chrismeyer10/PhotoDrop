package com.example.photodrop.dokument

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Divider
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Zeigt die Dokumentvorschau, auto-befuellte Felder, Hochladen- und optionalen KI-Button.
@Composable
fun DokumentGeladenInhalt(
    zustand: DokumentZustand.Geladen,
    onHochladen: (String, String) -> Unit = { _, _ -> },
    onAnalysieren: () -> Unit = {},
    onZurueck: () -> Unit = {}
) {
    var dateiname by remember(zustand.dateiname) { mutableStateOf(zustand.dateiname) }
    var drivePfad by remember(zustand.drivePfad) { mutableStateOf(zustand.drivePfad) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DokumentVorschauBild(vorschau = zustand.vorschau)

        EingabeFeld(
            wert = dateiname,
            onWertAendern = { dateiname = it },
            label = "Dateiname",
            laedt = zustand.analysiertLaeuft && dateiname.isBlank()
        )

        EingabeFeld(
            wert = drivePfad,
            onWertAendern = { drivePfad = it },
            label = "Drive-Ordner (z. B. Rechnungen/2026)",
            laedt = zustand.analysiertLaeuft && drivePfad.isBlank()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onHochladen(dateiname, drivePfad) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe),
            enabled = dateiname.isNotBlank() && drivePfad.isNotBlank()
        ) { Text("In Drive speichern") }

        Button(
            onClick = onZurueck,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OberflächenFarbe)
        ) { Text("Zurueck", color = TextHell) }

        Divider(color = OberflächenFarbe)
        Text("Optional", color = TextGedaempft, style = MaterialTheme.typography.labelMedium)

        Button(
            onClick = onAnalysieren,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OberflächenFarbe)
        ) { Text("Mit KI analysieren", color = TextHell) }
    }
}

// Textfeld mit optionalem Lade-Spinner wenn Analyse noch laeuft.
@Composable
private fun EingabeFeld(
    wert: String,
    onWertAendern: (String) -> Unit,
    label: String,
    laedt: Boolean
) {
    OutlinedTextField(
        value = wert,
        onValueChange = onWertAendern,
        label = {
            if (laedt) {
                CircularProgressIndicator(modifier = Modifier.height(16.dp), color = AkzentFarbe, strokeWidth = 2.dp)
            } else {
                Text(label, color = TextGedaempft)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextHell,
            unfocusedTextColor = TextHell,
            focusedBorderColor = AkzentFarbe,
            unfocusedBorderColor = TextGedaempft,
            focusedLabelColor = AkzentFarbe,
            unfocusedLabelColor = TextGedaempft,
            cursorColor = AkzentFarbe
        ),
        singleLine = true
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Analyse laeuft")
@Composable
private fun DokumentGeladenLaeadtVorschau() {
    PhotoDropTheme {
        DokumentGeladenInhalt(
            zustand = DokumentZustand.Geladen(Uri.EMPTY, null, analysiertLaeuft = true)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Analyse fertig")
@Composable
private fun DokumentGeladenFertigVorschau() {
    PhotoDropTheme {
        DokumentGeladenInhalt(
            zustand = DokumentZustand.Geladen(
                uri = Uri.EMPTY,
                vorschau = null,
                dateiname = "Rechnung Amazon Maerz 2026",
                drivePfad = "Rechnungen/2026",
                analysiertLaeuft = false
            )
        )
    }
}
