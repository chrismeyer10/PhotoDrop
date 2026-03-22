package com.example.photodrop.dokument.vorschlag

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.photodrop.dokument.DokumentZustand
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Zeigt den KI-Vorschlag mit editierbaren Feldern fuer Dateiname und Unterordner.
@Composable
fun VorschlagInhalt(
    zustand: DokumentZustand.VorschlagBereit,
    onHochladen: (String, String) -> Unit = { _, _ -> },
    onZurueck: () -> Unit = {}
) {
    var dateiname by remember(zustand.dateiname) { mutableStateOf(zustand.dateiname) }
    var unterordner by remember(zustand.unterordner) { mutableStateOf(zustand.unterordner) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        zustand.vorschau?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Dokumentvorschau",
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        OutlinedTextField(
            value = dateiname,
            onValueChange = { dateiname = it },
            label = { Text("Dateiname") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFeldFarben(),
            singleLine = true
        )

        OutlinedTextField(
            value = unterordner,
            onValueChange = { unterordner = it },
            label = { Text("Unterordner") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFeldFarben(),
            singleLine = true
        )

        if (zustand.begruendung.isNotBlank()) {
            Text(
                text = zustand.begruendung,
                color = TextGedaempft,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onHochladen(dateiname, unterordner) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe)
        ) { Text("Akzeptieren") }

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

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Vorschlag")
@Composable
private fun VorschlagInhaltVorschau() {
    PhotoDropTheme {
        VorschlagInhalt(
            zustand = DokumentZustand.VorschlagBereit(
                uri = Uri.EMPTY,
                vorschau = null,
                dateiname = "Rechnung_Amazon_2026-03.pdf",
                unterordner = "Rechnungen",
                begruendung = "Amazon-Rechnung erkannt, Datum Maerz 2026."
            )
        )
    }
}
