package com.daynest.data.local

import androidx.room.TypeConverter
import com.daynest.data.local.journal.Mood
import com.daynest.data.local.task.TaskPriority

class Converters {
    @TypeConverter
    fun fromTaskPriority(value: TaskPriority): String = value.name

    @TypeConverter
    fun toTaskPriority(value: String): TaskPriority = TaskPriority.valueOf(value)

    @TypeConverter
    fun fromMood(value: Mood): String = value.name

    @TypeConverter
    fun toMood(value: String): Mood = Mood.valueOf(value)
}
