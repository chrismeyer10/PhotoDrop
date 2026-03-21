# PhotoDrop

## Pflicht bei jeder Aufgabe

> **Den `aufgaben-koordinator` Agent aufrufen** — er übernimmt den gesamten Workflow automatisch.

Der Koordinator führt diese Schritte aus:

1. `CONVENTIONS.md` und `CLAUDE.md` lesen
2. **Feature-Branch erstellen** — NIEMALS direkt auf `main` arbeiten
   - Branch-Name: `feat/`, `fix/`, `docs/`, `refactor/` + kurzer beschreibender Name
3. Aufgabe umsetzen (Conventions einhalten)
4. **`build-check` Agent** — `./gradlew compileDebugKotlin` muss grün sein
5. **`code-cleanup` Agent** — tote Imports, Duplikate entfernen
6. **`struktur-check` Agent** — wenn neue Dateien entstanden sind
7. Committen, PR erstellen und mergen
8. `main` synchronisieren

## Git-Workflow

```
# 1. Branch anlegen
git checkout main && git pull
git checkout -b feat/mein-feature

# 2. Änderungen committen
git add <dateien>
git commit -m "feat: kurze Beschreibung"

# 3. Push + PR
git push -u origin feat/mein-feature
# → PR über GitHub API (Node.js) erstellen und mergen
```

**Wichtig:** Vor dem Branch-Anlegen immer `git pull` auf main — so gibt es keine Konflikte.

## Projektübersicht

> **TODO**: Beschreibe hier was PhotoDrop ist und kann.

Android-App (Kotlin + Jetpack Compose), die Claude-Agents und -Skills nutzt.

## Architektur

### Agents (`/.claude/agents/`)

Agents sind spezialisierte Claude-Instanzen für komplexe, mehrstufige Aufgaben.
Jeder Agent hat klar definierte Tools und einen eigenen Fokus.

| Agent | Datei | Zweck |
|-------|-------|-------|
| `aufgaben-koordinator` | `aufgaben-koordinator.md` | **Master-Agent** — Einstiegspunkt, orchestriert den kompletten Workflow |
| `build-check` | `build-check.md` | Führt `compileDebugKotlin` aus und meldet/repariert Fehler |
| `code-cleanup` | `code-cleanup.md` | Findet und löscht überflüssigen/redundanten Code |
| `struktur-check` | `struktur-check.md` | Überwacht Datei-/Paketstruktur, erzwingt kleine Komponenten |
| `figma-agent` | `figma-agent.md` | Ruft Figma-Designs ab und wandelt sie in Compose-Code um |
| *Placeholder* | `photo-agent.md` | TODO |

## MCP-Server

| Server | Status | Account |
|--------|--------|---------|
| Figma | ✅ Verbunden | christian.meyer19@gmail.com |

### Skills (`/.claude/skills/`)

Skills sind wiederverwendbare, aufrufbare Aktionen (`/skill-name`).
Sie kapseln wiederkehrende Aufgaben und Standards.

| Skill | Datei | Aufruf |
|-------|-------|--------|
| *Placeholder* | `photo-skill.md` | `/photo-skill` |

### Android-Infrastruktur

```
app/src/main/java/com/example/photodrop/
├── agent/
│   └── AgentService.kt       ← Führt Agents mit Skills als Tools aus
├── skills/
│   ├── Skill.kt              ← Basis-Typ (typealias für Supplier<String>)
│   ├── SkillRegistry.kt      ← Registriert alle aktiven Skills
│   └── GetPhotoInfoSkill.kt  ← Beispiel-Skill (Placeholder)
└── ui/
    ├── theme/                ← Farben, Typografie, Theme
    └── foto/                 ← Foto-Aufnahme-Feature
```

## Neuen Agent anlegen

1. Datei in `.claude/agents/[name].md` erstellen
2. Frontmatter mit `name`, `description`, `model`, `tools` ausfüllen
3. System-Prompt im Body schreiben
4. In `CLAUDE.md` Tabelle eintragen

## Neuen Skill anlegen

1. Datei in `.claude/skills/[name].md` erstellen
2. Kotlin-Klasse in `app/.../skills/[Name]Skill.kt` implementieren
3. In `SkillRegistry.kt` registrieren
4. In `CLAUDE.md` Tabelle eintragen

## Entwicklungs-Konventionen

- Modell: `claude-opus-4-6`
- API-Key: in `local.properties` → `ANTHROPIC_API_KEY=...` (niemals committen)
- Async: Kotlin Coroutines (`Dispatchers.IO` für API-Calls)
- Skill-Felder: `@JvmField` damit Jackson die Properties direkt lesen kann
