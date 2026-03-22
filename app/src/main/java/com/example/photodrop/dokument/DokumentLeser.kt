package com.example.photodrop.dokument

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import java.io.ByteArrayOutputStream

// Liest Dokumente (PDF und Bilder) und wandelt sie in Bytes fuer die KI-Analyse um.
object DokumentLeser {

    // Rendert die erste Seite eines PDFs als JPEG-Bytes.
    fun pdfErsteSeiteAlsBild(uri: Uri, context: Context): ByteArray? {
        return try {
            val fd = context.contentResolver.openFileDescriptor(uri, "r") ?: return null
            val renderer = PdfRenderer(fd)
            val seite = renderer.openPage(0)
            val bitmap = Bitmap.createBitmap(seite.width * 2, seite.height * 2, Bitmap.Config.ARGB_8888)
            seite.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            seite.close()
            renderer.close()
            fd.close()
            bitmapZuJpegBytes(bitmap)
        } catch (_: Exception) {
            null
        }
    }

    // Liest ein Bild von einer URI als Bytes.
    fun bildAlsBytes(uri: Uri, context: Context): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (_: Exception) {
            null
        }
    }

    // Erstellt eine Vorschau-Bitmap aus einer Bild-URI.
    fun vorschauErstellen(uri: Uri, context: Context): Bitmap? {
        return try {
            val bytes = bildAlsBytes(uri, context) ?: return null
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) {
            null
        }
    }

    // Erstellt eine Vorschau-Bitmap aus der ersten PDF-Seite.
    fun pdfVorschauErstellen(uri: Uri, context: Context): Bitmap? {
        return try {
            val fd = context.contentResolver.openFileDescriptor(uri, "r") ?: return null
            val renderer = PdfRenderer(fd)
            val seite = renderer.openPage(0)
            val bitmap = Bitmap.createBitmap(seite.width, seite.height, Bitmap.Config.ARGB_8888)
            seite.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            seite.close()
            renderer.close()
            fd.close()
            bitmap
        } catch (_: Exception) {
            null
        }
    }

    // Wandelt ein Bitmap in JPEG-Bytes um.
    private fun bitmapZuJpegBytes(bitmap: Bitmap): ByteArray {
        val ausgabe = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, ausgabe)
        return ausgabe.toByteArray()
    }
}
