package com.example.photodrop.agent

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

// Fuehrt KI-Analysen mit dem OpenAI GPT-4o-mini Modell durch.
class GptMiniService(private val apiKey: String) : KiDienst {

    private val httpClient = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val endpunkt = "https://api.openai.com/v1/chat/completions"

    // Fuehrt eine Analyse mit optionalem Bild aus und gibt das Ergebnis zurueck.
    override suspend fun analysieren(
        prompt: String,
        systemPrompt: String?,
        bild: ByteArray?,
        bildMimeType: String?
    ): AgentResult = withContext(Dispatchers.IO) {
        try {
            val body = anfrageBodyBauen(prompt, systemPrompt, bild, bildMimeType)
            val request = Request.Builder()
                .url(endpunkt)
                .header("Authorization", "Bearer $apiKey")
                .post(body.toString().toRequestBody(jsonMediaType))
                .build()
            val response = httpClient.newCall(request).execute()
            val antwortText = response.body?.string() ?: return@withContext AgentResult.Error(
                "Leere Antwort", Exception("Leere Antwort")
            )
            antwortVerarbeiten(response.code, antwortText)
        } catch (e: Exception) {
            AgentResult.Error(e.message ?: "Unbekannter Fehler", e)
        }
    }

    // Baut den JSON-Request-Body fuer die OpenAI API.
    private fun anfrageBodyBauen(
        prompt: String,
        systemPrompt: String?,
        bild: ByteArray?,
        bildMimeType: String?
    ): JSONObject {
        val nachrichten = JSONArray()
        systemPrompt?.let {
            nachrichten.put(JSONObject().put("role", "system").put("content", it))
        }
        val inhalt = if (bild != null) {
            bildNachrichtBauen(prompt, bild, bildMimeType ?: "image/jpeg")
        } else {
            JSONArray().put(JSONObject().put("type", "text").put("text", prompt))
        }
        nachrichten.put(JSONObject().put("role", "user").put("content", inhalt))
        return JSONObject()
            .put("model", KiAnbieter.GptMini.modellId)
            .put("max_tokens", 4096)
            .put("messages", nachrichten)
    }

    // Baut den Inhalt fuer eine multimodale Nachricht mit Bild und Text.
    private fun bildNachrichtBauen(
        text: String,
        bild: ByteArray,
        mimeType: String
    ): JSONArray {
        val base64 = Base64.encodeToString(bild, Base64.NO_WRAP)
        val bildUrl = "data:$mimeType;base64,$base64"
        val bildTeil = JSONObject()
            .put("type", "image_url")
            .put("image_url", JSONObject().put("url", bildUrl))
        val textTeil = JSONObject().put("type", "text").put("text", text)
        return JSONArray().put(bildTeil).put(textTeil)
    }

    // Liest die Antwort der API aus oder gibt einen Fehler zurueck.
    private fun antwortVerarbeiten(statusCode: Int, body: String): AgentResult {
        if (statusCode != 200) {
            val fehlerNachricht = fehlerAusBodyLesen(body, statusCode)
            return AgentResult.Error(fehlerNachricht, Exception(fehlerNachricht))
        }
        return try {
            val json = JSONObject(body)
            val text = json
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
            AgentResult.Success(text)
        } catch (e: Exception) {
            AgentResult.Error("Antwort konnte nicht gelesen werden: ${e.message}", e)
        }
    }

    // Liest eine Fehlermeldung aus dem API-Response-Body.
    private fun fehlerAusBodyLesen(body: String, statusCode: Int): String {
        return try {
            val json = JSONObject(body)
            val meldung = json.getJSONObject("error").getString("message")
            "[$statusCode] $meldung"
        } catch (e: Exception) {
            "[$statusCode] Unbekannter Fehler"
        }
    }
}
