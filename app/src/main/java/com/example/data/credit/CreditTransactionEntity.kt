package com.example.data.credit

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.CreditTransaction
import com.example.model.TransactionType

@Entity(tableName = "credit_transactions")
data class CreditTransactionEntity(
    @PrimaryKey
    val id: String,
    val userId: String = "usr_viral_01",
    val timestamp: Long,
    val amount: Int,
    val transactionType: String,
    val projectId: String? = null,
    val projectTitle: String? = null,
    val balanceAfter: Int,
    val description: String
) {
    fun toDomainModel(): CreditTransaction {
        return CreditTransaction(
            id = id,
            timestamp = timestamp,
            amount = amount,
            transactionType = try {
                TransactionType.valueOf(transactionType)
            } catch (e: Exception) {
                TransactionType.ADMIN_ADJUSTMENT
            },
            projectId = projectId,
            projectTitle = projectTitle,
            balanceAfter = balanceAfter,
            description = description
        )
    }

    companion object {
        fun fromDomainModel(domain: CreditTransaction, userId: String = "usr_viral_01"): CreditTransactionEntity {
            return CreditTransactionEntity(
                id = domain.id,
                userId = userId,
                timestamp = domain.timestamp,
                amount = domain.amount,
                transactionType = domain.transactionType.name,
                projectId = domain.projectId,
                projectTitle = domain.projectTitle,
                balanceAfter = domain.balanceAfter,
                description = domain.description
            )
        }
    }
}
