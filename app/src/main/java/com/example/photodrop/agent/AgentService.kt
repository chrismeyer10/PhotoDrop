package com.example.photodrop.agent

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.models.beta.messages.BetaMessage
import com.anthropic.models.beta.messages.MessageCreateParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.function.Supplier

/**
 * Core agent service. Runs a Claude agent with the given skills (tools).
 *
 * Usage:
 * ```kotlin
 * val agent = AgentService(BuildConfig.ANTHROPIC_API_KEY)
 * val result = agent.run(
 *     prompt = "Analysiere dieses Foto",
 *     skills = SkillRegistry.all,
 *     systemPrompt = "Du bist ein hilfreicher Foto-Assistent."
 * )
 * ```
 */
class AgentService(apiKey: String) {

    private val client: AnthropicClient = AnthropicOkHttpClient.builder()
        .apiKey(apiKey)
        .build()

    /**
     * Runs the agent with the given prompt and skills.
     * The tool runner handles the agentic loop automatically —
     * Claude calls skills as needed until it produces a final response.
     */
    suspend fun run(
        prompt: String,
        skills: List<Class<out Supplier<String>>> = emptyList(),
        systemPrompt: String? = null,
    ): AgentResult = withContext(Dispatchers.IO) {
        try {
            val paramsBuilder = MessageCreateParams.builder()
                .model("claude-opus-4-6")
                .maxTokens(16000L)
                .addUserMessage(prompt)

            systemPrompt?.let { paramsBuilder.system(it) }
            skills.forEach { paramsBuilder.addTool(it) }

            val output = StringBuilder()
            for (message: BetaMessage in client.beta().messages().toolRunner(paramsBuilder.build())) {
                message.content().forEach { block ->
                    block.text().ifPresent { output.append(it.text()) }
                }
            }
            AgentResult.Success(output.toString())
        } catch (e: Exception) {
            AgentResult.Error(e.message ?: "Unknown error", e)
        }
    }
}

sealed class AgentResult {
    data class Success(val text: String) : AgentResult()
    data class Error(val message: String, val cause: Throwable) : AgentResult()
}
