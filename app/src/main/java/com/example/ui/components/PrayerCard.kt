package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.NotificationMode
import com.example.data.model.PrayerName
import com.example.data.model.PrayerStatus
import com.example.ui.theme.CairoFontFamily
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun PrayerRowItem(
    prayer: PrayerName,
    time: LocalTime,
    isNext: Boolean,
    isCurrent: Boolean,
    status: PrayerStatus?,
    notificationMode: NotificationMode,
    onStatusClick: () -> Unit,
    onNotificationToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFard = prayer.isFard

    val backgroundColor = when {
        isNext -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        isCurrent -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isNext -> MaterialTheme.colorScheme.primary
        isCurrent -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.outline
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(if (isNext || isCurrent) 1.5.dp else 1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .testTag("prayer_row_${prayer.name}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Icon + Prayer Name + Status badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Status action button or prayer icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                status != null -> MaterialTheme.colorScheme.secondaryContainer
                                isNext -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .clickable(enabled = isFard) { onStatusClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (status != null) {
                        Text(text = status.icon, fontSize = 18.sp)
                    } else {
                        val iconVector = when (prayer) {
                            PrayerName.FAJR -> Icons.Default.Brightness4
                            PrayerName.SUNRISE -> Icons.Default.WbSunny
                            PrayerName.DHUHR -> Icons.Default.WbSunny
                            PrayerName.ASR -> Icons.Default.Brightness6
                            PrayerName.MAGHRIB -> Icons.Default.Brightness5
                            PrayerName.ISHA -> Icons.Default.NightsStay
                        }
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = prayer.arabicName,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = CairoFontFamily,
                            fontWeight = if (isNext || isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isNext) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "القادمة",
                                    fontSize = 10.sp,
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    if (isFard) {
                        Text(
                            text = status?.titleArabic ?: "انقر لتسجيل الصلاة",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (status != null) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.clickable { onStatusClick() }
                        )
                    }
                }
            }

            // Right: Time + Notification Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                val formattedTime = time.format(DateTimeFormatter.ofPattern("hh:mm a"))
                    .replace("AM", "ص")
                    .replace("PM", "م")

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (isFard) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onNotificationToggle,
                        modifier = Modifier.size(36.dp)
                    ) {
                        val (icon, tint) = when (notificationMode) {
                            NotificationMode.ADHAN -> Icons.Default.VolumeUp to MaterialTheme.colorScheme.primary
                            NotificationMode.NOTIFICATION -> Icons.Default.Notifications to MaterialTheme.colorScheme.secondary
                            NotificationMode.OFF -> Icons.Default.NotificationsOff to MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = notificationMode.labelArabic,
                            tint = tint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
