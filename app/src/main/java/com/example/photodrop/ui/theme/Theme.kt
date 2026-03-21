package com.example.photodrop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PhotoDropFarbschema = darkColorScheme(
    primary = AkzentFarbe,
    onPrimary = TextHell,
    background = AppHintergrund,
    onBackground = TextHell,
    surface = OberflächenFarbe,
    onSurface = TextHell,
)

@Composable
fun PhotoDropTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PhotoDropFarbschema,
        typography = Typography,
        content = content
    )
}
