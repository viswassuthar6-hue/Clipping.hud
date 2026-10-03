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
import com.example.ui.components.TitleHookSelector
import com.example.ui.components.VideoPreviewPlayer
import com.example.ui.components.ViralScoreBadge
import com.example.ui.theme.*

@Composable
fun ClipEditorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val clips by viewModel.clips.collectAsState()
    val activeClipId by viewModel.activeClipId.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()

    val currentClip = clips.find { it.id == activeClipId } ?: clips.firstOrNull()

    var showExportModal by remember { mutableStateOf(false) }
    var selectedResolution by remember { mutableStateOf("1080p") }
    var isRenderingJob by remember { mutableStateOf(false) }
    var renderProgress by remember { mutableStateOf(0f) }

    if (currentClip == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBg),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "No clip selected", color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.navigateTo(AppScreen.CLIPS_LIST) }) {
                    Text("Select a Clip")
                }
            }
        }
        return
    }

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
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.CLIPS_LIST) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Clip Studio Editor",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${currentClip.durationSec.toInt()}s • ${currentClip.activeAspectRatio.label}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Button(
                    onClick = { showExportModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = Color(0xFF00363F)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("editor_export_modal_button")
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Export", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Live Video Player Preview with Aspect Ratio & Captions
        item {
            VideoPreviewPlayer(
                playbackState = playbackState,
                aspectRatio = currentClip.activeAspectRatio,
                backgroundMode = currentClip.backgroundMode,
                captionWords = currentClip.captionWords,
                clipStartSec = currentClip.startSec,
                activeStyle = currentClip.activeStyle,
                autoEmojiEnabled = currentClip.autoEmojiEnabled,
                captionPosition = currentClip.captionPosition,
                fontSize = currentClip.fontSize,
                customActiveWordColor = currentClip.activeWordColor,
                zoomFactor = currentClip.zoomFactor,
                audioVolume = currentClip.audioVolume,
                onTogglePlay = { viewModel.togglePlayPause() },
                onSeek = { viewModel.seekTo(it) }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Timeline & Editing Tools (Split, Delete, Zoom, Volume)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Timeline & Media Adjustments",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.splitClipAtCurrentTime(currentClip.id) },
                            border = BorderStroke(1.dp, CyanGlow.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Split Clip", color = CyanGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.deleteClip(currentClip.id) },
                            border = BorderStroke(1.dp, RoseError.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = RoseError, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Delete Clip", color = RoseError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Zoom slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Video Crop Zoom", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "${"%.1f".format(currentClip.zoomFactor)}x", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanGlow)
                    }
                    Slider(
                        value = currentClip.zoomFactor,
                        onValueChange = { viewModel.updateClipZoomFactor(currentClip.id, it) },
                        valueRange = 0.8f..2.0f,
                        colors = SliderDefaults.colors(thumbColor = CyanGlow, activeTrackColor = CyanGlow),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Audio volume slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Clip Audio Volume", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "${(currentClip.audioVolume * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PurpleGlow)
                    }
                    Slider(
                        value = currentClip.audioVolume,
                        onValueChange = { viewModel.updateClipAudioVolume(currentClip.id, it) },
                        valueRange = 0.0f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = PurpleGlow, activeTrackColor = PurpleGlow),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Title & Hook Selector (User Enhancement Request: 3+ Options & Custom Edit)
        item {
            TitleHookSelector(
                clip = currentClip,
                onSelectOption = { idx ->
                    viewModel.selectTitleHookOption(currentClip.id, idx)
                },
                onSaveCustom = { title, hook ->
                    viewModel.saveCustomTitleAndHook(currentClip.id, title, hook)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Format: Aspect Ratio Selector
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Aspect Ratio Format",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Auto reframing preserves active speaker and key objects.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        AspectRatioType.values().forEach { ratio ->
                            val isSelected = currentClip.activeAspectRatio == ratio
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) CyanGlow.copy(alpha = 0.2f) else SurfaceCard,
                                border = BorderStroke(1.dp, if (isSelected) CyanGlow else SurfaceCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 3.dp)
                                    .clickable { viewModel.updateClipAspectRatio(currentClip.id, ratio) }
                                    .testTag("aspect_ratio_button_${ratio.name}")
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp)
                                ) {
                                    Text(
                                        text = ratio.label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) CyanGlow else TextPrimary
                                    )
                                    Text(
                                        text = ratio.platformHint.split("/").first().trim(),
                                        fontSize = 9.sp,
                                        color = TextMuted,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Smart Background Modes
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Smart Background & Reframe Mode",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        BackgroundMode.values().forEach { mode ->
                            val isSelected = currentClip.backgroundMode == mode
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) PurpleGlow.copy(alpha = 0.2f) else SurfaceCard,
                                border = BorderStroke(1.dp, if (isSelected) PurpleGlow else SurfaceCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp)
                                    .clickable { viewModel.updateClipBackgroundMode(currentClip.id, mode) }
                                    .testTag("bg_mode_${mode.name}")
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = mode.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PurpleGlow else TextPrimary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Captions Style & Auto Emoji
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
                        Column {
                            Text(
                                text = "Captions & Subtitles",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Word-level timing with keyword pop",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        // Auto Emoji Switch
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Auto Emoji", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = currentClip.autoEmojiEnabled,
                                onCheckedChange = { viewModel.toggleClipAutoEmoji(currentClip.id, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyanGlow,
                                    checkedTrackColor = CyanGlow.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.testTag("auto_emoji_switch")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Preset Caption Styles:", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(ClipStyle.values()) { style ->
                            val isSelected = currentClip.activeStyle == style
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(style.highlightColor).copy(alpha = 0.2f) else SurfaceCard,
                                border = BorderStroke(1.dp, if (isSelected) Color(style.highlightColor) else SurfaceCardBorder),
                                modifier = Modifier
                                    .clickable { viewModel.updateClipStyle(currentClip.id, style) }
                                    .testTag("caption_style_${style.name}")
                            ) {
                                Text(
                                    text = style.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(style.highlightColor) else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Caption Position Selector
                    Text(text = "Subtitle Screen Position:", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CaptionPosition.values().forEach { pos ->
                            val isPosSelected = currentClip.captionPosition == pos
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isPosSelected) CyanGlow.copy(alpha = 0.2f) else SurfaceCard,
                                border = BorderStroke(1.dp, if (isPosSelected) CyanGlow else SurfaceCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 3.dp)
                                    .clickable { viewModel.updateClipCaptionPosition(currentClip.id, pos) }
                                    .testTag("caption_pos_${pos.name}")
                            ) {
                                Text(
                                    text = pos.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPosSelected) CyanGlow else TextPrimary,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Font Size Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Caption Font Size", fontSize = 11.sp, color = TextMuted)
                        Text(text = "${currentClip.fontSize} sp", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanGlow)
                    }
                    Slider(
                        value = currentClip.fontSize.toFloat(),
                        onValueChange = { viewModel.updateClipFontSize(currentClip.id, it.toInt()) },
                        valueRange = 14f..28f,
                        steps = 6,
                        colors = SliderDefaults.colors(thumbColor = CyanGlow, activeTrackColor = CyanGlow),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Highlight Color Palette
                    Text(text = "Active Word Highlight Color:", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    val highlightColors = listOf(
                        0xFF06B6D4 to "Cyan",
                        0xFFFACC15 to "Yellow",
                        0xFF10B981 to "Emerald",
                        0xFFEF4444 to "Rose",
                        0xFFA855F7 to "Purple",
                        0xFF3B82F6 to "Blue"
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        highlightColors.forEach { (colorVal, name) ->
                            val isColorSelected = currentClip.activeWordColor == colorVal
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(colorVal))
                                    .border(BorderStroke(if (isColorSelected) 2.5.dp else 1.dp, if (isColorSelected) Color.White else Color.Transparent), RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updateClipActiveWordColor(currentClip.id, colorVal) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isColorSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = name, tint = Color.Black, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Full Transcript Readout
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Transcript & Highlighted Keywords",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentClip.transcript,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    // Export Resolution Modal with Compliance Label
    if (showExportModal) {
        val resolutions = listOf("720p", "1080p", "1440p", "4K")
        val isUpscaled = selectedResolution == "4K" // Label requirement: 4K export — source upscaled

        AlertDialog(
            onDismissRequest = { if (!isRenderingJob) showExportModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.MovieFilter, contentDescription = null, tint = CyanGlow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Export Short Clip", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(text = "Target Resolution:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        resolutions.forEach { res ->
                            val isSel = res == selectedResolution
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) CyanGlow.copy(alpha = 0.2f) else SurfaceCard,
                                border = BorderStroke(1.dp, if (isSel) CyanGlow else SurfaceCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp)
                                    .clickable { selectedResolution = res }
                            ) {
                                Text(
                                    text = res,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) CyanGlow else TextPrimary,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mandatory Compliance Label:
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceCard,
                        border = BorderStroke(1.dp, SurfaceCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (selectedResolution == "4K") "Label: 4K export — source upscaled" else "Label: Native HD source ($selectedResolution)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedResolution == "4K") AmberNeon else EmeraldNeon
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Encoding with FFmpeg NVENC/libx264 • Deterministic audio sync • 60 FPS target",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    if (isRenderingJob) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Rendering MP4 with FFmpeg...", fontSize = 12.sp, color = CyanGlow, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { renderProgress },
                            color = CyanGlow,
                            trackColor = SurfaceCard,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isRenderingJob = true
                        renderProgress = 1.0f
                        viewModel.showNotification("Clip exported as MP4 (${selectedResolution})!")
                        showExportModal = false
                        isRenderingJob = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = Color(0xFF00363F))
                ) {
                    Text("Render & Save MP4", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportModal = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
