package com.example.model

enum class TransactionType(val displayName: String, val isPositive: Boolean) {
    SIGNUP_BONUS("Initial Signup Bonus", true),
    VIDEO_PROCESSING("AI Video Processing", false),
    VIP_REDEMPTION("VIP Code Redemption", true),
    PURCHASE_REFILL("Credit Pack Purchase", true),
    ADMIN_ADJUSTMENT("System / Admin Grant", true)
}

data class CreditTransaction(
    val id: String,
    val timestamp: Long,
    val amount: Int,
    val transactionType: TransactionType,
    val projectId: String? = null,
    val projectTitle: String? = null,
    val balanceAfter: Int,
    val description: String
)

data class VipCode(
    val code: String,
    val rewardCredits: Int = 100_000,
    val maxRedemptions: Int = 10,
    val currentRedemptions: Int = 0,
    val expiresAt: Long? = null,
    val isActive: Boolean = true
)

data class UserAccount(
    val id: String = "usr_viral_01",
    val name: String = "Alex Rivera",
    val email: String = "alex.creator@viralclip.studio",
    val avatarUrl: String? = null,
    val creditsBalance: Int = 200,
    val planName: String = "Creator Pro",
    val totalMinutesProcessed: Float = 42.5f,
    val maxMinutesLimit: Int = 300,
    val maxStorageGb: Float = 10.0f,
    val currentStorageGb: Float = 1.85f,
    val maxFilesLimit: Int = 10,
    val totalProjects: Int = 4,
    val totalClipsGenerated: Int = 28,
    val costPer10Minutes: Int = 2,
    val isProcessingEnabled: Boolean = true
)
