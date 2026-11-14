package com.daynest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.daynest.data.local.expense.ExpenseDao
import com.daynest.data.local.expense.ExpenseEntity
import com.daynest.data.local.expense.ExpenseCategoryEntity
import com.daynest.data.local.expense.GroupDao
import com.daynest.data.local.expense.ExpenseGroupEntity
import com.daynest.data.local.expense.GroupMemberEntity
import com.daynest.data.local.expense.GroupExpenseEntity
import com.daynest.data.local.expense.ExpenseShareEntity
import com.daynest.data.local.expense.SettlementEntity
import com.daynest.data.local.habit.HabitCompletionEntity
import com.daynest.data.local.habit.HabitDao
import com.daynest.data.local.habit.HabitEntity
import com.daynest.data.local.journal.JournalDao
import com.daynest.data.local.journal.JournalEntryEntity
import com.daynest.data.local.task.TaskDao
import com.daynest.data.local.task.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        JournalEntryEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        ExpenseEntity::class,
        ExpenseCategoryEntity::class,
        ExpenseGroupEntity::class,
        GroupMemberEntity::class,
        GroupExpenseEntity::class,
        ExpenseShareEntity::class,
        SettlementEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DaynestDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun journalDao(): JournalDao
    abstract fun habitDao(): HabitDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun groupDao(): GroupDao

    companion object {
        @Volatile private var INSTANCE: DaynestDatabase? = null

        fun getInstance(context: android.content.Context): DaynestDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    DaynestDatabase::class.java,
                    "daynest.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
