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
import com.example.model.HookType
import com.example.model.ViralClip
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.ViralScoreBadge
import com.example.ui.theme.*

enum class ClipSortOption(val label: String) {
    HIGHEST_SCORE("Highest Viral Score"),
    BEST_HOOK("Best Hook"),
    MOST_EMOTIONAL("Most Emotional"),
    MOST_INFORMATIVE("Most Informative"),
    MOST_ENTERTAINING("Most Entertaining"),
    SHORTEST("Shortest"),
    LONGEST("Longest")
}

@Composable
fun ClipsListScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val clips by viewModel.clips.collectAsState()
    val activeProjectId by viewModel.activeProjectId.collectAsState()
    val projects by viewModel.projects.collectAsState()

    val currentProject = projects.find { it.id == activeProjectId } ?: projects.firstOrNull()
    val projectClips = clips.filter { it.projectId == (currentProject?.id ?: "") }.ifEmpty { clips }

    var sortOption by remember { mutableStateOf(ClipSortOption.HIGHEST_SCORE) }
    var selectedClipForExport by remember { mutableStateOf<ViralClip?>(null) }

    val sortedClips = remember(projectClips, sortOption) {
        when (sortOption) {
            ClipSortOption.HIGHEST_SCORE -> projectClips.sortedByDescending { it.viralScore }
            ClipSortOption.BEST_HOOK -> projectClips.sortedByDescending { it.scoreBreakdown.hookScore }
            ClipSortOption.MOST_EMOTIONAL -> projectClips.sortedByDescending { it.scoreBreakdown.emotionalScore }
            ClipSortOption.MOST_INFORMATIVE -> projectClips.sortedByDescending { it.scoreBreakdown.infoScore }
            ClipSortOption.MOST_ENTERTAINING -> projectClips.sortedByDescending { it.scoreBreakdown.entertainmentScore }
            ClipSortOption.SHORTEST -> projectClips.sortedBy { it.durationSec }
            ClipSortOption.LONGEST -> projectClips.sortedByDescending { it.durationSec }
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
                        text = "AI Viral Clips (${sortedClips.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = currentProject?.title ?: "Generated Clips",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Sorting Filter Chips
        item {
            Text(text = "Sort Highlights By", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(ClipSortOption.values()) { opt ->
                    val isSelected = opt == sortOption
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) CyanGlow.copy(alpha = 0.2f) else SurfaceCard,
                        border = BorderStroke(1.dp, if (isSelected) CyanGlow else SurfaceCardBorder),
                        modifier = Modifier.clickable { sortOption = opt }
                    ) {
                        Text(
                            text = opt.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyanGlow else TextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // List of Clips
        items(sortedClips) { clip ->
            ClipCardItem(
                clip = clip,
                onSelectTitleOption = { optIdx ->
                    viewModel.selectTitleHookOption(clip.id, optIdx)
                },
                onOpenEditor = {
                    viewModel.selectClip(clip.id)
                    viewModel.navigateTo(AppScreen.CLIP_EDITOR)
                },
                onExport = {
                    selectedClipForExport = clip
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
    }

    // Quick Export Dialog
    if (selectedClipForExport != null) {
        val clip = selectedClipForExport!!
        AlertDialog(
            onDismissRequest = { selectedClipForExport = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, tint = CyanGlow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Export Viral Clip", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(text = clip.currentTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Format: 9:16 (Shorts/TikTok/Reels)", fontSize = 12.sp, color = TextSecondary)
                    Text(text = "Resolution: 1080p HD (Native source)", fontSize = 12.sp, color = TextSecondary)
                    Text(text = "Duration: ${clip.durationSec.toInt()}s", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceCard,
                        border = BorderStroke(1.dp, SurfaceCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Rendering Engine: FFmpeg deterministic export", fontSize = 11.sp, color = TextMuted)
                            Text(text = "Captions: Animated ${clip.activeStyle.label} preset", fontSize = 11.sp, color = CyanGlow)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.showNotification("Clip '${clip.currentTitle}' exported successfully!")
                        selectedClipForExport = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = Color(0xFF00363F))
                ) {
                    Text("Export MP4", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedClipForExport = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ClipCardItem(
    clip: ViralClip,
    onSelectTitleOption: (Int) -> Unit,
    onOpenEditor: () -> Unit,
    onExport: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, SurfaceCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("clip_card_${clip.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Topic, Duration, Viral Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PurpleNeon.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, PurpleNeon.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = clip.topic,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleGlow,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SurfaceCard
                    ) {
                        Text(
                            text = "${clip.durationSec.toInt()}s",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }

                ViralScoreBadge(viralScore = clip.viralScore, breakdown = clip.scoreBreakdown)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = clip.currentTitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Hook
            Text(
                text = "\"${clip.currentHook}\"",
                fontSize = 12.sp,
                color = CyanGlow
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Reason for selection
            Text(
                text = clip.reasonForSelection,
                fontSize = 11.sp,
                color = TextMuted,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Title & Hook Option Pills (User request: switch between the 3+ distinct suggestions)
            Text(
                text = "AI Suggested Title & Hook Formulas (${clip.titleOptions.size}):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            clip.titleOptions.forEachIndexed { optIndex, opt ->
                val isSelected = clip.customTitle == null && clip.selectedOptionIndex == optIndex
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CyanGlow.copy(alpha = 0.12f) else SurfaceCard)
                        .border(
                            BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) CyanGlow else SurfaceCardBorder),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectTitleOption(optIndex) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("clip_${clip.id}_title_option_$optIndex")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = opt.hookType.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) CyanGlow else TextMuted
                                )
                            }
                            Text(
                                text = opt.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                maxLines = 1
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active",
                                tint = CyanGlow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Keywords row
            if (clip.keywords.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(clip.keywords) { kw ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceCard,
                            border = BorderStroke(1.dp, SurfaceCardBorder)
                        ) {
                            Text(
                                text = "#$kw",
                                fontSize = 10.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Actions row: Edit in Studio & Quick Export
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { onExport() },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Quick Export", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onOpenEditor() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = Color(0xFF00363F)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp).testTag("open_editor_button_${clip.id}")
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Open in Editor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
