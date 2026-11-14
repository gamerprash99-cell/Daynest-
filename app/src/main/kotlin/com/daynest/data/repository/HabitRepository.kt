package com.daynest.data.repository

import com.daynest.data.local.habit.HabitCompletionEntity
import com.daynest.data.local.habit.HabitDao
import com.daynest.data.local.habit.HabitEntity
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val dao: HabitDao) {
    fun observeActive(): Flow<List<HabitEntity>> = dao.observeActive()
    fun observeCompletions(habitId: Long): Flow<List<HabitCompletionEntity>> = dao.observeCompletions(habitId)
    fun observeCompletionsForDay(epochDay: Long): Flow<List<HabitCompletionEntity>> = dao.observeCompletionsForDay(epochDay)

    suspend fun getById(id: Long) = dao.getById(id)

    suspend fun save(habit: HabitEntity): Long = dao.upsert(habit)

    suspend fun delete(habit: HabitEntity) = dao.delete(habit)

    suspend fun toggleCompletion(habitId: Long, epochDay: Long) {
        val existing = dao.getCompletion(habitId, epochDay)
        if (existing != null) {
            dao.markIncomplete(habitId, epochDay)
        } else {
            dao.markComplete(HabitCompletionEntity(habitId = habitId, dateEpochDay = epochDay))
        }
    }
}
