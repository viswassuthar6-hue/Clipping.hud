package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

@Composable
fun ViralClipApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val notification by viewModel.notification.collectAsState()

    // Handle system back navigation
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    Scaffold(
        containerColor = DarkBg,
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            ViralClipBottomBar(
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Adaptive container width constraint for tablets / foldables
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 840.dp)
                    .align(Alignment.TopCenter)
            ) {
                when (currentScreen) {
                    AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    AppScreen.UPLOAD_AND_TRIM -> UploadAndTrimScreen(viewModel = viewModel)
                    AppScreen.CLIPS_LIST -> ClipsListScreen(viewModel = viewModel)
                    AppScreen.CLIP_EDITOR -> ClipEditorScreen(viewModel = viewModel)
                    AppScreen.CREDIT_LEDGER -> CreditLedgerScreen(viewModel = viewModel)
                    AppScreen.ADMIN_PANEL -> AdminPanelScreen(viewModel = viewModel)
                    AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }

            // Notification Banner Overlay
            AnimatedVisibility(
                visible = notification != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp)
            ) {
                notification?.let { notif ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (notif.isSuccess) SurfaceDark else Color(0xFF450A0A),
                        border = BorderStroke(1.dp, if (notif.isSuccess) CyanGlow else RoseError),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp)
                            .testTag("app_notification_toast")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = if (notif.isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (notif.isSuccess) CyanGlow else RoseError,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = notif.message,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearNotification() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ViralClipBottomBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    Surface(
        color = SurfaceDark,
        tonalElevation = 8.dp,
        border = BorderStroke(0.5.dp, SurfaceCardBorder)
    ) {
        NavigationBar(
            containerColor = SurfaceDark,
            contentColor = CyanGlow,
            tonalElevation = 0.dp,
            modifier = Modifier
                .height(64.dp)
                .testTag("main_navigation_bar")
        ) {
            NavigationBarItem(
                selected = currentScreen == AppScreen.DASHBOARD,
                onClick = { onNavigate(AppScreen.DASHBOARD) },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                label = { Text("Home", fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanGlow,
                    selectedTextColor = CyanGlow,
                    indicatorColor = CyanGlow.copy(alpha = 0.15f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_dashboard")
            )

            NavigationBarItem(
                selected = currentScreen == AppScreen.UPLOAD_AND_TRIM,
                onClick = { onNavigate(AppScreen.UPLOAD_AND_TRIM) },
                icon = { Icon(Icons.Default.ContentCut, contentDescription = "Upload & Trim") },
                label = { Text("Trim", fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanGlow,
                    selectedTextColor = CyanGlow,
                    indicatorColor = CyanGlow.copy(alpha = 0.15f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_trim")
            )

            NavigationBarItem(
                selected = currentScreen == AppScreen.CLIPS_LIST,
                onClick = { onNavigate(AppScreen.CLIPS_LIST) },
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Clips") },
                label = { Text("Clips", fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanGlow,
                    selectedTextColor = CyanGlow,
                    indicatorColor = CyanGlow.copy(alpha = 0.15f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_clips")
            )

            NavigationBarItem(
                selected = currentScreen == AppScreen.CLIP_EDITOR,
                onClick = { onNavigate(AppScreen.CLIP_EDITOR) },
                icon = { Icon(Icons.Default.Tune, contentDescription = "Editor") },
                label = { Text("Editor", fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanGlow,
                    selectedTextColor = CyanGlow,
                    indicatorColor = CyanGlow.copy(alpha = 0.15f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_editor")
            )

            NavigationBarItem(
                selected = currentScreen == AppScreen.CREDIT_LEDGER,
                onClick = { onNavigate(AppScreen.CREDIT_LEDGER) },
                icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Ledger") },
                label = { Text("Ledger", fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanGlow,
                    selectedTextColor = CyanGlow,
                    indicatorColor = CyanGlow.copy(alpha = 0.15f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_ledger")
            )
        }
    }
}
