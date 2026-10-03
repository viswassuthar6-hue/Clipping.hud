package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import com.example.model.TitleHookOption
import com.example.model.ViralClip
import com.example.ui.theme.*

@Composable
fun TitleHookSelector(
    clip: ViralClip,
    onSelectOption: (Int) -> Unit,
    onSaveCustom: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditingCustom by remember { mutableStateOf(false) }
    var editTitleText by remember(clip.currentTitle) { mutableStateOf(clip.currentTitle) }
    var editHookText by remember(clip.currentHook) { mutableStateOf(clip.currentHook) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, SurfaceCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("title_hook_selector_container")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CyanGlow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Viral Titles & Hooks",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PurpleNeon.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PurpleNeon.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${clip.titleOptions.size} Variations",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PurpleGlow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select from AI-generated hook formulas or customize your own copy before export.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Display the 3+ distinct options
            clip.titleOptions.forEachIndexed { index, option ->
                val isSelected = clip.customTitle == null && clip.selectedOptionIndex == index

                val optionBorderColor = if (isSelected) CyanGlow else SurfaceCardBorder
                val optionBgColor = if (isSelected) CyanGlow.copy(alpha = 0.08f) else SurfaceCard

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(optionBgColor)
                        .border(BorderStroke(if (isSelected) 1.5.dp else 1.dp, optionBorderColor), RoundedCornerShape(12.dp))
                        .clickable {
                            onSelectOption(index)
                        }
                        .padding(12.dp)
                        .testTag("title_hook_option_$index")
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (isSelected) "Selected" else "Select",
                                tint = if (isSelected) CyanGlow else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            // Hook type badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (option.hookType) {
                                    HookType.CURIOSITY_GAP -> CyanNeon.copy(alpha = 0.18f)
                                    HookType.HIGH_STAKES -> AmberNeon.copy(alpha = 0.18f)
                                    HookType.ACTIONABLE_SECRET -> EmeraldNeon.copy(alpha = 0.18f)
                                    HookType.CONTRARIAN -> PinkNeon.copy(alpha = 0.18f)
                                    HookType.STORY_CLIMAX -> PurpleNeon.copy(alpha = 0.18f)
                                },
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = option.hookType.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (option.hookType) {
                                        HookType.CURIOSITY_GAP -> CyanGlow
                                        HookType.HIGH_STAKES -> AmberNeon
                                        HookType.ACTIONABLE_SECRET -> EmeraldNeon
                                        HookType.CONTRARIAN -> PinkNeon
                                        HookType.STORY_CLIMAX -> PurpleGlow
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Title
                        Text(
                            text = option.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Hook
                        Text(
                            text = "\"${option.hook}\"",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = if (isSelected) CyanGlow else TextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = option.explanation,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Custom Edit button or Custom Display
            if (!isEditingCustom) {
                OutlinedButton(
                    onClick = {
                        editTitleText = clip.currentTitle
                        editHookText = clip.currentHook
                        isEditingCustom = true
                    },
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_custom_title_hook_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit title and hook",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (clip.customTitle != null) "Edit Custom Title & Hook" else "Write Custom Title & Hook",
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, CyanGlow.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Custom Title & Opening Hook",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanGlow
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editTitleText,
                            onValueChange = { editTitleText = it },
                            label = { Text("Video Title", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_title_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editHookText,
                            onValueChange = { editHookText = it },
                            label = { Text("Opening 3-Second Hook", fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_hook_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { isEditingCustom = false }) {
                                Text("Cancel", color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    onSaveCustom(editTitleText, editHookText)
                                    isEditingCustom = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow, contentColor = Color(0xFF00363F)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("save_custom_title_hook_button")
                            ) {
                                Text("Save & Apply", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
