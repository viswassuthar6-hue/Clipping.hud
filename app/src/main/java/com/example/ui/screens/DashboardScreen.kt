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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Project
import com.example.model.ProjectStatus
import com.example.model.UserAccount
import com.example.model.ViralClip
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.ViralClipLogo
import com.example.ui.components.ViralScoreBadge
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userAccount by viewModel.userAccount.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val clips by viewModel.clips.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Hero Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CyanNeon.copy(alpha = 0.15f), PurpleNeon.copy(alpha = 0.15f), SurfaceDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ViralClipLogo(iconSize = 32.dp, fontSize = 18)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = CyanGlow.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, CyanGlow.copy(alpha = 0.4f)),
                                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.CREDIT_LEDGER) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Savings,
                                            contentDescription = null,
                                            tint = CyanGlow,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${"%,d".format(userAccount.creditsBalance)} Credits",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanGlow
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Welcome back, ${userAccount.name.split(" ").first()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "AI Video Clipping & Viral Highlight Studio • ${userAccount.planName} Plan",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Primary CTA: "Find Viral Clips"
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.UPLOAD_AND_TRIM) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = Color(0xFF00363F)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("primary_find_viral_clips_button")
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Find Viral Clips", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Dashboard Metrics Grid (6 Total Cards)
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Studio Analytics & Quota",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Row 1: Total Projects & Videos Processed
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    title = "Total Projects",
                    value = "${projects.size}",
                    icon = Icons.Default.Folder,
                    accentColor = CyanGlow,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                MetricCard(
                    title = "Videos Processed",
                    value = "${projects.count { it.status == ProjectStatus.COMPLETED }.coerceAtLeast(1)}",
                    icon = Icons.Default.PlayCircle,
                    accentColor = EmeraldNeon,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Clips Generated & Credits Remaining
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    title = "Clips Generated",
                    value = "${clips.size}",
                    icon = Icons.Default.ContentCut,
                    accentColor = PurpleGlow,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                MetricCard(
                    title = "Credits Remaining",
                    value = "${"%,d".format(userAccount.creditsBalance)}",
                    icon = Icons.Default.Savings,
                    accentColor = CyanGlow,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(AppScreen.CREDIT_LEDGER) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: Storage Used & Minutes Used
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    title = "Storage Used",
                    value = "${"%.1f".format(userAccount.currentStorageGb)} / ${userAccount.maxStorageGb.toInt()} GB",
                    icon = Icons.Default.CloudQueue,
                    accentColor = AmberNeon,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                MetricCard(
                    title = "Minutes Used",
                    value = "${"%.1f".format(userAccount.totalMinutesProcessed)} / ${userAccount.maxMinutesLimit}m",
                    icon = Icons.Default.Timer,
                    accentColor = PinkNeon,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Actions Row
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Quick Tools",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionButton(
                    icon = Icons.Default.VideoCall,
                    label = "Upload Video",
                    onClick = { viewModel.navigateTo(AppScreen.UPLOAD_AND_TRIM) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                QuickActionButton(
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    label = "Credit Ledger",
                    onClick = { viewModel.navigateTo(AppScreen.CREDIT_LEDGER) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                QuickActionButton(
                    icon = Icons.Default.CardGiftcard,
                    label = "Redeem VIP",
                    onClick = { viewModel.navigateTo(AppScreen.CREDIT_LEDGER) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                QuickActionButton(
                    icon = Icons.Default.AdminPanelSettings,
                    label = "Admin Settings",
                    onClick = { viewModel.navigateTo(AppScreen.ADMIN_PANEL) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recent Projects Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Projects",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = { viewModel.navigateTo(AppScreen.UPLOAD_AND_TRIM) }) {
                    Text(text = "+ New Project", color = CyanGlow, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(projects) { project ->
            ProjectCard(
                project = project,
                onClick = {
                    viewModel.selectProject(project.id)
                    viewModel.navigateTo(AppScreen.UPLOAD_AND_TRIM)
                },
                onViewClips = {
                    viewModel.selectProject(project.id)
                    viewModel.navigateTo(AppScreen.CLIPS_LIST)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Top Viral Highlights Preview
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Top Viral Highlights",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(clips.take(3)) { clip ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        viewModel.selectClip(clip.id)
                        viewModel.navigateTo(AppScreen.CLIP_EDITOR)
                    }
                    .testTag("top_highlight_clip_${clip.id}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(PurpleNeon.copy(alpha = 0.3f), CyanNeon.copy(alpha = 0.3f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = CyanGlow,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = clip.currentTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = clip.currentHook,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    ViralScoreBadge(viralScore = clip.viralScore, breakdown = clip.scoreBreakdown)
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, SurfaceCardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, SurfaceCardBorder),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
        }
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    onClick: () -> Unit,
    onViewClips: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, SurfaceCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("project_item_${project.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(project.thumbnailPlaceholderColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${(project.durationSeconds / 60).toInt()}m ${(project.durationSeconds % 60).toInt()}s",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(text = " • ", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = project.resolution,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        if (project.isTrimmedSaved) {
                            Text(text = " • ", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = "Trimmed",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldNeon
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (project.status) {
                        ProjectStatus.COMPLETED -> EmeraldNeon.copy(alpha = 0.15f)
                        ProjectStatus.PROCESSING -> CyanNeon.copy(alpha = 0.15f)
                        else -> PurpleNeon.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = project.status.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (project.status) {
                            ProjectStatus.COMPLETED -> EmeraldNeon
                            ProjectStatus.PROCESSING -> CyanGlow
                            else -> PurpleGlow
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { onClick() },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Preview & Trim", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onViewClips() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = Color(0xFF00363F)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "View Clips", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
