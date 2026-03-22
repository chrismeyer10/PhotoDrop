package com.example.photodrop.ui.drive.api

// Einzelner Eintrag im Google Drive Ordner (Datei oder Unterordner).
data class DriveOrdnerDatei(
    val id: String,
    val name: String,
    val mimeType: String,
    val groesse: Long?,
    val geaendertAm: String?
) {
    // Gibt an ob dieser Eintrag ein Unterordner ist.
    val istOrdner: Boolean get() = mimeType == "application/vnd.google-apps.folder"

    // Gibt an ob dieser Eintrag ein Bild ist.
    val istBild: Boolean get() = mimeType.startsWith("image/")

    // Gibt das passende Icon fuer diesen Dateityp zurueck.
    val icon: String get() = when {
        istOrdner -> "\uD83D\uDCC1"
        istBild -> "\uD83D\uDCF7"
        else -> "\uD83D\uDCC4"
    }

    // Gibt die formatierte Dateigroesse zurueck (z.B. "1.2 MB").
    val groesseFormatiert: String get() {
        val bytes = groesse ?: return ""
        return when {
            bytes < 1_024 -> "${bytes}B"
            bytes < 1_048_576 -> "${"%.1f".format(bytes / 1_024.0)}KB"
            else -> "${"%.1f".format(bytes / 1_048_576.0)}MB"
        }
    }
}
