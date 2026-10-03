package com.example.model

enum class AIProviderType(val displayName: String, val defaultModel: String) {
    GEMINI("Google Gemini API", "gemini-3.5-flash"),
    OPENAI_COMPATIBLE("OpenAI-Compatible Adapter", "gpt-4o-mini"),
    LOCAL_FALLBACK("Local Smart Engine (Offline/Zero-Key)", "built-in-heuristics")
}

data class ScoringWeights(
    val hookWeight: Float = 0.25f,
    val emotionalWeight: Float = 0.15f,
    val infoWeight: Float = 0.15f,
    val entertainmentWeight: Float = 0.15f,
    val curiosityWeight: Float = 0.10f,
    val storyWeight: Float = 0.10f,
    val visualWeight: Float = 0.05f,
    val audioWeight: Float = 0.05f
)

data class AIProviderConfig(
    val activeProvider: AIProviderType = AIProviderType.GEMINI,
    val modelName: String = "gemini-3.5-flash",
    val customApiKey: String = "",
    val maxTokens: Int = 4096,
    val temperature: Float = 0.7f,
    val timeoutSec: Int = 60,
    val scoringWeights: ScoringWeights = ScoringWeights()
)

data class ExportConfig(
    val resolutionLabel: String = "1080p",
    val width: Int = 1080,
    val height: Int = 1920,
    val fps: Int = 30,
    val isUpscaledFromSource: Boolean = false,
    val sourceResolution: String = "1080p"
) {
    val complianceLabel: String
        get() = if (resolutionLabel == "4K" && isUpscaledFromSource) {
            "4K export — source upscaled"
        } else if (resolutionLabel == "4K") {
            "Native 4K source"
        } else {
            "Standard High Definition ($resolutionLabel)"
        }
}
