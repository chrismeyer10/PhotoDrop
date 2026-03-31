package com.example.photodrop.agent

import android.util.Base64
import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.models.beta.messages.BetaBase64ImageSource
import com.anthropic.models.beta.messages.BetaContentBlockParam
import com.anthropic.models.beta.messages.BetaImageBlockParam
import com.anthropic.models.beta.messages.BetaMessage
import com.anthropic.models.beta.messages.BetaTextBlockParam
import com.anthropic.models.beta.messages.MessageCreateParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.function.Supplier

// Fuehrt einen Claude-Agent mit Skills (Tools) aus.
// Unterstuetzt optionale Bild-Eingabe fuer multimodale Analyse.
class AgentService(private val apiKey: String) : KiDienst {

    private val client: AnthropicClient = AnthropicOkHttpClient.builder()
        .apiKey(apiKey)
        .build()

    // Fuehrt den Agent mit dem gegebenen Prompt und Skills aus.
    suspend fun run(
        prompt: String,
        skills: List<Class<out Supplier<String>>> = emptyList(),
        systemPrompt: String? = null,
        bild: ByteArray? = null,
        bildMimeType: String? = null,
    ): AgentResult = withContext(Dispatchers.IO) {
        try {
            val paramsBauer = MessageCreateParams.builder()
                .model("claude-haiku-4-5-20251001")
                .maxTokens(16000L)

            if (bild != null) {
                paramsBauer.addUserMessageOfBetaContentBlockParams(
                    bildNachrichtBauen(bild, bildMimeType ?: "image/jpeg", prompt)
                )
            } else {
                paramsBauer.addUserMessage(prompt)
            }

            systemPrompt?.let { paramsBauer.system(it) }
            skills.forEach { paramsBauer.addTool(it) }

            val ausgabe = StringBuilder()
            for (nachricht: BetaMessage in client.beta().messages().toolRunner(paramsBauer.build())) {
                nachricht.content().forEach { block ->
                    block.text().ifPresent { ausgabe.append(it.text()) }
                }
            }
            AgentResult.Success(ausgabe.toString())
        } catch (e: Exception) {
            AgentResult.Error(fehlerBereinigen(e), e)
        }
    }

    // Implementierung der KiDienst-Schnittstelle.
    override suspend fun analysieren(
        prompt: String,
        systemPrompt: String?,
        bild: ByteArray?,
        bildMimeType: String?
    ): AgentResult = run(prompt = prompt, systemPrompt = systemPrompt, bild = bild, bildMimeType = bildMimeType)

    // Baut eine multimodale Nachricht mit Bild und Text.
    private fun bildNachrichtBauen(
        bild: ByteArray,
        mimeType: String,
        text: String
    ): List<BetaContentBlockParam> {
        val medienTyp = mimeTypZuMediaType(mimeType)
        val base64Daten = Base64.encodeToString(bild, Base64.NO_WRAP)

        val bildBlock = BetaContentBlockParam.ofImage(
            BetaImageBlockParam.builder()
                .source(
                    BetaBase64ImageSource.builder()
                        .data(base64Daten)
                        .mediaType(medienTyp)
                        .build()
                )
                .build()
        )
        val textBlock = BetaContentBlockParam.ofText(
            BetaTextBlockParam.builder().text(text).build()
        )
        return listOf(bildBlock, textBlock)
    }

    // Wandelt einen MIME-Type-String in den SDK-MediaType um.
    private fun mimeTypZuMediaType(mimeType: String): BetaBase64ImageSource.MediaType {
        return when (mimeType) {
            "image/png" -> BetaBase64ImageSource.MediaType.IMAGE_PNG
            "image/gif" -> BetaBase64ImageSource.MediaType.IMAGE_GIF
            "image/webp" -> BetaBase64ImageSource.MediaType.IMAGE_WEBP
            else -> BetaBase64ImageSource.MediaType.IMAGE_JPEG
        }
    }

    // Bereinigt die Fehlermeldung und unterscheidet Fehlertypen genauer.
    private fun fehlerBereinigen(e: Exception): String {
        val meldung = e.message ?: return "Unbekannter Fehler"
        val name = e.javaClass.simpleName
        // Anthropic SDK wirft spezifische Exceptions — Klassenname hilft
        return when {
            "AuthenticationException" in name || "401" in meldung ->
                "API-Schluessel ungueltig [401].\n\n" +
                "Hinweis: Ein Claude Pro oder Max Abo (claude.ai) gibt keinen API-Zugang. " +
                "Bitte einen separaten API-Key unter console.anthropic.com erstellen."
            "PermissionDeniedException" in name || "403" in meldung ->
                "Kein API-Zugang [403].\n\n" +
                "Hinweis: Ein Claude Pro oder Max Abo (claude.ai) gibt keinen API-Zugang. " +
                "Bitte einen separaten API-Key unter console.anthropic.com erstellen."
            "RateLimitException" in name || "429" in meldung ->
                "Zu viele Anfragen [429]. Bitte kurz warten."
            "InternalServerException" in name || "500" in meldung ->
                "Anthropic-Server-Fehler [500]. Bitte spaeter erneut versuchen."
            else -> meldung
        }
    }
}
