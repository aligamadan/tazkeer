package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.data.audio.AdhanAudioManager
import com.example.data.notification.PrayerNotificationManager
import com.example.data.local.AppPreferences
import com.example.data.model.PrayerName
import com.example.data.model.PrayerStatus
import com.example.ui.components.*
import com.example.ui.prayer.DidYouPrayDialog
import com.example.ui.prayer.PostPrayerDhikrSheet
import com.example.ui.stats.PrayerStatsCard
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.CairoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    appPreferences: AppPreferences,
    audioManager: AdhanAudioManager,
    onOpenSettings: () -> Unit,
    onTabSelected: (Int) -> Unit,
    currentTab: Int = 0
) {
    val uiState by viewModel.uiState.collectAsState()
    var manualPrayerForDialog by remember { mutableStateOf<PrayerName?>(null) }
    var showPostPrayerDhikr by remember { mutableStateOf(false) }

    val activeDialogPrayer = uiState.promptPrayer ?: manualPrayerForDialog

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FloralWreathLogo(size = 38.dp, showSubtitle = false)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "تذكير",
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = uiState.cityName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .testTag("settings_button")
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "الإعدادات",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                val navItems = listOf(
                    Triple(0, "الرئيسية", Icons.Default.Home),
                    Triple(1, "المصحف", Icons.Default.AutoStories),
                    Triple(2, "الأذكار", Icons.Default.FavoriteBorder),
                    Triple(3, "السبحة", Icons.Default.MotionPhotosOn),
                    Triple(4, "المزيد", Icons.Default.MoreHoriz)
                )

                navItems.forEach { (index, label, icon) ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick = { onTabSelected(index) },
                        icon = { Icon(icon, contentDescription = label) },
                        label = {
                            Text(
                                text = label,
                                fontFamily = CairoFontFamily,
                                fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Greeting & Date banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = uiState.greeting,
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.hijriDateText,
                                fontFamily = AmiriFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = uiState.gregorianDateText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Next Prayer Hero Card with Circular Countdown Ring
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PrayerCountdownRing(
                            nextPrayer = uiState.nextPrayer,
                            remainingHours = uiState.remainingHours,
                            remainingMinutes = uiState.remainingMinutes,
                            remainingSeconds = uiState.remainingSeconds,
                            progressFraction = uiState.progressFraction
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        BotanicalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        // Next prayer time + Adhan voice preview
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "موعد الأذان: ${uiState.nextPrayerTime.toString().take(5)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "المؤذن: ${appPreferences.adhanMuezzin.nameArabic}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val context = LocalContext.current
                                val isAudioPlaying by audioManager.isPlaying.collectAsState()

                                // Notification button
                                FilledTonalIconButton(
                                    onClick = {
                                        PrayerNotificationManager.showPrayerNotification(
                                            context = context,
                                            prayer = uiState.nextPrayer,
                                            cityName = uiState.cityName
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "تجربة إشعار الصلاة",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Quick Adhan Audio Preview button
                                FilledTonalButton(
                                    onClick = {
                                        if (isAudioPlaying) audioManager.stopAdhan()
                                        else audioManager.playAdhan(appPreferences.adhanMuezzin)
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isAudioPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                        contentDescription = "استماع للأذان",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAudioPlaying) "إيقاف" else "تجربة الأذان",
                                        fontFamily = CairoFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Signature Feature Banner: Quick Follow-up
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "🕌", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "متابعة أداء الصلوات (هل صليت؟)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "سجل صلواتك في وقتها أو بالمسجد وارتقِ بعبادتك",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = {
                                // Default to previous or next fard prayer
                                manualPrayerForDialog = uiState.nextPrayer
                            },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("تسجيل", fontFamily = CairoFontFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Section: All Prayer Times of the Day
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مواقيت صلوات اليوم",
                        fontFamily = CairoFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (uiState.isDst) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { viewModel.toggleDst() }
                    ) {
                        Text(
                            text = if (uiState.isDst) "☀️ التوقيت الصيفي (+١س)" else "❄️ التوقيت الشتوي",
                            fontSize = 11.sp,
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isDst) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Prayers list
            val orderedPrayers = listOf(
                PrayerName.FAJR,
                PrayerName.SUNRISE,
                PrayerName.DHUHR,
                PrayerName.ASR,
                PrayerName.MAGHRIB,
                PrayerName.ISHA
            )

            items(orderedPrayers) { prayer ->
                val time = uiState.prayerTimes[prayer] ?: java.time.LocalTime.NOON
                val isNext = prayer == uiState.nextPrayer
                val status = uiState.prayerStatuses[prayer]
                val notifMode = uiState.notificationModes[prayer] ?: com.example.data.model.NotificationMode.ADHAN

                PrayerRowItem(
                    prayer = prayer,
                    time = time,
                    isNext = isNext,
                    isCurrent = false,
                    status = status,
                    notificationMode = notifMode,
                    onStatusClick = {
                        manualPrayerForDialog = prayer
                    },
                    onNotificationToggle = {
                        viewModel.toggleNotificationMode(prayer)
                    }
                )
            }

            // Qiyam & Last Third of Night Indicator
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌙", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "الثلث الأخير من الليل (قيام الليل)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "يبدأ الثلث الأخير: ${uiState.lastThirdTime.toString().take(5)} • نصف الليل: ${uiState.midnightTime.toString().take(5)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }

            // Worship Statistics and Streak
            item {
                PrayerStatsCard(appPreferences = appPreferences)
            }

            // Spiritual Ayah of the Day
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "آية اليوم",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "﴿ وَأَقِمِ الصَّلَاةَ طَرَفَيِ النَّهَارِ وَزُلَفًا مِّنَ اللَّيْلِ ۚ إِنَّ الْحَسَنَاتِ يُذْهِبْنَ السَّيِّئَاتِ ۚ ذَٰلِكَ ذِكْرَىٰ لِلذَّاكِرِينَ ﴾",
                            fontFamily = AmiriFontFamily,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 32.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "[سورة هود: ١١٤]",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Active "Did you pray?" Dialog
    if (activeDialogPrayer != null) {
        DidYouPrayDialog(
            prayer = activeDialogPrayer,
            userName = uiState.userName,
            onDismiss = {
                viewModel.dismissPrompt()
                manualPrayerForDialog = null
            },
            onAnswerSelected = { status ->
                viewModel.recordPrayerStatus(activeDialogPrayer, status)
            },
            onOpenPostPrayerDhikr = {
                showPostPrayerDhikr = true
            }
        )
    }

    // Post prayer dhikr sheet
    if (showPostPrayerDhikr) {
        PostPrayerDhikrSheet(
            onDismiss = { showPostPrayerDhikr = false }
        )
    }
}
