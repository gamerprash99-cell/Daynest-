package com.daynest.ui.habits

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daynest.data.local.habit.HabitEntity
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.*
import com.daynest.ui.components.PremiumEmptyState
import com.daynest.ui.components.PremiumProgressRing
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.components.PremiumSummaryCard
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant
import com.daynest.ui.theme.NestSageGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen() {
    val viewModel: HabitsViewModel = viewModel(factory = LocalViewModelFactory.current)
    val state by viewModel.uiState.collectAsState()
    var editingHabit by remember { mutableStateOf<HabitEntity?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(HabitTab.Cards) }
    val spacing = LocalDaynestSpacing.current

    Scaffold(
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = {
                    editingHabit = null
                    showSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = com.daynest.ui.theme.full
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Habit")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PremiumSectionHeader(
                title = "Habits",
                subtitle = "Build the small things that matter"
            )
            Spacer(modifier = Modifier.height(spacing.md))

            // Stats summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                PremiumSummaryCard(
                    title = "Active Habits",
                    value = "${state.habits.size}",
                    subtitle = "Keep it going",
                    icon = Icons.Filled.Spa,
                    iconColor = NestSageGreen,
                    modifier = Modifier.weight(1f)
                )
                PremiumSummaryCard(
                    title = "Today's Streak",
                    value = "${state.habits.count { it.doneToday }}",
                    subtitle = "habits done",
                    icon = Icons.Filled.CheckCircle,
                    iconColor = NestLavender,
                    modifier = Modifier.weight(1f)
                )
                PremiumSummaryCard(
                    title = "Best Streak",
                    value = "${state.habits.maxOfOrNull { it.currentStreak } ?: 0}",
                    subtitle = "days",
                    icon = Icons.Filled.Fireplace,
                    iconColor = com.daynest.ui.theme.NestMutedPeach,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Tab selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                listOf(HabitTab.Cards, HabitTab.Heatmap, HabitTab.List).forEach { tab ->
                    PremiumChip(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.label) },
                        color = NestLavender
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Content based on tab
            when (selectedTab) {
                HabitTab.Cards -> {
                    if (state.habits.isEmpty()) {
                        PremiumEmptyState(
                            icon = Icons.Filled.Spa,
                            title = "No habits yet",
                            message = "Add one small habit you want to build.",
                            actionLabel = "Add Habit",
                            onAction = { editingHabit = null; showSheet = true }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().weight(1f),
                            verticalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            items(state.habits, key = { it.habit.id }) { row ->
                                PremiumHabitCard(
                                    habit = row.habit,
                                    doneToday = row.doneToday,
                                    currentStreak = row.currentStreak,
                                    onToggle = { viewModel.toggleToday(row.habit.id) },
                                    onClick = { editingHabit = row.habit; showSheet = true },
                                    onDelete = { viewModel.delete(row.habit) }
                                )
                            }
                        }
                    }
                }
                HabitTab.Heatmap -> {
                    HabitHeatmapScreen(
                        habits = state.habits,
                        onHabitClick = { editingHabit = it; showSheet = true }
                    )
                }
                HabitTab.List -> {
                    HabitListViewScreen(
                        habits = state.habits,
                        onToggle = { viewModel.toggleToday(it.habit.id) },
                        onClick = { editingHabit = it.habit; showSheet = true },
                        onDelete = { viewModel.delete(it.habit) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.xxl))
        }
    }

    if (showSheet) {
        AddEditHabitSheet(
            existing = editingHabit,
            onDismiss = { showSheet = false },
            onSave = { name, emoji, color ->
                viewModel.saveHabit(editingHabit, name, emoji, color)
                showSheet = false
            }
        )
    }
}

enum class HabitTab {
    Cards, Heatmap, List
}

val HabitTab.label: String
    get() = when (this) {
        HabitTab.Cards -> "Cards"
        HabitTab.Heatmap -> "Heatmap"
        HabitTab.List -> "List"
    }

@Composable
private fun PremiumHabitCard(
    habit: HabitEntity,
    doneToday: Boolean,
    currentStreak: Int,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val spacing = LocalDaynestSpacing.current
    val habitColor = androidx.compose.ui.graphics.Color(
        android.graphics.Color.parseColor(habit.colorHex)
    )

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Habit icon
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.size(48.dp),
                shape = com.daynest.ui.theme.DaynestShapes.medium,
                background = habitColor.copy(alpha = 0.15f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = habit.emoji,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(spacing.sm))
            Column(Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStreak > 0) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.Fireplace,
                            contentDescription = "Streak",
                            tint = com.daynest.ui.theme.NestMutedPeach,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$currentStreak day streak",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = if (doneToday) "Done today" else "Not done",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (doneToday) com.daynest.ui.theme.NestSageGreen
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(1.dp))
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (doneToday) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = if (doneToday) "Done today" else "Mark done today",
                        tint = if (doneToday) com.daynest.ui.theme.NestSageGreen
                        else MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete habit")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditHabitSheet(
    existing: HabitEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, emoji: String, color: String) -> Unit
) {
    val spacing = LocalDaynestSpacing.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var emoji by remember { mutableStateOf(existing?.emoji ?: "\u2728") }
    var colorHex by remember { mutableStateOf(existing?.colorHex ?: "#3F6A5E") }

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
                text = if (existing == null) "New Habit" else "Edit Habit",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.lg))

            PremiumTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Habit name") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(spacing.md))

            Text("Emoji", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(spacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                listOf("\u2728", "\uD83D\ude42", "\uD83D\ude04", "\uD83D\udc38", "\uD83D\udc41", "\uD83E\udd9c").forEach { e ->
                    FilterChip(
                        selected = emoji == e,
                        onClick = { emoji = e },
                        label = { Text(e) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(spacing.md))

            Text("Color", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(spacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                listOf("#3F6A5E", "#BCD4A7", "#C3B1E1", "#F4C2C2", "#A8DADC", "#E3AFA0").forEach { c ->
                    FilterChip(
                        selected = colorHex == c,
                        onClick = { colorHex = c },
                        label = { Text(c) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.lg))
            androidx.compose.material3.Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim(), emoji, colorHex)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Save Habit")
            }
            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}
