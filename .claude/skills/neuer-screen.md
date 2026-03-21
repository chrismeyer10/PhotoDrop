---
name: neuer-screen
description: >
  Erstellt einen neuen Android-Screen nach dem PhotoDrop-Standardmuster: ViewModel,
  stateful Screen-Composable, stateless Inhalt-Composable mit @Preview, Navigation-
  Eintrag. Aufrufen wenn ein neues Feature mit eigenem Screen gebaut werden soll.
---

# Neuen Android-Screen anlegen

Erstellt alle notwendigen Dateien für einen neuen Feature-Screen.

## Eingabe

- Feature-Name (z.B. `Einstellungen`, `Galerie`, `Upload`)
- Route (z.B. `einstellungen`, `galerie`)
- Icon (aus `Icons.Filled.*`)

## Zu erstellende Dateien

### 1. ViewModel — `ui/[feature]/[Feature]ViewModel.kt`

```kotlin
package com.example.photodrop.ui.[feature]

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Verwaltet den Zustand des [Feature]-Screens.
class [Feature]ViewModel : ViewModel() {
    // TODO: Zustand definieren
}
```

### 2. Screen — `ui/[feature]/[Feature]Screen.kt`

```kotlin
package com.example.photodrop.ui.[feature]

// Stateful: Verbindet den ViewModel mit dem UI.
@Composable
fun [Feature]Screen(
    viewModel: [Feature]ViewModel = viewModel(),
    onMenuOeffnen: () -> Unit = {}
) {
    // Zustand beobachten
    [Feature]Inhalt(onMenuOeffnen = onMenuOeffnen)
}

// Stateless: Zeigt den Screen-Inhalt.
// Bekommt alles was es braucht als Parameter — keine ViewModel-Abhängigkeit.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun [Feature]Inhalt(
    onMenuOeffnen: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("[Feature-Titel]", color = TextHell) },
                navigationIcon = {
                    IconButton(onClick = onMenuOeffnen) {
                        Icon(Icons.Filled.Menu, "Menü öffnen", tint = TextHell)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = OberflächenFarbe
                )
            )
        },
        containerColor = AppHintergrund
    ) { innenAbstand ->
        // TODO: Inhalt hier
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A, name = "[Feature]")
@Composable
private fun [Feature]InhaltVorschau() {
    PhotoDropTheme {
        [Feature]Inhalt()
    }
}
```

### 3. Navigation erweitern — `ui/navigation/NavigationsZiel.kt`

```kotlin
object [Feature] : NavigationsZiel("[route]", "[Titel]", Icons.Filled.[Icon])
```

### 4. NavHost ergänzen — `ui/navigation/AppNavigation.kt`

```kotlin
composable(NavigationsZiel.[Feature].route) {
    [Feature]Screen(onMenuOeffnen = { scope.launch { schubladenZustand.open() } })
}
```

### 5. Seitenleiste ergänzen — `ui/navigation/NavigationsLeiste.kt`

```kotlin
val ziele = listOf(
    NavigationsZiel.FotoAufnahme,
    NavigationsZiel.GoogleDrive,
    NavigationsZiel.[Feature]  // hinzufügen
)
```

## Checkliste nach dem Erstellen

- [ ] ViewModel hat keine Compose-Imports
- [ ] Screen hat Stateful/Stateless-Trennung
- [ ] `[Feature]Inhalt` hat mindestens eine `@Preview`
- [ ] Navigation-Ziel in NavigationsZiel.kt eingetragen
- [ ] NavHost-Route eingetragen
- [ ] Seitenleiste enthält den neuen Eintrag
- [ ] Build-Check grün
