package com.example.photodrop.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// Alle Navigationsziele der App als typsichere Objekte.
// Jedes Ziel kennt seine Route, seinen Titel und sein Icon fuer die Seitenleiste.
sealed class NavigationsZiel(
    val route: String,
    val titel: String,
    val icon: ImageVector
) {
    // Screen fuer Dokument-Archivierung mit KI-Analyse und Drive-Upload
    object Archiv : NavigationsZiel("archiv", "Scannen oder auswählen", Icons.Filled.Archive)

    // Screen fuer die Google Drive Verbindung
    object GoogleDrive : NavigationsZiel("drive", "Google Drive", Icons.Filled.CloudSync)

    // Screen fuer API-Schluessel und App-Einstellungen
    object Einstellungen : NavigationsZiel("einstellungen", "Einstellungen", Icons.Filled.Settings)
}
