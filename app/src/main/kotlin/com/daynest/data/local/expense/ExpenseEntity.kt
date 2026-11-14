package com.daynest.data.local.expense

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [Index(value = ["categoryId"]), Index(value = ["dateEpochDay"])]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amountPaise: Long, // stored in paise for accurate money math
    val categoryId: String,
    val dateEpochDay: Long,
    val note: String? = null,
    val paymentMethod: String? = null,
    val isIncome: Boolean = false,
    val receiptUri: String? = null,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "expense_categories",
    indices = [Index(value = ["isIncome"])]
)
data class ExpenseCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String = "#C3B1E1",
    val icon: String = "\uD83D\uDCB2",
    val isIncome: Boolean = false,
    val monthlyBudgetPaise: Long = 0,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "expense_groups"
)
data class ExpenseGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)

@Entity(
    tableName = "group_members",
    foreignKeys = [ForeignKey(entity = ExpenseGroupEntity::class, parentColumns = ["id"], childColumns = ["groupId"], onDelete = ForeignKey.CASCADE)]
)
data class GroupMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val name: String,
    val colorHex: String = "#C3B1E1"
)

@Entity(
    tableName = "group_expenses",
    foreignKeys = [
        ForeignKey(entity = ExpenseGroupEntity::class, parentColumns = ["id"], childColumns = ["groupId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = GroupMemberEntity::class, parentColumns = ["id"], childColumns = ["paidByMemberId"])
    ]
)
data class GroupExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val description: String,
    val amountPaise: Long,
    val paidByMemberId: Long,
    val splitType: String, // equal, unequal, percentage, exact
    val splitDetails: String? = null, // JSON
    val dateEpochDay: Long,
    val receiptUri: String? = null,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "expense_shares",
    foreignKeys = [
        ForeignKey(entity = GroupExpenseEntity::class, parentColumns = ["id"], childColumns = ["groupExpenseId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = GroupMemberEntity::class, parentColumns = ["id"], childColumns = ["memberId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ExpenseShareEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupExpenseId: Long,
    val memberId: Long,
    val amountOwedPaise: Long,
    val amountPaidPaise: Long = 0,
    val isSettled: Boolean = false
)

@Entity(
    tableName = "settlements",
    foreignKeys = [
        ForeignKey(entity = ExpenseGroupEntity::class, parentColumns = ["id"], childColumns = ["groupId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = GroupMemberEntity::class, parentColumns = ["id"], childColumns = ["fromMemberId"]),
        ForeignKey(entity = GroupMemberEntity::class, parentColumns = ["id"], childColumns = ["toMemberId"])
    ]
)
data class SettlementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val fromMemberId: Long,
    val toMemberId: Long,
    val amountPaise: Long,
    val dateEpochDay: Long,
    val note: String? = null,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)
