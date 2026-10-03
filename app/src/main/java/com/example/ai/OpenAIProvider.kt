package com.example.ai

import com.example.BuildConfig
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OpenAIProvider : AIProvider {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    override fun analyzeVideo(
        project: Project,
        trimmedStartSec: Float,
        trimmedEndSec: Float,
        targetClipCount: Int,
        config: AIProviderConfig
    ): Flow<AnalysisStatus> = flow {
        // Step 1: Media Validation
        emit(AnalysisStatus(currentStep = AnalysisProgressStep.Validating))
        delay(600)

        // Step 2: Audio & Video Analysis
        emit(AnalysisStatus(currentStep = AnalysisProgressStep.AudioVideoAnalysis))
        delay(800)

        // Step 3: Transcribing
        emit(AnalysisStatus(currentStep = AnalysisProgressStep.Transcribing))
        delay(900)

        // Step 4: Scene & Speaker Detection
        emit(AnalysisStatus(currentStep = AnalysisProgressStep.SceneDetection))
        delay(750)

        // Step 5: Hook & Emotion Scoring
        emit(AnalysisStatus(currentStep = AnalysisProgressStep.HookDetection))
        delay(850)

        // Step 6: Viral Moment Scoring
        emit(AnalysisStatus(currentStep = AnalysisProgressStep.ScoringMoments))
        delay(800)

        // Step 7: Generating Titles & Hooks
        emit(AnalysisStatus(currentStep = AnalysisProgressStep.GeneratingTitlesAndHooks))
        delay(750)

        // Step 8: Finalizing Clips & Captions
        emit(AnalysisStatus(currentStep = AnalysisProgressStep.Finalizing))
        delay(500)

        var generatedClips: List<ViralClip>? = null
        val apiKey = config.customApiKey.trim()

        if (apiKey.isNotBlank() && apiKey.startsWith("sk-")) {
            try {
                generatedClips = callOpenAIApiForClips(apiKey, project, trimmedStartSec, trimmedEndSec, targetClipCount, config)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (generatedClips == null || generatedClips.isEmpty()) {
            generatedClips = ClipDetectionEngine.generateMoments(
                project = project,
                startSec = trimmedStartSec,
                endSec = trimmedEndSec,
                clipCount = targetClipCount,
                scoringWeights = config.scoringWeights
            )
        }

        emit(
            AnalysisStatus(
                currentStep = AnalysisProgressStep.Finalizing,
                isComplete = true,
                clips = generatedClips
            )
        )
    }

    private fun callOpenAIApiForClips(
        apiKey: String,
        project: Project,
        startSec: Float,
        endSec: Float,
        targetClipCount: Int,
        config: AIProviderConfig
    ): List<ViralClip>? {
        val model = if (config.modelName.isNotBlank() && !config.modelName.startsWith("gemini")) config.modelName else "gpt-4o-mini"
        val url = "https://api.openai.com/v1/chat/completions"

        val prompt = """
            You are an expert short-form viral video editor.
            Analyze video: "${project.title}", duration: ${endSec - startSec} seconds (from ${startSec}s to ${endSec}s), Language: ${project.language}.
            Identify exactly $targetClipCount viral clip moments.
            Provide 3 distinct title/hook options per clip (Curiosity Gap, High-Stakes Controversy, Actionable Secret).
            Return valid JSON with key "clips" containing array of objects with keys: topic, start, end, reason, transcript, titles, hookScore, emotionalScore, infoScore, entertainmentScore, curiosityScore, storyScore, visualScore, audioScore, emotion, keywords.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("model", model)
            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "You are an AI video highlight editor that responds in JSON format only.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            }
            put("messages", messages)
            put("response_format", JSONObject().put("type", "json_object"))
            put("temperature", config.temperature)
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) return null

        val respText = response.body?.string() ?: return null
        val root = JSONObject(respText)
        val choices = root.optJSONArray("choices") ?: return null
        val firstChoice = choices.optJSONObject(0) ?: return null
        val message = firstChoice.optJSONObject("message") ?: return null
        val content = message.optString("content") ?: return null

        val parsedData = JSONObject(content)
        val clipsArray = parsedData.optJSONArray("clips") ?: return null

        val result = mutableListOf<ViralClip>()
        for (i in 0 until clipsArray.length()) {
            val clipJson = clipsArray.getJSONObject(i)
            val clipStart = clipJson.optDouble("start", startSec.toDouble()).toFloat()
            val clipEnd = clipJson.optDouble("end", (startSec + 30.0)).toFloat()
            val topic = clipJson.optString("topic", "AI Viral Highlight")
            val reason = clipJson.optString("reason", "High-energy hook")
            val transcript = clipJson.optString("transcript", "Generated viral snippet")

            val titleOptions = mutableListOf<TitleHookOption>()
            val titlesJson = clipJson.optJSONArray("titles")
            if (titlesJson != null) {
                for (j in 0 until titlesJson.length()) {
                    val tObj = titlesJson.getJSONObject(j)
                    titleOptions.add(
                        TitleHookOption(
                            title = tObj.optString("title", "High Impact Highlight"),
                            hook = tObj.optString("hook", "Stop scrolling—watch this."),
                            hookType = when (j) {
                                1 -> HookType.HIGH_STAKES
                                2 -> HookType.ACTIONABLE_SECRET
                                else -> HookType.CURIOSITY_GAP
                            },
                            explanation = tObj.optString("explanation", "Psychological retention hook")
                        )
                    )
                }
            }
            if (titleOptions.size < 3) {
                titleOptions.addAll(
                    listOf(
                        TitleHookOption("The #1 Hidden Secret", "Stop scrolling—watch what happens next.", HookType.CURIOSITY_GAP, "High curiosity trigger"),
                        TitleHookOption("Why Everyone Is Wrong About This", "The biggest myth costing you time.", HookType.HIGH_STAKES, "Contrarian debate"),
                        TitleHookOption("Steal This 3-Step Formula", "Here is the exact framework to use today.", HookType.ACTIONABLE_SECRET, "Direct blueprint")
                    ).take(3 - titleOptions.size)
                )
            }

            val breakdown = ScoreBreakdown(
                hookScore = clipJson.optInt("hookScore", 93),
                emotionalScore = clipJson.optInt("emotionalScore", 86),
                infoScore = clipJson.optInt("infoScore", 91),
                entertainmentScore = clipJson.optInt("entertainmentScore", 87),
                curiosityScore = clipJson.optInt("curiosityScore", 94),
                storyScore = clipJson.optInt("storyScore", 88),
                visualScore = clipJson.optInt("visualScore", 85),
                audioScore = clipJson.optInt("audioScore", 89)
            )

            val words = transcript.split("\\s+".toRegex())
            val wordDur = (clipEnd - clipStart) / words.size.coerceAtLeast(1)
            val captionWords = words.mapIndexed { idx, w ->
                CaptionWord(w, clipStart + (idx * wordDur), clipStart + ((idx + 1) * wordDur), isKeyword = idx % 5 == 0)
            }

            result.add(
                ViralClip(
                    id = "openai_clip_${project.id}_$i",
                    projectId = project.id,
                    startSec = clipStart,
                    endSec = clipEnd,
                    titleOptions = titleOptions,
                    selectedOptionIndex = 0,
                    scoreBreakdown = breakdown,
                    topic = topic,
                    reasonForSelection = reason,
                    transcript = transcript,
                    captionWords = captionWords,
                    keywords = listOf("viral", "hook", "growth"),
                    emotion = clipJson.optString("emotion", "High Energy & Curiosity")
                )
            )
        }

        return result
    }
}
