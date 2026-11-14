package com.daynest.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.daynest.data.preferences.ThemeOption
import com.daynest.di.AppContainer
import com.daynest.ui.navigation.MainScaffold
import com.daynest.ui.onboarding.OnboardingScreen
import com.daynest.ui.theme.DaynestTheme

@Composable
fun DaynestApp(container: AppContainer) {
    val factory = remember { DaynestViewModelFactory(container) }
    val themeOption by container.userPreferencesRepository.themeOption
        .collectAsState(initial = ThemeOption.SYSTEM)
    val onboardingComplete by container.userPreferencesRepository.isOnboardingComplete
        .collectAsState(initial = null)
    val userName by container.userPreferencesRepository.userName.collectAsState(initial = "")

    val darkTheme = when (themeOption) {
        ThemeOption.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
        ThemeOption.LIGHT -> false
        ThemeOption.DARK -> true
    }

    CompositionLocalProvider(LocalViewModelFactory provides factory) {
        DaynestTheme(darkTheme = darkTheme) {
            when (onboardingComplete) {
                null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                false -> OnboardingScreen(onFinished = { /* state flips automatically via DataStore flow */ })
                true -> MainScaffold(userName = userName)
            }
        }
    }
}
