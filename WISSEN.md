# Projekt-Wissensdatenbank -- PhotoDrop

> Zuletzt aktualisiert: 2026-03-22

## Feature-Uebersicht

| Feature | Hauptdateien | Status |
|---------|--------------|--------|
| Foto aufnehmen | `FotoAufnahmeScreen`, `FotoViewModel`, `kamera/*` | Implementiert |
| Foto-Galerie anzeigen | `galerie/FotoListe`, `galerie/FotoKarte` | Implementiert |
| KI-Foto-Analyse | `analyse/FotoAnalyseDialog`, `analyse/FotoAnalyseViewModel` | Implementiert |
| Google Drive verbinden | `DriveScreen`, `DriveViewModel`, `api/DriveVerbindung` | Implementiert |
| Drive-Anmeldung | `anmeldung/DriveAnmeldung` | Implementiert |
| Drive-Zustaende | `zustand/DriveZustand`, `zustand/*Inhalt` | Implementiert |
| Ordner-Einstellungen | `OrdnerEinstellungen` | Implementiert |
| Persistente Drive-Session | `DriveViewModel.automatischVerbinden()` | Implementiert |
| Linke Navigationsleiste | `AppNavigation`, `NavigationsLeiste`, `NavigationsZiel` | Implementiert |
| KI-Agent (Claude) | `AgentService`, `SkillRegistry` | Eingebunden via FotoAnalyse |
| Foto-Info-Skill | `GetPhotoInfoSkill` | Implementiert |

## Paketstruktur

```
app/src/main/java/com/example/photodrop/
├── MainActivity.kt
├── agent/
│   └── AgentService.kt
├── skills/
│   ├── Skill.kt
│   ├── SkillRegistry.kt
│   └── GetPhotoInfoSkill.kt
└── ui/
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   └── Type.kt
    ├── navigation/
    │   ├── AppNavigation.kt
    │   ├── NavigationsLeiste.kt
    │   └── NavigationsZiel.kt
    ├── foto/
    │   ├── FotoAufnahmeScreen.kt      (Stateful + Stateless)
    │   ├── FotoAufnahmeVorschau.kt    (Previews)
    │   ├── FotoViewModel.kt
    │   ├── OrdnerFehltDialog.kt
    │   ├── analyse/
    │   │   ├── FotoAnalyseDialog.kt   (KI-Analyse Dialog)
    │   │   ├── FotoAnalyseViewModel.kt
    │   │   └── FotoAnalyseZustand.kt
    │   ├── kamera/
    │   │   ├── KameraAusloeser.kt     (FAB-Button)
    │   │   ├── KameraHilfe.kt         (Datei-URI, Erlaubnis)
    │   │   └── KameraSteuerer.kt      (Composable Launcher)
    │   └── galerie/
    │       ├── FotoKarte.kt           (Einzelne Foto-Kachel, klickbar)
    │       ├── FotoListe.kt           (Grid-Ansicht)
    │       └── FotoListeLeerzustand.kt
    └── drive/
        ├── DriveScreen.kt             (Stateful + Stateless)
        ├── DriveScreenVorschau.kt     (Previews)
        ├── DriveViewModel.kt
        ├── OrdnerEinstellungen.kt     (SharedPreferences)
        ├── anmeldung/
        │   └── DriveAnmeldung.kt      (Google Sign-In)
        ├── api/
        │   ├── DriveOrdner.kt         (Datenmodell)
        │   ├── DriveOrdnerDatei.kt    (Datenmodell)
        │   └── DriveVerbindung.kt     (REST API)
        └── zustand/
            ├── DriveZustand.kt        (Sealed Interface)
            ├── ZustandInhaltAuswaehlen.kt
            ├── NichtVerbundenInhalt.kt
            ├── LadeInhalt.kt
            ├── FehlerInhalt.kt
            ├── OrdnerAuswaehlenInhalt.kt
            ├── OrdnerBenennenInhalt.kt
            ├── OrdnerInhaltInhalt.kt
            └── VerbundenAnimiertInhalt.kt
```

## State Diagrams

| Diagram | Datei | Beschreibung |
|---------|-------|--------------|
| Gesamt | `docs/state-diagram.mmd` | App-Navigation, alle Zustaende |
| Foto | `docs/foto-state-diagram.mmd` | Kamera, Galerie, ViewModel |
| Drive | `docs/drive-state-diagram.mmd` | Anmeldung, API, Zustandsfluss |
| Navigation | `docs/navigation-state-diagram.mmd` | Seitenleiste, Routen |
| Agent | `docs/agent-state-diagram.mmd` | AgentService, Skills |

## Offene TODOs

Keine offenen TODOs.

## Architektur-Notizen

- **Modulare Paketstruktur:** Jedes Feature hat Unter-Packages fuer API/Daten, Zustaende und UI
- **Muster: Stateful + Stateless Composables** -- `XyzScreen` + `XyzInhalt`
- **State: StateFlow + collectAsState()** -- ViewModels halten MutableStateFlow
- **Navigation: ModalNavigationDrawer** -- DriveViewModel auf Activity-Ebene
- **Drive-Zustand: Sealed Interface** -- exhaustive when-Ausdruecke
- **Anmelde-Logik: DriveAnmeldung** -- Singleton kapselt Google Sign-In
- **Ordner-Persistenz: OrdnerEinstellungen** -- SharedPreferences-Wrapper
- **KI-Integration: Anthropic Java SDK** -- AgentService mit ToolRunner, eingebunden via FotoAnalyseDialog
- **FotoKarte klickbar** -- Oeffnet KI-Analyse-Dialog bei Tap auf ein Foto
