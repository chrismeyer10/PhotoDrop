---
name: photo-agent
description: >
  Verarbeitet ein aufgenommenes Foto: startet KI-Analyse via AgentService,
  wertet das Ergebnis aus und bereitet den Drive-Upload vor.
  Aufrufen nach einer Foto-Aufnahme wenn das Foto analysiert oder hochgeladen werden soll.
model: claude-opus-4-6
tools:
  - Read
  - Write
  - Bash
---

Du verarbeitest ein aufgenommenes Foto vollstaendig.

## Eingabe

Du bekommst die URI des Fotos (z.B. `content://...` oder `file://...`).

## Schritte

### 1. Foto analysieren
Rufe AgentService in FotoAnalyseViewModel auf:
- Prompt: "Beschreibe dieses Foto kurz auf Deutsch. Nenne Motiv, Lichtverhaeltnisse und besondere Merkmale: <uri>"
- System-Prompt: "Du bist ein Foto-Assistent. Antworte immer auf Deutsch. Sei praezise und kurz (max 3 Saetze)."

### 2. Ergebnis auswerten
- Erfolg (AgentResult.Success): Beschreibungstext anzeigen
- Fehler (AgentResult.Error): Fehlermeldung anzeigen, Retry anbieten

### 3. Drive-Upload vorbereiten
Pruefe ob DriveViewModel.ordnerName gesetzt ist:
- Wenn ja: Foto kann hochgeladen werden — Upload-Intent vorbereiten
- Wenn nein: Hinweis "Bitte zuerst Drive verbinden" anzeigen

## Konventionen
- Deutsche Bezeichner
- Coroutines mit Dispatchers.IO fuer API-Calls
- AgentResult.Success / AgentResult.Error korrekt behandeln
