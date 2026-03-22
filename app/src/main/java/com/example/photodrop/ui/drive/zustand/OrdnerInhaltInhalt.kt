package com.example.photodrop.ui.drive.zustand

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.photodrop.ui.drive.api.DriveOrdnerDatei
import com.example.photodrop.ui.theme.AkzentFarbe
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft

// Gruene Terminal-Schriftfarbe fuer Dateinamen.
private val TerminalGruen = Color(0xFF00FF88)

// Tuerkise Farbe fuer Metadaten.
private val TerminalMeta = Color(0xFF4EC9B0)

// Zeigt den Ordnerinhalt in einer Terminal-aehnlichen Tree-Struktur.
@Composable
fun OrdnerInhaltInhalt(
    zustand: DriveZustand.InhaltGeladen,
    onOrdnerWechseln: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OrdnerKopfzeile(zustand.kontoName, onOrdnerWechseln)
        if (zustand.dateien.isEmpty()) {
            OrdnerLeerHinweis()
        } else {
            OrdnerBaumInhalt(zustand.dateien)
        }
    }
}

// Kopfzeile mit Ordner-Symbol, Kontoname und Wechsel-Button.
@Composable
private fun OrdnerKopfzeile(kontoName: String, onOrdnerWechseln: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "\uD83D\uDCC1  Drive-Ordner", color = TerminalGruen,
                fontFamily = FontFamily.Monospace, fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "    $kontoName", color = TextGedaempft,
                fontFamily = FontFamily.Monospace, fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        IconButton(onClick = onOrdnerWechseln) {
            Icon(
                Icons.Filled.SwapHoriz, "Ordner wechseln",
                tint = AkzentFarbe, modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Hinweis wenn der Ordner leer ist.
@Composable
private fun OrdnerLeerHinweis() {
    Text(
        "    \u2514\u2500\u2500 (leer)", color = TextGedaempft,
        fontFamily = FontFamily.Monospace, fontSize = 13.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    Text(
        "\nNoch keine Dateien im Ordner \u2014 mach dein erstes Foto.",
        color = TextGedaempft, fontFamily = FontFamily.Monospace, fontSize = 12.sp
    )
}

// Zeigt alle Dateien als Tree mit Praefix-Zeichen.
@Composable
private fun OrdnerBaumInhalt(dateien: List<DriveOrdnerDatei>) {
    dateien.forEachIndexed { index, datei ->
        val istLetzte = index == dateien.lastIndex
        val praefix = if (istLetzte) "    \u2514\u2500\u2500 " else "    \u251C\u2500\u2500 "
        DateiZeile(praefix, datei)
    }
}

// Einzelne Datei-Zeile mit Icon, Name und Metadaten.
@Composable
private fun DateiZeile(praefix: String, datei: DriveOrdnerDatei) {
    val meta = buildString {
        if (datei.groesseFormatiert.isNotEmpty()) append("  ${datei.groesseFormatiert}")
        datei.geaendertAm?.take(10)?.let { append("  $it") }
    }
    Column(modifier = Modifier.padding(bottom = 2.dp)) {
        Text(
            "$praefix${datei.icon}  ${datei.name}",
            color = if (datei.istOrdner) TerminalMeta else TerminalGruen,
            fontFamily = FontFamily.Monospace, fontSize = 13.sp
        )
        if (meta.isNotBlank()) {
            Text(
                "         $meta", color = TextGedaempft,
                fontFamily = FontFamily.Monospace, fontSize = 11.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Mit Dateien")
@Composable
private fun OrdnerInhaltInhaltVorschau() {
    PhotoDropTheme {
        OrdnerInhaltInhalt(
            DriveZustand.InhaltGeladen(
                "max@gmail.com", "abc",
                listOf(
                    DriveOrdnerDatei("1", "foto_001.jpg", "image/jpeg", 1_234_567, "2026-03-21T10:00:00Z"),
                    DriveOrdnerDatei("2", "Sicherung", "application/vnd.google-apps.folder", null, null)
                )
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Leer")
@Composable
private fun OrdnerInhaltLeerVorschau() {
    PhotoDropTheme {
        OrdnerInhaltInhalt(DriveZustand.InhaltGeladen("max@gmail.com", "abc", emptyList()))
    }
}
