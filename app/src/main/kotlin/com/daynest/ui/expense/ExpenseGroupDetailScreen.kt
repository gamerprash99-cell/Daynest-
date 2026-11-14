package com.daynest.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daynest.data.local.expense.GroupExpenseEntity
import com.daynest.data.local.expense.GroupMemberEntity
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.PremiumEmptyState
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.components.PremiumTextField
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestSageGreen
import java.time.LocalDate

/** Formats paise (1/100 rupee, same unit used across the expense entities) as a rupee string. */
private fun formatPaiseAsRupees(paise: Long): String {
    val rupees = paise / 100.0
    return "\u20B9${"%.2f".format(kotlin.math.abs(rupees))}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseGroupDetailScreen(
    groupId: Long? = null,
    onBack: () -> Unit = {}
) {
    val viewModel: GroupViewModel = viewModel(factory = LocalViewModelFactory.current)
    val spacing = LocalDaynestSpacing.current
    val state by viewModel.groupDetailState.collectAsState()

    LaunchedEffect(groupId) {
        groupId?.let { viewModel.setGroupId(it) }
    }

    var newMemberName by remember { mutableStateOf("") }
    var showAddExpenseSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Column {
                Text(
                    text = state.group?.name ?: "Group Details",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage members and expenses",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(1.dp))
        }
        Spacer(modifier = Modifier.height(spacing.md))

        // Members section with real, live balances
        PremiumSectionHeader(title = "Members")
        Spacer(modifier = Modifier.height(spacing.sm))

        if (state.members.isEmpty()) {
            Text(
                text = "Add members below to start splitting expenses.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            state.members.forEach { member ->
                val balance = state.memberBalances.firstOrNull { it.memberId == member.id }?.amountOwedPaise ?: 0L
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.md, vertical = spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = member.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = when {
                                balance > 0 -> "gets back ${formatPaiseAsRupees(balance)}"
                                balance < 0 -> "owes ${formatPaiseAsRupees(balance)}"
                                else -> "settled up"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                balance > 0 -> NestSageGreen
                                balance < 0 -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.md))

        // Add member
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PremiumTextField(
                value = newMemberName,
                onValueChange = { newMemberName = it },
                label = { Text("Add member name") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                if (newMemberName.isNotBlank() && groupId != null) {
                    viewModel.upsertMember(
                        GroupMemberEntity(groupId = groupId, name = newMemberName.trim())
                    )
                    newMemberName = ""
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add member")
            }
        }

        Spacer(modifier = Modifier.height(spacing.md))

        // Expenses section
        PremiumSectionHeader(title = "Group Expenses")
        Spacer(modifier = Modifier.height(spacing.sm))

        if (state.expenses.isEmpty()) {
            PremiumEmptyState(
                icon = Icons.Default.AttachMoney,
                title = "No expenses yet",
                message = "Add your first group expense.",
                actionLabel = "Add Expense",
                onAction = { showAddExpenseSheet = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                items(state.expenses, key = { it.id }) { expense ->
                    val payerName = state.members.firstOrNull { it.id == expense.paidByMemberId }?.name ?: "Someone"
                    androidx.compose.material3.Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(spacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(expense.description, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = "Paid by $payerName",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = formatPaiseAsRupees(expense.amountPaise),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.xxl))
    }

    // Add expense FAB
    androidx.compose.material3.FloatingActionButton(
        onClick = { showAddExpenseSheet = true },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = com.daynest.ui.theme.full
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Expense")
    }

    if (showAddExpenseSheet && groupId != null) {
        AddGroupExpenseSheet(
            groupId = groupId,
            members = state.members,
            onDismiss = { showAddExpenseSheet = false },
            onSave = { description, amountPaise, paidByMemberId, splitMemberIds ->
                viewModel.addEqualSplitExpense(
                    groupId = groupId,
                    description = description,
                    amountPaise = amountPaise,
                    paidByMemberId = paidByMemberId,
                    memberIds = splitMemberIds,
                    dateEpochDay = LocalDate.now().toEpochDay()
                )
                showAddExpenseSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddGroupExpenseSheet(
    groupId: Long,
    members: List<GroupMemberEntity>,
    onDismiss: () -> Unit,
    onSave: (description: String, amountPaise: Long, paidByMemberId: Long, splitMemberIds: List<Long>) -> Unit
) {
    val spacing = LocalDaynestSpacing.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var paidBy by remember { mutableStateOf(members.firstOrNull()?.id) }
    var splitAmong by remember { mutableStateOf(members.map { it.id }.toSet()) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.fillMaxWidth().padding(spacing.lg)) {
            Text("Add group expense", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(spacing.lg))

            if (members.isEmpty()) {
                Text(
                    "Add at least one member to this group before adding an expense.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(spacing.lg))
                return@Column
            }

            PremiumTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("What was it for?") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(spacing.sm))
            PremiumTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount (\u20B9)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(spacing.md))
            Text("Paid by", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(spacing.xs))
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                members.forEach { member ->
                    FilterChip(
                        selected = paidBy == member.id,
                        onClick = { paidBy = member.id },
                        label = { Text(member.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NestLavender,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(spacing.md))
            Text("Split equally among", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(spacing.xs))
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                members.forEach { member ->
                    FilterChip(
                        selected = member.id in splitAmong,
                        onClick = {
                            splitAmong = if (member.id in splitAmong) {
                                splitAmong - member.id
                            } else {
                                splitAmong + member.id
                            }
                        },
                        label = { Text(member.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NestSageGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(spacing.lg))
            val amountPaise = ((amountText.toDoubleOrNull() ?: 0.0) * 100).toLong()
            androidx.compose.material3.Button(
                onClick = {
                    val payer = paidBy ?: return@Button
                    if (description.isNotBlank() && amountPaise > 0 && splitAmong.isNotEmpty()) {
                        onSave(description, amountPaise, payer, splitAmong.toList())
                    }
                },
                enabled = description.isNotBlank() && amountPaise > 0 && splitAmong.isNotEmpty() && paidBy != null,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Save expense")
            }
            Spacer(Modifier.height(spacing.lg))
        }
    }
}
