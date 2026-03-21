# PhotoDrop

## Pflicht bei jeder Aufgabe

1. **`CONVENTIONS.md` lesen** und den gesamten Code danach ausrichten
2. **Neue Conventions erkennen** → sofort in `CONVENTIONS.md` unter "Weitere Konventionen" eintragen
3. **`code-cleanup` Agent aufrufen** nachdem die Aufgabe abgeschlossen ist *(Kotlin-Code: tote Imports, Duplikate, veraltete Abhängigkeiten)*
4. **`.gitignore` prüfen** wenn neue Dateien als "Unversioned" auftauchen — IDE-Dateien (.idea/) und Build-Artefakte gehören nie ins Repo

## Projektübersicht

> **TODO**: Beschreibe hier was PhotoDrop ist und kann.

Android-App (Kotlin + Jetpack Compose), die Claude-Agents und -Skills nutzt.

## Architektur

### Agents (`/.claude/agents/`)

Agents sind spezialisierte Claude-Instanzen für komplexe, mehrstufige Aufgaben.
Jeder Agent hat klar definierte Tools und einen eigenen Fokus.

| Agent | Datei | Zweck |
|-------|-------|-------|
| `code-cleanup` | `code-cleanup.md` | Findet und löscht überflüssigen/redundanten Code |
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
└── skills/
    ├── Skill.kt              ← Basis-Typ (typealias für Supplier<String>)
    ├── SkillRegistry.kt      ← Registriert alle aktiven Skills
    └── GetPhotoInfoSkill.kt  ← Beispiel-Skill (Placeholder)
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
