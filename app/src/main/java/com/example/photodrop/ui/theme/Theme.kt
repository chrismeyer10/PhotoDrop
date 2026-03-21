package com.example.photodrop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Definiert das dunkle Farbschema der App.
// Weist den Material3-Rollen die App-Farben zu.
private val PhotoDropFarbschema = darkColorScheme(
    primary = AkzentFarbe,
    onPrimary = TextHell,
    background = AppHintergrund,
    onBackground = TextHell,
    surface = OberflächenFarbe,
    onSurface = TextHell,
)

// Wrapper für das gesamte App-Design.
// Alle Composables sollen in diesem Theme laufen — auch in Previews.
@Composable
fun PhotoDropTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PhotoDropFarbschema,
        typography = Typography,
        content = content
    )
}
