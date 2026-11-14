package com.daynest.data.local.expense

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // Expenses
    @Query("SELECT * FROM expenses ORDER BY dateEpochDay DESC, createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE dateEpochDay >= :startDay AND dateEpochDay <= :endDay ORDER BY dateEpochDay DESC")
    fun observeByDateRange(startDay: Long, endDay: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: Long): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId ORDER BY dateEpochDay DESC")
    fun observeByCategory(categoryId: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    @Query("SELECT SUM(amountPaise) FROM expenses WHERE isIncome = 1 AND dateEpochDay >= :startDay AND dateEpochDay <= :endDay")
    suspend fun getTotalIncome(startDay: Long, endDay: Long): Long

    @Query("SELECT SUM(amountPaise) FROM expenses WHERE isIncome = 0 AND dateEpochDay >= :startDay AND dateEpochDay <= :endDay")
    suspend fun getTotalExpense(startDay: Long, endDay: Long): Long

    // Categories
    @Query("SELECT * FROM expense_categories ORDER BY isIncome ASC, name ASC")
    fun observeAllCategories(): Flow<List<ExpenseCategoryEntity>>

    @Query("SELECT * FROM expense_categories WHERE isIncome = :isIncome ORDER BY name ASC")
    fun observeCategoriesByType(isIncome: Boolean): Flow<List<ExpenseCategoryEntity>>

    @Query("SELECT * FROM expense_categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): ExpenseCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategory(category: ExpenseCategoryEntity): Long

    @Update
    suspend fun updateCategory(category: ExpenseCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: ExpenseCategoryEntity)
}
