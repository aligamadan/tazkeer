package com.example.ui.settings

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.audio.AdhanAudioManager
import com.example.data.local.AppPreferences
import com.example.data.model.*
import com.example.data.prayer.CityData
import com.example.ui.components.BotanicalDivider
import com.example.ui.theme.CairoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appPreferences: AppPreferences,
    audioManager: AdhanAudioManager,
    onBack: () -> Unit,
    onDataUpdated: () -> Unit
) {
    var userName by remember { mutableStateOf(appPreferences.userName) }
    var selectedCityName by remember { mutableStateOf(appPreferences.selectedCityName) }
    var selectedMethod by remember { mutableStateOf(appPreferences.calculationMethod) }
    var selectedSchool by remember { mutableStateOf(appPreferences.juristicSchool) }
    var selectedMuezzin by remember { mutableStateOf(appPreferences.adhanMuezzin) }
    var followUpDelay by remember { mutableStateOf(appPreferences.followUpDelayMinutes) }
    var hijriAdjustment by remember { mutableStateOf(appPreferences.hijriAdjustment) }

    var showCityDialog by remember { mutableStateOf(false) }
    var showMethodDialog by remember { mutableStateOf(false) }
    var showSchoolDialog by remember { mutableStateOf(false) }
    var showMuezzinDialog by remember { mutableStateOf(false) }
    var showDelayDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }

    val isPlaying by audioManager.isPlaying.collectAsState()
    val playingMuezzin by audioManager.currentPlayingMuezzin.collectAsState()

    DisposableEffect(Unit) {
        onDispose { audioManager.stopAdhan() }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الإعدادات وضبط المواقيت",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SectionHeader("الحساب والموقع")
            }

            // City item
            item {
                SettingsClickableCard(
                    title = "المدينة والموقع الجغرافي",
                    subtitle = selectedCityName,
                    icon = Icons.Default.LocationOn,
                    onClick = { showCityDialog = true }
                )
            }

            // Calculation Method item
            item {
                SettingsClickableCard(
                    title = "طريقة حساب المواقيت",
                    subtitle = selectedMethod.titleArabic,
                    icon = Icons.Default.Calculate,
                    onClick = { showMethodDialog = true }
                )
            }

            // Juristic School (Asr)
            item {
                SettingsClickableCard(
                    title = "مذهب حساب صلاة العصر",
                    subtitle = selectedSchool.titleArabic,
                    icon = Icons.Default.Schedule,
                    onClick = { showSchoolDialog = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                SectionHeader("الأذان والتنبيهات")
            }

            // Adhan Voice
            item {
                SettingsClickableCard(
                    title = "صوت الأذان المفضل",
                    subtitle = selectedMuezzin.nameArabic,
                    icon = Icons.Default.VolumeUp,
                    onClick = { showMuezzinDialog = true }
                )
            }

            // Did you pray follow up delay
            item {
                SettingsClickableCard(
                    title = "وقت متابعة «هل صليت؟»",
                    subtitle = "بعد $followUpDelay دقائق من الأذان",
                    icon = Icons.Default.Alarm,
                    onClick = { showDelayDialog = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                SectionHeader("البيانات والتخصيص")
            }

            // User Name
            item {
                SettingsClickableCard(
                    title = "اسم المستخدم",
                    subtitle = if (userName.isBlank()) "غير محدد" else userName,
                    icon = Icons.Default.Person,
                    onClick = { showNameDialog = true }
                )
            }

            // Hijri adjustment
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "تعديل التاريخ الهجري",
                                style = MaterialTheme.typography.titleSmall,
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${if (hijriAdjustment > 0) "+$hijriAdjustment" else "$hijriAdjustment"} يوم",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalIconButton(
                                onClick = {
                                    if (hijriAdjustment > -2) {
                                        hijriAdjustment--
                                        appPreferences.hijriAdjustment = hijriAdjustment
                                        onDataUpdated()
                                    }
                                }
                            ) {
                                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            FilledTonalIconButton(
                                onClick = {
                                    if (hijriAdjustment < 2) {
                                        hijriAdjustment++
                                        appPreferences.hijriAdjustment = hijriAdjustment
                                        onDataUpdated()
                                    }
                                }
                            ) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                BotanicalDivider()
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "تذكير (Tazkeer) • تطبيق إسلامي شخصي بدون خوادم أو إعلانات أو تسجيل دخول. تُحفظ كافة عباداتك ووردك على هاتفك فقط ابتغاء الأجر وحفظ الخصوصية.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // City Selection Dialog
    if (showCityDialog) {
        var query by remember { mutableStateOf("") }
        val filtered = remember(query) {
            if (query.isBlank()) CityData.CITIES
            else CityData.CITIES.filter { it.nameArabic.contains(query) || it.countryArabic.contains(query) }
        }

        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = { Text("اختر المدينة", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.height(350.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("بحث...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(filtered) { city ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCityName = city.nameArabic
                                        appPreferences.selectedCityName = city.nameArabic
                                        appPreferences.customLatitude = city.latitude
                                        appPreferences.customLongitude = city.longitude
                                        appPreferences.timezoneOffset = city.timezoneOffsetHours
                                        showCityDialog = false
                                        onDataUpdated()
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(city.nameArabic, fontWeight = FontWeight.SemiBold)
                                Text(city.countryArabic, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) { Text("إغلاق") }
            }
        )
    }

    // Method Dialog
    if (showMethodDialog) {
        AlertDialog(
            onDismissRequest = { showMethodDialog = false },
            title = { Text("طريقة الحساب", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn {
                    items(CalculationMethod.values()) { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMethod = method
                                    appPreferences.calculationMethod = method
                                    showMethodDialog = false
                                    onDataUpdated()
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(method.titleArabic, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            if (method == selectedMethod) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showMethodDialog = false }) { Text("إغلاق") } }
        )
    }

    // Juristic School Dialog
    if (showSchoolDialog) {
        AlertDialog(
            onDismissRequest = { showSchoolDialog = false },
            title = { Text("مذهب حساب العصر", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    JuristicSchool.values().forEach { school ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSchool = school
                                    appPreferences.juristicSchool = school
                                    showSchoolDialog = false
                                    onDataUpdated()
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(school.titleArabic, style = MaterialTheme.typography.bodyMedium)
                            if (school == selectedSchool) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showSchoolDialog = false }) { Text("إغلاق") } }
        )
    }

    // Muezzin Selector Dialog
    if (showMuezzinDialog) {
        AlertDialog(
            onDismissRequest = {
                audioManager.stopAdhan()
                showMuezzinDialog = false
            },
            title = { Text("صوت المؤذن", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn {
                    items(AdhanMuezzin.values()) { muezzin ->
                        val isSelected = muezzin == selectedMuezzin
                        val isThisPlaying = isPlaying && playingMuezzin == muezzin
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMuezzin = muezzin
                                    appPreferences.adhanMuezzin = muezzin
                                }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(muezzin.nameArabic, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                Text(muezzin.reciterName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            }
                            IconButton(onClick = {
                                if (isThisPlaying) audioManager.stopAdhan()
                                else audioManager.playAdhan(muezzin)
                            }) {
                                Icon(
                                    imageVector = if (isThisPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    audioManager.stopAdhan()
                    showMuezzinDialog = false
                    onDataUpdated()
                }) { Text("حفظ") }
            }
        )
    }

    // Follow-up delay dialog
    if (showDelayDialog) {
        val options = listOf(5, 10, 15, 20)
        AlertDialog(
            onDismissRequest = { showDelayDialog = false },
            title = { Text("تأخير سؤال «هل صليت؟»", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("كم دقيقة بعد الأذان تود أن نسألك عن أداء الفريضة؟", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    options.forEach { mins ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    followUpDelay = mins
                                    appPreferences.followUpDelayMinutes = mins
                                    showDelayDialog = false
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("بعد $mins دقائق من الأذان")
                            if (mins == followUpDelay) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showDelayDialog = false }) { Text("إغلاق") } }
        )
    }

    // Name change dialog
    if (showNameDialog) {
        var tempName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("تعديل الاسم", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("اسمك الكريم") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    userName = tempName
                    appPreferences.userName = tempName
                    showNameDialog = false
                    onDataUpdated()
                }) { Text("حفظ") }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontFamily = CairoFontFamily,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun SettingsClickableCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
