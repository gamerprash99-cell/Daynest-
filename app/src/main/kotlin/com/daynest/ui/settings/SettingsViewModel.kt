package com.daynest.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daynest.data.preferences.ThemeOption
import com.daynest.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userName: String = "",
    val themeOption: ThemeOption = ThemeOption.SYSTEM
)

class SettingsViewModel(private val preferences: UserPreferencesRepository) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferences.userName,
        preferences.themeOption
    ) { name, theme -> SettingsUiState(name, theme) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setThemeOption(option: ThemeOption) {
        viewModelScope.launch { preferences.setThemeOption(option) }
    }

    fun setUserName(name: String) {
        viewModelScope.launch { preferences.setUserName(name) }
    }
}
