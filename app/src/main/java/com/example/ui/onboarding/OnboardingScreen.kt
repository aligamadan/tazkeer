package com.example.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.example.data.audio.AdhanAudioManager
import com.example.data.local.AppPreferences
import com.example.data.model.AdhanMuezzin
import com.example.data.model.CalculationMethod
import com.example.data.model.CityLocation
import com.example.data.prayer.CityData
import com.example.ui.components.BotanicalDivider
import com.example.ui.components.FloralWreathLogo
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.CairoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    appPreferences: AppPreferences,
    audioManager: AdhanAudioManager,
    onFinish: () -> Unit
) {
    var step by remember { mutableStateOf(0) } // 0: Name, 1: Location & Method, 2: Adhan & Ready
    var nameInput by remember { mutableStateOf(appPreferences.userName) }
    var selectedCity by remember {
        mutableStateOf(CityData.CITIES.find { it.nameArabic == appPreferences.selectedCityName } ?: CityData.DEFAULT_CITY)
    }
    var selectedMethod by remember { mutableStateOf(appPreferences.calculationMethod) }
    var selectedMuezzin by remember { mutableStateOf(appPreferences.adhanMuezzin) }
    val isAudioPlaying by audioManager.isPlaying.collectAsState()
    val playingMuezzin by audioManager.currentPlayingMuezzin.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    DisposableEffect(Unit) {
        onDispose {
            audioManager.stopAdhan()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        appPreferences.isFirstLaunch = false
                        if (appPreferences.userName.isBlank()) appPreferences.userName = "يا عبد الله"
                        audioManager.stopAdhan()
                        onFinish()
                    }
                ) {
                    Text(
                        text = "تخطي إلى الرئيسية ↵",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Logo
            Spacer(modifier = Modifier.height(8.dp))
            FloralWreathLogo(size = 110.dp, showSubtitle = false)

            Spacer(modifier = Modifier.height(8.dp))
            BotanicalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Progress Indicators
            Row(
                modifier = Modifier.padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { idx ->
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (step == idx) 32.dp else 16.dp)
                            .clip(CircleShape)
                            .background(
                                if (step >= idx) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                    )
                }
            }

            // Steps Content
            AnimatedContent(
                targetState = step,
                modifier = Modifier.weight(1f),
                label = "onboarding_steps"
            ) { currentStep ->
                when (currentStep) {
                    0 -> {
                        // Step 0: Name
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "مرحبًا بك في تذكير",
                                fontFamily = AmiriFontFamily,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "رفيقك الهادئ للمحافظة على صلاتك وأذكارك ووردك اليومي بصفاء وطمأنينة.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("ما اسمك الكريم؟") },
                                placeholder = { Text("مثال: عبد الرحمن، فاطمة...") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("name_input_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "✨ جميع بياناتك وعباداتك محفوظة على هاتفك فقط دون تسجيل دخول أو إنترنت.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    1 -> {
                        // Step 1: City & Method
                        var citySearchQuery by remember { mutableStateOf("") }
                        val filteredCities = remember(citySearchQuery) {
                            if (citySearchQuery.isBlank()) CityData.CITIES
                            else CityData.CITIES.filter {
                                it.nameArabic.contains(citySearchQuery, ignoreCase = true) ||
                                it.countryArabic.contains(citySearchQuery, ignoreCase = true)
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "حدد مدينتك لحساب مواقيت الصلاة",
                                fontFamily = CairoFontFamily,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "تعمل الحسابات الفلكية الدقيقة بالكامل دون الحاجة للإنترنت.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = citySearchQuery,
                                onValueChange = { citySearchQuery = it },
                                placeholder = { Text("ابحث عن مدينتك أو دولتك...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(filteredCities) { city ->
                                    val isSelected = city.nameArabic == selectedCity.nameArabic
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                                else Color.Transparent
                                            )
                                            .clickable { selectedCity = city }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = city.nameArabic,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                                else MaterialTheme.colorScheme.onBackground
                                            )
                                            Text(
                                                text = city.countryArabic,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Step 2: Adhan Voice & Final Welcome
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "اختر صوت الأذان المفضل",
                                fontFamily = CairoFontFamily,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "يمكنك تجربة الاستماع للأذان الآن وتغييره في أي وقت من الإعدادات.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(AdhanMuezzin.values()) { muezzin ->
                                    val isSelected = muezzin == selectedMuezzin
                                    val isThisPlaying = isAudioPlaying && playingMuezzin == muezzin

                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surface
                                        ),
                                        border = if (isSelected) {
                                            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                        } else {
                                            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedMuezzin = muezzin }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = muezzin.nameArabic,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                                    else MaterialTheme.colorScheme.onBackground
                                                )
                                                Text(
                                                    text = muezzin.reciterName,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            // Audio Preview Button
                                            IconButton(
                                                onClick = {
                                                    if (isThisPlaying) {
                                                        audioManager.stopAdhan()
                                                    } else {
                                                        audioManager.playAdhan(muezzin)
                                                    }
                                                },
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                                            ) {
                                                Icon(
                                                    imageVector = if (isThisPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                    contentDescription = "استماع للأذان",
                                                    tint = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    OutlinedButton(
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            step--
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("السابق", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (step < 2) {
                            if (step == 0 && nameInput.isBlank()) {
                                nameInput = "يا عبد الله"
                            }
                            step++
                        } else {
                            // Save and finish
                            appPreferences.userName = nameInput.trim()
                            appPreferences.selectedCityName = selectedCity.nameArabic
                            appPreferences.customLatitude = selectedCity.latitude
                            appPreferences.customLongitude = selectedCity.longitude
                            appPreferences.timezoneOffset = selectedCity.timezoneOffsetHours
                            appPreferences.calculationMethod = selectedMethod
                            appPreferences.adhanMuezzin = selectedMuezzin
                            appPreferences.isFirstLaunch = false
                            audioManager.stopAdhan()
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (step == 2) "ابدأ رحلتك المباركة ✨" else "متابعة",
                        fontFamily = CairoFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
