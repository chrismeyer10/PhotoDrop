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

- **Jede öffentliche Klasse und Funktion bekommt einen kurzen Kommentar** direkt darüber
- Kommentar auf Deutsch, 1–2 Zeilen, einfache Sprache — verständlich auch ohne Vorkenntnisse
- Erklärt **was** die Einheit tut (bei Klassen/Composables) oder **was** zurückgegeben wird (bei Funktionen)
- Private Hilfsfunktionen nur kommentieren wenn die Logik nicht selbsterklärend ist
- Kein auskommentierter Code im Repository
- TODOs immer mit Kontext: `// TODO: Warum ist das noch offen?`

**Richtig:**
```kotlin
// Zeigt ein einzelnes Foto als quadratische Karte mit abgerundeten Ecken.
@Composable
fun FotoKarte(uri: Uri, modifier: Modifier = Modifier) { ... }

// Fügt ein neues Foto an das Ende der Liste an.
fun fotoHinzufuegen(uri: Uri) { ... }
```
**Falsch:**
```kotlin
// Diese Funktion fügt ein Foto hinzu indem sie die Liste updated
fun fotoHinzufuegen(uri: Uri) { ... }
```

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

## Stateful / Stateless Trennung

Jeder Screen besteht aus zwei Composables:
- `XyzScreen` — **stateful**: hat ViewModel, sammelt State, startet ActivityResult-Launcher
- `XyzInhalt` — **stateless**: bekommt alles als Parameter, hat `@Preview`, kennt kein ViewModel

```kotlin
// Stateful: Verbindet den ViewModel mit dem UI.
@Composable
fun FotoScreen(viewModel: FotoViewModel = viewModel(), onMenuOeffnen: () -> Unit = {}) {
    val fotos by viewModel.fotos.collectAsState()
    FotoInhalt(fotos = fotos, onMenuOeffnen = onMenuOeffnen)
}

// Stateless: Zeigt den Inhalt — keine ViewModel-Abhängigkeit.
@Composable
fun FotoInhalt(fotos: List<Uri>, onMenuOeffnen: () -> Unit = {}) { ... }
```

`XyzScreen` bekommt **keine** `@Preview` — wegen ViewModel-Abhängigkeit.
`XyzInhalt` bekommt immer mindestens eine `@Preview`.

---

## Scaffold-Standard

Jeder Screen verwendet `Scaffold` mit `CenterAlignedTopAppBar`:

```kotlin
Scaffold(
    topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Titel", color = TextHell) },
            navigationIcon = {
                IconButton(onClick = onMenuOeffnen) {
                    Icon(Icons.Filled.Menu, "Menü öffnen", tint = TextHell)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = OberflächenFarbe
            )
        )
    },
    containerColor = AppHintergrund
) { innenAbstand -> /* Inhalt mit .padding(innenAbstand) */ }
```

---

## Navigations-Vollstaendigkeit

- **Jeder Screen** muss ueber die Seitenleiste (Menu-Button) oder einen Zurueck-Button verlassbar sein
- **Jeder Dialog** braucht eine Schliessen- oder Abbrechen-Option und korrektes `onDismissRequest`
- **Jeder Erstellvorgang** (Ordner erstellen, etc.) braucht eine Abbrechen-Option
- **Jeder ViewModel-Zustand** muss einen Ausweg haben (Zurueck, Zuruecksetzen oder Fehlerbehandlung)
- Wird vom `navigations-pruefung` Agent nach jeder Aufgabe geprueft

## Weitere Konventionen

> Neue Einträge hier einfügen wenn sie während einer Aufgabe erkannt werden.
