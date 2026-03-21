package com.example.photodrop.ui.foto

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

fun dateiUriErstellen(context: Context): Uri {
    val datei = File.createTempFile("foto_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.anbieter", datei)
}

fun hatKameraErlaubnis(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
