---
name: compose-preview
description: >
  Prüft alle Kotlin-Dateien auf fehlende @Preview-Annotationen und fügt sie hinzu.
  Aufrufen wenn der struktur-check fehlende Previews meldet oder nach dem Schreiben
  neuer Composables. Jedes öffentliche Composable braucht mindestens eine @Preview.
---

# Fehlende @Preview-Annotationen hinzufügen

## Prüfung

Für jede `.kt`-Datei im `ui/`-Verzeichnis:

```bash
grep -l "@Composable" app/src/main/java/com/example/photodrop/ui/**/*.kt
```

Für jede gefundene Datei prüfen:
1. Welche `@Composable`-Funktionen sind **öffentlich** (nicht `private`)?
2. Hat jede davon eine `@Preview`-Funktion in derselben Datei?

## Preview-Standard

```kotlin
@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "<Beschreibung>")
@Composable
private fun <FunktionsName>Vorschau() {
    PhotoDropTheme {
        <FunktionsName>(
            // sinnvolle Testdaten eintragen
        )
    }
}
```

## Regeln

- **Name**: Immer `<Funktion>Vorschau` (deutsch)
- **Background**: Immer `0xFF0A0A0A` (App-Hintergrundfarbe)
- **PhotoDropTheme**: Immer als Wrapper — sonst stimmen Farben nicht
- **Mehrere Zustände**: Für jeden Hauptzustand eine eigene Preview
  - z.B. `Leer`, `Mit Inhalt`, `Fehler`, `Laden`
- **Testdaten**: Realistische Werte — kein `""` oder `0` wenn es einen Zustand darstellt

## Imports die benötigt werden

```kotlin
import androidx.compose.ui.tooling.preview.Preview
import com.example.photodrop.ui.theme.PhotoDropTheme
```
