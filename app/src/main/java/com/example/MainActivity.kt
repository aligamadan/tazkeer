package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.audio.AdhanAudioManager
import com.example.data.local.AppPreferences
import com.example.data.notification.PrayerNotificationManager
import com.example.ui.adhkar.AdhkarScreen
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.more.MoreScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.quran.QuranScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.tasbeeh.TasbeehScreen
import com.example.ui.theme.TazkeerTheme

class MainActivity : ComponentActivity() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var audioManager: AdhanAudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PrayerNotificationManager.createNotificationChannel(this)

        appPreferences = AppPreferences(this)
        audioManager = AdhanAudioManager(this)

        val homeViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return HomeViewModel(appPreferences, audioManager) as T
                }
            }
        )[HomeViewModel::class.java]

        setContent {
            var isFirstLaunch by remember { mutableStateOf(appPreferences.isFirstLaunch) }
            var currentTab by remember { mutableStateOf(0) }
            var isSettingsOpen by remember { mutableStateOf(false) }

            // Enforce Arabic RTL layout globally
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                // Use light cream theme matching the user's logo!
                TazkeerTheme(darkTheme = false) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AnimatedContent(
                            targetState = Triple(isFirstLaunch, isSettingsOpen, currentTab),
                            label = "main_screen_nav"
                        ) { (firstLaunch, settingsOpen, tab) ->
                            when {
                                firstLaunch -> {
                                    OnboardingScreen(
                                        appPreferences = appPreferences,
                                        audioManager = audioManager,
                                        onFinish = {
                                            isFirstLaunch = false
                                            homeViewModel.loadData()
                                        }
                                    )
                                }
                                settingsOpen -> {
                                    SettingsScreen(
                                        appPreferences = appPreferences,
                                        audioManager = audioManager,
                                        onBack = { isSettingsOpen = false },
                                        onDataUpdated = { homeViewModel.loadData() }
                                    )
                                }
                                else -> {
                                    when (tab) {
                                        0 -> {
                                            HomeScreen(
                                                viewModel = homeViewModel,
                                                appPreferences = appPreferences,
                                                audioManager = audioManager,
                                                onOpenSettings = { isSettingsOpen = true },
                                                onTabSelected = { newTab ->
                                                    currentTab = newTab
                                                },
                                                currentTab = currentTab
                                            )
                                        }
                                        1 -> {
                                            // Full Digital Quran
                                            QuranScreen(
                                                onBackToHome = { currentTab = 0 }
                                            )
                                        }
                                        2 -> {
                                            // Full Adhkar & Hadith
                                            AdhkarScreen(
                                                onBackToHome = { currentTab = 0 }
                                            )
                                        }
                                        3 -> {
                                            // Smart Electronic Tasbeeh
                                            TasbeehScreen(
                                                onBackToHome = { currentTab = 0 }
                                            )
                                        }
                                        4 -> {
                                            // More (Qibla, 99 Names, Zakat, Calendar, Settings)
                                            MoreScreen(
                                                appPreferences = appPreferences,
                                                onOpenSettings = { isSettingsOpen = true },
                                                onBackToHome = { currentTab = 0 }
                                            )
                                        }
                                        else -> {
                                            HomeScreen(
                                                viewModel = homeViewModel,
                                                appPreferences = appPreferences,
                                                audioManager = audioManager,
                                                onOpenSettings = { isSettingsOpen = true },
                                                onTabSelected = { currentTab = it },
                                                currentTab = currentTab
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
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.stopAdhan()
    }
}
