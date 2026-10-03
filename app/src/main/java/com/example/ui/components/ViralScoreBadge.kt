package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScoreBreakdown
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.SurfaceCard

@Composable
fun ViralScoreBadge(
    viralScore: Int,
    breakdown: ScoreBreakdown,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    val scoreColor = when {
        viralScore >= 90 -> EmeraldNeon
        viralScore >= 75 -> AmberNeon
        else -> Color(0xFFF97316)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = scoreColor.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, scoreColor.copy(alpha = 0.4f)),
        modifier = modifier
            .testTag("viral_score_badge_${viralScore}")
            .clickable { showDialog = true }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = "Viral Potential",
                tint = scoreColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AI Viral Score: $viralScore",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = scoreColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "View score calculation",
                tint = scoreColor.copy(alpha = 0.7f),
                modifier = Modifier.size(13.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = scoreColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "AI Viral Score: $viralScore / 100", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Estimated viral potential based on hook retention, visual pacing, and emotional vectors. (AI estimate, not a guarantee).",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    ScoreMetricRow("Hook Strength (25%)", breakdown.hookScore)
                    ScoreMetricRow("Emotional Intensity (15%)", breakdown.emotionalScore)
                    ScoreMetricRow("Information / Value (15%)", breakdown.infoScore)
                    ScoreMetricRow("Entertainment Factor (15%)", breakdown.entertainmentScore)
                    ScoreMetricRow("Audience Curiosity (10%)", breakdown.curiosityScore)
                    ScoreMetricRow("Story Completeness (10%)", breakdown.storyScore)
                    ScoreMetricRow("Visual Cadence (5%)", breakdown.visualScore)
                    ScoreMetricRow("Audio Clarity (5%)", breakdown.audioScore)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun ScoreMetricRow(label: String, score: Int) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, color = Color(0xFFCBD5E1))
            Text(text = "$score/100", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EmeraldNeon)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp),
            color = if (score >= 90) EmeraldNeon else AmberNeon,
            trackColor = SurfaceCard
        )
    }
}
