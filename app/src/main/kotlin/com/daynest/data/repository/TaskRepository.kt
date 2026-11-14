package com.daynest.data.repository

import com.daynest.data.local.task.TaskDao
import com.daynest.data.local.task.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {
    fun observeAll(): Flow<List<TaskEntity>> = dao.observeAll()
    fun observeForDay(epochDay: Long): Flow<List<TaskEntity>> = dao.observeForDay(epochDay)
    fun observeOverdueCount(todayEpochDay: Long): Flow<Int> = dao.observeOverdueCount(todayEpochDay)

    suspend fun getById(id: Long) = dao.getById(id)

    suspend fun save(task: TaskEntity): Long = dao.upsert(task)

    suspend fun delete(task: TaskEntity) = dao.delete(task)

    suspend fun setDone(task: TaskEntity, isDone: Boolean) {
        dao.setDone(task.id, isDone, if (isDone) System.currentTimeMillis() else null)
    }
}
