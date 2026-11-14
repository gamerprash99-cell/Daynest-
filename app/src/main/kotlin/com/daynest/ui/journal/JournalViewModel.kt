package com.daynest.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daynest.data.local.journal.JournalEntryEntity
import com.daynest.data.local.journal.Mood
import com.daynest.data.repository.JournalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class JournalUiState(
    val isLoading: Boolean = true,
    val entries: List<JournalEntryEntity> = emptyList()
)

class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    val uiState: StateFlow<JournalUiState> = repository.observeAll()
        .map { JournalUiState(isLoading = false, entries = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), JournalUiState())

    fun saveEntry(
        existing: JournalEntryEntity?,
        mood: Mood,
        text: String,
        entryDate: LocalDate,
        title: String = ""
    ) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val entry = existing?.copy(
                mood = mood,
                text = text.trim(),
                title = title.trim(),
                updatedAtEpochMillis = System.currentTimeMillis()
            ) ?: JournalEntryEntity(
                entryDateEpochDay = entryDate.toEpochDay(),
                mood = mood,
                text = text.trim(),
                title = title.trim()
            )
            repository.save(entry)
        }
    }

    fun delete(entry: JournalEntryEntity) {
        viewModelScope.launch { repository.delete(entry) }
    }

    /** Loads a single entry for the editor's "edit existing" flow. */
    suspend fun getById(id: Long): JournalEntryEntity? = repository.getById(id)
}
