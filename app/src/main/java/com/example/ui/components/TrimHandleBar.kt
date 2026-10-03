package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Schedule
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
import com.example.ui.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrimHandleBar(
    totalDurationSec: Float,
    startTrimSec: Float,
    endTrimSec: Float,
    isTrimSaved: Boolean,
    onTrimChange: (Float, Float) -> Unit,
    onSaveTrim: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationSafe = totalDurationSec.coerceAtLeast(10f)
    var sliderRange by remember(startTrimSec, endTrimSec, durationSafe) {
        mutableStateOf(startTrimSec.coerceIn(0f, durationSafe)..endTrimSec.coerceIn(0f, durationSafe))
    }

    val selectedDuration = (sliderRange.endInclusive - sliderRange.start).coerceAtLeast(0f)
    val minutes = selectedDuration / 60f
    val estimatedCredits = kotlin.math.max(2, kotlin.math.ceil(minutes / 10f).toInt() * 2)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, if (isTrimSaved) EmeraldNeon.copy(alpha = 0.5f) else SurfaceCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("trim_handle_bar_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = "Trim Tool",
                        tint = CyanGlow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Video Trimming & Analysis Window",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                if (isTrimSaved) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldNeon.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, EmeraldNeon.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = EmeraldNeon,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Trim Locked",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldNeon
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Drag handles to select high-energy section for AI clipping. Saves credits on long uploads.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Time markers readout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimestampChip(label = "START", timeStr = formatTimestamp(sliderRange.start), color = CyanGlow)
                TimestampChip(label = "DURATION", timeStr = formatDuration(selectedDuration), color = PurpleGlow)
                TimestampChip(label = "END", timeStr = formatTimestamp(sliderRange.endInclusive), color = CyanGlow)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Range Slider
            RangeSlider(
                value = sliderRange,
                onValueChange = { newRange ->
                    // enforce minimum 10 seconds segment
                    if (newRange.endInclusive - newRange.start >= 10f) {
                        sliderRange = newRange
                        onTrimChange(newRange.start, newRange.endInclusive)
                    }
                },
                valueRange = 0f..durationSafe,
                colors = SliderDefaults.colors(
                    thumbColor = CyanGlow,
                    activeTrackColor = CyanGlow,
                    inactiveTrackColor = SurfaceCard
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trim_range_slider")
            )

            // Timeline ruler labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "00:00", fontSize = 10.sp, color = TextMuted)
                Text(text = formatTimestamp(durationSafe * 0.25f), fontSize = 10.sp, color = TextMuted)
                Text(text = formatTimestamp(durationSafe * 0.50f), fontSize = 10.sp, color = TextMuted)
                Text(text = formatTimestamp(durationSafe * 0.75f), fontSize = 10.sp, color = TextMuted)
                Text(text = formatTimestamp(durationSafe), fontSize = 10.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Credit Calculation & Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = "Credit Cost",
                        tint = AmberNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cost: $estimatedCredits Credits",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberNeon
                    )
                    Text(
                        text = " (2 credits / 10m)",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = { onSaveTrim() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTrimSaved) EmeraldNeon.copy(alpha = 0.2f) else CyanGlow,
                        contentColor = if (isTrimSaved) EmeraldNeon else Color(0xFF00363F)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = if (isTrimSaved) BorderStroke(1.dp, EmeraldNeon) else null,
                    modifier = Modifier.testTag("save_trimmed_section_button")
                ) {
                    Icon(
                        imageVector = if (isTrimSaved) Icons.Default.Check else Icons.Default.ContentCut,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTrimSaved) "Trim Saved" else "Save Trimmed Section",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TimestampChip(label: String, timeStr: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Text(text = timeStr, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

private fun formatTimestamp(seconds: Float): String {
    val m = (seconds / 60).toInt()
    val s = (seconds % 60).toInt()
    return "%02d:%02d".format(m, s)
}

private fun formatDuration(seconds: Float): String {
    val m = (seconds / 60).toInt()
    val s = (seconds % 60).toInt()
    return if (m > 0) "${m}m ${s}s" else "${s}s"
}
