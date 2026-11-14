package com.daynest.data.local.journal

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Mood(val emoji: String, val label: String) {
    GREAT("\uD83D\uDE04", "Great"),
    GOOD("\uD83D\uDE42", "Good"),
    OKAY("\uD83D\uDE10", "Okay"),
    LOW("\uD83D\uDE15", "Low"),
    AWFUL("\uD83D\uDE1E", "Awful")
}

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entryDateEpochDay: Long,
    val mood: Mood,
    val text: String,
    val title: String = "",
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)
