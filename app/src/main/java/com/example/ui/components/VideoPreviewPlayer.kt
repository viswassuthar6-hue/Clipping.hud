package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AspectRatioType
import com.example.model.BackgroundMode
import com.example.model.CaptionPosition
import com.example.model.CaptionWord
import com.example.model.ClipStyle
import com.example.ui.PlayerPlaybackState
import com.example.ui.theme.*

@Composable
fun VideoPreviewPlayer(
    playbackState: PlayerPlaybackState,
    aspectRatio: AspectRatioType = AspectRatioType.RATIO_9_16,
    backgroundMode: BackgroundMode = BackgroundMode.SMART_CROP,
    captionWords: List<CaptionWord> = emptyList(),
    clipStartSec: Float = 0f,
    activeStyle: ClipStyle = ClipStyle.MODERN,
    autoEmojiEnabled: Boolean = true,
    captionPosition: CaptionPosition = CaptionPosition.BOTTOM,
    fontSize: Int = 18,
    customActiveWordColor: Long? = null,
    zoomFactor: Float = 1.0f,
    audioVolume: Float = 1.0f,
    onTogglePlay: () -> Unit,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audioWave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_preview_player")
    ) {
        // Player Canvas Area conforming to the Aspect Ratio
        val ratioAspect = aspectRatio.ratioWidth / aspectRatio.ratioHeight

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 280.dp, max = 460.dp)
                .aspectRatio(ratioAspect.coerceIn(0.5625f, 1.77f), matchHeightConstraintsFirst = true)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF070B12))
                .border(androidx.compose.foundation.BorderStroke(1.5.dp, SurfaceCardBorder), RoundedCornerShape(16.dp))
                .clickable { onTogglePlay() },
            contentAlignment = Alignment.Center
        ) {
            // Simulated video background according to BackgroundMode
            when (backgroundMode) {
                BackgroundMode.BLUR -> {
                    // Blurred video replica in background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(PurpleNeon.copy(alpha = 0.35f), CyanNeon.copy(alpha = 0.25f), Color(0xFF090D16))
                                )
                            )
                    )
                }
                BackgroundMode.FIT_VIDEO -> {
                    // Letterboxed
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF1E293B), Color(0xFF334155))
                                )
                            )
                    )
                }
                else -> {
                    // Smart crop / Face tracking: rich gradient with moving grid
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF1E293B), Color(0xFF0A0F1D)),
                                    center = Offset(300f, 300f),
                                    radius = 600f
                                )
                            )
                    )
                }
            }

            // Audio & Video Visualizer Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw center speaker silhouette or pattern
                drawCircle(
                    color = CyanGlow.copy(alpha = 0.08f),
                    radius = canvasWidth * 0.28f,
                    center = Offset(canvasWidth / 2f, canvasHeight * 0.42f)
                )

                // Face tracking target simulation
                if (backgroundMode == BackgroundMode.FACE_TRACKING) {
                    val boxSize = canvasWidth * 0.32f
                    val boxTop = canvasHeight * 0.28f
                    val boxLeft = (canvasWidth - boxSize) / 2f
                    drawRect(
                        color = EmeraldNeon.copy(alpha = 0.65f),
                        topLeft = Offset(boxLeft, boxTop),
                        size = Size(boxSize, boxSize),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                    )
                }

                // If playing, render dynamic audio waveform bars at bottom
                if (playbackState.isPlaying) {
                    val barCount = 28
                    val barWidth = canvasWidth / (barCount * 1.6f)
                    for (i in 0 until barCount) {
                        val barHeight = (kotlin.math.sin((i * 0.4f) + (waveOffset * 6.28f)) * 0.5f + 0.5f) * (canvasHeight * 0.18f) + 10f
                        val x = i * (canvasWidth / barCount) + 12f
                        drawRoundRect(
                            color = if (i % 2 == 0) CyanGlow.copy(alpha = 0.7f) else PurpleGlow.copy(alpha = 0.6f),
                            topLeft = Offset(x, canvasHeight * 0.75f - barHeight / 2f),
                            size = Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )
                    }
                }
            }

            // Captions overlay positioned according to captionPosition
            if (captionWords.isNotEmpty()) {
                CaptionsOverlay(
                    words = captionWords,
                    currentTimeSec = playbackState.currentTimeSec,
                    clipStartSec = clipStartSec,
                    activeStyle = activeStyle,
                    autoEmojiEnabled = autoEmojiEnabled,
                    captionPosition = captionPosition,
                    fontSize = fontSize,
                    customActiveWordColor = customActiveWordColor
                )
            }

            // Top Badges (Aspect Ratio & Background Mode)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Text(
                        text = "${aspectRatio.label} • ${aspectRatio.platformHint.split("/").first().trim()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanGlow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Text(
                        text = backgroundMode.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Floating Play / Pause Center Trigger (fades or stays minimal)
            if (!playbackState.isPlaying) {
                Surface(
                    shape = CircleShape,
                    color = CyanGlow,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("center_play_button")
                        .clickable { onTogglePlay() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Video",
                            tint = Color(0xFF00363F),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Playback Scrub Bar & Controls
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                // Slider
                Slider(
                    value = playbackState.currentTimeSec.coerceIn(0f, playbackState.durationSec.coerceAtLeast(1f)),
                    onValueChange = { onSeek(it) },
                    valueRange = 0f..playbackState.durationSec.coerceAtLeast(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = CyanGlow,
                        activeTrackColor = CyanGlow,
                        inactiveTrackColor = Color(0xFF334155)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .testTag("player_scrub_slider")
                )

                // Controls row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onTogglePlay() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("player_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                                tint = CyanGlow
                            )
                        }

                        IconButton(
                            onClick = { onSeek(0f) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Restart",
                                tint = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "${formatTime(playbackState.currentTimeSec)} / ${formatTime(playbackState.durationSec)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Audio Enabled",
                        tint = CyanGlow,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private fun formatTime(seconds: Float): String {
    val m = (seconds / 60).toInt()
    val s = (seconds % 60).toInt()
    return "%02d:%02d".format(m, s)
}
