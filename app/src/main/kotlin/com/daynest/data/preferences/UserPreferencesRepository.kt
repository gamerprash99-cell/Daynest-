package com.daynest.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "daynest_prefs")

enum class ThemeOption { SYSTEM, LIGHT, DARK }

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val USER_NAME = stringPreferencesKey("user_name")
        val THEME_OPTION = stringPreferencesKey("theme_option")
    }

    val isOnboardingComplete: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.ONBOARDING_COMPLETE] ?: false }

    val userName: Flow<String> =
        context.dataStore.data.map { it[Keys.USER_NAME] ?: "" }

    val themeOption: Flow<ThemeOption> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.THEME_OPTION]?.let { runCatching { ThemeOption.valueOf(it) }.getOrNull() }
                ?: ThemeOption.SYSTEM
        }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { it[Keys.USER_NAME] = name }
    }

    suspend fun setThemeOption(option: ThemeOption) {
        context.dataStore.edit { it[Keys.THEME_OPTION] = option.name }
    }
}
