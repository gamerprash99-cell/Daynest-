package com.daynest.ui.journal

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daynest.data.local.journal.JournalEntryEntity
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.*
import com.daynest.ui.components.PremiumEmptyState
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant
import com.daynest.util.friendlyLabel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalCalendarScreen(
    onOpenEntry: (Long) -> Unit = {},
    onNewEntry: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val viewModel: JournalViewModel = viewModel(factory = LocalViewModelFactory.current)
    val state by viewModel.uiState.collectAsState()
    val spacing = LocalDaynestSpacing.current

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val yearMonth = currentMonth
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    val daysInMonth = firstDayOfMonth..lastDayOfMonth
    val startOffset = firstDayOfMonth.dayOfWeek.getValue() - 1 // Monday = 1

    val entriesByDate = state.entries.associateBy {
        java.time.LocalDate.ofEpochDay(it.entryDateEpochDay)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(spacing.md)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Diary",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onNewEntry) {
                Icon(Icons.Default.Add, contentDescription = "Write new entry")
            }
        }
        Spacer(modifier = Modifier.height(spacing.sm))

        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous month")
            }
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next month")
            }
        }
        Spacer(modifier = Modifier.height(spacing.md))

        // Calendar grid
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Weekday headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Calendar days
            var dayIndex = 0
            while (dayIndex < startOffset + daysInMonth.count()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(7) { col ->
                        val globalIndex = dayIndex + col
                        if (globalIndex < startOffset || globalIndex >= startOffset + daysInMonth.count()) {
                            Spacer(
                                modifier = Modifier
                                    .width(0.dp)
                                    .height(40.dp)
                            )
                        } else {
                            val day = daysInMonth.elementAt(globalIndex - startOffset)
                            val isToday = day == LocalDate.now()
                            val isSelected = selectedDate == day
                            val entry = entriesByDate[day]
                            val hasEntry = entry != null

                            CalendarDay(
                                day = day.dayOfMonth,
                                isToday = isToday,
                                isSelected = isSelected,
                                hasEntry = hasEntry,
                                mood = entry?.mood,
                                onClick = { selectedDate = day }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                dayIndex += 7
            }
        }

        Spacer(modifier = Modifier.height(spacing.md))

        // Selected date entries
        if (selectedDate != null) {
            PremiumSectionHeader(
                title = "Memories on ${selectedDate!!.format(DateTimeFormatter.ofPattern("EEEE, MMM d"))}"
            )
            Spacer(modifier = Modifier.height(spacing.sm))

            val dayEntries = state.entries.filter {
                java.time.LocalDate.ofEpochDay(it.entryDateEpochDay) == selectedDate
            }

            if (dayEntries.isEmpty()) {
                PremiumEmptyState(
                    icon = Icons.Default.Book,
                    title = "No entries for this day",
                    message = "Write your first memory for this date.",
                    actionLabel = "Write Entry",
                    onAction = onNewEntry
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    items(dayEntries, key = { it.id }) { entry ->
                        JournalRow(
                            entry = entry,
                            onClick = { onOpenEntry(entry.id) },
                            onDelete = { viewModel.delete(entry) }
                        )
                    }
                }
            }
        } else {
            // Show recent entries when no date selected
            PremiumSectionHeader(title = "Recent Memories")
            Spacer(modifier = Modifier.height(spacing.sm))

            val recent = state.entries.take(5)
            if (recent.isEmpty()) {
                PremiumEmptyState(
                    icon = Icons.Default.Book,
                    title = "Your journal is empty",
                    message = "Start writing your first memory.",
                    actionLabel = "Write Entry",
                    onAction = onNewEntry
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    items(recent, key = { it.id }) { entry ->
                        JournalRow(
                            entry = entry,
                            onClick = { selectedDate = java.time.LocalDate.ofEpochDay(entry.entryDateEpochDay) },
                            onDelete = { viewModel.delete(entry) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    hasEntry: Boolean,
    mood: Mood?,
    onClick: () -> Unit
) {
    val spacing = LocalDaynestSpacing.current
    val moodColor = when (mood) {
        Mood.GREAT -> com.daynest.ui.theme.MoodGreat
        Mood.GOOD -> com.daynest.ui.theme.MoodGood
        Mood.OKAY -> com.daynest.ui.theme.MoodOkay
        Mood.LOW -> com.daynest.ui.theme.MoodLow
        Mood.AWFUL -> com.daynest.ui.theme.MoodAwful
        null -> null
    }

    androidx.compose.material3.Card(
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick),
        shape = com.daynest.ui.theme.full,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected) com.daynest.ui.theme.NestLavender
            else if (isToday) com.daynest.ui.theme.NestLavender.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.Center
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected || isToday) androidx.compose.ui.text.font.FontWeight.Bold
                    else androidx.compose.ui.text.font.FontWeight.Normal
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else if (isToday) com.daynest.ui.theme.NestLavenderDark
                else MaterialTheme.colorScheme.onSurface
            )
            if (hasEntry && mood != null) {
                Text(
                    text = mood.emoji,
                    fontSize = 10.sp
                )
            } else if (hasEntry) {
                // Small dot for entry without mood
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.size(4.dp),
                    shape = com.daynest.ui.theme.full,
                    background = com.daynest.ui.theme.NestOutline
                )
            }
        }
    }
}
