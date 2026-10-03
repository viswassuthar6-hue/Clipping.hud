package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AIProviderConfig
import com.example.model.AIProviderType
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun AdminPanelScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val vipCodes by viewModel.vipCodes.collectAsState()
    val aiConfig by viewModel.aiConfig.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()

    var showCreateVipModal by remember { mutableStateOf(false) }
    var newCodeName by remember { mutableStateOf("") }
    var newCodeReward by remember { mutableStateOf("100000") }
    var newCodeMaxUsers by remember { mutableStateOf("10") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Header
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
                        text = "Admin & Studio Controls",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "System health, VIP promo codes, AI provider settings",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // System Health Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "SYSTEM STATUS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (userAccount.isProcessingEnabled) EmeraldNeon.copy(alpha = 0.15f) else RoseError.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, if (userAccount.isProcessingEnabled) EmeraldNeon.copy(alpha = 0.4f) else RoseError.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = if (userAccount.isProcessingEnabled) "HEALTHY • ALL SERVICES ONLINE" else "PROCESSING PAUSED (MAINTENANCE)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (userAccount.isProcessingEnabled) EmeraldNeon else RoseError,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AdminStatItem(label = "Active Provider", value = aiConfig.activeProvider.displayName.split(" ").first())
                        AdminStatItem(label = "Latency", value = "18 ms")
                        AdminStatItem(label = "Failed Jobs", value = "0")
                        AdminStatItem(label = "Cost / 10m", value = "${userAccount.costPer10Minutes} cr")
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Admin System Limits & Processing Controls
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Global Studio Limits & Cost Governance",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enforce quotas, storage thresholds, and video processing costs.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Processing Toggle Kill Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Global AI Processing Pipeline", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(text = if (userAccount.isProcessingEnabled) "Normal operations enabled" else "Processing paused for users", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = userAccount.isProcessingEnabled,
                            onCheckedChange = { viewModel.toggleAdminProcessing() },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanGlow, checkedTrackColor = CyanGlow.copy(alpha = 0.3f))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Configurable Credit Cost Per 10 Min
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Credit Cost per 10 Min Source", fontSize = 12.sp, color = TextPrimary)
                            Text(text = "Current: ${userAccount.costPer10Minutes} credits per 10 min", fontSize = 11.sp, color = TextMuted)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.updateAdminCostPer10Min(userAccount.costPer10Minutes - 1) }) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = TextSecondary, modifier = Modifier.size(18.dp))
                            }
                            Text(text = "${userAccount.costPer10Minutes}", fontWeight = FontWeight.Bold, color = CyanGlow, fontSize = 14.sp)
                            IconButton(onClick = { viewModel.updateAdminCostPer10Min(userAccount.costPer10Minutes + 1) }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = CyanGlow, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Storage and Minute Limits
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceCard,
                            border = BorderStroke(1.dp, SurfaceCardBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "MAX STORAGE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "${userAccount.maxStorageGb.toInt()} GB", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row {
                                    TextButton(
                                        onClick = { viewModel.updateAdminStorageLimit(10f) },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) { Text("10GB", fontSize = 10.sp, color = CyanGlow) }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    TextButton(
                                        onClick = { viewModel.updateAdminStorageLimit(25f) },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) { Text("25GB", fontSize = 10.sp, color = CyanGlow) }
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceCard,
                            border = BorderStroke(1.dp, SurfaceCardBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "MAX MINUTES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "${userAccount.maxMinutesLimit} min", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row {
                                    TextButton(
                                        onClick = { viewModel.updateAdminMinuteLimit(300) },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) { Text("300m", fontSize = 10.sp, color = PurpleGlow) }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    TextButton(
                                        onClick = { viewModel.updateAdminMinuteLimit(600) },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) { Text("600m", fontSize = 10.sp, color = PurpleGlow) }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Live Background Job Queue
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Live Job Queue & Worker State", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "3 Recent Jobs", fontSize = 11.sp, color = CyanGlow)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    val sampleJobs = listOf(
                        Triple("JOB-8921", "Scene & Hook Detection", "COMPLETED"),
                        Triple("JOB-8922", "Word-Level Caption Render", "COMPLETED"),
                        Triple("JOB-8923", "9:16 Smart Crop Reframe", "COMPLETED")
                    )

                    sampleJobs.forEach { (jobId, task, status) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = jobId, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanGlow)
                                Text(text = task, fontSize = 11.sp, color = TextSecondary)
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldNeon.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = status,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldNeon,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // VIP Code Management Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VIP Promo Codes (${vipCodes.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = { showCreateVipModal = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Create Code", color = CyanGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(vipCodes) { vip ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("admin_vip_code_${vip.code}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = vip.code,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = CyanGlow
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (vip.isActive) EmeraldNeon.copy(alpha = 0.15f) else RoseError.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (vip.isActive) "ACTIVE" else "DISABLED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (vip.isActive) EmeraldNeon else RoseError,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+${"%,d".format(vip.rewardCredits)} credits • ${vip.currentRedemptions} / ${vip.maxRedemptions} used",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = vip.isActive,
                        onCheckedChange = { viewModel.toggleVipCode(vip.code) },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyanGlow, checkedTrackColor = CyanGlow.copy(alpha = 0.3f))
                    )
                }
            }
        }

        // AI Provider Architecture Settings
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Modular AI Provider",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "The clipping engine connects to modular providers without code changes.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AIProviderType.values().forEach { providerType ->
                        val isSelected = aiConfig.activeProvider == providerType
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateAiConfig(aiConfig.copy(activeProvider = providerType, modelName = providerType.defaultModel))
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.updateAiConfig(aiConfig.copy(activeProvider = providerType, modelName = providerType.defaultModel)) },
                                colors = RadioButtonDefaults.colors(selectedColor = CyanGlow)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = providerType.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) CyanGlow else TextPrimary
                                )
                                Text(
                                    text = "Default Model: ${providerType.defaultModel}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    var keyInput by remember(aiConfig.customApiKey) { mutableStateOf(aiConfig.customApiKey) }
                    var modelInput by remember(aiConfig.modelName) { mutableStateOf(aiConfig.modelName) }

                    OutlinedTextField(
                        value = modelInput,
                        onValueChange = {
                            modelInput = it
                            viewModel.updateAiConfig(aiConfig.copy(modelName = it))
                        },
                        label = { Text("Model Name", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = {
                            keyInput = it
                            viewModel.updateAiConfig(aiConfig.copy(customApiKey = it))
                        },
                        label = { Text("Custom API Key (Optional)", fontSize = 11.sp) },
                        placeholder = { Text("Managed via AI Studio secrets or enter override", fontSize = 10.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // Modal to create VIP Code
    if (showCreateVipModal) {
        AlertDialog(
            onDismissRequest = { showCreateVipModal = false },
            title = {
                Text(text = "Create VIP Promo Code", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newCodeName,
                        onValueChange = { newCodeName = it },
                        label = { Text("Code (e.g. VIRAL100K)", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCodeReward,
                        onValueChange = { newCodeReward = it },
                        label = { Text("Reward Credits", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCodeMaxUsers,
                        onValueChange = { newCodeMaxUsers = it },
                        label = { Text("Max Redemptions", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val reward = newCodeReward.toIntOrNull() ?: 100000
                        val maxUsers = newCodeMaxUsers.toIntOrNull() ?: 10
                        if (newCodeName.isNotBlank()) {
                            viewModel.addNewVipCode(newCodeName, reward, maxUsers)
                            showCreateVipModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = Color(0xFF00363F))
                ) {
                    Text("Create Code", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateVipModal = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun AdminStatItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
