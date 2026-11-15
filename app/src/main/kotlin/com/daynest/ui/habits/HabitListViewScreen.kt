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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.daynest.data.local.habit.HabitEntity
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.*
import com.daynest.ui.components.PremiumEmptyState
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestSageGreen
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListViewScreen(
    habits: List<HabitRow>,
    onToggle: (Long) -> Unit,
    onClick: (HabitRow) -> Unit,
    onDelete: (HabitEntity) -> Unit
) {
    val spacing = LocalDaynestSpacing.current

    Column(
        modifier = Modifier.fillMaxSize().padding(spacing.md)
    ) {
        PremiumSectionHeader(
            title = "Habit List",
            subtitle = "Track your daily progress"
        )
        Spacer(modifier = Modifier.height(spacing.md))

        if (habits.isEmpty()) {
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                shape = com.daynest.ui.theme.DaynestShapes.medium,
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "No habits",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No habits yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add your first habit to start tracking.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                items(habits, key = { it.habit.id }) { row ->
                    HabitListRow(
                        habitRow = row,
                        onToggle = { onToggle(row.habit.id) },
                        onClick = { onClick(row) },
                        onDelete = { onDelete(row.habit) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitListRow(
    habitRow: HabitRow,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val spacing = LocalDaynestSpacing.current
    val habit = habitRow.habit
    val habitColor = androidx.compose.ui.graphics.Color(
        android.graphics.Color.parseColor(habit.colorHex)
    )

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
            // Habit color indicator
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.size(8.dp).background(habitColor, shape = com.daynest.ui.theme.full)
            )
            Spacer(modifier = Modifier.width(spacing.sm))
            Column(Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (habitRow.doneToday) "Completed today" else "Not completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(1.dp))
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${habitRow.currentStreak}d",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    ),
                    color = if (habitRow.currentStreak > 0) com.daynest.ui.theme.NestMutedPeach
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (habitRow.doneToday) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = if (habitRow.doneToday) "Done today" else "Mark done today",
                        tint = if (habitRow.doneToday) NestSageGreen else MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete habit")
                }
            }
        }
    }
}
