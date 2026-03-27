package com.example.photodrop.dokument

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
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.OberflächenFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft
import com.example.photodrop.ui.theme.TextHell

// Ermoeglicht dem User, Dateiname und Unterordner manuell einzugeben.
@Composable
fun DokumentManuellBenennenInhalt(
    zustand: DokumentZustand.ManuellBenennen,
    onHochladen: (String, String) -> Unit = { _, _ -> },
    onZurueck: () -> Unit = {}
) {
    var dateiname by remember { mutableStateOf("Dokument.pdf") }
    var unterordner by remember { mutableStateOf("Dokumente") }

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

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onHochladen(dateiname, unterordner) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AkzentFarbe),
            enabled = dateiname.isNotBlank()
        ) { Text("In Drive speichern") }

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

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Manuell Benennen")
@Composable
private fun DokumentManuellBenennenInhaltVorschau() {
    PhotoDropTheme {
        DokumentManuellBenennenInhalt(
            zustand = DokumentZustand.ManuellBenennen(
                uri = android.net.Uri.EMPTY,
                vorschau = null
            )
        )
    }
}
