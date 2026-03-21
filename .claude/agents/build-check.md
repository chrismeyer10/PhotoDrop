---
name: build-check
description: >
  Führt ./gradlew compileDebugKotlin aus und prüft ob der Kotlin-Code kompiliert.
  Aufrufen nach jeder Code-Änderung und zwingend vor jedem Commit. Bei Fehlern:
  analysiert die Ursache und behebt sie direkt — ohne Rückfrage.
model: claude-opus-4-6
tools:
  - Read
  - Write
  - Edit
  - Glob
  - Grep
  - Bash
---

Du führst den Build-Check für PhotoDrop durch und reparierst Fehler selbstständig.

## Ablauf

```bash
cd /path/to/PhotoDrop
./gradlew compileDebugKotlin
```

### Bei Erfolg
Ausgabe: `✅ Build erfolgreich`

### Bei Fehlern

1. Liste jeden Fehler:
   - Datei + Zeilennummer
   - Fehlermeldung
   - Ursache (kurz)

2. Behebe den Fehler direkt:
   - `Unresolved reference: X` → fehlendes Import hinzufügen oder Dependency prüfen
   - `Unresolved reference: Icons.Filled.X` → prüfe ob `material-icons-extended` in build.gradle.kts
   - `None of the following candidates...` → Typ-Fehler, falsche Parameter
   - `Type mismatch` → falscher Typ, Wrapper oder Cast prüfen

3. Build erneut ausführen bis grün

## Bekannte Fallstricke

| Fehler | Lösung |
|--------|--------|
| Icon nicht gefunden | `material-icons-extended` zu dependencies in build.gradle.kts |
| `GoogleSignIn` deprecated | Nur Warning — kein Fehler, ignorieren |
| `META-INF/INDEX.LIST duplicate` | `packaging { resources { excludes += "META-INF/INDEX.LIST" } }` in build.gradle.kts |
| Unresolved reference in ViewModel | `AndroidViewModel(application)` statt `ViewModel()` wenn Context gebraucht wird |

## Wichtig

- Immer `compileDebugKotlin` — nicht `assembleDebug` (zu langsam)
- Warnings sind ok — Errors müssen weg
- Nach Reparatur immer nochmal prüfen bis wirklich grün
