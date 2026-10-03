package com.example.model

enum class AspectRatioType(val label: String, val ratioWidth: Float, val ratioHeight: Float, val platformHint: String) {
    RATIO_9_16("9:16", 9f, 16f, "TikTok / Reels / Shorts"),
    RATIO_1_1("1:1", 1f, 1f, "Instagram / LinkedIn"),
    RATIO_16_9("16:9", 16f, 9f, "YouTube Landscape"),
    RATIO_4_5("4:5", 4f, 5f, "Feed / Portrait")
}

enum class BackgroundMode(val label: String, val description: String) {
    SMART_CROP("Smart Crop", "Crops dynamically around active speaker"),
    BLUR("Blur Background", "Duplicates and blurs video behind frame"),
    FIT_VIDEO("Fit Video", "Preserves complete original video aspect"),
    FACE_TRACKING("Face Tracking", "Centers detected speaker face constantly")
}

enum class ClipStyle(val label: String, val tag: String, val highlightColor: Long, val fontStyle: String) {
    MODERN("Modern", "Clean & Sleek", 0xFF06B6D4, "Sans-Serif Bold"),
    BOLD("Bold Impact", "High Contrast", 0xFFFACC15, "Display Heavy"),
    BOUNCY("Bouncy Pop", "Playful Animation", 0xFFEC4899, "Spring Jump"),
    HIGH_ENERGY("High Energy", "Fast-Paced", 0xFFEF4444, "Dynamic Pop"),
    CREATOR("Creator Pro", "Vlogger Favorite", 0xFFA855F7, "Rounded Heavy"),
    PODCAST("Podcast Studio", "Subtle Subtitles", 0xFF38BDF8, "Clean Sans"),
    GAMING("Gaming Rush", "Vibrant Glow", 0xFF10B981, "Neon Outline"),
    TECH("Tech Minimal", "Futuristic Mono", 0xFF3B82F6, "Monospace Bold"),
    BUSINESS("Business Executive", "Professional", 0xFF6366F1, "Editorial"),
    MINIMAL("Minimalist", "Understated", 0xFFFFFFFF, "Light Sans")
}

enum class CaptionPosition(val label: String) {
    BOTTOM("Bottom"),
    MIDDLE("Middle"),
    TOP("Top")
}

enum class HookType(val title: String, val iconDescription: String) {
    CURIOSITY_GAP("The Curiosity Gap", "Makes audience stop scrolling to find the answer"),
    HIGH_STAKES("High-Stakes Controversy", "Challenges common beliefs or creates urgency"),
    ACTIONABLE_SECRET("Actionable Secret / Blueprint", "Promises high-value direct advice or hack"),
    STORY_CLIMAX("Story Climax / Transformation", "Teases an emotional or shocking outcome"),
    CONTRARIAN("Contrarian Truth", "Debunks widespread myths or bad advice")
}

data class TitleHookOption(
    val title: String,
    val hook: String,
    val hookType: HookType,
    val explanation: String
)

data class CaptionWord(
    val word: String,
    val startSec: Float,
    val endSec: Float,
    val isKeyword: Boolean = false,
    val suggestedEmoji: String? = null
)

data class ScoreBreakdown(
    val hookScore: Int,         // 25% weight
    val emotionalScore: Int,    // 15% weight
    val infoScore: Int,         // 15% weight
    val entertainmentScore: Int,// 15% weight
    val curiosityScore: Int,    // 10% weight
    val storyScore: Int,        // 10% weight
    val visualScore: Int,       // 5% weight
    val audioScore: Int         // 5% weight
) {
    val weightedViralScore: Int
        get() = (
            hookScore * 0.25f +
            emotionalScore * 0.15f +
            infoScore * 0.15f +
            entertainmentScore * 0.15f +
            curiosityScore * 0.10f +
            storyScore * 0.10f +
            visualScore * 0.05f +
            audioScore * 0.05f
        ).toInt().coerceIn(0, 100)
}

data class ViralClip(
    val id: String,
    val projectId: String,
    val startSec: Float,
    val endSec: Float,
    val titleOptions: List<TitleHookOption>,
    val selectedOptionIndex: Int = 0,
    val customTitle: String? = null,
    val customHook: String? = null,
    val scoreBreakdown: ScoreBreakdown,
    val topic: String,
    val reasonForSelection: String,
    val transcript: String,
    val captionWords: List<CaptionWord>,
    val keywords: List<String>,
    val emotion: String,
    val activeAspectRatio: AspectRatioType = AspectRatioType.RATIO_9_16,
    val activeStyle: ClipStyle = ClipStyle.MODERN,
    val backgroundMode: BackgroundMode = BackgroundMode.SMART_CROP,
    val autoEmojiEnabled: Boolean = true,
    val captionPosition: CaptionPosition = CaptionPosition.BOTTOM,
    val activeWordColor: Long = 0xFF06B6D4,
    val fontSize: Int = 22,
    val zoomFactor: Float = 1.0f,
    val audioVolume: Float = 1.0f
) {
    val durationSec: Float
        get() = (endSec - startSec).coerceAtLeast(0f)

    val currentTitle: String
        get() = customTitle ?: titleOptions.getOrNull(selectedOptionIndex)?.title ?: "AI Highlight Clip"

    val currentHook: String
        get() = customHook ?: titleOptions.getOrNull(selectedOptionIndex)?.hook ?: ""

    val viralScore: Int
        get() = scoreBreakdown.weightedViralScore
}
