package com.daynest.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daynest.data.local.expense.ExpenseEntity
import com.daynest.data.local.expense.ExpenseCategoryEntity
import com.daynest.data.repository.ExpenseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ExpenseUiState(
    val isLoading: Boolean = true,
    val expenses: List<ExpenseEntity> = emptyList(),
    val categories: List<ExpenseCategoryEntity> = emptyList(),
    val totalIncome: Long = 0,
    val totalExpense: Long = 0,
    val selectedCategory: String? = null,
    val error: String? = null
)

data class ExpenseFormState(
    val title: String = "",
    val amount: String = "",
    val categoryId: String = "",
    val date: Long = LocalDate.now().toEpochDay(),
    val note: String = "",
    val paymentMethod: String = "Cash",
    val isIncome: Boolean = false,
    val error: String? = null
)

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val today = LocalDate.now()
    private val startOfMonth = today.withDayOfMonth(1)
    private val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())

    val uiState: StateFlow<ExpenseUiState> = combine(
        repository.observeByDateRange(startOfMonth.toEpochDay(), endOfMonth.toEpochDay()),
        repository.observeAllCategories()
    ) { expenses, categories ->
        val income = expenses.filter { it.isIncome }.sumOf { it.amountPaise }
        val expense = expenses.filter { !it.isIncome }.sumOf { it.amountPaise }
        ExpenseUiState(
            isLoading = false,
            expenses = expenses,
            categories = categories,
            totalIncome = income,
            totalExpense = expense,
            selectedCategory = null,
            error = null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExpenseUiState())

    fun saveExpense(
        title: String,
        amountPaise: Long,
        categoryId: String,
        date: Long,
        note: String?,
        paymentMethod: String?,
        isIncome: Boolean
    ) {
        if (title.isBlank()) {
            // Error handling would be done via UI state
            return
        }
        viewModelScope.launch {
            val expense = ExpenseEntity(
                title = title.trim(),
                amountPaise = amountPaise,
                categoryId = categoryId,
                dateEpochDay = date,
                note = note,
                paymentMethod = paymentMethod,
                isIncome = isIncome
            )
            repository.upsert(expense)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch { repository.delete(expense) }
    }

    fun filterByCategory(categoryId: String) {
        // Would update UI state to filter
    }
}

class GroupViewModel(private val repository: com.daynest.data.repository.GroupRepository) : ViewModel() {

    data class GroupUiState(
        val isLoading: Boolean = true,
        val groups: List<com.daynest.data.local.expense.ExpenseGroupEntity> = emptyList(),
        val error: String? = null
    )

    data class GroupDetailUiState(
        val group: com.daynest.data.local.expense.ExpenseGroupEntity? = null,
        val members: List<com.daynest.data.local.expense.GroupMemberEntity> = emptyList(),
        val expenses: List<com.daynest.data.local.expense.GroupExpenseEntity> = emptyList(),
        val settlements: List<com.daynest.data.local.expense.SettlementEntity> = emptyList(),
        val memberBalances: List<MemberBalance> = emptyList(),
        val suggestedSettlements: List<SuggestedSettlement> = emptyList(),
        val error: String? = null
    )

    data class MemberBalance(
        val memberId: Long,
        val memberName: String,
        val amountOwedPaise: Long,
        val colorHex: String
    )

    data class SuggestedSettlement(
        val fromMemberId: Long,
        val fromMemberName: String,
        val toMemberId: Long,
        val toMemberName: String,
        val amountPaise: Long
    )

    val groupsState: StateFlow<GroupUiState> = repository.observeAllGroups()
        .map { GroupUiState(isLoading = false, groups = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GroupUiState())

    private val _currentGroupId = kotlinx.coroutines.flow.MutableStateFlow<Long?>(null)

    /** Call once when entering the group detail screen so groupDetailState starts observing that group. */
    fun setGroupId(id: Long) {
        _currentGroupId.value = id
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val groupDetailState: StateFlow<GroupDetailUiState> = _currentGroupId
        .filterNotNull()
        .flatMapLatest { id ->
            combine(
                repository.observeGroupById(id),
                repository.observeMembers(id),
                repository.observeGroupExpenses(id),
                repository.observeSettlements(id)
            ) { group, members, expenses, settlements ->
                // Pull every expense's shares together so we can compute each member's net balance.
                // combine already re-runs this whole block whenever any underlying table changes,
                // so a one-shot suspend read per expense is always current.
                val shares = expenses.flatMap { expense ->
                    repository.observeShares(expense.id).first()
                }

                val paidByMember = expenses.groupBy { it.paidByMemberId }
                    .mapValues { (_, exp) -> exp.sumOf { it.amountPaise } }
                val owedByMember = shares.groupBy { it.memberId }
                    .mapValues { (_, s) -> s.sumOf { it.amountOwedPaise } }
                val settledOut = settlements.groupBy { it.fromMemberId }
                    .mapValues { (_, s) -> s.sumOf { it.amountPaise } }
                val settledIn = settlements.groupBy { it.toMemberId }
                    .mapValues { (_, s) -> s.sumOf { it.amountPaise } }

                val balances = members.map { member ->
                    val net = (paidByMember[member.id] ?: 0L) -
                        (owedByMember[member.id] ?: 0L) +
                        (settledIn[member.id] ?: 0L) -
                        (settledOut[member.id] ?: 0L)
                    MemberBalance(
                        memberId = member.id,
                        memberName = member.name,
                        amountOwedPaise = net,
                        colorHex = member.colorHex
                    )
                }

                GroupDetailUiState(
                    group = group,
                    members = members,
                    expenses = expenses,
                    settlements = settlements,
                    memberBalances = balances,
                    suggestedSettlements = suggestSettlements(balances),
                    error = null
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GroupDetailUiState())

    /** Simple greedy debt-simplification: match the biggest creditor with the biggest debtor, repeat. */
    private fun suggestSettlements(balances: List<MemberBalance>): List<SuggestedSettlement> {
        val creditors = balances.filter { it.amountOwedPaise > 0 }
            .map { it.memberId to it.amountOwedPaise }.toMutableList()
        val debtors = balances.filter { it.amountOwedPaise < 0 }
            .map { it.memberId to -it.amountOwedPaise }.toMutableList()
        val nameOf = balances.associate { it.memberId to it.memberName }
        val result = mutableListOf<SuggestedSettlement>()

        var ci = 0
        var di = 0
        while (ci < creditors.size && di < debtors.size) {
            val (creditorId, creditAmount) = creditors[ci]
            val (debtorId, debtAmount) = debtors[di]
            val amount = minOf(creditAmount, debtAmount)
            if (amount > 0) {
                result += SuggestedSettlement(
                    fromMemberId = debtorId,
                    fromMemberName = nameOf[debtorId].orEmpty(),
                    toMemberId = creditorId,
                    toMemberName = nameOf[creditorId].orEmpty(),
                    amountPaise = amount
                )
            }
            creditors[ci] = creditorId to (creditAmount - amount)
            debtors[di] = debtorId to (debtAmount - amount)
            if (creditors[ci].second == 0L) ci++
            if (di < debtors.size && debtors[di].second == 0L) di++
        }
        return result
    }

    /**
     * Creates a group expense split equally among [memberIds]. Any leftover paise from integer
     * division (amount doesn't divide evenly) is added to the first member's share so the shares
     * always sum to exactly amountPaise.
     */
    fun addEqualSplitExpense(
        groupId: Long,
        description: String,
        amountPaise: Long,
        paidByMemberId: Long,
        memberIds: List<Long>,
        dateEpochDay: Long
    ) {
        if (description.isBlank() || amountPaise <= 0 || memberIds.isEmpty()) return
        viewModelScope.launch {
            val expenseId = repository.upsertGroupExpense(
                com.daynest.data.local.expense.GroupExpenseEntity(
                    groupId = groupId,
                    description = description.trim(),
                    amountPaise = amountPaise,
                    paidByMemberId = paidByMemberId,
                    splitType = "equal",
                    dateEpochDay = dateEpochDay
                )
            )
            val base = amountPaise / memberIds.size
            val remainder = amountPaise % memberIds.size
            memberIds.forEachIndexed { index, memberId ->
                val owed = base + if (index == 0) remainder else 0
                repository.upsertShare(
                    com.daynest.data.local.expense.ExpenseShareEntity(
                        groupExpenseId = expenseId,
                        memberId = memberId,
                        amountOwedPaise = owed
                    )
                )
            }
        }
    }

    fun recordSettlement(groupId: Long, fromMemberId: Long, toMemberId: Long, amountPaise: Long, dateEpochDay: Long) {
        if (amountPaise <= 0) return
        viewModelScope.launch {
            repository.upsertSettlement(
                com.daynest.data.local.expense.SettlementEntity(
                    groupId = groupId,
                    fromMemberId = fromMemberId,
                    toMemberId = toMemberId,
                    amountPaise = amountPaise,
                    dateEpochDay = dateEpochDay
                )
            )
        }
    }

    fun upsertGroup(group: com.daynest.data.local.expense.ExpenseGroupEntity) {
        viewModelScope.launch { repository.upsertGroup(group) }
    }

    fun deleteGroup(group: com.daynest.data.local.expense.ExpenseGroupEntity) {
        viewModelScope.launch { repository.deleteGroup(group) }
    }

    fun upsertMember(member: com.daynest.data.local.expense.GroupMemberEntity) {
        viewModelScope.launch { repository.upsertMember(member) }
    }

    fun deleteMember(member: com.daynest.data.local.expense.GroupMemberEntity) {
        viewModelScope.launch { repository.deleteMember(member) }
    }

    fun upsertGroupExpense(expense: com.daynest.data.local.expense.GroupExpenseEntity) {
        viewModelScope.launch { repository.upsertGroupExpense(expense) }
    }

    fun upsertSettlement(settlement: com.daynest.data.local.expense.SettlementEntity) {
        viewModelScope.launch { repository.upsertSettlement(settlement) }
    }
}
