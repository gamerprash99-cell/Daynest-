package com.daynest.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daynest.data.local.task.TaskEntity
import com.daynest.data.repository.HabitRepository
import com.daynest.data.repository.JournalRepository
import com.daynest.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val isLoading: Boolean = true,
    val todaysTasks: List<TaskEntity> = emptyList(),
    val overdueCount: Int = 0,
    val journalEntriesToday: Int = 0,
    val habitsDoneToday: Int = 0,
    val totalHabits: Int = 0,
    val completedHabitsToday: Int = 0,
    val habitCompletionRate: Float = 0f,
    val todaysExpense: Long = 0
)

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val journalRepository: JournalRepository,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val todayEpochDay = LocalDate.now().toEpochDay()

    val uiState: StateFlow<HomeUiState> = combine(
        taskRepository.observeForDay(todayEpochDay),
        taskRepository.observeOverdueCount(todayEpochDay),
        journalRepository.observeForDay(todayEpochDay).map { it.size },
        habitRepository.observeActive(),
        habitRepository.observeCompletionsForDay(todayEpochDay)
    ) { tasks, overdue, journalCount, habits, completions ->
        val completedHabitIds = completions.map { it.habitId }.toSet()
        val doneToday = habits.count { it.id in completedHabitIds }
        val rate = if (habits.isEmpty()) 0f else doneToday.toFloat() / habits.size
        HomeUiState(
            isLoading = false,
            todaysTasks = tasks,
            overdueCount = overdue,
            journalEntriesToday = journalCount,
            habitsDoneToday = doneToday,
            totalHabits = habits.size,
            completedHabitsToday = doneToday,
            habitCompletionRate = rate,
            todaysExpense = 0 // Will be wired when expense module is integrated
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.setDone(task, !task.isDone)
        }
    }
}
