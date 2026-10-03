package com.example.data

import com.example.data.credit.CreditService
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.max

class ClipRepository(
    customCreditService: CreditService? = null
) {
    val creditService: CreditService? = customCreditService ?: try {
        val app = com.example.ViralClipApplication.instance
        CreditService(com.example.ViralClipApplication.database.creditDao())
    } catch (e: Throwable) {
        null
    }

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _projects = MutableStateFlow<List<Project>>(SampleDataProvider.createInitialProjects())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _clips = MutableStateFlow<List<ViralClip>>(SampleDataProvider.createInitialClips())
    val clips: StateFlow<List<ViralClip>> = _clips.asStateFlow()

    private val _transactions = MutableStateFlow<List<CreditTransaction>>(SampleDataProvider.createInitialTransactions())
    val transactions: StateFlow<List<CreditTransaction>> = _transactions.asStateFlow()

    private val _vipCodes = MutableStateFlow<List<VipCode>>(SampleDataProvider.createInitialVipCodes())
    val vipCodes: StateFlow<List<VipCode>> = _vipCodes.asStateFlow()

    private val _userAccount = MutableStateFlow(
        UserAccount(
            creditsBalance = 186, // Remaining after initial sample deductions
            totalProjects = 3,
            totalClipsGenerated = 15
        )
    )
    val userAccount: StateFlow<UserAccount> = _userAccount.asStateFlow()

    private val _aiConfig = MutableStateFlow(AIProviderConfig())
    val aiConfig: StateFlow<AIProviderConfig> = _aiConfig.asStateFlow()

    private val _activeProjectId = MutableStateFlow<String?>("proj_01")
    val activeProjectId: StateFlow<String?> = _activeProjectId.asStateFlow()

    private val _activeClipId = MutableStateFlow<String?>("clip_01")
    val activeClipId: StateFlow<String?> = _activeClipId.asStateFlow()

    init {
        creditService?.let { service ->
            repositoryScope.launch {
                try {
                    service.initializeIfEmpty(
                        initialTransactions = SampleDataProvider.createInitialTransactions()
                    )
                    val currentBal = service.getCurrentBalance()
                    if (currentBal > 0) {
                        _userAccount.value = _userAccount.value.copy(creditsBalance = currentBal)
                    }

                    service.getTransactionsStream().collect { roomTransactions ->
                        if (roomTransactions.isNotEmpty()) {
                            _transactions.value = roomTransactions
                            _userAccount.value = _userAccount.value.copy(creditsBalance = roomTransactions.first().balanceAfter)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun calculateJobCost(durationSeconds: Float): Int {
        return creditService?.calculateCost(durationSeconds, _userAccount.value.costPer10Minutes)
            ?: max(2, ceil(durationSeconds / 600f).toInt() * _userAccount.value.costPer10Minutes)
    }

    suspend fun validateJobBalance(requiredCredits: Int): Boolean {
        return creditService?.validateBalanceForJob(requiredCredits = requiredCredits)
            ?: (_userAccount.value.creditsBalance >= requiredCredits)
    }

    fun setActiveProject(projectId: String?) {
        _activeProjectId.value = projectId
    }

    fun setActiveClip(clipId: String?) {
        _activeClipId.value = clipId
    }

    fun addProject(
        title: String,
        sourceUrl: String,
        sourceType: VideoSourceType,
        durationSeconds: Float,
        fileSizeMb: Float,
        language: String,
        targetDurationMode: String,
        clipCountTarget: Int
    ): Project {
        val newProj = Project(
            id = "proj_${System.currentTimeMillis() % 100000}",
            title = title.ifBlank { "Untitled Project" },
            sourceUrl = sourceUrl,
            sourceType = sourceType,
            durationSeconds = durationSeconds,
            fileSizeMb = fileSizeMb,
            status = ProjectStatus.DRAFT,
            language = language,
            trimmedStartSec = 0f,
            trimmedEndSec = durationSeconds,
            isTrimmedSaved = false,
            clipCountTarget = clipCountTarget,
            targetDurationMode = targetDurationMode,
            thumbnailPlaceholderColor = when ((0..4).random()) {
                0 -> 0xFF0D9488
                1 -> 0xFF7C3AED
                2 -> 0xFF2563EB
                3 -> 0xFFDB2777
                else -> 0xFFEA580C
            }
        )
        _projects.value = listOf(newProj) + _projects.value
        _userAccount.value = _userAccount.value.copy(
            totalProjects = _userAccount.value.totalProjects + 1
        )
        _activeProjectId.value = newProj.id
        return newProj
    }

    fun updateProjectTrim(projectId: String, startSec: Float, endSec: Float, markAsSaved: Boolean) {
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                proj.copy(
                    trimmedStartSec = startSec.coerceAtLeast(0f),
                    trimmedEndSec = endSec.coerceAtMost(proj.durationSeconds),
                    isTrimmedSaved = markAsSaved,
                    status = if (markAsSaved) ProjectStatus.TRIMMED else proj.status
                )
            } else proj
        }
    }

    fun addClipsForProject(projectId: String, newClips: List<ViralClip>) {
        _clips.value = newClips + _clips.value.filter { it.projectId != projectId }
        _projects.value = _projects.value.map { proj ->
            if (proj.id == projectId) {
                proj.copy(status = ProjectStatus.COMPLETED)
            } else proj
        }
        _userAccount.value = _userAccount.value.copy(
            totalClipsGenerated = _userAccount.value.totalClipsGenerated + newClips.size
        )
        if (newClips.isNotEmpty()) {
            _activeClipId.value = newClips.first().id
        }
    }

    fun updateClipSelection(
        clipId: String,
        selectedTitleIndex: Int? = null,
        customTitle: String? = null,
        customHook: String? = null,
        aspectRatio: AspectRatioType? = null,
        style: ClipStyle? = null,
        backgroundMode: BackgroundMode? = null,
        autoEmoji: Boolean? = null,
        captionPosition: CaptionPosition? = null,
        fontSize: Int? = null,
        activeWordColor: Long? = null,
        zoomFactor: Float? = null,
        audioVolume: Float? = null
    ) {
        _clips.value = _clips.value.map { clip ->
            if (clip.id == clipId) {
                clip.copy(
                    selectedOptionIndex = selectedTitleIndex ?: clip.selectedOptionIndex,
                    customTitle = if (customTitle != null) customTitle else clip.customTitle,
                    customHook = if (customHook != null) customHook else clip.customHook,
                    activeAspectRatio = aspectRatio ?: clip.activeAspectRatio,
                    activeStyle = style ?: clip.activeStyle,
                    backgroundMode = backgroundMode ?: clip.backgroundMode,
                    autoEmojiEnabled = autoEmoji ?: clip.autoEmojiEnabled,
                    captionPosition = captionPosition ?: clip.captionPosition,
                    fontSize = fontSize ?: clip.fontSize,
                    activeWordColor = activeWordColor ?: clip.activeWordColor,
                    zoomFactor = zoomFactor ?: clip.zoomFactor,
                    audioVolume = audioVolume ?: clip.audioVolume
                )
            } else clip
        }
    }

    fun splitClip(clipId: String, splitOffsetSec: Float): ViralClip? {
        val original = _clips.value.find { it.id == clipId } ?: return null
        val splitPoint = (original.startSec + splitOffsetSec).coerceIn(original.startSec + 3f, original.endSec - 3f)

        val updatedOriginal = original.copy(endSec = splitPoint)
        val newClip = original.copy(
            id = "clip_${System.currentTimeMillis() % 100000}",
            startSec = splitPoint,
            endSec = original.endSec,
            customTitle = "${original.currentTitle} (Part 2)"
        )

        _clips.value = _clips.value.map { if (it.id == clipId) updatedOriginal else it } + listOf(newClip)
        _activeClipId.value = newClip.id
        return newClip
    }

    fun deleteClip(clipId: String) {
        _clips.value = _clips.value.filter { it.id != clipId }
        if (_activeClipId.value == clipId) {
            _activeClipId.value = _clips.value.firstOrNull()?.id
        }
    }

    fun updateCostPer10Min(cost: Int) {
        _userAccount.value = _userAccount.value.copy(costPer10Minutes = cost.coerceAtLeast(1))
    }

    fun updateStorageLimit(gb: Float) {
        _userAccount.value = _userAccount.value.copy(maxStorageGb = gb.coerceAtLeast(1f))
    }

    fun updateMinuteLimit(minutes: Int) {
        _userAccount.value = _userAccount.value.copy(maxMinutesLimit = minutes.coerceAtLeast(10))
    }

    fun updateMaxFilesLimit(maxFiles: Int) {
        _userAccount.value = _userAccount.value.copy(maxFilesLimit = maxFiles.coerceAtLeast(1))
    }

    fun toggleProcessingEnabled() {
        _userAccount.value = _userAccount.value.copy(isProcessingEnabled = !_userAccount.value.isProcessingEnabled)
    }

    /**
     * Credit Transaction Execution with Server-like Ledger Integrity
     */
    fun deductCreditsForProcessing(projectId: String, amount: Int, description: String): Result<Int> {
        val currentBalance = _userAccount.value.creditsBalance
        if (currentBalance < amount) {
            return Result.failure(Exception("Insufficient credits: Required $amount, available $currentBalance."))
        }

        val newBalance = currentBalance - amount
        val proj = _projects.value.find { it.id == projectId }

        val tx = CreditTransaction(
            id = "tx_${UUID.randomUUID().toString().take(8)}",
            timestamp = System.currentTimeMillis(),
            amount = -amount,
            transactionType = TransactionType.VIDEO_PROCESSING,
            projectId = projectId,
            projectTitle = proj?.title ?: "Video Project",
            balanceAfter = newBalance,
            description = description
        )

        _transactions.value = listOf(tx) + _transactions.value
        _userAccount.value = _userAccount.value.copy(
            creditsBalance = newBalance,
            totalMinutesProcessed = _userAccount.value.totalMinutesProcessed + ((proj?.activeDurationSec ?: 0f) / 60f)
        )

        creditService?.let { service ->
            repositoryScope.launch {
                try {
                    service.addCredits(
                        amount = -amount,
                        type = TransactionType.VIDEO_PROCESSING,
                        description = description,
                        projectId = projectId,
                        projectTitle = proj?.title
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return Result.success(newBalance)
    }

    /**
     * Redeem VIP Code (e.g. VIRAL100K for 100,000 credits)
     */
    fun redeemVipCode(rawCode: String): Result<Int> {
        val codeTrimmed = rawCode.trim().uppercase()
        val matchingCode = _vipCodes.value.find { it.code.uppercase() == codeTrimmed }
            ?: return Result.failure(Exception("Invalid VIP code: '$codeTrimmed' does not exist."))

        if (!matchingCode.isActive) {
            return Result.failure(Exception("This VIP code is currently disabled or expired."))
        }

        if (matchingCode.currentRedemptions >= matchingCode.maxRedemptions) {
            return Result.failure(Exception("This VIP code has reached its maximum redemptions limit (${matchingCode.maxRedemptions} users)."))
        }

        val reward = matchingCode.rewardCredits
        val currentBalance = _userAccount.value.creditsBalance
        val newBalance = currentBalance + reward

        // Update code redemption count
        _vipCodes.value = _vipCodes.value.map { code ->
            if (code.code.uppercase() == codeTrimmed) {
                code.copy(currentRedemptions = code.currentRedemptions + 1)
            } else code
        }

        // Add ledger record
        val tx = CreditTransaction(
            id = "tx_${UUID.randomUUID().toString().take(8)}",
            timestamp = System.currentTimeMillis(),
            amount = reward,
            transactionType = TransactionType.VIP_REDEMPTION,
            projectId = null,
            projectTitle = null,
            balanceAfter = newBalance,
            description = "VIP Code '$codeTrimmed' redeemed successfully for +${"%,d".format(reward)} credits"
        )

        _transactions.value = listOf(tx) + _transactions.value
        _userAccount.value = _userAccount.value.copy(creditsBalance = newBalance)

        creditService?.let { service ->
            repositoryScope.launch {
                try {
                    service.addCredits(
                        amount = reward,
                        type = TransactionType.VIP_REDEMPTION,
                        description = tx.description
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return Result.success(reward)
    }

    fun addVipCode(code: String, rewardCredits: Int, maxRedemptions: Int) {
        val newCode = VipCode(
            code = code.trim().uppercase(),
            rewardCredits = rewardCredits,
            maxRedemptions = maxRedemptions,
            currentRedemptions = 0,
            isActive = true
        )
        _vipCodes.value = listOf(newCode) + _vipCodes.value
    }

    fun toggleVipCodeStatus(code: String) {
        _vipCodes.value = _vipCodes.value.map {
            if (it.code == code) it.copy(isActive = !it.isActive) else it
        }
    }

    fun updateAiConfig(config: AIProviderConfig) {
        _aiConfig.value = config
    }
}
