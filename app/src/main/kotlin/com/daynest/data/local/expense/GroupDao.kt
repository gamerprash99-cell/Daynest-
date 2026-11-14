package com.daynest.data.local.expense

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    // Groups
    @Query("SELECT * FROM expense_groups WHERE isArchived = 0 ORDER BY createdAtEpochMillis DESC")
    fun observeAllGroups(): Flow<List<ExpenseGroupEntity>>

    @Query("SELECT * FROM expense_groups WHERE id = :id")
    suspend fun getGroupById(id: Long): ExpenseGroupEntity?

    @Query("SELECT * FROM expense_groups WHERE id = :id")
    fun observeGroupById(id: Long): Flow<ExpenseGroupEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGroup(group: ExpenseGroupEntity): Long

    @Update
    suspend fun updateGroup(group: ExpenseGroupEntity)

    @Delete
    suspend fun deleteGroup(group: ExpenseGroupEntity)

    // Members
    @Query("SELECT * FROM group_members WHERE groupId = :groupId ORDER BY name ASC")
    fun observeMembers(groupId: Long): Flow<List<GroupMemberEntity>>

    @Query("SELECT * FROM group_members WHERE id = :id")
    suspend fun getMemberById(id: Long): GroupMemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMember(member: GroupMemberEntity): Long

    @Delete
    suspend fun deleteMember(member: GroupMemberEntity)

    // Group Expenses
    @Query("SELECT * FROM group_expenses WHERE groupId = :groupId ORDER BY dateEpochDay DESC")
    fun observeGroupExpenses(groupId: Long): Flow<List<GroupExpenseEntity>>

    @Query("SELECT * FROM group_expenses WHERE id = :id")
    suspend fun getGroupExpenseById(id: Long): GroupExpenseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGroupExpense(expense: GroupExpenseEntity): Long

    @Update
    suspend fun updateGroupExpense(expense: GroupExpenseEntity)

    @Delete
    suspend fun deleteGroupExpense(expense: GroupExpenseEntity)

    // Shares
    @Query("SELECT * FROM expense_shares WHERE groupExpenseId = :groupExpenseId")
    fun observeShares(groupExpenseId: Long): Flow<List<ExpenseShareEntity>>

    @Query("SELECT * FROM expense_shares WHERE id = :id")
    suspend fun getShareById(id: Long): ExpenseShareEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertShare(share: ExpenseShareEntity): Long

    @Update
    suspend fun updateShare(share: ExpenseShareEntity)

    // Settlements
    @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY dateEpochDay DESC")
    fun observeSettlements(groupId: Long): Flow<List<SettlementEntity>>

    @Query("SELECT * FROM settlements WHERE id = :id")
    suspend fun getSettlementById(id: Long): SettlementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettlement(settlement: SettlementEntity): Long

    @Delete
    suspend fun deleteSettlement(settlement: SettlementEntity)
}
