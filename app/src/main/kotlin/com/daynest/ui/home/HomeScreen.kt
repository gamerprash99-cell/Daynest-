package com.daynest.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daynest.data.local.task.TaskEntity
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.*
import com.daynest.ui.components.PremiumEmptyState
import com.daynest.ui.components.PremiumProgressRing
import com.daynest.ui.components.PremiumQuickAction
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.components.PremiumSummaryCard
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant
import com.daynest.util.friendlyLabel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    userName: String,
    onOpenPlanner: () -> Unit,
    onOpenJournal: () -> Unit,
    onOpenHabits: () -> Unit,
    onOpenExpenses: () -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    val viewModel: HomeViewModel = viewModel(factory = LocalViewModelFactory.current)
    val state by viewModel.uiState.collectAsState()
    val spacing = LocalDaynestSpacing.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.lg)
    ) {
        // Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (userName.isNotBlank()) "Good day, $userName" else "Good day",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }
        }

        if (state.isLoading) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                    CircularProgressIndicator()
                }
            }
            return@LazyColumn
        }

        // Summary cards row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                PremiumSummaryCard(
                    title = "Tasks Today",
                    value = "${state.todaysTasks.count { !it.isDone }}",
                    subtitle = "${state.overdueCount} overdue",
                    icon = Icons.Filled.CheckCircle,
                    iconColor = NestLavender,
                    onClick = onOpenPlanner,
                    modifier = Modifier.weight(1f)
                )
                PremiumSummaryCard(
                    title = "Habits Done",
                    value = "${state.habitsDoneToday}",
                    subtitle = "of ${state.totalHabits} today",
                    icon = Icons.Filled.Spa,
                    iconColor = com.daynest.ui.theme.NestSageGreen,
                    onClick = onOpenHabits,
                    modifier = Modifier.weight(1f)
                )
                PremiumSummaryCard(
                    title = "Spending",
                    value = "₹${state.todaysExpense}",
                    subtitle = "Today",
                    icon = Icons.Filled.AttachMoney,
                    iconColor = com.daynest.ui.theme.NestBlushPink,
                    onClick = onOpenExpenses,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Progress ring for habits
        item {
            PremiumSectionHeader(title = "Your Progress")
            Spacer(modifier = Modifier.height(spacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumProgressRing(
                    progress = state.habitCompletionRate,
                    size = 70.dp,
                    lineWidth = 8.dp,
                    color = com.daynest.ui.theme.NestSageGreen,
                    centerContent = {
                        Text(
                            text = "${(state.habitCompletionRate * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                Column {
                    Text(
                        text = "Habit Completion",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${state.completedHabitsToday}/${state.totalHabits} today",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(1.dp))
                Column {
                    Text(
                        text = "Journal Entries",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.journalEntriesToday.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Quick Actions
        item {
            PremiumSectionHeader(title = "Quick Actions")
            Spacer(modifier = Modifier.height(spacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                PremiumQuickAction(
                    icon = Icons.Filled.Edit,
                    label = "Write Entry",
                    color = com.daynest.ui.theme.NestLavender,
                    onClick = onOpenJournal,
                    modifier = Modifier.weight(1f)
                )
                PremiumQuickAction(
                    icon = Icons.Filled.Add,
                    label = "Add Task",
                    color = com.daynest.ui.theme.NestPowderBlue,
                    onClick = onOpenPlanner,
                    modifier = Modifier.weight(1f)
                )
                PremiumQuickAction(
                    icon = Icons.Filled.Add,
                    label = "Habit",
                    color = com.daynest.ui.theme.NestSageGreen,
                    onClick = onOpenHabits,
                    modifier = Modifier.weight(1f)
                )
                PremiumQuickAction(
                    icon = Icons.Filled.Add,
                    label = "Expense",
                    color = com.daynest.ui.theme.NestBlushPink,
                    onClick = onOpenExpenses,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Today's Tasks
        item {
            PremiumSectionHeader(
                title = "Today's Plan",
                trailing = {
                    Text(
                        "See all",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }

        if (state.todaysTasks.isEmpty()) {
            item {
                PremiumEmptyState(
                    icon = Icons.Filled.EventNote,
                    title = "Nothing scheduled today",
                    message = "Add a task to get your day moving.",
                    actionLabel = "Open Planner",
                    onAction = onOpenPlanner
                )
            }
        } else {
            items(state.todaysTasks.take(5), key = { it.id }) { task ->
                PremiumHomeTaskRow(
                    task = task,
                    onToggle = { viewModel.toggleTask(task) },
                    onClick = { /* Navigate to task detail */ }
                )
            }
        }

        // Recent Journal
        item {
            PremiumSectionHeader(title = "Recent Journal")
        }
        if (state.journalEntriesToday > 0) {
            item {
                PremiumHomeJournalRow(
                    title = "${state.journalEntriesToday} entry written today",
                    subtitle = "Keep reflecting on your day",
                    onClick = onOpenJournal
                )
            }
        } else {
            item {
                PremiumHomeJournalRow(
                    title = "No entry yet today",
                    subtitle = "Take a moment to reflect on your day",
                    onClick = onOpenJournal
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(spacing.xxl))
        }
    }
}

@Composable
private fun PremiumHomeTaskRow(
    task: TaskEntity,
    onToggle: () -> Unit,
    onClick: () -> Unit
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
                    textDecoration = if (task.isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough
                    else null,
                    color = if (task.isDone) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                task.dueDateEpochDay?.let {
                    Text(
                        text = java.time.LocalDate.ofEpochDay(it).friendlyLabel(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumHomeJournalRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
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
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.Book,
                contentDescription = "Journal",
                tint = com.daynest.ui.theme.NestLavender,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(spacing.sm))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
