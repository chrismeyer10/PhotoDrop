# PhotoDrop

Eine Android-App zum Aufnehmen von Fotos und zur automatischen Synchronisation mit Google Drive.
Die App integriert KI-gestuetzte Funktionen ueber Claude-Agents und -Skills (Anthropic API).

---

## Features

- **Fotos aufnehmen** — Kamera-Intent mit temporaerer URI; aufgenommene Bilder werden sofort in der Galerie angezeigt
- **Foto-Galerie** — Responsive Grid-Ansicht aller aufgenommenen Bilder
- **Google Drive Verbindung** — OAuth 2.0 Login via Google Sign-In; erstellt automatisch einen App-Ordner in Drive
- **Persistente Session** — Drive-Verbindung bleibt nach App-Neustart erhalten (Auto-Reconnect)
- **Linke Navigationsleiste** — Modal Navigation Drawer mit allen Hauptbereichen
- **KI-Agent (Claude)** — AgentService mit Tool-Loop; Skills koennen als Tools registriert werden

---

## Technologie-Stack

| Bereich | Technologie |
|---------|-------------|
| Sprache | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation + Modal Drawer |
| Bilder laden | Coil |
| State | ViewModel + StateFlow + `collectAsState()` |
| Asynchron | Kotlin Coroutines (`Dispatchers.IO`) |
| KI | Anthropic Java SDK (`claude-opus-4-6`) |
| Google Drive | REST API via `HttpURLConnection` (Scope: `drive.file`) |
| Auth | Google Sign-In (`play-services-auth`) |
| Min SDK | 33 (Android 13) |
| Target SDK | 36 |

---

## Setup

### 1. Repository klonen

```bash
git clone https://github.com/chrismeyer10/PhotoDrop.git
cd PhotoDrop
```

### 2. Anthropic API-Key eintragen

Erstelle oder oeffne `local.properties` im Projektstamm und trage deinen Key ein:

```properties
ANTHROPIC_API_KEY=sk-ant-...
```

Der Key wird zur Build-Zeit als `BuildConfig.ANTHROPIC_API_KEY` eingebunden.
`local.properties` ist in `.gitignore` — er wird nie committet.

### 3. Google-Dienste konfigurieren

Lade `google-services.json` aus der Firebase-/Google-Cloud-Konsole herunter
und lege sie unter `app/google-services.json` ab.

### 4. Projekt bauen

```bash
./gradlew assembleDebug
```

---

## Projektstruktur

```
app/src/main/java/com/example/photodrop/
├── MainActivity.kt               ← Einstiegspunkt, startet AppNavigation
│
├── agent/
│   └── AgentService.kt           ← Fuehrt Claude-Agenten mit Skills als Tools aus
│
├── skills/
│   ├── Skill.kt                  ← Typalias: Skill = Supplier<String>
│   ├── SkillRegistry.kt          ← Zentrale Liste aller aktiven Skills
│   └── GetPhotoInfoSkill.kt      ← Beispiel-Skill: Foto-Basisinfos (Stub)
│
└── ui/
    ├── theme/                    ← Farben, Typografie, PhotoDropTheme
    ├── navigation/               ← AppNavigation, NavigationsLeiste, NavigationsZiel
    ├── foto/                     ← FotoAufnahmeScreen, FotoListe, FotoViewModel, ...
    └── drive/                    ← DriveScreen, DriveViewModel, DriveVerbindung
```

---

## Architektur-Hinweise

- **Stateful / Stateless** — jeder Screen besteht aus `XyzScreen` (ViewModel-Zugriff) und `XyzInhalt` (nur Parameter, testbar, mit `@Preview`)
- **Drive-Zustand** — Sealed Interface `NichtVerbunden | Verbindet | Verbunden | Fehler` fuer exhaustive `when`-Ausdruecke
- **Drive REST** — direkte `HttpURLConnection` ohne externe Bibliothek; Scope `drive.file` beschraenkt den Zugriff auf App-eigene Dateien
- **KI-Loop** — `AgentService` nutzt `toolRunner()` aus dem Anthropic SDK; Skills werden als Jackson-annotierte Klassen registriert

---

## Konventionen

Der Quellcode folgt deutschen Bezeichnern und den Regeln aus `CONVENTIONS.md`:
- Alle Klassen-, Funktions- und Variablennamen auf Deutsch
- Jedes oeffentliche Composable hat eine `@Preview`-Funktion
- Kurze deutsche Kommentare ueber jeder oeffentlichen Klasse und Funktion
- Maximale Dateilaenge ~100 Zeilen
