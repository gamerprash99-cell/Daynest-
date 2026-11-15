package com.daynest.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daynest.data.local.expense.ExpenseEntity
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.*
import com.daynest.ui.components.PremiumEmptyState
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.components.PremiumSummaryCard
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestBlushPink
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant
import com.daynest.ui.theme.NestSageGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDashboardScreen(onAddExpense: () -> Unit = {}, onOpenSettings: () -> Unit = {}) {
    val viewModel: ExpenseViewModel = viewModel(factory = LocalViewModelFactory.current)
    val state by viewModel.uiState.collectAsState()
    val spacing = LocalDaynestSpacing.current

    Column(
        modifier = Modifier.fillMaxSize().padding(spacing.md)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Expenses",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Track your spending and savings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
        Spacer(modifier = Modifier.height(spacing.md))

        // Summary cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            PremiumSummaryCard(
                title = "Income",
                value = formatCurrency(state.totalIncome),
                subtitle = "This month",
                icon = Icons.Filled.ArrowUpward,
                iconColor = NestSageGreen,
                modifier = Modifier.weight(1f)
            )
            PremiumSummaryCard(
                title = "Expenses",
                value = formatCurrency(state.totalExpense),
                subtitle = "This month",
                icon = Icons.Filled.ArrowDownward,
                iconColor = NestBlushPink,
                modifier = Modifier.weight(1f)
            )
            PremiumSummaryCard(
                title = "Savings",
                value = formatCurrency(state.totalIncome - state.totalExpense),
                subtitle = "Remaining budget",
                icon = Icons.Filled.AccountBalanceWallet,
                iconColor = NestLavender,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(spacing.md))

        // Recent transactions
        PremiumSectionHeader(
            title = "Recent Transactions",
            trailing = {
                androidx.compose.material3.TextButton(
                    onClick = { /* view all */ }
                ) {
                    Text(text = "See all")
                }
            }
        )

        if (state.expenses.isEmpty()) {
            PremiumEmptyState(
                icon = Icons.Filled.AttachMoney,
                title = "No transactions yet",
                message = "Add your first expense or income.",
                actionLabel = "Add Transaction",
                onAction = onAddExpense
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                items(state.expenses.take(10), key = { it.id }) { expense ->
                    TransactionRow(
                        expense = expense,
                        onClick = { /* view detail */ }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.xxl))
    }

    // Floating action button for adding expense
    androidx.compose.material3.FloatingActionButton(
        onClick = onAddExpense,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = com.daynest.ui.theme.full
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add Expense")
    }
}

@Composable
private fun TransactionRow(
    expense: ExpenseEntity,
    onClick: () -> Unit
) {
    val spacing = LocalDaynestSpacing.current
    val isIncome = expense.isIncome
    val color = if (isIncome) NestSageGreen else NestBlushPink

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isIncome) "\u2191" else "\u2193",
                fontSize = 18.sp,
                color = color
            )
            Spacer(modifier = Modifier.width(spacing.sm))
            Column(Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = java.time.LocalDate.ofEpochDay(expense.dateEpochDay)
                        .format(java.time.format.DateTimeFormatter.ofPattern("MMM d")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isIncome) "+" else "-",
                        style = MaterialTheme.typography.bodyMedium,
                        color = color
                    )
                    Text(
                        text = formatCurrency(expense.amountPaise),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun formatCurrency(paise: Long): String {
    val rupees = paise / 100
    val remainingPaise = paise % 100
    return "₹$rupees.${String.format("%02d", remainingPaise)}"
}
