package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.PurpleGlow
import com.example.ui.theme.TextPrimary

@Composable
fun ViralClipLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 32.dp,
    showText: Boolean = true,
    fontSize: Int = 18
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Minimal Play Button + AI Spark / Waveform
        Canvas(modifier = Modifier.size(iconSize)) {
            val w = size.width
            val h = size.height

            val gradientBrush = Brush.linearGradient(
                colors = listOf(CyanGlow, PurpleGlow),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )

            // Minimal play button triangle (rounded aesthetic)
            val playPath = Path().apply {
                moveTo(w * 0.22f, h * 0.16f)
                lineTo(w * 0.78f, h * 0.50f)
                lineTo(w * 0.22f, h * 0.84f)
                close()
            }
            drawPath(
                path = playPath,
                brush = gradientBrush
            )

            // AI Waveform / Spark overlay bars on the right
            val strokeW = w * 0.08f
            // Bar 1
            drawLine(
                color = Color.White.copy(alpha = 0.95f),
                start = Offset(w * 0.88f, h * 0.30f),
                end = Offset(w * 0.88f, h * 0.70f),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
            // Bar 2 (center spark)
            drawLine(
                color = CyanGlow,
                start = Offset(w * 0.98f, h * 0.18f),
                end = Offset(w * 0.98f, h * 0.82f),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )

            // Outer subtle neon glow arc
            drawArc(
                brush = Brush.sweepGradient(listOf(CyanGlow, PurpleGlow, CyanGlow)),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = w * 0.06f, cap = StrokeCap.Round)
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ViralClip",
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Studio",
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Light,
                    color = CyanGlow
                )
            }
        }
    }
}
