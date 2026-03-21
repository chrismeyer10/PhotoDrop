package com.example.photodrop.ui.drive

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// Kommuniziert mit der Google Drive REST API.
// Alle Methoden laufen auf dem IO-Thread und geben null zurück wenn etwas schiefgeht.
object DriveVerbindung {

    private const val DRIVE_API = "https://www.googleapis.com/drive/v3"
    private const val ORDNER_TYP = "application/vnd.google-apps.folder"

    // Sucht nach einem Ordner mit dem gegebenen Namen in Drive.
    // Gibt die Ordner-ID zurück oder null wenn kein Ordner existiert.
    suspend fun ordnerSuchen(token: String, ordnerName: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val abfrage = URLEncoder.encode(
                    "name='$ordnerName' and mimeType='$ORDNER_TYP' and trashed=false",
                    "UTF-8"
                )
                val url = URL("$DRIVE_API/files?q=$abfrage&fields=files(id,name)")
                val verbindung = url.openConnection() as HttpURLConnection
                verbindung.setRequestProperty("Authorization", "Bearer $token")

                val antwort = verbindung.inputStream.bufferedReader().readText()
                val dateien = JSONObject(antwort).getJSONArray("files")
                if (dateien.length() > 0) dateien.getJSONObject(0).getString("id") else null
            } catch (e: Exception) {
                null
            }
        }

    // Erstellt einen neuen Ordner mit dem gegebenen Namen in Google Drive.
    // Gibt die ID des erstellten Ordners zurück oder null bei Fehler.
    suspend fun ordnerErstellen(token: String, ordnerName: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("$DRIVE_API/files")
                val verbindung = url.openConnection() as HttpURLConnection
                verbindung.requestMethod = "POST"
                verbindung.setRequestProperty("Authorization", "Bearer $token")
                verbindung.setRequestProperty("Content-Type", "application/json")
                verbindung.doOutput = true

                val koerper = """{"name":"$ordnerName","mimeType":"$ORDNER_TYP"}"""
                verbindung.outputStream.write(koerper.toByteArray())

                val antwort = verbindung.inputStream.bufferedReader().readText()
                JSONObject(antwort).getString("id")
            } catch (e: Exception) {
                null
            }
        }

    // Stellt sicher dass ein Ordner mit dem gegebenen Namen existiert.
    // Findet einen vorhandenen Ordner oder erstellt einen neuen.
    // Gibt die Ordner-ID zurück oder null bei Fehler.
    suspend fun ordnerSicherstellen(token: String, ordnerName: String): String? {
        return ordnerSuchen(token, ordnerName) ?: ordnerErstellen(token, ordnerName)
    }
}
