package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CreditTransaction
import com.example.model.TransactionType
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LedgerFilter(val label: String) {
    ALL("All Activity"),
    PROCESSING("Processing (-)"),
    VIP_AND_BONUS("VIP & Grants (+)"),
    PURCHASES("Purchases")
}

@Composable
fun CreditLedgerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userAccount by viewModel.userAccount.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var vipCodeInput by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf(LedgerFilter.ALL) }

    val filteredTransactions = remember(transactions, activeFilter) {
        when (activeFilter) {
            LedgerFilter.ALL -> transactions
            LedgerFilter.PROCESSING -> transactions.filter { it.transactionType == TransactionType.VIDEO_PROCESSING }
            LedgerFilter.VIP_AND_BONUS -> transactions.filter {
                it.transactionType == TransactionType.VIP_REDEMPTION ||
                it.transactionType == TransactionType.SIGNUP_BONUS ||
                it.transactionType == TransactionType.ADMIN_ADJUSTMENT
            }
            LedgerFilter.PURCHASES -> transactions.filter { it.transactionType == TransactionType.PURCHASE_REFILL }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Screen Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Credit History & Ledger",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Real-time transparent audit log of all balance changes",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Current Balance Summary Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, CyanGlow.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("credit_balance_summary_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "CURRENT AVAILABLE BALANCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${"%,d".format(userAccount.creditsBalance)} Credits",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = CyanGlow
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PurpleNeon.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, PurpleNeon.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = userAccount.planName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleGlow,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = SurfaceCardBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Cost Rate", fontSize = 11.sp, color = TextMuted)
                            Text(text = "2 Credits / 10 Mins", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                        Column {
                            Text(text = "Processed Time", fontSize = 11.sp, color = TextMuted)
                            Text(text = "${"%.1f".format(userAccount.totalMinutesProcessed)} Mins", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                        Column {
                            Text(text = "Total Entries", fontSize = 11.sp, color = TextMuted)
                            Text(text = "${transactions.size} records", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // VIP Code Redemption Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, tint = AmberNeon, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Redeem VIP Promo Code", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter a creator VIP code (e.g. VIRAL100K) to claim instant bonus credits.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = vipCodeInput,
                            onValueChange = { vipCodeInput = it },
                            placeholder = { Text("e.g. VIRAL100K", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("vip_code_input_field")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (vipCodeInput.isNotBlank()) {
                                    viewModel.redeemVipCode(vipCodeInput)
                                    vipCodeInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberNeon, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("redeem_vip_code_button")
                        ) {
                            Text("Redeem", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Ledger Filters
        item {
            Text(text = "Transaction History", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(LedgerFilter.values()) { filter ->
                    val isSel = filter == activeFilter
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) CyanGlow.copy(alpha = 0.2f) else SurfaceCard,
                        border = BorderStroke(1.dp, if (isSel) CyanGlow else SurfaceCardBorder),
                        modifier = Modifier.clickable { activeFilter = filter }
                    ) {
                        Text(
                            text = filter.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) CyanGlow else TextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Transactions List
        items(filteredTransactions) { tx ->
            TransactionLedgerRow(tx)
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No transactions found in this category.", fontSize = 13.sp, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun TransactionLedgerRow(tx: CreditTransaction) {
    val isCreditGain = tx.amount > 0
    val amountColor = if (isCreditGain) EmeraldNeon else RoseError
    val formattedDate = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(tx.timestamp))

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, SurfaceCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ledger_item_${tx.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Type Badge + Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (tx.transactionType) {
                        TransactionType.VIDEO_PROCESSING -> RoseError.copy(alpha = 0.15f)
                        TransactionType.VIP_REDEMPTION -> AmberNeon.copy(alpha = 0.15f)
                        TransactionType.SIGNUP_BONUS -> CyanNeon.copy(alpha = 0.15f)
                        else -> EmeraldNeon.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = tx.transactionType.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (tx.transactionType) {
                            TransactionType.VIDEO_PROCESSING -> RoseError
                            TransactionType.VIP_REDEMPTION -> AmberNeon
                            TransactionType.SIGNUP_BONUS -> CyanGlow
                            else -> EmeraldNeon
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = if (isCreditGain) "+${"%,d".format(tx.amount)}" else "${"%,d".format(tx.amount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = amountColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = tx.description,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )

            // Project reference if available
            if (tx.projectTitle != null || tx.projectId != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Project: ${tx.projectTitle ?: tx.projectId}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = SurfaceCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Bottom metadata: Timestamp & Balance After Transaction
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = formattedDate, fontSize = 11.sp, color = TextMuted)
                Text(
                    text = "Balance after: ${"%,d".format(tx.balanceAfter)} Credits",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
        }
    }
}
