package com.daynest.ui.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daynest.data.local.task.TaskEntity
import com.daynest.data.local.task.TaskPriority
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.PremiumChip
import com.daynest.ui.components.PremiumEmptyState
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.components.PremiumTextField
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant
import com.daynest.util.friendlyLabel
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen() {
    val viewModel: TasksViewModel = viewModel(factory = LocalViewModelFactory.current)
    val state by viewModel.uiState.collectAsState()
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var showCompleted by rememberSaveable { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(TaskFilter.All) }
    val spacing = LocalDaynestSpacing.current

    Scaffold(
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = {
                    editingTask = null
                    showSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = com.daynest.ui.theme.full
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PremiumSectionHeader(
                title = "Planner",
                subtitle = "Organize your day, one task at a time"
            )
            Spacer(modifier = Modifier.height(spacing.md))

            // Filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                listOf(TaskFilter.All, TaskFilter.Pending, TaskFilter.Completed).forEach { filter ->
                    PremiumChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.label) },
                        color = NestLavender
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Tasks list
            val filteredTasks = when (selectedFilter) {
                TaskFilter.All -> state.pendingTasks + state.completedTasks
                TaskFilter.Pending -> state.pendingTasks
                TaskFilter.Completed -> state.completedTasks
            }

            if (filteredTasks.isEmpty()) {
                PremiumEmptyState(
                    icon = Icons.Default.EventNote,
                    title = "Your planner is empty",
                    message = "Add your first task with the button below.",
                    actionLabel = "Add Task",
                    onAction = { editingTask = null; showSheet = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        val isCompleted = task.isDone
                        if (selectedFilter == TaskFilter.Completed && !isCompleted) return@items
                        if (selectedFilter == TaskFilter.Pending && isCompleted) return@items

                        PremiumTaskRow(
                            task = task,
                            onToggle = { viewModel.toggleDone(task) },
                            onClick = { editingTask = task; showSheet = true },
                            onDelete = { viewModel.delete(task) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.xxl))
        }
    }

    if (showSheet) {
        AddEditTaskSheet(
            existing = editingTask,
            onDismiss = { showSheet = false },
            onSave = { title, notes, dueDate, priority ->
                viewModel.saveTask(editingTask, title, notes, dueDate, priority)
                showSheet = false
            }
        )
    }
}

enum class TaskFilter {
    All, Pending, Completed
}

val TaskFilter.label: String
    get() = when (this) {
        TaskFilter.All -> "All"
        TaskFilter.Pending -> "Pending"
        TaskFilter.Completed -> "Completed"
    }

@Composable
private fun PremiumTaskRow(
    task: TaskEntity,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val spacing = LocalDaynestSpacing.current
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggle() },
                colors = androidx.compose.material3.CheckboxDefaults.colors(
                    uncheckedColor = MaterialTheme.colorScheme.outlineVariant,
                    checkedColor = com.daynest.ui.theme.NestSageGreen
                )
            )
            Spacer(modifier = Modifier.width(spacing.sm))
            Column(Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                    color = if (task.isDone) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    task.dueDateEpochDay?.let {
                        Text(
                            text = java.time.LocalDate.ofEpochDay(it).friendlyLabel(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = task.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall,
                        color = when (task.priority) {
                            TaskPriority.HIGH -> com.daynest.ui.theme.NestBlushPinkDark
                            TaskPriority.MEDIUM -> com.daynest.ui.theme.NestMutedPeach
                            TaskPriority.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete task")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditTaskSheet(
    existing: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, notes: String, dueDate: Long?, priority: TaskPriority) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }
    var dueDate by remember { mutableStateOf(existing?.dueDateEpochDay) }
    var priority by remember { mutableStateOf(existing?.priority ?: TaskPriority.MEDIUM) }
    var showDatePicker by remember { mutableStateOf(false) }
    val spacing = LocalDaynestSpacing.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.lg)
        ) {
            Text(
                text = if (existing == null) "New Task" else "Edit Task",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.lg))

            PremiumTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("What do you need to do?") },
                singleLine = true,
                error = title.isBlank()
            )
            Spacer(modifier = Modifier.height(spacing.md))

            PremiumTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                maxLines = 3,
                singleLine = false
            )
            Spacer(modifier = Modifier.height(spacing.md))

            Text("Priority", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(spacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                TaskPriority.entries.forEach { p ->
                    PremiumChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        color = when (p) {
                            TaskPriority.HIGH -> com.daynest.ui.theme.NestBlushPinkDark
                            TaskPriority.MEDIUM -> com.daynest.ui.theme.NestMutedPeach
                            TaskPriority.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))
            Text("Due Date", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(spacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                androidx.compose.material3.AssistChip(
                    onClick = { dueDate = null },
                    label = { Text("None") }
                )
                androidx.compose.material3.AssistChip(
                    onClick = { dueDate = LocalDate.now().toEpochDay() },
                    label = { Text("Today") }
                )
                androidx.compose.material3.AssistChip(
                    onClick = { dueDate = LocalDate.now().plusDays(1).toEpochDay() },
                    label = { Text("Tomorrow") }
                )
                androidx.compose.material3.AssistChip(
                    onClick = { showDatePicker = true },
                    label = { Text("Pick…") }
                )
            }
            dueDate?.let {
                Spacer(modifier = Modifier.height(spacing.xs))
                Text(
                    text = "Due ${java.time.LocalDate.ofEpochDay(it).friendlyLabel()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NestLavender
                )
            }

            Spacer(modifier = Modifier.height(spacing.lg))
            androidx.compose.material3.Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, notes, dueDate, priority)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium,
                elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(2.dp)
            ) {
                Text(
                    text = "Save Task",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                )
            }
            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate?.let {
                LocalDate.ofEpochDay(it).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        dueDate = java.time.Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toEpochDay()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
