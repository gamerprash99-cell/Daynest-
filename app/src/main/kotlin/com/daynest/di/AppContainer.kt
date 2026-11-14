package com.daynest.di

import android.content.Context
import com.daynest.data.local.DaynestDatabase
import com.daynest.data.preferences.UserPreferencesRepository
import com.daynest.data.repository.ExpenseRepository
import com.daynest.data.repository.GroupRepository
import com.daynest.data.repository.HabitRepository
import com.daynest.data.repository.JournalRepository
import com.daynest.data.repository.TaskRepository

class AppContainer(context: Context) {
    private val database = DaynestDatabase.getInstance(context)

    val taskRepository = TaskRepository(database.taskDao())
    val journalRepository = JournalRepository(database.journalDao())
    val habitRepository = HabitRepository(database.habitDao())
    val expenseRepository = ExpenseRepository(database.expenseDao())
    val groupRepository = GroupRepository(database.groupDao())
    val userPreferencesRepository = UserPreferencesRepository(context)
}
