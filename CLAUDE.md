# PhotoDrop

## Pflicht bei jeder Aufgabe

> **Den `aufgaben-koordinator` Agent aufrufen** — er übernimmt den gesamten Workflow automatisch.
> **NIEMALS direkt implementieren ohne den Koordinator zu starten.**

Der Koordinator führt diese Schritte aus:

1. `CONVENTIONS.md` und `CLAUDE.md` lesen
2. **Feature-Branch erstellen** — NIEMALS direkt auf `main` arbeiten
   - Branch-Name: `feat/`, `fix/`, `docs/`, `refactor/`, `chore/` + kurzer beschreibender Name
3. Aufgabe umsetzen — Conventions einhalten (deutsche Bezeichner, @Preview, ~100 Zeilen max)
4. **`build-check` Agent** — `./gradlew compileDebugKotlin` muss grün sein
5. **`code-cleanup` Agent** — tote Imports, Duplikate, veralteter Code entfernen
6. **`struktur-check` Agent** — wenn neue Dateien entstanden sind (prüft auch @Preview)
7. **`docs/state-diagram.mmd` aktualisieren** — wenn Zustände, Screens oder Flows geändert wurden
8. Committen und PR erstellen/mergen via `/github-pr` Skill
9. `main` synchronisieren

## Git-Workflow

```bash
# 1. Branch anlegen
git checkout main && git pull
git checkout -b feat/mein-feature

# 2. Änderungen committen (immer gezielt — nie git add .)
git add <spezifische Dateien>
git commit -m "feat: kurze deutsche Beschreibung"

# 3. Push + PR (via /github-pr Skill)
git push -u origin feat/mein-feature
```

**Wichtig:** Vor dem Branch-Anlegen immer `git pull` auf main.

## Wissensdatenbank

`WISSEN.md` enthält den aktuellen Projektstatus (Feature-Übersicht, Datei-Karte, TODOs).
Wird vom `wissens-agent` erstellt und aktualisiert. Der `aufgaben-koordinator` liest
sie automatisch am Anfang jeder Aufgabe.

## Projektübersicht

PhotoDrop ist eine Android-App (Kotlin + Jetpack Compose) zum Aufnehmen von Fotos
und zur automatischen Synchronisation mit Google Drive.
Die App verwendet Claude-Agents und -Skills für KI-gestützte Funktionen.

## Agents (`/.claude/agents/`)

Agents sind spezialisierte Claude-Instanzen für komplexe, mehrstufige Aufgaben.

| Agent | Zweck | Wann aufrufen |
|-------|-------|---------------|
| `aufgaben-koordinator` | **Master-Agent** — orchestriert den Workflow | Bei jeder neuen Aufgabe zuerst |
| `wissens-agent` | Scannt Projekt und aktualisiert `WISSEN.md` | Nach größeren Änderungen oder wenn WISSEN.md veraltet ist |
| `build-check` | `compileDebugKotlin` ausführen und Fehler reparieren | Nach jeder Code-Änderung, vor Commit |
| `code-cleanup` | Tote Imports, Duplikate, veralteten Code entfernen | Nach Implementierung, vor Struktur-Check |
| `struktur-check` | Paketstruktur, Dateigröße, @Preview-Vollständigkeit prüfen | Nach Aufgaben mit neuen Dateien |
| `figma-agent` | Figma-Designs abrufen und in Compose-Code umwandeln | Wenn ein Design aus Figma implementiert wird |

## Skills (`/.claude/skills/`)

Skills sind wiederverwendbare Muster für häufige Aufgaben.

| Skill | Zweck | Wann aufrufen |
|-------|-------|---------------|
| `/github-pr` | PR erstellen und mergen via GitHub API | Nach `git push` am Ende jeder Aufgabe |
| `/neuer-screen` | Neuen Feature-Screen nach Standardmuster anlegen | Wenn ein neuer Screen gebaut wird |
| `/compose-preview` | Fehlende @Preview-Annotationen hinzufügen | Wenn struktur-check Previews bemängelt |
| `/photo-skill` | Foto nach Aufnahme verarbeiten und speichern | Wenn ein Foto verarbeitet werden soll |
| `/hinweis-text` | Hinweistext für den Nutzer schreiben (kurz, deutsch, kontextgerecht) | Wenn ein Hinweistext in die UI eingebaut werden soll |

## State Diagram (`docs/state-diagram.mmd`)

Zeigt alle App-Zustände: Navigation, Screen-States, DriveViewModel, AgentService.

**Pflege-Regel:** Bei jeder Aufgabe prüfen ob das Diagram noch stimmt und ggf. aktualisieren.
Wann aktualisieren:
- Neuer Screen hinzugefügt
- Neuer Zustand in einem ViewModel
- Navigation geändert
- Neuer Service mit eigenem Zustandsfluss

## Android-Infrastruktur

```
app/src/main/java/com/example/photodrop/
├── agent/
│   └── AgentService.kt            ← Führt Agents mit Skills als Tools aus
├── skills/
│   ├── Skill.kt                   ← Basis-Typ (typealias für Supplier<String>)
│   ├── SkillRegistry.kt           ← Registriert alle aktiven Skills
│   └── GetPhotoInfoSkill.kt       ← Foto-Info-Skill
└── ui/
    ├── theme/                     ← Farben, Typografie, Theme
    ├── navigation/                ← AppNavigation, NavigationsLeiste, NavigationsZiel
    ├── foto/                      ← Foto-Aufnahme-Feature
    └── drive/                     ← Google Drive Verbindungs-Feature
```

## Neuen Agent anlegen

1. Datei in `.claude/agents/[name].md` erstellen
2. Frontmatter: `name`, `description` (klar formuliert — beschreibt WANN aufzurufen), `model`, `tools`
3. System-Prompt: konkreter Ablauf mit Schritten, nicht nur Prinzipien
4. In `CLAUDE.md` Tabelle eintragen (Spalten: Agent, Zweck, Wann aufrufen)

## Neuen Skill anlegen

1. Datei in `.claude/skills/[name].md` erstellen
2. `description` klar formulieren: wann wird dieser Skill verwendet?
3. Konkrete Vorlage/Muster dokumentieren
4. In `CLAUDE.md` Tabelle eintragen
5. Wenn Kotlin-Implementierung nötig: `app/.../skills/[Name]Skill.kt` + `SkillRegistry.kt`

## Konventions-Pflicht

Jede Implementierung hält `CONVENTIONS.md` ein. Die wichtigsten Regeln:
- **Deutsche Bezeichner** für alle selbst geschriebenen Namen
- **@Preview** für jedes öffentliche Composable (Pflicht — wird vom struktur-check geprüft)
- **Stateful/Stateless-Trennung**: `XyzScreen` + `XyzInhalt`
- **~100 Zeilen** pro Datei — größere Dateien aufteilen
- **Kurze deutsche Kommentare** über Klassen und Funktionen

## Entwicklungs-Konventionen

- Modell: `claude-opus-4-6`
- API-Key: in `local.properties` → `ANTHROPIC_API_KEY=...` (niemals committen)
- Async: Kotlin Coroutines (`Dispatchers.IO` für API-Calls und Dateioperationen)
- Skill-Felder: `@JvmField` damit Jackson die Properties direkt lesen kann

## MCP-Server

| Server | Status | Account |
|--------|--------|---------|
| Figma | ✅ Verbunden | christian.meyer19@gmail.com |
