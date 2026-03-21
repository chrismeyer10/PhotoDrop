package com.example.photodrop.ui.drive

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.photodrop.ui.theme.PhotoDropTheme
import com.example.photodrop.ui.theme.TextGedaempft

// Grüne Terminal-Schriftfarbe für Dateinamen.
private val TerminalGruen = Color(0xFF00FF88)

// Türkise Farbe für Metadaten (Größe, Datum).
private val TerminalMeta = Color(0xFF4EC9B0)

// Zeigt den Ordnerinhalt in einer Terminal-ähnlichen Tree-Struktur.
@Composable
fun OrdnerInhaltInhalt(zustand: DriveZustand.InhaltGeladen) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OrdnerKopfzeile(zustand.kontoName)
        if (zustand.dateien.isEmpty()) {
            OrdnerLeerHinweis()
        } else {
            OrdnerBaumInhalt(zustand.dateien)
        }
    }
}

// Zeigt die Kopfzeile mit Ordner-Symbol und Kontoname.
@Composable
private fun OrdnerKopfzeile(kontoName: String) {
    Text(
        text = "📁  Drive-Ordner",
        color = TerminalGruen,
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    Text(
        text = "    $kontoName",
        color = TextGedaempft,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

// Zeigt den Hinweis wenn der Ordner leer ist.
@Composable
private fun OrdnerLeerHinweis() {
    Text(
        text = "    └── (leer)",
        color = TextGedaempft,
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    Text(
        text = "\nNoch keine Dateien im Ordner — mach dein erstes Foto.",
        color = TextGedaempft,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp
    )
}

// Zeigt alle Dateien als Tree mit Präfix-Zeichen.
@Composable
private fun OrdnerBaumInhalt(dateien: List<DriveOrdnerDatei>) {
    dateien.forEachIndexed { index, datei ->
        val istLetzte = index == dateien.lastIndex
        val praefix = if (istLetzte) "    └── " else "    ├── "
        DateiZeile(praefix, datei)
    }
}

// Zeigt eine einzelne Datei-Zeile mit Icon, Name und Metadaten.
@Composable
private fun DateiZeile(praefix: String, datei: DriveOrdnerDatei) {
    val meta = buildString {
        if (datei.groesseFormatiert.isNotEmpty()) append("  ${datei.groesseFormatiert}")
        datei.geaendertAm?.take(10)?.let { append("  $it") }
    }
    Column(modifier = Modifier.padding(bottom = 2.dp)) {
        Text(
            text = "$praefix${datei.icon}  ${datei.name}",
            color = if (datei.istOrdner) TerminalMeta else TerminalGruen,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        )
        if (meta.isNotBlank()) {
            Text(
                text = "         $meta",
                color = TextGedaempft,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
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
                kontoName = "max@gmail.com",
                ordnerId = "abc",
                dateien = listOf(
                    DriveOrdnerDatei("1", "foto_001.jpg", "image/jpeg", 1_234_567, "2026-03-21T10:00:00Z"),
                    DriveOrdnerDatei("2", "foto_002.jpg", "image/jpeg", 987_000, "2026-03-20T08:30:00Z"),
                    DriveOrdnerDatei("3", "Sicherung", "application/vnd.google-apps.folder", null, null),
                    DriveOrdnerDatei("4", "notizen.txt", "text/plain", 512, "2026-03-19T14:00:00Z")
                )
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Leer")
@Composable
private fun OrdnerInhaltLeerVorschau() {
    PhotoDropTheme {
        OrdnerInhaltInhalt(
            DriveZustand.InhaltGeladen("max@gmail.com", "abc", emptyList())
        )
    }
}
