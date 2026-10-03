package com.example.data.credit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditDao {

    @Query("SELECT * FROM credit_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsFlow(userId: String = "usr_viral_01"): Flow<List<CreditTransactionEntity>>

    @Query("SELECT * FROM credit_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllTransactions(userId: String = "usr_viral_01"): List<CreditTransactionEntity>

    @Query("SELECT * FROM credit_transactions WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestTransaction(userId: String = "usr_viral_01"): CreditTransactionEntity?

    @Query("SELECT COALESCE(SUM(amount), 0) FROM credit_transactions WHERE userId = :userId")
    suspend fun calculateTotalBalance(userId: String = "usr_viral_01"): Int

    @Query("SELECT COUNT(*) FROM credit_transactions WHERE userId = :userId")
    suspend fun getTransactionCount(userId: String = "usr_viral_01"): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CreditTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<CreditTransactionEntity>)

    @Query("DELETE FROM credit_transactions WHERE userId = :userId")
    suspend fun clearTransactionsForUser(userId: String = "usr_viral_01")
}
