package com.example.photodrop.agent

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.models.beta.messages.BetaMessage
import com.anthropic.models.beta.messages.MessageCreateParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.function.Supplier

// Fuehrt einen Claude-Agent mit Skills (Tools) aus.
// Benoetigt einen gueltigen Anthropic API-Key.
class AgentService(apiKey: String) {

    private val client: AnthropicClient = AnthropicOkHttpClient.builder()
        .apiKey(apiKey)
        .build()

    // Fuehrt den Agent mit dem gegebenen Prompt und Skills aus.
    // Der ToolRunner handhabt die Agenten-Schleife automatisch.
    suspend fun run(
        prompt: String,
        skills: List<Class<out Supplier<String>>> = emptyList(),
        systemPrompt: String? = null,
    ): AgentResult = withContext(Dispatchers.IO) {
        try {
            val paramsBauer = MessageCreateParams.builder()
                .model("claude-opus-4-6")
                .maxTokens(16000L)
                .addUserMessage(prompt)

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
            AgentResult.Error(e.message ?: "Unbekannter Fehler", e)
        }
    }
}

// Moegliche Ergebnisse eines Agent-Laufs.
sealed class AgentResult {
    // Erfolgreiche Ausfuehrung mit Textantwort.
    data class Success(val text: String) : AgentResult()

    // Fehler bei der Ausfuehrung.
    data class Error(val message: String, val cause: Throwable) : AgentResult()
}
