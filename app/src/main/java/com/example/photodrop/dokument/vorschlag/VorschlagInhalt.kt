package com.example.photodrop.dokument.vorschlag

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.photodrop.dokument.DokumentVorschauBild
import com.example.photodrop.dokument.DokumentZustand
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Zeigt den KI-Vorschlag mit editierbaren Feldern fuer Dateiname und Drive-Pfad.
@Composable
fun VorschlagInhalt(
    zustand: DokumentZustand.VorschlagBereit,
    onHochladen: (String, String) -> Unit = { _, _ -> },
    onZurueck: () -> Unit = {}
) {
    var dateiname by remember(zustand.dateiname) { mutableStateOf(zustand.dateiname) }
    val startPfad = if (zustand.drivePfad.isNotBlank()) zustand.drivePfad else zustand.unterordner
    var drivePfad by remember(startPfad) { mutableStateOf(startPfad) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DokumentVorschauBild(vorschau = zustand.vorschau)

        Text(
            "KI-Vorschlag bestätigen",
            style = MaterialTheme.typography.titleMedium,
            color = TextHell
        )

        OutlinedTextField(
            value = dateiname,
            onValueChange = { dateiname = it },
            label = { Text("Dateiname", color = TextGedaempft) },
            modifier = Modifier.fillMaxWidth(),
            colors = textFeldFarben(),
            singleLine = true
        )

        OutlinedTextField(
            value = drivePfad,
            onValueChange = { drivePfad = it },
            label = { Text("Drive-Pfad (z.B. Rechnungen/2026)", color = TextGedaempft) },
            modifier = Modifier.fillMaxWidth(),
            colors = textFeldFarben(),
            singleLine = true
        )

        if (zustand.begruendung.isNotBlank()) {
            Text(
                text = zustand.begruendung,
                color = TextGedaempft,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onHochladen(dateiname, drivePfad) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe),
            enabled = dateiname.isNotBlank() && drivePfad.isNotBlank()
        ) { Text("Akzeptieren & hochladen") }

        Button(
            onClick = onZurueck,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OberflächenFarbe)
        ) { Text("Zurueck", color = TextHell) }
    }
}

// Einheitliche Farben fuer die Text-Eingabefelder.
@Composable
private fun textFeldFarben() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextHell,
    unfocusedTextColor = TextHell,
    focusedBorderColor = AkzentFarbe,
    unfocusedBorderColor = TextGedaempft,
    focusedLabelColor = AkzentFarbe,
    unfocusedLabelColor = TextGedaempft,
    cursorColor = AkzentFarbe
)

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Vorschlag mit Pfad")
@Composable
private fun VorschlagInhaltVorschau() {
    PhotoDropTheme {
        VorschlagInhalt(
            zustand = DokumentZustand.VorschlagBereit(
                uri = Uri.EMPTY,
                vorschau = null,
                dateiname = "Rechnung_Amazon_2026-03.pdf",
                unterordner = "Rechnungen",
                begruendung = "Amazon-Rechnung erkannt, Datum Maerz 2026.",
                drivePfad = "Rechnungen/2026"
            )
        )
    }
}
