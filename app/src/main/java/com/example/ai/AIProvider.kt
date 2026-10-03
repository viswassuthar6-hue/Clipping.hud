package com.example.ai

import com.example.model.Project
import com.example.model.ViralClip
import com.example.model.AIProviderConfig
import kotlinx.coroutines.flow.Flow

sealed class AnalysisProgressStep(val stepName: String, val progressFraction: Float, val detail: String) {
    object Validating : AnalysisProgressStep("MEDIA VALIDATION", 0.10f, "Verifying video format, audio stream, and permitted rights...")
    object AudioVideoAnalysis : AnalysisProgressStep("AUDIO & VIDEO ANALYSIS", 0.25f, "Deconstructing visual cadence, speech cadence, and energy peaks...")
    object Transcribing : AnalysisProgressStep("TRANSCRIPT GENERATION", 0.40f, "Generating high-accuracy speech-to-text with word-level timestamps...")
    object SceneDetection : AnalysisProgressStep("SCENE & SPEAKER DETECTION", 0.55f, "Tracking active speakers and visual pattern interrupts...")
    object HookDetection : AnalysisProgressStep("HOOK & EMOTION SCORING", 0.70f, "Evaluating opening retention vectors, curiosity gap, and emotional intensity...")
    object ScoringMoments : AnalysisProgressStep("VIRAL MOMENT SCORING", 0.85f, "Computing weighted viral potential scores across detected sections...")
    object GeneratingTitlesAndHooks : AnalysisProgressStep("AI TITLES & HOOKS", 0.92f, "Synthesizing 3+ viral title and hook variations per moment...")
    object Finalizing : AnalysisProgressStep("FINALIZING CLIPS & CAPTIONS", 1.0f, "Applying smart reframing, active keyword highlights, and auto-emojis...")
}

data class AnalysisStatus(
    val currentStep: AnalysisProgressStep,
    val isComplete: Boolean = false,
    val clips: List<ViralClip> = emptyList(),
    val errorMessage: String? = null
)

interface AIProvider {
    fun analyzeVideo(
        project: Project,
        trimmedStartSec: Float,
        trimmedEndSec: Float,
        targetClipCount: Int,
        config: AIProviderConfig
    ): Flow<AnalysisStatus>
}
