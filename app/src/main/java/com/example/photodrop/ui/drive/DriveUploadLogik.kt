package com.example.photodrop.ui.drive

import android.content.Context
import android.net.Uri
import com.example.photodrop.ui.drive.api.DriveVerbindung
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Kapselt die Foto-Upload-Logik fuer den Schnell-Upload.
class DriveUploadLogik {

    // Laedt ein Foto in den angegebenen Ordner hoch und gibt den Dateinamen zurueck.
    suspend fun fotoHochladen(
        fotoUri: Uri,
        context: Context,
        token: String,
        ordnerId: String
    ): String {
        val bytes = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(fotoUri)?.readBytes()
        } ?: throw Exception("Foto nicht lesbar")
        val dateiname = "foto_${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        }.jpg"
        DriveVerbindung.dateiHochladen(token, ordnerId, dateiname, "image/jpeg", bytes)
        return dateiname
    }

    // Erstellt einen neuen Ordner und gibt seine ID zurueck.
    suspend fun ordnerBestaetigen(token: String, name: String): String? =
        DriveVerbindung.ordnerSicherstellen(token, name)

    // Laedt den Inhalt eines Ordners.
    suspend fun ordnerInhaltLaden(token: String, ordnerId: String) =
        DriveVerbindung.ordnerInhaltLaden(token, ordnerId)
}
