---
name: hinweis-text
description: >
  Wird aufgerufen wenn Claude einen Hinweistext für den Nutzer schreiben will.
  Enthält Regeln und Beispiele wie gute Hinweistexte für PhotoDrop aussehen.
  Aufrufen bevor ein Hinweistext in die UI eingebaut wird, um den besten Text zu finden.
---

# Hinweistexte für PhotoDrop schreiben

Dieser Skill definiert wie gute Hinweistexte in der PhotoDrop-App aussehen.
Lies diese Regeln, denke über den Kontext nach, und wähle den passendsten Text.

## Regeln

1. **Kurz** — maximal 1–2 Sätze. Kein Fließtext.
2. **Deutsch** — immer auf Deutsch, keine englischen Floskeln.
3. **Konkret** — bezieht sich auf die App und den aktuellen Kontext, nie generisch.
4. **Ohne Ausrufezeichen** — ruhiger Ton, nicht aufdringlich.
5. **Kein "Bitte"** — direkte Sprache, nicht unterwürfig.
6. **Kein "Sie"** — immer "du" (informal).
7. **Kein Marketing** — keine Phrasen wie "Entdecke jetzt" oder "Starte durch".
8. **Kontext-passend** — wenn etwas leer ist, erkläre kurz warum und was zu tun ist.

## Struktur eines guten Hinweistexts

```
[Was der aktuelle Zustand ist] + [Was der Nutzer tun kann oder was als nächstes passiert]
```

## Beispiele

### Leerer Ordner in Drive
Schlecht: "Dieser Ordner ist noch leer. Füge Fotos hinzu!"
Gut: "Noch keine Fotos — mach dein erstes Foto und es landet automatisch hier."

### Kein Ordner ausgewählt
Schlecht: "Bitte wähle einen Ordner aus."
Gut: "Kein Ordner gewählt — verbinde Drive um Fotos automatisch zu sichern."

### Laden dauert lange
Schlecht: "Bitte warte..."
Gut: "Drive lädt gerade den Ordnerinhalt."

### Verbindung verloren
Schlecht: "Ein Fehler ist aufgetreten."
Gut: "Drive ist nicht erreichbar — prüf deine Verbindung."

### Keine Fotos aufgenommen
Schlecht: "Noch keine Fotos vorhanden."
Gut: "Noch kein Foto — tippe unten auf den Auslöser."

## Entscheidungsbaum

Bevor du einen Hinweistext schreibst, beantworte:
1. Was sieht der Nutzer gerade? (Zustand)
2. Was kann oder soll er als nächstes tun?
3. Ist der Text in max. 2 Sätzen erklärbar?

Wenn nein zu 3: kürze so lange bis es passt.

## Für leere Terminal-Ansicht (OrdnerInhaltLeer)

Passender Text: "Noch keine Dateien im Ordner — mach dein erstes Foto."
