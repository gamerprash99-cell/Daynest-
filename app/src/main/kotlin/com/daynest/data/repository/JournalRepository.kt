package com.daynest.data.repository

import com.daynest.data.local.journal.JournalDao
import com.daynest.data.local.journal.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val dao: JournalDao) {
    fun observeAll(): Flow<List<JournalEntryEntity>> = dao.observeAll()
    fun observeForDay(epochDay: Long): Flow<List<JournalEntryEntity>> = dao.observeForDay(epochDay)
    fun observeCount(): Flow<Int> = dao.observeCount()

    suspend fun getById(id: Long) = dao.getById(id)

    suspend fun save(entry: JournalEntryEntity): Long = dao.upsert(entry)

    suspend fun delete(entry: JournalEntryEntity) = dao.delete(entry)
}
