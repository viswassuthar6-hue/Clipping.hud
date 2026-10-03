package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ClipRepository
import com.example.data.credit.AppDatabase
import com.example.data.credit.CreditService
import com.example.ai.ClipDetectionEngine
import com.example.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ViralClip Studio", appName)
  }

  @Test
  fun `verify credit service calculation of video processing costs`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.createInMemory(context)
    val creditService = CreditService(db.creditDao())

    // 10 minutes = 600s -> 2 credits
    assertEquals(2, creditService.calculateCost(600f, costPer10Minutes = 2))

    // 30 minutes = 1800s -> 6 credits
    assertEquals(6, creditService.calculateCost(1800f, costPer10Minutes = 2))

    // 60 minutes = 3600s -> 12 credits
    assertEquals(12, creditService.calculateCost(3600f, costPer10Minutes = 2))

    // 5 minutes (under 10m) -> minimum 2 credits
    assertEquals(2, creditService.calculateCost(300f, costPer10Minutes = 2))

    // 15 minutes -> 4 credits
    assertEquals(4, creditService.calculateCost(900f, costPer10Minutes = 2))
  }

  @Test
  fun `verify credit service room backend persistence and balance validation`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.createInMemory(context)
    val creditService = CreditService(db.creditDao())

    // 1. Initialize default signup bonus (200 credits)
    creditService.initializeIfEmpty(userId = "test_user_01", defaultBonus = 200)
    assertEquals(200, creditService.getCurrentBalance("test_user_01"))

    // 2. Validate balance for jobs
    assertTrue(creditService.validateBalanceForJob("test_user_01", 20))
    assertTrue(creditService.validateBalanceForVideo("test_user_01", 3600f)) // 60 min = 12 credits <= 200
    assertFalse(creditService.validateBalanceForJob("test_user_01", 500))

    // 3. Deduct for processing
    val deductResult = creditService.deductForProcessing(
      userId = "test_user_01",
      projectId = "test_proj_1",
      projectTitle = "Founder Interview",
      durationSeconds = 1800f, // 30 min = 6 credits
      costPer10Minutes = 2
    )
    assertTrue(deductResult.isSuccess)
    assertEquals(194, creditService.getCurrentBalance("test_user_01"))

    // 4. Verify Flow stream from Room DAO
    val txList = creditService.getTransactionsStream("test_user_01").first()
    assertEquals(2, txList.size)
    assertEquals(-6, txList.first().amount)
    assertEquals(194, txList.first().balanceAfter)

    // 5. Test insufficient balance rejection
    val insufficientResult = creditService.deductForProcessing(
      userId = "test_user_01",
      projectId = "test_proj_2",
      projectTitle = "Mega Video",
      durationSeconds = 60000f, // 1000 min = 200 credits > 194
      costPer10Minutes = 2
    )
    assertTrue(insufficientResult.isFailure)
    assertEquals(194, creditService.getCurrentBalance("test_user_01"))

    db.close()
  }

  @Test
  fun `verify credit deduction and ledger integrity`() {
    val repository = ClipRepository()
    val initialBalance = repository.userAccount.value.creditsBalance

    // Deduct 4 credits for processing
    val result = repository.deductCreditsForProcessing("proj_01", 4, "Test processing deduction")
    assertTrue(result.isSuccess)
    assertEquals(initialBalance - 4, repository.userAccount.value.creditsBalance)

    // Check latest transaction
    val latestTx = repository.transactions.value.first()
    assertEquals(-4, latestTx.amount)
    assertEquals(TransactionType.VIDEO_PROCESSING, latestTx.transactionType)
    assertEquals(initialBalance - 4, latestTx.balanceAfter)
  }

  @Test
  fun `verify VIP code redemption rewards 100000 credits`() {
    val repository = ClipRepository()
    val initialBalance = repository.userAccount.value.creditsBalance

    val result = repository.redeemVipCode("VIRAL100K")
    assertTrue(result.isSuccess)
    assertEquals(initialBalance + 100_000, repository.userAccount.value.creditsBalance)

    val latestTx = repository.transactions.value.first()
    assertEquals(100_000, latestTx.amount)
    assertEquals(TransactionType.VIP_REDEMPTION, latestTx.transactionType)
  }

  @Test
  fun `verify clip engine generates at least three title and hook options`() {
    val project = Project(
      id = "test_p1",
      title = "Test Video",
      sourceUrl = "test://video.mp4",
      sourceType = VideoSourceType.UPLOAD,
      durationSeconds = 120f,
      fileSizeMb = 50f
    )

    val clips = ClipDetectionEngine.generateMoments(
      project = project,
      startSec = 0f,
      endSec = 120f,
      clipCount = 2,
      scoringWeights = ScoringWeights()
    )

    assertEquals(2, clips.size)
    for (clip in clips) {
      assertTrue("Each clip must have at least 3 title and hook options", clip.titleOptions.size >= 3)
      assertTrue("Viral score must be between 0 and 100", clip.viralScore in 0..100)
    }
  }
}

