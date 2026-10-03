package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.TrimHandleBar
import com.example.ui.components.VideoPreviewPlayer
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadAndTrimScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.projects.collectAsState()
    val activeProjectId by viewModel.activeProjectId.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val trimStart by viewModel.trimStart.collectAsState()
    val trimEnd by viewModel.trimEnd.collectAsState()
    val isTrimSaved by viewModel.isTrimSaved.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analysisStep by viewModel.analysisStep.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()

    val currentProject = projects.find { it.id == activeProjectId } ?: projects.firstOrNull()

    var selectedInputTab by remember { mutableStateOf(0) } // 0: Upload File, 1: YouTube URL
    var videoTitleInput by remember { mutableStateOf("") }
    var youtubeUrlInput by remember { mutableStateOf("https://youtube.com/watch?v=scaling-startups-2026") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var targetClipCount by remember { mutableStateOf(4) }
    var selectedDurationMode by remember { mutableStateOf("Auto") }
    var selectedAspectRatio by remember { mutableStateOf(AspectRatioType.RATIO_9_16) }
    var selectedClipStyle by remember { mutableStateOf(ClipStyle.MODERN) }

    val languages = listOf("English", "Hindi", "Spanish", "Portuguese", "French", "German", "Japanese", "Korean", "Arabic")
    val durationModes = listOf("Auto", "15s", "30s", "45s", "60s", "90s")

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
                        text = "Upload, Preview & Trim",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Select timestamps and let AI detect high-retention viral moments",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Section 1: Video Input Methods
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Tab Selector
                    TabRow(
                        selectedTabIndex = selectedInputTab,
                        containerColor = SurfaceCard,
                        contentColor = CyanGlow,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedInputTab]),
                                color = CyanGlow
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedInputTab == 0,
                            onClick = { selectedInputTab = 0 },
                            text = { Text("Upload File", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        )
                        Tab(
                            selected = selectedInputTab == 1,
                            onClick = { selectedInputTab = 1 },
                            text = { Text("Permitted YouTube URL", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (selectedInputTab == 0) {
                        // Upload area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceCard)
                                .border(BorderStroke(1.dp, CyanGlow.copy(alpha = 0.3f)), RoundedCornerShape(12.dp))
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = "Upload",
                                    tint = CyanGlow,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Drag & drop video or tap to browse",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Supported: MP4, MOV, WEBM, MKV • Up to 10 GB storage",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        viewModel.createProject(
                                            title = if (videoTitleInput.isNotBlank()) videoTitleInput else "Uploaded Video Clip",
                                            sourceUrl = "local_storage://device_video.mp4",
                                            sourceType = VideoSourceType.UPLOAD,
                                            durationSec = 620f,
                                            fileSizeMb = 142.5f,
                                            language = selectedLanguage,
                                            clipCount = targetClipCount
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = Color(0xFF00363F)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("simulate_upload_button")
                                ) {
                                    Text("Select Video File", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // YouTube URL input
                        Column {
                            OutlinedTextField(
                                value = youtubeUrlInput,
                                onValueChange = { youtubeUrlInput = it },
                                label = { Text("Paste YouTube URL", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = CyanGlow)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("youtube_url_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Permitted content disclosure notice
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AmberNeon.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, AmberNeon.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Permission Policy",
                                        tint = AmberNeon,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Only use videos you own or have permission to process. DRM-protected or unauthorized videos cannot be processed.",
                                        fontSize = 11.sp,
                                        color = Color(0xFFFDE68A)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    viewModel.createProject(
                                        title = "YouTube Video: ${youtubeUrlInput.split("=").last().take(12)}",
                                        sourceUrl = youtubeUrlInput,
                                        sourceType = VideoSourceType.YOUTUBE_URL,
                                        durationSec = 880f,
                                        fileSizeMb = 180f,
                                        language = selectedLanguage,
                                        clipCount = targetClipCount
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = Color(0xFF00363F)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("load_youtube_video_button")
                            ) {
                                Text("Load Video For Analysis", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preset Quick-Pick Demos
                    Text(
                        text = "Or pick an existing project to preview & trim:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(projects) { p ->
                            val isSelected = p.id == currentProject?.id
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) CyanGlow.copy(alpha = 0.15f) else SurfaceCard,
                                border = BorderStroke(1.dp, if (isSelected) CyanGlow else SurfaceCardBorder),
                                modifier = Modifier
                                    .clickable {
                                        viewModel.selectProject(p.id)
                                    }
                                    .testTag("select_project_chip_${p.id}")
                            ) {
                                Text(
                                    text = p.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) CyanGlow else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section 2: Playable Preview Player
        if (currentProject != null) {
            item {
                Text(
                    text = "Video Preview: ${currentProject.title}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                VideoPreviewPlayer(
                    playbackState = playbackState,
                    aspectRatio = selectedAspectRatio,
                    backgroundMode = BackgroundMode.SMART_CROP,
                    activeStyle = selectedClipStyle,
                    onTogglePlay = { viewModel.togglePlayPause() },
                    onSeek = { viewModel.seekTo(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Section 3: Dual Trim Handles Tool
            item {
                TrimHandleBar(
                    totalDurationSec = currentProject.durationSeconds,
                    startTrimSec = trimStart,
                    endTrimSec = trimEnd,
                    isTrimSaved = isTrimSaved,
                    onTrimChange = { s, e -> viewModel.updateTrimHandles(s, e) },
                    onSaveTrim = { viewModel.saveTrimmedSection(currentProject.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Section 4: AI Parameters (Language, Clip count, Style, Aspect Ratio)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "AI Clipping Configuration",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Language Selector
                        Text(text = "Video & Transcript Language", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(languages) { lang ->
                                val isSelected = lang == selectedLanguage
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) CyanGlow.copy(alpha = 0.2f) else SurfaceCard,
                                    border = BorderStroke(1.dp, if (isSelected) CyanGlow else SurfaceCardBorder),
                                    modifier = Modifier.clickable { selectedLanguage = lang }
                                ) {
                                    Text(
                                        text = lang,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) CyanGlow else TextPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Target Duration
                        Text(text = "Target Clip Length", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(durationModes) { mode ->
                                val isSelected = mode == selectedDurationMode
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) PurpleGlow.copy(alpha = 0.2f) else SurfaceCard,
                                    border = BorderStroke(1.dp, if (isSelected) PurpleGlow else SurfaceCardBorder),
                                    modifier = Modifier.clickable { selectedDurationMode = mode }
                                ) {
                                    Text(
                                        text = mode,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PurpleGlow else TextPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Aspect Ratio Format
                        Text(text = "Default Aspect Ratio", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            AspectRatioType.values().forEach { ratio ->
                                val isSelected = ratio == selectedAspectRatio
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) CyanGlow.copy(alpha = 0.2f) else SurfaceCard,
                                    border = BorderStroke(1.dp, if (isSelected) CyanGlow else SurfaceCardBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 3.dp)
                                        .clickable { selectedAspectRatio = ratio }
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Text(text = ratio.label, fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (isSelected) CyanGlow else TextPrimary)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Target number of clips
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Number of Viral Clips", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "$targetClipCount Clips", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyanGlow)
                        }
                        Slider(
                            value = targetClipCount.toFloat(),
                            onValueChange = { targetClipCount = it.toInt() },
                            valueRange = 1f..8f,
                            steps = 6,
                            colors = SliderDefaults.colors(thumbColor = CyanGlow, activeTrackColor = CyanGlow),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 5: Processing CTA
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, CyanGlow.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Ready to Generate Viral Clips",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                val isTrimActive = isTrimSaved || (trimEnd - trimStart < currentProject.durationSeconds)
                                Text(
                                    text = if (isTrimActive) "Target: Trimmed Section (${(trimEnd - trimStart).toInt()}s)" else "Target: Full Video (${currentProject.durationSeconds.toInt()}s)",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            Text(
                                text = "${currentProject.estimatedCreditCost} Credits",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberNeon
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val analyzeTrimmed = isTrimSaved || (trimEnd - trimStart < currentProject.durationSeconds)
                                viewModel.runAiAnalysis(currentProject.id, analyzeTrimmedOnly = analyzeTrimmed)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = Color(0xFF00363F)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("find_viral_clips_action_button")
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Find Viral Clips", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // AI Analysis Animated Modal
    if (isAnalyzing) {
        AlertDialog(
            onDismissRequest = { /* Modal prevents dismiss during analysis */ },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = CyanGlow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ViralClip AI Engine", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(
                        progress = { analysisStep?.progressFraction ?: 0.3f },
                        color = CyanGlow,
                        trackColor = SurfaceCard,
                        modifier = Modifier.size(64.dp),
                        strokeWidth = 6.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = analysisStep?.stepName ?: "ANALYZING VIDEO...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = CyanGlow
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = analysisStep?.detail ?: "Evaluating visual pacing and viral potential vectors...",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            },
            confirmButton = {}
        )
    }
}
