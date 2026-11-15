package com.daynest.ui.habits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daynest.data.local.habit.HabitEntity
import com.daynest.data.local.journal.Mood
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.components.PremiumSummaryCard
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant
import com.daynest.ui.theme.NestSageGreen
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitHeatmapScreen(
    habits: List<HabitRow>,
    onHabitClick: (HabitEntity) -> Unit
) {
    val spacing = LocalDaynestSpacing.current
    var selectedHabit by remember { mutableStateOf<HabitRow?>(null) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val habitToShow = selectedHabit ?: habits.firstOrNull() ?: return

    val completions = habitToShow.completions.map { it.dateEpochDay }.toSet()
    val yearMonth = currentMonth
    val firstDay = yearMonth.atDay(1)
    val lastDay = yearMonth.atEndOfMonth()
    val days = (1..lastDay.dayOfMonth).map { firstDay.withDayOfMonth(it) }
    val startOffset = firstDay.dayOfWeek.getValue() - 1

    Column(
        modifier = Modifier.fillMaxSize().padding(spacing.md)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { selectedHabit = null }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Column {
                Text(
                    text = habitToShow.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${habitToShow.currentStreak} day streak",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(1.dp))
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous month")
            }
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "Next month")
            }
        }
        Spacer(modifier = Modifier.height(spacing.md))

        // Heatmap legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeatmapLegendItem(color = Color(0xFFEBEDF0), label = "None")
            HeatmapLegendItem(color = Color(0xFF9BE9A8), label = "1")
            HeatmapLegendItem(color = Color(0xFF40C463), label = "2")
            HeatmapLegendItem(color = Color(0xFF30A14E), label = "3+")
        }

        Spacer(modifier = Modifier.height(spacing.md))

        // Weekday headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Heatmap grid
        var dayIndex = 0
        while (dayIndex < startOffset + days.count()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(7) { col ->
                    val globalIndex = dayIndex + col
                    if (globalIndex < startOffset || globalIndex >= startOffset + days.count()) {
                        Spacer(modifier = Modifier.width(0.dp).height(24.dp))
                    } else {
                        val day = days.elementAt(globalIndex - startOffset)
                        val isCompleted = completions.contains(day.toEpochDay())
                        val isToday = day == LocalDate.now()

                        HeatmapCell(
                            day = day,
                            isCompleted = isCompleted,
                            isToday = isToday,
                            onClick = { /* toggle completion */ }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            dayIndex += 7
        }

        Spacer(modifier = Modifier.height(spacing.md))

        // Streak stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            PremiumSummaryCard(
                title = "Current Streak",
                value = "${habitToShow.currentStreak}",
                subtitle = "days",
                icon = Icons.Filled.Fireplace,
                iconColor = com.daynest.ui.theme.NestMutedPeach,
                modifier = Modifier.weight(1f)
            )
            PremiumSummaryCard(
                title = "Total Completions",
                value = "${habitToShow.completions.size}",
                subtitle = "this month",
                icon = Icons.Filled.CheckCircle,
                iconColor = NestLavender,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HeatmapLegendItem(color: Color, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.size(12.dp).background(color, shape = com.daynest.ui.theme.full)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HeatmapCell(
    day: java.time.LocalDate,
    isCompleted: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val color = when {
        isCompleted -> Color(0xFF30A14E) // Dark green
        else -> Color(0xFFEBEDF0) // Light gray
    }

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(24.dp)
            .clickable(onClick = onClick)
            .background(
                color = color,
                shape = com.daynest.ui.theme.DaynestShapes.small
            )
            .border(
                if (isToday) 1.dp else 0.dp,
                com.daynest.ui.theme.NestLavender,
                com.daynest.ui.theme.DaynestShapes.small
            )
    )
}
