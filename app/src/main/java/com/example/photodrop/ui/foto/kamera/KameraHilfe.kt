package com.example.photodrop.ui.foto.kamera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

// Erstellt eine leere temporaere Datei und gibt ihre Adresse zurueck.
// Die Kamera schreibt das Foto spaeter in diese Datei.
fun dateiUriErstellen(context: Context): Uri {
    val datei = File.createTempFile("foto_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.anbieter", datei)
}

// Prueft ob die App bereits die Erlaubnis hat, die Kamera zu benutzen.
fun hatKameraErlaubnis(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
