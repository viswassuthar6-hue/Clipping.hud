package com.example.ai

import com.example.model.AIProviderConfig
import com.example.model.AIProviderType
import com.example.model.Project
import kotlinx.coroutines.flow.Flow

class ModularAIProvider(
    private val geminiProvider: GeminiProvider = GeminiProvider(),
    private val openAIProvider: OpenAIProvider = OpenAIProvider()
) : AIProvider {

    override fun analyzeVideo(
        project: Project,
        trimmedStartSec: Float,
        trimmedEndSec: Float,
        targetClipCount: Int,
        config: AIProviderConfig
    ): Flow<AnalysisStatus> {
        return when (config.activeProvider) {
            AIProviderType.OPENAI_COMPATIBLE -> {
                openAIProvider.analyzeVideo(project, trimmedStartSec, trimmedEndSec, targetClipCount, config)
            }
            AIProviderType.GEMINI,
            AIProviderType.LOCAL_FALLBACK -> {
                geminiProvider.analyzeVideo(project, trimmedStartSec, trimmedEndSec, targetClipCount, config)
            }
        }
    }
}
