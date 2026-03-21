package com.example.photodrop.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.ui.graphics.vector.ImageVector

// Alle Navigationsziele der App als typsichere Objekte.
// Jedes Ziel kennt seine Route, seinen Titel und sein Icon für die Seitenleiste.
sealed class NavigationsZiel(
    val route: String,
    val titel: String,
    val icon: ImageVector
) {
    // Haupt-Screen für Fotoaufnahmen
    object FotoAufnahme : NavigationsZiel("fotos", "Fotos", Icons.Filled.CameraAlt)

    // Screen für die Google Drive Verbindung
    object GoogleDrive : NavigationsZiel("drive", "Google Drive", Icons.Filled.CloudSync)
}
