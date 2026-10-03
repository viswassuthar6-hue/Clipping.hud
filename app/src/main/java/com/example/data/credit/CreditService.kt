package com.example.data.credit

import com.example.model.CreditTransaction
import com.example.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.max

class CreditService(
    private val creditDao: CreditDao
) {
    /**
     * Reactive Stream of Credit Transactions for UI Ledger
     */
    fun getTransactionsStream(userId: String = "usr_viral_01"): Flow<List<CreditTransaction>> {
        return creditDao.getTransactionsFlow(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Fetches current available balance from the latest transaction or ledger sum.
     */
    suspend fun getCurrentBalance(userId: String = "usr_viral_01"): Int {
        val latest = creditDao.getLatestTransaction(userId)
        return latest?.balanceAfter ?: creditDao.calculateTotalBalance(userId)
    }

    /**
     * Calculates required credits for a video processing job.
     * Business rule: 2 credits per 10 minutes of active video (minimum 2 credits).
     * Example:
     * - 10 min = 2 credits
     * - 30 min = 6 credits
     * - 60 min = 12 credits
     */
    fun calculateCost(durationSeconds: Float, costPer10Minutes: Int = 2): Int {
        val minutes = max(0f, durationSeconds) / 60f
        val units = ceil(minutes / 10f).toInt()
        val baseUnits = max(1, units)
        return max(costPer10Minutes, baseUnits * costPer10Minutes)
    }

    /**
     * Validates whether the user has sufficient balance to execute the processing job.
     */
    suspend fun validateBalanceForJob(userId: String = "usr_viral_01", requiredCredits: Int): Boolean {
        val currentBalance = getCurrentBalance(userId)
        return currentBalance >= requiredCredits
    }

    /**
     * Validates user balance specifically for a video with the given duration.
     */
    suspend fun validateBalanceForVideo(
        userId: String = "usr_viral_01",
        durationSeconds: Float,
        costPer10Minutes: Int = 2
    ): Boolean {
        val cost = calculateCost(durationSeconds, costPer10Minutes)
        return validateBalanceForJob(userId, cost)
    }

    /**
     * Atomically validates balance and executes credit deduction for a processing job.
     * Records transaction in the Room database.
     */
    suspend fun deductForProcessing(
        userId: String = "usr_viral_01",
        projectId: String,
        projectTitle: String,
        durationSeconds: Float,
        costPer10Minutes: Int = 2
    ): Result<CreditTransaction> {
        val cost = calculateCost(durationSeconds, costPer10Minutes)
        val currentBalance = getCurrentBalance(userId)

        if (currentBalance < cost) {
            return Result.failure(
                IllegalStateException("Insufficient credits: Required $cost credits, but available balance is $currentBalance.")
            )
        }

        val newBalance = currentBalance - cost
        val minutesText = "%.1f".format(durationSeconds / 60f)
        val tx = CreditTransaction(
            id = "tx_${UUID.randomUUID().toString().take(8)}",
            timestamp = System.currentTimeMillis(),
            amount = -cost,
            transactionType = TransactionType.VIDEO_PROCESSING,
            projectId = projectId,
            projectTitle = projectTitle,
            balanceAfter = newBalance,
            description = "Analyzed $minutesText min video for viral clips"
        )

        creditDao.insertTransaction(CreditTransactionEntity.fromDomainModel(tx, userId))
        return Result.success(tx)
    }

    /**
     * Adds credits to the user's ledger (e.g. signup bonus, VIP code, purchase, admin grant).
     */
    suspend fun addCredits(
        userId: String = "usr_viral_01",
        amount: Int,
        type: TransactionType,
        description: String,
        projectId: String? = null,
        projectTitle: String? = null
    ): Result<CreditTransaction> {
        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Credit amount must be greater than zero."))
        }

        val currentBalance = getCurrentBalance(userId)
        val newBalance = currentBalance + amount

        val tx = CreditTransaction(
            id = "tx_${UUID.randomUUID().toString().take(8)}",
            timestamp = System.currentTimeMillis(),
            amount = amount,
            transactionType = type,
            projectId = projectId,
            projectTitle = projectTitle,
            balanceAfter = newBalance,
            description = description
        )

        creditDao.insertTransaction(CreditTransactionEntity.fromDomainModel(tx, userId))
        return Result.success(tx)
    }

    /**
     * Initializes default balance / initial transactions if database is fresh.
     */
    suspend fun initializeIfEmpty(
        userId: String = "usr_viral_01",
        defaultBonus: Int = 200,
        initialTransactions: List<CreditTransaction> = emptyList()
    ) {
        val count = creditDao.getTransactionCount(userId)
        if (count == 0) {
            if (initialTransactions.isNotEmpty()) {
                val entities = initialTransactions.map { CreditTransactionEntity.fromDomainModel(it, userId) }
                creditDao.insertTransactions(entities)
            } else {
                val initialTx = CreditTransaction(
                    id = "tx_init_signup",
                    timestamp = System.currentTimeMillis() - (86400 * 1000 * 3),
                    amount = defaultBonus,
                    transactionType = TransactionType.SIGNUP_BONUS,
                    projectId = null,
                    projectTitle = null,
                    balanceAfter = defaultBonus,
                    description = "Welcome to ViralClip Studio signup bonus"
                )
                creditDao.insertTransaction(CreditTransactionEntity.fromDomainModel(initialTx, userId))
            }
        }
    }
}
