package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CaptionPosition
import com.example.model.CaptionWord
import com.example.model.ClipStyle
import com.example.ui.theme.CyanGlow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaptionsOverlay(
    words: List<CaptionWord>,
    currentTimeSec: Float,
    clipStartSec: Float,
    activeStyle: ClipStyle,
    autoEmojiEnabled: Boolean,
    captionPosition: CaptionPosition = CaptionPosition.BOTTOM,
    fontSize: Int = 18,
    customActiveWordColor: Long? = null,
    modifier: Modifier = Modifier
) {
    if (words.isEmpty()) return

    // Find visible window of ~6-8 words around current time
    val absoluteCurrentTime = clipStartSec + currentTimeSec
    val activeIndex = words.indexOfFirst { absoluteCurrentTime in it.startSec..it.endSec }.let {
        if (it == -1) 0 else it
    }

    // Display sliding group of words (4 words before and 4 words after)
    val startIndex = (activeIndex - 3).coerceAtLeast(0)
    val endIndex = (startIndex + 7).coerceAtMost(words.size)
    val visibleWords = words.subList(startIndex, endIndex)

    val styleHighlightColor = if (customActiveWordColor != null) Color(customActiveWordColor) else Color(activeStyle.highlightColor)

    val alignment = when (captionPosition) {
        CaptionPosition.TOP -> Alignment.TopCenter
        CaptionPosition.MIDDLE -> Alignment.Center
        CaptionPosition.BOTTOM -> Alignment.BottomCenter
    }

    val verticalPadding = when (captionPosition) {
        CaptionPosition.TOP -> 24.dp
        CaptionPosition.MIDDLE -> 0.dp
        CaptionPosition.BOTTOM -> 24.dp
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = verticalPadding),
        contentAlignment = alignment
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            visibleWords.forEach { wordItem ->
                val isCurrent = absoluteCurrentTime in wordItem.startSec..wordItem.endSec
                val showEmoji = autoEmojiEnabled && wordItem.suggestedEmoji != null

                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when {
                                isCurrent -> styleHighlightColor
                                wordItem.isKeyword -> Color(0x44FFFFFF)
                                else -> Color.Transparent
                            }
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = wordItem.word,
                            fontSize = if (isCurrent) (fontSize + 2).sp else fontSize.sp,
                            fontWeight = if (isCurrent || wordItem.isKeyword) FontWeight.Black else FontWeight.Bold,
                            color = when {
                                isCurrent -> if (activeStyle == ClipStyle.BOLD || activeStyle == ClipStyle.MINIMAL) Color.Black else Color.White
                                wordItem.isKeyword -> styleHighlightColor
                                else -> Color.White
                            },
                            textAlign = TextAlign.Center
                        )

                        if (showEmoji && isCurrent) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = wordItem.suggestedEmoji!!,
                                fontSize = (fontSize + 1).sp
                            )
                        }
                    }
                }
            }
        }
    }
}
