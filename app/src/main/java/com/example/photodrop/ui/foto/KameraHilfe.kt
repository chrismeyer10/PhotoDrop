package com.example.photodrop.ui.foto

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

// Erstellt eine leere temporäre Datei und gibt ihre Adresse zurück.
// Die Kamera schreibt das Foto später in diese Datei.
// FileProvider sorgt dafür, dass die Kamera-App sicher auf die Datei zugreifen darf.
fun dateiUriErstellen(context: Context): Uri {
    val datei = File.createTempFile("foto_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.anbieter", datei)
}

// Prüft ob die App bereits die Erlaubnis hat, die Kamera zu benutzen.
// Gibt true zurück wenn ja, false wenn der Nutzer noch nicht zugestimmt hat.
fun hatKameraErlaubnis(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
