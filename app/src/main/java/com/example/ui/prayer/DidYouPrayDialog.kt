package com.example.ui.prayer

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PrayerName
import com.example.data.model.PrayerStatus
import com.example.ui.components.BotanicalDivider
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.CairoFontFamily

@Composable
fun DidYouPrayDialog(
    prayer: PrayerName,
    userName: String,
    onDismiss: () -> Unit,
    onAnswerSelected: (PrayerStatus) -> Unit,
    onOpenPostPrayerDhikr: () -> Unit
) {
    var answeredStatus by remember { mutableStateOf<PrayerStatus?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("did_you_pray_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AnimatedContent(
                    targetState = answeredStatus,
                    label = "dialog_content"
                ) { status ->
                    if (status == null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "هل صليت صلاة ${prayer.arabicName}؟",
                                fontFamily = AmiriFontFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )

                            if (userName.isNotBlank()) {
                                Text(
                                    text = "يا $userName، أسعدك الله بطاعته",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            BotanicalDivider()
                            Spacer(modifier = Modifier.height(14.dp))

                            // Action options
                            val options = listOf(
                                Triple(PrayerStatus.ON_TIME, "صليت في الوقت", "✅"),
                                Triple(PrayerStatus.IN_MOSQUE, "صليت في المسجد (جماعة)", "🕌"),
                                Triple(PrayerStatus.LATE, "صليت متأخرًا", "⏰"),
                                Triple(PrayerStatus.NOT_YET, "لم أصلِّ بعد", "❌"),
                                Triple(PrayerStatus.REMIND_LATER, "ذكّرني بعد ١٠ دقائق", "⏳")
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                options.forEach { (optionStatus, label, emoji) ->
                                    val isHighlight = optionStatus == PrayerStatus.ON_TIME || optionStatus == PrayerStatus.IN_MOSQUE
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isHighlight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isHighlight) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                            else MaterialTheme.colorScheme.outline
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onAnswerSelected(optionStatus)
                                                if (isHighlight) {
                                                    answeredStatus = optionStatus
                                                } else {
                                                    onDismiss()
                                                }
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = emoji,
                                                fontSize = 20.sp,
                                                modifier = Modifier.padding(end = 12.dp)
                                            )
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontFamily = CairoFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Thank you & Prompt post-prayer adhkar!
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "تقبّل الله طاعتك وعبادتك 🌿",
                                fontFamily = AmiriFontFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "ما شاء الله! قال رسول الله ﷺ: «من سبّح الله في دبر كل صلاة ثلاثًا وثلاثين... غفرت خطاياه ولو كانت مثل زبد البحر»",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    onOpenPostPrayerDhikr()
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = "📿 قراءة أذكار ما بعد الصلاة",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(
                                onClick = onDismiss,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("تم، الحمد لله", fontFamily = CairoFontFamily)
                            }
                        }
                    }
                }
            }
        }
    }
}
