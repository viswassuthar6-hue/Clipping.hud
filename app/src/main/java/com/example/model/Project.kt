package com.example.model

enum class VideoSourceType {
    UPLOAD,
    YOUTUBE_URL,
    DEMO_SAMPLE
}

enum class ProjectStatus {
    DRAFT,
    TRIMMED,
    PROCESSING,
    COMPLETED,
    FAILED
}

data class Project(
    val id: String,
    val title: String,
    val sourceUrl: String,
    val sourceType: VideoSourceType,
    val durationSeconds: Float,
    val fileSizeMb: Float,
    val resolution: String = "1080p",
    val fps: Int = 30,
    val status: ProjectStatus = ProjectStatus.DRAFT,
    val language: String = "English",
    val trimmedStartSec: Float = 0f,
    val trimmedEndSec: Float = durationSeconds,
    val isTrimmedSaved: Boolean = false,
    val clipCountTarget: Int = 5,
    val targetDurationMode: String = "Auto", // Auto, 15s, 30s, 45s, 60s, 90s, Custom
    val createdAt: Long = System.currentTimeMillis(),
    val thumbnailPlaceholderColor: Long = 0xFF1E293B
) {
    val activeDurationSec: Float
        get() = if (isTrimmedSaved && trimmedEndSec > trimmedStartSec) {
            trimmedEndSec - trimmedStartSec
        } else {
            durationSeconds
        }

    val estimatedCreditCost: Int
        get() {
            // 2 credits per 10 minutes of active video (minimum 2 credits)
            val minutes = activeDurationSec / 60f
            val units = kotlin.math.ceil(minutes / 10f).toInt()
            return kotlin.math.max(2, units * 2)
        }
}
