package com.daynest.data.repository

import com.daynest.data.local.expense.ExpenseDao
import com.daynest.data.local.expense.ExpenseEntity
import com.daynest.data.local.expense.ExpenseCategoryEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {

    // Expenses
    fun observeAll(): Flow<List<ExpenseEntity>> = dao.observeAll()
    fun observeByDateRange(startDay: Long, endDay: Long): Flow<List<ExpenseEntity>> =
        dao.observeByDateRange(startDay, endDay)
    fun observeByCategory(categoryId: String): Flow<List<ExpenseEntity>> =
        dao.observeByCategory(categoryId)
    suspend fun getById(id: Long): ExpenseEntity? = dao.getById(id)
    suspend fun upsert(expense: ExpenseEntity): Long = dao.upsert(expense)
    suspend fun delete(expense: ExpenseEntity) = dao.delete(expense)

    // Totals
    suspend fun getTotalIncome(startDay: Long, endDay: Long): Long =
        dao.getTotalIncome(startDay, endDay) ?: 0
    suspend fun getTotalExpense(startDay: Long, endDay: Long): Long =
        dao.getTotalExpense(startDay, endDay) ?: 0

    // Categories
    fun observeAllCategories(): Flow<List<ExpenseCategoryEntity>> =
        dao.observeAllCategories()
    fun observeCategoriesByType(isIncome: Boolean): Flow<List<ExpenseCategoryEntity>> =
        dao.observeCategoriesByType(isIncome)
    suspend fun getCategoryById(id: Long): ExpenseCategoryEntity? =
        dao.getCategoryById(id)
    suspend fun upsertCategory(category: ExpenseCategoryEntity): Long =
        dao.upsertCategory(category)
    suspend fun updateCategory(category: ExpenseCategoryEntity) =
        dao.updateCategory(category)
    suspend fun deleteCategory(category: ExpenseCategoryEntity) =
        dao.deleteCategory(category)
}
