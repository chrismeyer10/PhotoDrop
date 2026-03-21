# Code Conventions — PhotoDrop

> Lebende Dokumentation. Wird bei jeder Aufgabe gelesen und bei neuen
> erkannten Mustern erweitert.

---

## Sprache

- Alle Kommentare, Bezeichner, Funktions- und Klassennamen auf **Deutsch**
- Ausnahmen: Android/Jetpack-Compose-APIs, externe Bibliotheken, technische
  Pflichtbezeichner (z.B. `onCreate`, `get`, `set`)
- Strings die der Nutzer sieht: in `strings.xml` auf Deutsch

**Richtig:**
```kotlin
class FotoAnalyse {
    fun bildLaden(uri: String): Bitmap { ... }
}
```
**Falsch:**
```kotlin
class PhotoAnalysis {
    fun loadImage(uri: String): Bitmap { ... }
}
```

---

## Klassenlänge

- Eine Klasse hat **eine einzige Verantwortung**
- Maximale Orientierung: **~50 Zeilen** — wird es mehr, prüfen ob aufteilen sinnvoll ist
- Keine God-Classes — lieber mehrere kleine Klassen

---

## Funktionslänge

- Maximale Orientierung: **~10 Zeilen** pro Funktion
- Wird eine Funktion länger → Teilschritte in eigene Funktionen auslagern
- Funktionsname beschreibt **was** sie tut, nicht wie

**Richtig:**
```kotlin
fun fotoVerarbeiten(uri: String): Ergebnis {
    val bild = bildLaden(uri)
    val metadaten = metadatenLesen(bild)
    return ergebnisErstellen(bild, metadaten)
}
```
**Falsch:**
```kotlin
fun fotoVerarbeiten(uri: String): Ergebnis {
    // 40 Zeilen gemischte Logik ...
}
```

---

## Kommentare

- Kommentare erklären das **Warum**, nicht das Was
- Kein auskommentierter Code im Repository
- TODOs immer mit Kontext: `// TODO: Warum ist das noch offen?`

---

## Dateistruktur

- Eine Klasse pro Datei
- Dateiname = Klassenname (auf Deutsch, PascalCase)
- Packages widerspiegeln die Funktion: `agent/`, `skills/`, `ui/`, `daten/`

---

## Composable Previews

- **Jedes öffentliche `@Composable` bekommt eine `@Preview`-Funktion**
- Preview-Funktionen sind `private` und enden auf `Vorschau`
- Immer in `PhotoDropTheme` wrappen
- Immer `backgroundColor = 0xFF0A0A0A` setzen (Dark Theme)
- Bei mehreren Zuständen: mehrere Previews mit `name = "..."` anlegen

**Richtig:**
```kotlin
@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "Leerzustand")
@Composable
private fun MeineKomponenteVorschau() {
    PhotoDropTheme {
        MeineKomponente()
    }
}
```

## Weitere Konventionen

> Neue Einträge hier einfügen wenn sie während einer Aufgabe erkannt werden.
