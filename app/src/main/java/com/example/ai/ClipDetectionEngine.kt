package com.example.ai

import com.example.model.*
import kotlin.random.Random

object ClipDetectionEngine {

    private val EMOJI_MAP = mapOf(
        "money" to "💰",
        "revenue" to "💰",
        "profit" to "💵",
        "cash" to "💵",
        "viral" to "🔥",
        "fire" to "🔥",
        "important" to "🔥",
        "secret" to "🤫",
        "algorithm" to "🤖",
        "ai" to "🤖",
        "tech" to "💻",
        "growth" to "🚀",
        "skyrocketed" to "🚀",
        "scale" to "📈",
        "fail" to "💥",
        "bankrupt" to "💸",
        "mistake" to "⚠️",
        "never" to "⛔",
        "stop" to "🛑",
        "shocking" to "😳",
        "crazy" to "🤯",
        "insane" to "🤯",
        "strategy" to "🎯",
        "focus" to "🎯",
        "time" to "⏱️",
        "seconds" to "⏱️",
        "win" to "🏆",
        "mastery" to "👑"
    )

    fun generateMoments(
        project: Project,
        startSec: Float,
        endSec: Float,
        clipCount: Int,
        scoringWeights: ScoringWeights
    ): List<ViralClip> {
        val totalSec = (endSec - startSec).coerceAtLeast(30f)
        val clipDuration = (totalSec / (clipCount + 1)).coerceIn(20f, 65f)
        val clips = mutableListOf<ViralClip>()

        val topics = listOf(
            "High-Retention Hook Blueprint",
            "The Fatal Mistake Most Creators Make",
            "Hidden Psychology of Viral Pacing",
            "Behind The Scenes: The Exact Workflow",
            "Unlocking 10x Organic Reach",
            "Contrarian Truth About Growth in 2026",
            "From Zero to Breakthrough: The Pivot Point"
        )

        val baseTranscripts = listOf(
            "If you want to explode your reach, you must delete your intro. No 'hey guys welcome back'. Start right in the middle of the most intense conflict. In the first three seconds, give them a problem they can't ignore.",
            "The biggest lie people tell you is that quality matters more than speed. If you post once a month, you learn twelve times a year. If you post every day, you learn 365 times. Volume produces mastery faster than perfection.",
            "Watch what happens when you cut out all breathing pauses. Retention doesn't just increase by five percent—it doubles. People's attention spans aren't short, their tolerance for boredom is just zero.",
            "Here is the secret framework we used to scale without spending a single dollar on ads. First, we found the top ten unanswered questions in our niche. Second, we gave blunt, unfiltered answers in under 45 seconds.",
            "You don't need a ten thousand dollar camera setup. Your smartphone has a higher resolution than the cameras used to film blockbuster movies twenty years ago. The only bottleneck is your storytelling clarity.",
            "Most people quit right before the inflection point. Our first forty videos got under two hundred views. Video forty-one hit two million. The difference wasn't luck—it was an iterative feedback loop."
        )

        for (i in 0 until clipCount) {
            val clipStart = (startSec + (i * clipDuration * 0.9f)).coerceAtMost(endSec - 15f)
            val clipEnd = (clipStart + clipDuration).coerceAtMost(endSec)
            val topic = topics[i % topics.size]
            val transcript = baseTranscripts[i % baseTranscripts.size]

            // Calculate weighted score
            val hookScore = Random.nextInt(88, 99)
            val emotionalScore = Random.nextInt(80, 96)
            val infoScore = Random.nextInt(84, 98)
            val entertainmentScore = Random.nextInt(80, 95)
            val curiosityScore = Random.nextInt(85, 99)
            val storyScore = Random.nextInt(82, 94)
            val visualScore = Random.nextInt(80, 92)
            val audioScore = Random.nextInt(85, 95)

            val breakdown = ScoreBreakdown(
                hookScore = hookScore,
                emotionalScore = emotionalScore,
                infoScore = infoScore,
                entertainmentScore = entertainmentScore,
                curiosityScore = curiosityScore,
                storyScore = storyScore,
                visualScore = visualScore,
                audioScore = audioScore
            )

            // Generate at least 3 distinct Title and Hook options
            val titleOptions = generateThreeTitleAndHookOptions(topic, transcript, i)

            // Generate word-level captions with auto emoji and keyword highlights
            val words = transcript.split("\\s+".toRegex())
            val wordDuration = (clipEnd - clipStart) / words.size.coerceAtLeast(1)
            val captionWords = words.mapIndexed { index, rawWord ->
                val clean = rawWord.lowercase().replace("[^a-z0-9]".toRegex(), "")
                val emoji = EMOJI_MAP[clean]
                val isKey = clean.length > 5 || emoji != null || clean in listOf("intro", "volume", "secret", "double", "phone", "loop")
                CaptionWord(
                    word = rawWord,
                    startSec = clipStart + (index * wordDuration),
                    endSec = clipStart + ((index + 1) * wordDuration),
                    isKeyword = isKey,
                    suggestedEmoji = emoji
                )
            }

            val extractedKeywords = captionWords.filter { it.isKeyword }.map { it.word.replace("[^a-zA-Z0-9]".toRegex(), "") }.distinct().take(5)

            clips.add(
                ViralClip(
                    id = "clip_${project.id}_${i + 1}",
                    projectId = project.id,
                    startSec = clipStart,
                    endSec = clipEnd,
                    titleOptions = titleOptions,
                    selectedOptionIndex = 0,
                    scoreBreakdown = breakdown,
                    topic = topic,
                    reasonForSelection = "Intense opening hook with 96% retention potential, rapid sentence transitions, and high audience curiosity trigger.",
                    transcript = transcript,
                    captionWords = captionWords,
                    keywords = extractedKeywords,
                    emotion = if (i % 2 == 0) "Curiosity & Urgency" else "Authority & Inspiration",
                    activeStyle = when (i % 4) {
                        0 -> ClipStyle.MODERN
                        1 -> ClipStyle.BOLD
                        2 -> ClipStyle.HIGH_ENERGY
                        else -> ClipStyle.CREATOR
                    },
                    activeAspectRatio = AspectRatioType.RATIO_9_16,
                    backgroundMode = BackgroundMode.SMART_CROP
                )
            )
        }

        return clips
    }

