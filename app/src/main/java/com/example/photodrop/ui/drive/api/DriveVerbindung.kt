package com.example.photodrop.ui.drive.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// Kommuniziert mit der Google Drive REST API.
// Alle Methoden laufen auf dem IO-Thread.
object DriveVerbindung {

    private const val DRIVE_API = "https://www.googleapis.com/drive/v3"
    private const val ORDNER_TYP = "application/vnd.google-apps.folder"

    // Laedt alle Ordner im Drive-Root des Nutzers.
    suspend fun ordnerListeLaden(token: String): List<DriveOrdner> =
        withContext(Dispatchers.IO) {
            try {
                val abfrage = URLEncoder.encode(
                    "mimeType='$ORDNER_TYP' and 'root' in parents and trashed=false", "UTF-8"
                )
                val url = URL("$DRIVE_API/files?q=$abfrage&fields=files(id,name,modifiedTime)&orderBy=modifiedTime+desc")
                val verbindung = url.openConnection() as HttpURLConnection
                verbindung.setRequestProperty("Authorization", "Bearer $token")
                val antwort = verbindung.inputStream.bufferedReader().readText()
                val dateiArray = JSONObject(antwort).getJSONArray("files")
                (0 until dateiArray.length()).map { i ->
                    val obj = dateiArray.getJSONObject(i)
                    DriveOrdner(id = obj.getString("id"), name = obj.getString("name"))
                }
            } catch (e: Exception) {
                emptyList()
            }
        }

    // Sucht nach einem Ordner mit dem gegebenen Namen in Drive.
    suspend fun ordnerSuchen(token: String, ordnerName: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val abfrage = URLEncoder.encode(
                    "name='$ordnerName' and mimeType='$ORDNER_TYP' and trashed=false", "UTF-8"
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

    // Erstellt einen neuen Ordner mit dem gegebenen Namen.
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

    // Stellt sicher dass ein Ordner existiert — sucht oder erstellt.
    suspend fun ordnerSicherstellen(token: String, ordnerName: String): String? {
        return ordnerSuchen(token, ordnerName) ?: ordnerErstellen(token, ordnerName)
    }

    // Delegiert Datei-Upload an DriveUpload.
    suspend fun dateiHochladen(
        token: String, ordnerId: String, dateiName: String, mimeType: String, inhalt: ByteArray
    ): String = DriveUpload.dateiHochladen(token, ordnerId, dateiName, mimeType, inhalt)

    // Delegiert Unterordner-Sicherstellung an DriveUpload.
    suspend fun unterordnerSicherstellen(
        token: String, elternId: String, name: String
    ): String = DriveUpload.unterordnerSicherstellen(token, elternId, name)

    // Stellt einen mehrstufigen Pfad sicher (z.B. "Rechnungen/2026").
    suspend fun pfadSicherstellen(token: String, wurzelId: String, pfad: String): String {
        val teile = pfad.split("/").map { it.trim() }.filter { it.isNotBlank() }
        var aktuelleId = wurzelId
        for (teil in teile) {
            aktuelleId = DriveUpload.unterordnerSicherstellen(token, aktuelleId, teil)
        }
        return aktuelleId
    }

    // Laedt den Inhalt eines Drive-Ordners (alle Dateien direkt im Ordner).
    // pageSize=1000 stellt sicher dass auch groessere Ordner vollstaendig geladen werden.
    suspend fun ordnerInhaltLaden(token: String, ordnerId: String): List<DriveOrdnerDatei> =
        withContext(Dispatchers.IO) {
            try {
                val abfrage = URLEncoder.encode(
                    "'$ordnerId' in parents and trashed=false", "UTF-8"
                )
                val felder = URLEncoder.encode("files(id,name,mimeType,size,modifiedTime)", "UTF-8")
                val url = URL(
                    "$DRIVE_API/files?q=$abfrage&fields=$felder&pageSize=1000&orderBy=modifiedTime+desc"
                )
                val verbindung = url.openConnection() as HttpURLConnection
                verbindung.setRequestProperty("Authorization", "Bearer $token")
                val antwort = verbindung.inputStream.bufferedReader().readText()
                val json = JSONObject(antwort)
                val dateiArray = if (json.has("files")) json.getJSONArray("files")
                    else return@withContext emptyList()
                (0 until dateiArray.length()).map { i ->
                    val obj = dateiArray.getJSONObject(i)
                    DriveOrdnerDatei(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        mimeType = obj.optString("mimeType", "application/octet-stream"),
                        groesse = if (obj.has("size")) obj.getLong("size") else null,
                        geaendertAm = if (obj.has("modifiedTime")) obj.getString("modifiedTime") else null
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
}
