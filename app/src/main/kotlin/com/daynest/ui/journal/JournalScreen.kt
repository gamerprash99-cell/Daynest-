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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daynest.data.local.journal.JournalEntryEntity
import com.daynest.data.local.journal.Mood
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.*
import com.daynest.ui.components.PremiumEmptyState
import com.daynest.ui.components.PremiumMoodPicker
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant
import com.daynest.util.friendlyLabel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(onOpenCalendar: () -> Unit = {}) {
    val viewModel: JournalViewModel = viewModel(factory = LocalViewModelFactory.current)
    val state by viewModel.uiState.collectAsState()
    var editingEntry by remember { mutableStateOf<JournalEntryEntity?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val spacing = LocalDaynestSpacing.current

    Scaffold(
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = {
                    editingEntry = null
                    selectedDate = LocalDate.now()
                    showSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = com.daynest.ui.theme.full
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Entry")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PremiumSectionHeader(
                title = "Journal",
                subtitle = "Capture your thoughts and memories",
                trailing = {
                    IconButton(onClick = onOpenCalendar) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar view")
                    }
                }
            )
            Spacer(modifier = Modifier.height(spacing.md))

            // Quick date selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                val today = LocalDate.now()
                val days = listOf(
                    Pair("Today", today),
                    Pair("Yesterday", today.minusDays(1)),
                    Pair("Tomorrow", today.plusDays(1))
                )
                days.forEach { (label, date) ->
                    val isSelected = selectedDate == date
                    androidx.compose.material3.FilterChip(
                        selected = isSelected,
                        onClick = { selectedDate = date },
                        label = { Text(label) },
                        colors = androidx.compose.material3.ChipDefaults.colors(
                            containerColor = if (isSelected) NestLavender else Color.Transparent,
                            borderColor = if (isSelected) NestLavender else MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
                androidx.compose.material3.FilterChip(
                    selected = selectedDate == null,
                    onClick = { selectedDate = null },
                    label = { Text("All") },
                    colors = androidx.compose.material3.ChipDefaults.colors(
                        containerColor = if (selectedDate == null) NestLavender else Color.Transparent,
                        borderColor = if (selectedDate == null) NestLavender else MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Entries list
            val entriesToShow = if (selectedDate != null) {
                state.entries.filter {
                    java.time.LocalDate.ofEpochDay(it.entryDateEpochDay) == selectedDate
                }
            } else {
                state.entries
            }

            if (entriesToShow.isEmpty()) {
                PremiumEmptyState(
                    icon = Icons.Default.Book,
                    title = if (selectedDate != null) "No entries on this date" else "Your journal is empty",
                    message = if (selectedDate != null) "Write your first entry for this day." else "Write your first entry whenever you're ready.",
                    actionLabel = "Write Entry",
                    onAction = { editingEntry = null; selectedDate = LocalDate.now(); showSheet = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    items(entriesToShow, key = { it.id }) { entry ->
                        JournalRow(
                            entry = entry,
                            onClick = { editingEntry = entry; selectedDate = java.time.LocalDate.ofEpochDay(entry.entryDateEpochDay); showSheet = true },
                            onDelete = { viewModel.delete(entry) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.xxl))
        }
    }

    if (showSheet) {
        AddEditJournalSheet(
            existing = editingEntry,
            onDismiss = { showSheet = false },
            onSave = { mood, text, date ->
                val entryDate = date ?: LocalDate.now()
                viewModel.saveEntry(editingEntry, mood, text, entryDate)
                showSheet = false
            }
        )
    }
}

@Composable
private fun JournalRow(
    entry: JournalEntryEntity,
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
            Text(
                text = entry.mood.emoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(spacing.sm))
            Column(Modifier.weight(1f)) {
                Text(
                    text = java.time.LocalDate.ofEpochDay(entry.entryDateEpochDay).friendlyLabel(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = entry.text,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete entry")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditJournalSheet(
    existing: JournalEntryEntity?,
    onDismiss: () -> Unit,
    onSave: (Mood, String, LocalDate?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var mood by remember { mutableStateOf(existing?.mood ?: Mood.OKAY) }
    var text by remember { mutableStateOf(existing?.text ?: "") }
    var selectedDate by remember { mutableStateOf(
        existing?.let { java.time.LocalDate.ofEpochDay(it.entryDateEpochDay) } ?: LocalDate.now()
    ) }

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
                text = if (existing == null) "New Entry" else "Edit Entry",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.lg))

            // Date picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Date")
                Spacer(modifier = Modifier.width(spacing.sm))
                Text(
                    text = selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d")),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(spacing.md))

            // Mood picker
            Text("How are you feeling?", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(spacing.xs))
            PremiumMoodPicker(
                selectedMood = mood.name,
                onMoodSelected = { mood = Mood.valueOf(it ?: Mood.OKAY.name) }
            )

            Spacer(modifier = Modifier.height(spacing.md))

            // Text editor
            PremiumTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("What's on your mind?") },
                maxLines = 6,
                singleLine = false,
                error = text.isBlank()
            )

            Spacer(modifier = Modifier.height(spacing.lg))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                androidx.compose.material3.TextButton(
                    onClick = { onDismiss() },
                    colors = androidx.compose.material3.TextButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(text = "Cancel")
                }
                androidx.compose.material3.Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSave(mood, text, selectedDate)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Save Entry",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}
