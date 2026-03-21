---
name: photo-skill
description: >
  Verarbeitet ein Foto nach der Aufnahme: erstellt eine Vorschau, speichert es
  lokal im internen Speicher und stellt die URI bereit. Aufrufen wenn ein neues
  Foto aufgenommen wurde und verarbeitet werden soll.
---

# Photo-Verarbeitung

Verarbeitet ein aufgenommenes Foto und bereitet es für die Anzeige und den Upload vor.

## Eingabe

- `uri: Uri` — URI des aufgenommenen Fotos (von der Kamera-App geliefert)

## Ausgabe

- `Uri` — gespeicherte lokale URI (im internen App-Speicher)
- `null` — wenn die Verarbeitung fehlschlug

## Kotlin-Implementierung

Implementiert in: `app/src/main/java/com/example/photodrop/skills/GetPhotoInfoSkill.kt`

## Verwendung im ViewModel

```kotlin
val fotoUri = viewModelScope.launch(Dispatchers.IO) {
    // Foto in internen Speicher kopieren
    context.contentResolver.openInputStream(uri)?.use { input ->
        val datei = File(context.filesDir, "foto_${System.currentTimeMillis()}.jpg")
        datei.outputStream().use { output -> input.copyTo(output) }
        Uri.fromFile(datei)
    }
}
```

## Konventionen

- Immer auf `Dispatchers.IO` ausführen — nie auf dem Main-Thread
- Dateinamen mit Timestamp — vermeidet Kollisionen
- Fehler → `null` zurückgeben, nie Exception werfen
