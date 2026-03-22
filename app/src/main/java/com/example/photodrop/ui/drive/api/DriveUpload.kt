package com.example.photodrop.ui.drive.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// Spezialisierte Upload- und Unterordner-Operationen fuer Google Drive.
object DriveUpload {

    private const val DRIVE_API = "https://www.googleapis.com/drive/v3"
    private const val ORDNER_TYP = "application/vnd.google-apps.folder"

    // Laedt eine Datei per Multipart-Upload in einen Drive-Ordner hoch.
    suspend fun dateiHochladen(
        token: String, ordnerId: String, dateiName: String, mimeType: String, inhalt: ByteArray
    ): String = withContext(Dispatchers.IO) {
        val grenze = "---photodrop_grenze_${System.currentTimeMillis()}"
        val metadaten = """{"name":"$dateiName","parents":["$ordnerId"]}"""
        val koerper = multipartKoerperBauen(grenze, metadaten, mimeType, inhalt)
        val url = URL("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
        val verbindung = url.openConnection() as HttpURLConnection
        verbindung.requestMethod = "POST"
        verbindung.setRequestProperty("Authorization", "Bearer $token")
        verbindung.setRequestProperty("Content-Type", "multipart/related; boundary=$grenze")
        verbindung.doOutput = true
        verbindung.outputStream.write(koerper)
        JSONObject(verbindung.inputStream.bufferedReader().readText()).getString("id")
    }

    // Findet oder erstellt einen Unterordner in einem Elternordner.
    suspend fun unterordnerSicherstellen(
        token: String, elternId: String, name: String
    ): String = withContext(Dispatchers.IO) {
        val abfrage = URLEncoder.encode(
            "name='$name' and mimeType='$ORDNER_TYP' and '$elternId' in parents and trashed=false", "UTF-8"
        )
        val suchUrl = URL("$DRIVE_API/files?q=$abfrage&fields=files(id)")
        val suchVerbindung = suchUrl.openConnection() as HttpURLConnection
        suchVerbindung.setRequestProperty("Authorization", "Bearer $token")
        val dateien = JSONObject(suchVerbindung.inputStream.bufferedReader().readText()).getJSONArray("files")
        if (dateien.length() > 0) {
            dateien.getJSONObject(0).getString("id")
        } else {
            unterordnerErstellen(token, elternId, name)
        }
    }

    // Erstellt einen Unterordner in einem Elternordner.
    private suspend fun unterordnerErstellen(
        token: String, elternId: String, name: String
    ): String = withContext(Dispatchers.IO) {
        val url = URL("$DRIVE_API/files")
        val verbindung = url.openConnection() as HttpURLConnection
        verbindung.requestMethod = "POST"
        verbindung.setRequestProperty("Authorization", "Bearer $token")
        verbindung.setRequestProperty("Content-Type", "application/json")
        verbindung.doOutput = true
        val koerper = """{"name":"$name","mimeType":"$ORDNER_TYP","parents":["$elternId"]}"""
        verbindung.outputStream.write(koerper.toByteArray())
        JSONObject(verbindung.inputStream.bufferedReader().readText()).getString("id")
    }

    // Baut den Multipart-Body fuer den Drive-Upload.
    private fun multipartKoerperBauen(
        grenze: String, metadaten: String, mimeType: String, inhalt: ByteArray
    ): ByteArray {
        val ausgabe = java.io.ByteArrayOutputStream()
        val nl = "\r\n"
        ausgabe.write("--$grenze${nl}Content-Type: application/json$nl$nl$metadaten$nl".toByteArray())
        ausgabe.write("--$grenze${nl}Content-Type: $mimeType$nl$nl".toByteArray())
        ausgabe.write(inhalt)
        ausgabe.write("$nl--$grenze--$nl".toByteArray())
        return ausgabe.toByteArray()
    }
}
