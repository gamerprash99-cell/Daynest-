package com.daynest.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.daynest.di.AppContainer
import com.daynest.ui.expense.ExpenseViewModel
import com.daynest.ui.expense.GroupViewModel
import com.daynest.ui.habits.HabitsViewModel
import com.daynest.ui.home.HomeViewModel
import com.daynest.ui.journal.JournalViewModel
import com.daynest.ui.onboarding.OnboardingViewModel
import com.daynest.ui.settings.SettingsViewModel
import com.daynest.ui.tasks.TasksViewModel

class DaynestViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(container.taskRepository, container.journalRepository, container.habitRepository) as T

            modelClass.isAssignableFrom(TasksViewModel::class.java) ->
                TasksViewModel(container.taskRepository) as T

            modelClass.isAssignableFrom(JournalViewModel::class.java) ->
                JournalViewModel(container.journalRepository) as T

            modelClass.isAssignableFrom(HabitsViewModel::class.java) ->
                HabitsViewModel(container.habitRepository) as T

            modelClass.isAssignableFrom(ExpenseViewModel::class.java) ->
                ExpenseViewModel(container.expenseRepository) as T

            modelClass.isAssignableFrom(GroupViewModel::class.java) ->
                GroupViewModel(container.groupRepository) as T

            modelClass.isAssignableFrom(OnboardingViewModel::class.java) ->
                OnboardingViewModel(container.userPreferencesRepository) as T

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(container.userPreferencesRepository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

val LocalViewModelFactory = staticCompositionLocalOf<DaynestViewModelFactory> {
    throw IllegalStateException("LocalViewModelFactory not provided")
}
