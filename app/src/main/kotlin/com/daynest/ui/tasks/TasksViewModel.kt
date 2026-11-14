package com.daynest.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daynest.data.local.task.TaskEntity
import com.daynest.data.local.task.TaskPriority
import com.daynest.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TasksUiState(
    val isLoading: Boolean = true,
    val pendingTasks: List<TaskEntity> = emptyList(),
    val completedTasks: List<TaskEntity> = emptyList()
)

class TasksViewModel(private val repository: TaskRepository) : ViewModel() {

    val uiState: StateFlow<TasksUiState> = repository.observeAll()
        .map { all ->
            TasksUiState(
                isLoading = false,
                pendingTasks = all.filter { !it.isDone },
                completedTasks = all.filter { it.isDone }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TasksUiState())

    fun toggleDone(task: TaskEntity) {
        viewModelScope.launch { repository.setDone(task, !task.isDone) }
    }

    fun delete(task: TaskEntity) {
        viewModelScope.launch { repository.delete(task) }
    }

    fun saveTask(
        existing: TaskEntity?,
        title: String,
        notes: String,
        dueDateEpochDay: Long?,
        priority: TaskPriority
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = existing?.copy(
                title = title.trim(),
                notes = notes.trim(),
                dueDateEpochDay = dueDateEpochDay,
                priority = priority
            ) ?: TaskEntity(
                title = title.trim(),
                notes = notes.trim(),
                dueDateEpochDay = dueDateEpochDay,
                priority = priority
            )
            repository.save(task)
        }
    }
}
