package com.daynest.data.repository

import com.daynest.data.local.expense.GroupDao
import com.daynest.data.local.expense.ExpenseGroupEntity
import com.daynest.data.local.expense.GroupMemberEntity
import com.daynest.data.local.expense.GroupExpenseEntity
import com.daynest.data.local.expense.ExpenseShareEntity
import com.daynest.data.local.expense.SettlementEntity
import kotlinx.coroutines.flow.Flow

class GroupRepository(private val dao: GroupDao) {

    // Groups
    fun observeAllGroups(): Flow<List<ExpenseGroupEntity>> = dao.observeAllGroups()
    suspend fun getGroupById(id: Long): ExpenseGroupEntity? = dao.getGroupById(id)
    fun observeGroupById(id: Long): Flow<ExpenseGroupEntity?> = dao.observeGroupById(id)
    suspend fun upsertGroup(group: ExpenseGroupEntity): Long = dao.upsertGroup(group)
    suspend fun updateGroup(group: ExpenseGroupEntity) = dao.updateGroup(group)
    suspend fun deleteGroup(group: ExpenseGroupEntity) = dao.deleteGroup(group)

    // Members
    fun observeMembers(groupId: Long): Flow<List<GroupMemberEntity>> = dao.observeMembers(groupId)
    suspend fun getMemberById(id: Long): GroupMemberEntity? = dao.getMemberById(id)
    suspend fun upsertMember(member: GroupMemberEntity): Long = dao.upsertMember(member)
    suspend fun deleteMember(member: GroupMemberEntity) = dao.deleteMember(member)

    // Group Expenses
    fun observeGroupExpenses(groupId: Long): Flow<List<GroupExpenseEntity>> = dao.observeGroupExpenses(groupId)
    suspend fun getGroupExpenseById(id: Long): GroupExpenseEntity? = dao.getGroupExpenseById(id)
    suspend fun upsertGroupExpense(expense: GroupExpenseEntity): Long = dao.upsertGroupExpense(expense)
    suspend fun updateGroupExpense(expense: GroupExpenseEntity) = dao.updateGroupExpense(expense)
    suspend fun deleteGroupExpense(expense: GroupExpenseEntity) = dao.deleteGroupExpense(expense)

    // Shares
    fun observeShares(groupExpenseId: Long): Flow<List<ExpenseShareEntity>> = dao.observeShares(groupExpenseId)
    suspend fun getShareById(id: Long): ExpenseShareEntity? = dao.getShareById(id)
    suspend fun upsertShare(share: ExpenseShareEntity): Long = dao.upsertShare(share)
    suspend fun updateShare(share: ExpenseShareEntity) = dao.updateShare(share)

    // Settlements
    fun observeSettlements(groupId: Long): Flow<List<SettlementEntity>> = dao.observeSettlements(groupId)
    suspend fun getSettlementById(id: Long): SettlementEntity? = dao.getSettlementById(id)
    suspend fun upsertSettlement(settlement: SettlementEntity): Long = dao.upsertSettlement(settlement)
    suspend fun deleteSettlement(settlement: SettlementEntity) = dao.deleteSettlement(settlement)
}
