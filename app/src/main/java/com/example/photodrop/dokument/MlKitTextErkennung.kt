package com.example.photodrop.dokument

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// Erkennt Text in Bildern mit Google ML Kit (on-device, kostenlos).
object MlKitTextErkennung {

    // Extrahiert Text aus einem Bild per URI.
    suspend fun textErkennen(uri: Uri, context: Context): String {
        return withContext(Dispatchers.IO) {
            val bild = InputImage.fromFilePath(context, uri)
            val erkenner = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val ergebnis = erkenner.process(bild).await()
            ergebnis.text
        }
    }

    // Extrahiert Text aus Bild-Bytes.
    suspend fun textAusBytesErkennen(bytes: ByteArray, context: Context): String {
        return withContext(Dispatchers.IO) {
            val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: throw Exception("Bild konnte nicht dekodiert werden")
            val bild = InputImage.fromBitmap(bitmap, 0)
            val erkenner = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val ergebnis = erkenner.process(bild).await()
            ergebnis.text
        }
    }

    // Extrahiert Text direkt aus einem Bitmap (z.B. gerenderter PDF-Seite).
    suspend fun textAusBitmapErkennen(bitmap: android.graphics.Bitmap): String {
        return withContext(Dispatchers.IO) {
            val bild = InputImage.fromBitmap(bitmap, 0)
            val erkenner = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val ergebnis = erkenner.process(bild).await()
            ergebnis.text
        }
    }
}
