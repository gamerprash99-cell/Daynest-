package com.daynest.data.local.task

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isDone ASC, dueDateEpochDay IS NULL, dueDateEpochDay ASC, priority DESC, createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDateEpochDay = :epochDay ORDER BY isDone ASC, priority DESC")
    fun observeForDay(epochDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isDone = 0 AND dueDateEpochDay IS NOT NULL AND dueDateEpochDay < :todayEpochDay")
    fun observeOverdueCount(todayEpochDay: Long): Flow<Int>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("UPDATE tasks SET isDone = :isDone, completedAtEpochMillis = :completedAt WHERE id = :id")
    suspend fun setDone(id: Long, isDone: Boolean, completedAt: Long?)
}
