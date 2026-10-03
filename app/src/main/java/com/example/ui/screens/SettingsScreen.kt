package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userAccount by viewModel.userAccount.collectAsState()

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
                        text = "Account & Studio Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Manage credentials, plan tiers, and studio preferences",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Profile Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(CyanGlow.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = userAccount.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = userAccount.email, fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldNeon.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Google Account Connected",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldNeon,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    var showAuthModal by remember { mutableStateOf(false) }

                    IconButton(onClick = { showAuthModal = true }) {
                        Icon(imageVector = Icons.Default.SwitchAccount, contentDescription = "Switch Account", tint = CyanGlow)
                    }

                    if (showAuthModal) {
                        var emailInput by remember { mutableStateOf("") }
                        var passwordInput by remember { mutableStateOf("") }
                        var isSignUp by remember { mutableStateOf(false) }

                        AlertDialog(
                            onDismissRequest = { showAuthModal = false },
                            title = {
                                Text(
                                    text = if (isSignUp) "Create ViralClip Account" else "Sign In to ViralClip Studio",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Column {
                                    Text(
                                        text = "Access your saved projects, credit ledger, and AI presets across devices.",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Continue with Google Button
                                    Button(
                                        onClick = {
                                            viewModel.showNotification("Signed in with Google as Alex Rivera")
                                            showAuthModal = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard, contentColor = TextPrimary),
                                        border = BorderStroke(1.dp, SurfaceCardBorder),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = CyanGlow)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Continue with Google", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "— OR WITH EMAIL —",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMuted,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = emailInput,
                                        onValueChange = { emailInput = it },
                                        label = { Text("Email Address", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = passwordInput,
                                        onValueChange = { passwordInput = it },
                                        label = { Text("Password", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        TextButton(onClick = { viewModel.showNotification("Password reset email sent!") }) {
                                            Text("Forgot Password?", fontSize = 11.sp, color = CyanGlow)
                                        }
                                        TextButton(onClick = { isSignUp = !isSignUp }) {
                                            Text(if (isSignUp) "Have account? Sign in" else "New? Sign up", fontSize = 11.sp, color = PurpleGlow)
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.showNotification("Welcome to ViralClip Studio!")
                                        showAuthModal = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = Color(0xFF00363F))
                                ) {
                                    Text(if (isSignUp) "Sign Up" else "Continue with Email", fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAuthModal = false }) {
                                    Text("Cancel", color = TextSecondary)
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Credit Ledger Shortcut
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, CyanGlow.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(AppScreen.CREDIT_LEDGER) }
                    .testTag("settings_credit_ledger_shortcut")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Credit History & Ledger", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "${"%,d".format(userAccount.creditsBalance)} credits available • View all transactions", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Plan Quotas & Governance Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
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
                        Text(text = "Current Plan: ${userAccount.planName}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Surface(shape = RoundedCornerShape(6.dp), color = CyanGlow.copy(alpha = 0.2f)) {
                            Text(text = "ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanGlow, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "• Monthly Limit: ${userAccount.maxMinutesLimit} minutes (${"%.1f".format(userAccount.totalMinutesProcessed)}m used)", fontSize = 12.sp, color = TextSecondary)
                    Text(text = "• Cloud Storage: ${userAccount.maxStorageGb.toInt()} GB target (${"%.1f".format(userAccount.currentStorageGb)} GB used)", fontSize = 12.sp, color = TextSecondary)
                    Text(text = "• Max Video Files per Project: ${userAccount.maxFilesLimit} files", fontSize = 12.sp, color = TextSecondary)
                    Text(text = "• Processing Rate: ${userAccount.costPer10Minutes} credits per 10 minutes", fontSize = 12.sp, color = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Admin Dashboard Shortcut Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, PurpleNeon.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(AppScreen.ADMIN_PANEL) }
                    .testTag("settings_admin_panel_shortcut")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = PurpleGlow, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Admin & Studio Governance", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "VIP promo codes, provider switching, system limits", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = AmberNeon, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Gemini API Key Configuration", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "To use your personal Gemini API key, configure it securely in the Secrets panel in AI Studio (GEMINI_API_KEY). Built-in offline fallback is active automatically.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // About Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "About ViralClip Studio", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Version 1.0.0 (Build 2026)", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ViralClip Studio uses advanced AI multimodal reasoning, hook detection heuristics, dynamic reframing, and animated speech-to-text captions to help content creators scale short-form engagement.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
