package com.daynest.data.local.habit

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String = "\u2728",
    val colorHex: String = "#3F6A5E",
    val targetDaysPerWeek: Int = 7,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
