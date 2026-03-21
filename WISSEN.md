# Projekt-Wissensdatenbank — PhotoDrop

> Zuletzt aktualisiert: 2026-03-21 — `8617519`

## Feature-Übersicht

| Feature | Hauptdateien | Status |
|---------|--------------|--------|
| Foto aufnehmen | `FotoAufnahmeScreen`, `FotoViewModel`, `KameraSteuerer`, `KameraHilfe` | Implementiert |
| Foto-Galerie anzeigen | `FotoListe`, `FotoKarte` | Implementiert |
| Google Drive verbinden | `DriveScreen`, `DriveViewModel`, `DriveVerbindung` | Implementiert |
| Persistente Drive-Session | `DriveViewModel.automatischVerbinden()` | Implementiert |
| Linke Navigationsleiste | `AppNavigation`, `NavigationsLeiste`, `NavigationsZiel` | Implementiert |
| KI-Agent (Claude) | `AgentService`, `SkillRegistry` | Grundstruktur vorhanden |
| Foto-Info-Skill | `GetPhotoInfoSkill` | Stub (TODO offen) |

## Datei-Karte

| Datei | Package | Zweck | Zeilen |
|-------|---------|-------|--------|
| `MainActivity.kt` | `photodrop` | App-Einstiegspunkt, startet `AppNavigation` | 25 |
| `AgentService.kt` | `agent` | Fuehrt Claude-Agenten mit Skills als Tools aus | 65 |
| `Skill.kt` | `skills` | Typalias `Skill = Supplier<String>` mit Dokumentation | 29 |
| `SkillRegistry.kt` | `skills` | Zentrale Liste aller aktiven Skills | 16 |
| `GetPhotoInfoSkill.kt` | `skills` | Beispiel-Skill: gibt Foto-Basisinfos zurueck (Stub) | 21 |
| `DriveScreen.kt` | `ui.drive` | Stateful + Stateless UI fuer Drive-Verbindung, 4 Previews | 277 |
| `DriveViewModel.kt` | `ui.drive` | Drive-Zustandsverwaltung + Google Sign-In + Auto-Reconnect | 125 |
| `DriveVerbindung.kt` | `ui.drive` | Drive REST API: Ordner suchen und erstellen | 65 |
| `FotoAufnahmeScreen.kt` | `ui.foto` | Stateful + Stateless UI fuer Fotoaufnahme, 2 Previews | 124 |
| `FotoKarte.kt` | `ui.foto` | Einzelne Foto-Kachel in der Galerie | 60 |
| `FotoListe.kt` | `ui.foto` | Responsive Fotogalerie als Grid | 85 |
| `FotoViewModel.kt` | `ui.foto` | Haelt Liste aufgenommener Fotos als StateFlow | 26 |
| `KameraHilfe.kt` | `ui.foto` | Erstellt temporaere Bild-URI fuer Kamera-Intent | 25 |
| `KameraSteuerer.kt` | `ui.foto` | `rememberLauncherForActivityResult` fuer Kamera | 53 |
| `AppNavigation.kt` | `ui.navigation` | ModalNavigationDrawer + NavHost (2 Routen) | 68 |
| `NavigationsLeiste.kt` | `ui.navigation` | Inhalt der linken Schublade mit allen Zielen | 109 |
| `NavigationsZiel.kt` | `ui.navigation` | Sealed class: typsichere Routen mit Icon und Titel | 20 |
| `Color.kt` | `ui.theme` | App-Farbpalette (dunkel, Akzent = Tuerkis/Gruen) | 13 |
| `Theme.kt` | `ui.theme` | `PhotoDropTheme` — MaterialTheme-Konfiguration | 27 |
| `Type.kt` | `ui.theme` | Typografie-Definitionen | 35 |

## Letzte Git-Aenderungen

```
8617519 feat: Persistente Google Drive Verbindung mit Auto-Reconnect und Logout (#12)
9c96713 fix: Google Services Plugin hinzugefuegt fuer OAuth-Anmeldung (#11)
90e5f07 refactor: Agent-System ueberarbeitet, Skills und Previews hinzugefuegt (#10)
2112dfa Merge pull request #9 from chrismeyer10/feat/google-drive-integration
61a0f73 feat: Google Drive Integration mit linker Navigation
3ab95a5 feat: aufgaben-koordinator als Master-Agent
33bb88d feat: Branch-Pflicht und struktur-check Agent
f22967c docs: Kurze deutsche Kommentare ueber alle Klassen und Funktionen
cc924fa Merge pull request #5 from chrismeyer10/feat/composable-previews
dacca2f refactor: State Hoisting in FotoAufnahmeScreen
```

## Offene TODOs

| Datei | Zeile | Beschreibung |
|-------|-------|--------------|
| `GetPhotoInfoSkill.kt` | 18 | Echte Implementierung — z.B. ContentResolver, ExifInterface, etc. |

## Architektur-Notizen

- **Muster: Stateful + Stateless Composables** — jeder Screen hat `XyzScreen` (ViewModel-Zugriff) und `XyzInhalt` (nur Parameter, testbar, Preview-faehig)
- **State: StateFlow + collectAsState()** — ViewModels halten MutableStateFlow, Screens beobachten via `collectAsState()`
- **Navigation: ModalNavigationDrawer** — linke Schublade oeffnet sich per Hamburger-Icon; DriveViewModel wird auf Activity-Ebene in `AppNavigation` erstellt damit er Navigation ueberlebt
- **Drive-Zustand: Sealed Interface** — `NichtVerbunden | Verbindet | Verbunden(kontoName, ordnerId) | Fehler(meldung)` sichert exhaustive when-Ausdruecke
- **KI-Integration: Anthropic Java SDK** — `AgentService` nutzt `toolRunner()` fuer den agentischen Loop; Skills werden als Jackson-annotierte Klassen registriert (`@JsonClassDescription`, `@JvmField`)
- **Konventionen:** Deutsche Bezeichner, @Preview auf jedem public Composable, max ~100 Zeilen pro Datei, kurze deutsche Kommentare ueber Klassen
- **API-Key:** in `local.properties` als `ANTHROPIC_API_KEY` (nie committen, in .gitignore)
- **Modell:** `claude-opus-4-6` (fest in `AgentService` eingetragen)
- **Drive REST:** direkte `HttpURLConnection` ohne Bibliothek; Scope: `drive.file` (nur App-eigene Dateien)
