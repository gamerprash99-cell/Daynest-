package com.daynest.ui.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daynest.data.local.habit.HabitCompletionEntity
import com.daynest.data.local.habit.HabitEntity
import com.daynest.data.repository.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HabitRow(
    val habit: HabitEntity,
    val completions: List<HabitCompletionEntity>,
    val doneToday: Boolean,
    val currentStreak: Int
)

data class HabitsUiState(
    val isLoading: Boolean = true,
    val habits: List<HabitRow> = emptyList()
)

private fun computeStreak(completedDays: Set<Long>, today: Long): Int {
    var streak = 0
    var day = today
    while (completedDays.contains(day)) {
        streak++
        day--
    }
    return streak
}

class HabitsViewModel(private val repository: HabitRepository) : ViewModel() {

    private val todayEpochDay = LocalDate.now().toEpochDay()

    val uiState: StateFlow<HabitsUiState> = repository.observeActive()
        .flatMapLatest { habits ->
            if (habits.isEmpty()) {
                flowOf(HabitsUiState(isLoading = false, habits = emptyList()))
            } else {
                val completionFlows = habits.map { habit ->
                    repository.observeCompletions(habit.id).map { habit to it }
                }
                combine(completionFlows) { pairs ->
                    HabitsUiState(
                        isLoading = false,
                        habits = pairs.map { (habit, completions) ->
                            val days = completions.map { it.dateEpochDay }.toSet()
                            HabitRow(
                                habit = habit,
                                completions = completions,
                                doneToday = todayEpochDay in days,
                                currentStreak = computeStreak(days, todayEpochDay)
                            )
                        }
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HabitsUiState())

    fun toggleToday(habitId: Long) {
        viewModelScope.launch { repository.toggleCompletion(habitId, todayEpochDay) }
    }

    fun saveHabit(existing: HabitEntity?, name: String, emoji: String, colorHex: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val habit = existing?.copy(
                name = name.trim(),
                emoji = emoji,
                colorHex = colorHex
            ) ?: HabitEntity(
                name = name.trim(),
                emoji = emoji,
                colorHex = colorHex
            )
            repository.save(habit)
        }
    }

    fun delete(habit: HabitEntity) {
        viewModelScope.launch { repository.delete(habit) }
    }
}