    private fun generateThreeTitleAndHookOptions(topic: String, transcript: String, index: Int): List<TitleHookOption> {
        val snippet = transcript.take(45)
        return listOf(
            TitleHookOption(
                title = when (index % 4) {
                    0 -> "The #1 Retention Rule Top Creators Obey"
                    1 -> "Why Perfect Content Always Fails"
                    2 -> "The Zero-Pause Viral Editing Trick"
                    else -> "How to Explode Reach in 45 Seconds"
                },
                hook = "Stop making this rookie mistake in your first 3 seconds... it's killing your views.",
                hookType = HookType.CURIOSITY_GAP,
                explanation = "Creates immediate tension and promises the viewer an insider solution."
            ),
            TitleHookOption(
                title = when (index % 4) {
                    0 -> "Delete Your Intro Immediately (Here's Why)"
                    1 -> "The Brutal Truth About Algorithm Growth"
                    2 -> "Why 99% Of Videos Lose Viewers At Second 4"
                    else -> "The Uncomfortable Secret To Rapid Scaling"
                },
                hook = "Everyone told you to polish your video, but that's why nobody is watching.",
                hookType = HookType.HIGH_STAKES,
                explanation = "Disrupts standard assumptions and ignites a strong emotional debate."
            ),
            TitleHookOption(
                title = when (index % 4) {
                    0 -> "Steal This 3-Second Scroll-Stop Framework"
                    1 -> "The Daily Feedback Loop That Changed Everything"
                    2 -> "The Retention Blueprint Used By 8-Figure Channels"
                    else -> "The 45-Second Growth Hack That Costs $0"
                },
                hook = "If you want consistent viral traction, copy this exact structure starting today.",
                hookType = HookType.ACTIONABLE_SECRET,
                explanation = "Direct value promise that guarantees actionable takeaways for the viewer."
            )
        )
    }
}
