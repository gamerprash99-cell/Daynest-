package com.daynest.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daynest.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val preferences: UserPreferencesRepository
) : ViewModel() {

    fun completeOnboarding(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                preferences.setUserName(name.trim())
            }
            preferences.setOnboardingComplete(true)
        }
    }
}
