package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AIProvider
import com.example.ai.AnalysisProgressStep
import com.example.ai.ModularAIProvider
import com.example.data.ClipRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    UPLOAD_AND_TRIM,
    CLIPS_LIST,
    CLIP_EDITOR,
    CREDIT_LEDGER,
    ADMIN_PANEL,
    SETTINGS
}

data class PlayerPlaybackState(
    val isPlaying: Boolean = false,
    val currentTimeSec: Float = 0f,
    val durationSec: Float = 60f
)

data class UiNotification(
    val message: String,
    val isSuccess: Boolean = true
)

class MainViewModel(
    private val repository: ClipRepository = ClipRepository(),
    private val aiProvider: AIProvider = ModularAIProvider()
) : ViewModel() {

    val projects = repository.projects
    val clips = repository.clips
    val transactions = repository.transactions
    val vipCodes = repository.vipCodes
    val userAccount = repository.userAccount
    val aiConfig = repository.aiConfig
    val activeProjectId = repository.activeProjectId
    val activeClipId = repository.activeClipId

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _playbackState = MutableStateFlow(PlayerPlaybackState())
    val playbackState: StateFlow<PlayerPlaybackState> = _playbackState.asStateFlow()

    // Trimming tool state
    private val _trimStart = MutableStateFlow(0f)
    val trimStart: StateFlow<Float> = _trimStart.asStateFlow()

    private val _trimEnd = MutableStateFlow(60f)
    val trimEnd: StateFlow<Float> = _trimEnd.asStateFlow()

    private val _isTrimSaved = MutableStateFlow(false)
    val isTrimSaved: StateFlow<Boolean> = _isTrimSaved.asStateFlow()

    // AI Analysis status
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisStep = MutableStateFlow<AnalysisProgressStep?>(null)
    val analysisStep: StateFlow<AnalysisProgressStep?> = _analysisStep.asStateFlow()

    private val _notification = MutableStateFlow<UiNotification?>(null)
    val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

    private var playbackJob: Job? = null
    private var analysisJob: Job? = null

    init {
        // Sync active project trim settings on init
        val initialProj = projects.value.firstOrNull()
        if (initialProj != null) {
            setupTrimmingForProject(initialProj)
        }
    }

    fun navigateTo(screen: AppScreen) {
        pausePlayback()
        _currentScreen.value = screen
    }

    fun selectProject(projectId: String) {
        repository.setActiveProject(projectId)
        val proj = projects.value.find { it.id == projectId }
        if (proj != null) {
            setupTrimmingForProject(proj)
        }
    }

    fun selectClip(clipId: String) {
        repository.setActiveClip(clipId)
        val clip = clips.value.find { it.id == clipId }
        if (clip != null) {
            _playbackState.value = PlayerPlaybackState(
                isPlaying = false,
                currentTimeSec = 0f,
                durationSec = clip.durationSec
            )
        }
    }

    fun setupTrimmingForProject(project: Project) {
        _trimStart.value = project.trimmedStartSec
        _trimEnd.value = if (project.trimmedEndSec > 0f) project.trimmedEndSec else project.durationSeconds
        _isTrimSaved.value = project.isTrimmedSaved
        _playbackState.value = PlayerPlaybackState(
            isPlaying = false,
            currentTimeSec = _trimStart.value,
            durationSec = project.durationSeconds
        )
    }

    fun updateTrimHandles(start: Float, end: Float) {
        _trimStart.value = start
        _trimEnd.value = end
        _isTrimSaved.value = false
    }

    fun saveTrimmedSection(projectId: String) {
        val start = _trimStart.value
        val end = _trimEnd.value
        repository.updateProjectTrim(projectId, start, end, markAsSaved = true)
        _isTrimSaved.value = true
        showNotification("Trimmed section (${formatTime(start)} - ${formatTime(end)}) saved!")
    }

    // Video Player controls
    fun togglePlayPause() {
        if (_playbackState.value.isPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    fun seekTo(timeSec: Float) {
        val clamped = timeSec.coerceIn(0f, _playbackState.value.durationSec)
        _playbackState.value = _playbackState.value.copy(currentTimeSec = clamped)
    }

    private fun startPlayback() {
        playbackJob?.cancel()
        _playbackState.value = _playbackState.value.copy(isPlaying = true)
        playbackJob = viewModelScope.launch {
            while (_playbackState.value.isPlaying) {
                delay(100)
                val next = _playbackState.value.currentTimeSec + 0.1f
                if (next >= _playbackState.value.durationSec) {
                    _playbackState.value = _playbackState.value.copy(currentTimeSec = 0f, isPlaying = false)
                    break
                } else {
                    _playbackState.value = _playbackState.value.copy(currentTimeSec = next)
                }
            }
        }
    }

    fun pausePlayback() {
        playbackJob?.cancel()
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
    }

    // Create new project
    fun createProject(
        title: String,
        sourceUrl: String,
        sourceType: VideoSourceType,
        durationSec: Float,
        fileSizeMb: Float,
        language: String,
        clipCount: Int
    ) {
        val proj = repository.addProject(
            title = title,
            sourceUrl = sourceUrl,
            sourceType = sourceType,
            durationSeconds = durationSec,
            fileSizeMb = fileSizeMb,
            language = language,
            targetDurationMode = "Auto",
            clipCountTarget = clipCount
        )
        setupTrimmingForProject(proj)
        showNotification("Project '${proj.title}' created! Preview & trim below.")
    }

    // Cost Calculation & Balance Validation
    fun calculateJobCost(durationSeconds: Float): Int {
        return repository.calculateJobCost(durationSeconds)
    }

    suspend fun validateJobBalance(requiredCredits: Int): Boolean {
        return repository.validateJobBalance(requiredCredits)
    }

    fun canAffordVideo(durationSeconds: Float): Boolean {
        val cost = calculateJobCost(durationSeconds)
        return userAccount.value.creditsBalance >= cost
    }

    // AI Analysis execution
    fun runAiAnalysis(projectId: String, analyzeTrimmedOnly: Boolean) {
        if (!userAccount.value.isProcessingEnabled) {
            showNotification("⚠️ Processing is temporarily disabled by admin maintenance.", isSuccess = false)
            return
        }

        val project = projects.value.find { it.id == projectId } ?: return
        val startSec = if (analyzeTrimmedOnly) _trimStart.value else 0f
        val endSec = if (analyzeTrimmedOnly) _trimEnd.value else project.durationSeconds
        val cost = calculateJobCost(endSec - startSec)

        // Verify credit balance
        val deductResult = repository.deductCreditsForProcessing(
            projectId = projectId,
            amount = cost,
            description = "Analyzed ${"%.1f".format((endSec - startSec) / 60f)} min video for ${project.clipCountTarget} clips"
        )

        if (deductResult.isFailure) {
            showNotification(deductResult.exceptionOrNull()?.message ?: "Insufficient credits", isSuccess = false)
            return
        }

        pausePlayback()
        _isAnalyzing.value = true
        analysisJob?.cancel()

        analysisJob = viewModelScope.launch {
            aiProvider.analyzeVideo(
                project = project,
                trimmedStartSec = startSec,
                trimmedEndSec = endSec,
                targetClipCount = project.clipCountTarget,
                config = aiConfig.value
            ).collect { status ->
                _analysisStep.value = status.currentStep
                if (status.isComplete) {
                    repository.addClipsForProject(projectId, status.clips)
                    _isAnalyzing.value = false
                    showNotification("AI Analysis complete! Found ${status.clips.size} high-potential viral clips.")
                    navigateTo(AppScreen.CLIPS_LIST)
                }
            }
        }
    }

    // Title and Hook option selection
    fun selectTitleHookOption(clipId: String, optionIndex: Int) {
        repository.updateClipSelection(clipId, selectedTitleIndex = optionIndex, customTitle = null, customHook = null)
        showNotification("Applied title & hook variation #${optionIndex + 1}")
    }

    fun saveCustomTitleAndHook(clipId: String, customTitle: String, customHook: String) {
        repository.updateClipSelection(clipId, customTitle = customTitle, customHook = customHook)
        showNotification("Custom title & hook saved!")
    }

    // Clip customization
    fun updateClipAspectRatio(clipId: String, ratio: AspectRatioType) {
        repository.updateClipSelection(clipId, aspectRatio = ratio)
    }

    fun updateClipStyle(clipId: String, style: ClipStyle) {
        repository.updateClipSelection(clipId, style = style)
    }

    fun updateClipBackgroundMode(clipId: String, mode: BackgroundMode) {
        repository.updateClipSelection(clipId, backgroundMode = mode)
    }

    fun toggleClipAutoEmoji(clipId: String, enabled: Boolean) {
        repository.updateClipSelection(clipId, autoEmoji = enabled)
    }

    fun updateClipCaptionPosition(clipId: String, position: CaptionPosition) {
        repository.updateClipSelection(clipId, captionPosition = position)
    }

    fun updateClipFontSize(clipId: String, size: Int) {
        repository.updateClipSelection(clipId, fontSize = size)
    }

    fun updateClipActiveWordColor(clipId: String, color: Long) {
        repository.updateClipSelection(clipId, activeWordColor = color)
    }

    fun updateClipZoomFactor(clipId: String, zoom: Float) {
        repository.updateClipSelection(clipId, zoomFactor = zoom)
    }

    fun updateClipAudioVolume(clipId: String, volume: Float) {
        repository.updateClipSelection(clipId, audioVolume = volume)
    }

    fun splitClipAtCurrentTime(clipId: String) {
        val currentSec = _playbackState.value.currentTimeSec
        val newClip = repository.splitClip(clipId, currentSec)
        if (newClip != null) {
            showNotification("✂️ Clip split into two segments successfully!")
        } else {
            showNotification("Cannot split too close to start or end of clip.", isSuccess = false)
        }
    }

    fun deleteClip(clipId: String) {
        repository.deleteClip(clipId)
        showNotification("🗑️ Clip deleted.")
        if (clips.value.isEmpty()) {
            navigateTo(AppScreen.DASHBOARD)
        }
    }

    // Admin controls
    fun updateAdminCostPer10Min(cost: Int) {
        repository.updateCostPer10Min(cost)
        showNotification("Admin: Cost updated to $cost credits per 10 min.")
    }

    fun updateAdminStorageLimit(gb: Float) {
        repository.updateStorageLimit(gb)
        showNotification("Admin: Storage limit set to ${gb.toInt()} GB.")
    }

    fun updateAdminMinuteLimit(minutes: Int) {
        repository.updateMinuteLimit(minutes)
        showNotification("Admin: Monthly minute limit set to $minutes min.")
    }

    fun updateAdminMaxFilesLimit(maxFiles: Int) {
        repository.updateMaxFilesLimit(maxFiles)
        showNotification("Admin: Max files per project set to $maxFiles.")
    }

    fun toggleAdminProcessing() {
        repository.toggleProcessingEnabled()
        val newState = repository.userAccount.value.isProcessingEnabled
        showNotification(if (newState) "Admin: Processing re-enabled." else "Admin: Processing disabled.")
    }

    // VIP code redemption
    fun redeemVipCode(code: String) {
        val result = repository.redeemVipCode(code)
        if (result.isSuccess) {
            val reward = result.getOrNull() ?: 0
            showNotification("🎉 VIP Code Redeemed! +${"%,d".format(reward)} credits added to your ledger.")
        } else {
            showNotification(result.exceptionOrNull()?.message ?: "Failed to redeem code", isSuccess = false)
        }
    }

    fun addNewVipCode(code: String, reward: Int, maxUsers: Int) {
        repository.addVipCode(code, reward, maxUsers)
        showNotification("VIP Code '$code' created!")
    }

    fun toggleVipCode(code: String) {
        repository.toggleVipCodeStatus(code)
    }

    fun updateAiConfig(config: AIProviderConfig) {
        repository.updateAiConfig(config)
        showNotification("AI Provider settings updated.")
    }

    fun showNotification(msg: String, isSuccess: Boolean = true) {
        _notification.value = UiNotification(msg, isSuccess)
        viewModelScope.launch {
            delay(3500)
            if (_notification.value?.message == msg) {
                _notification.value = null
            }
        }
    }

    fun clearNotification() {
        _notification.value = null
    }

    private fun formatTime(seconds: Float): String {
        val m = (seconds / 60).toInt()
        val s = (seconds % 60).toInt()
        return "%02d:%02d".format(m, s)
    }
}
