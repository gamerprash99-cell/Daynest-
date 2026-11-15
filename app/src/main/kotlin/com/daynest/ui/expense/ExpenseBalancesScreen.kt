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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Payment
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.*
import com.daynest.ui.components.PremiumEmptyState
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant
import com.daynest.ui.theme.NestSageGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseBalancesScreen(onBack: () -> Unit = {}, onViewGroups: () -> Unit = {}) {
    val viewModel: GroupViewModel = viewModel(factory = LocalViewModelFactory.current)
    val spacing = LocalDaynestSpacing.current

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
            Text(
                text = "Balances",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(1.dp))
        }
        Spacer(modifier = Modifier.height(spacing.md))

        // Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            PremiumSummaryCard(
                title = "Total Spent",
                value = "₹0",
                subtitle = "This month",
                icon = Icons.Default.AttachMoney,
                iconColor = NestLavender,
                modifier = Modifier.weight(1f)
            )
            PremiumSummaryCard(
                title = "You Owe",
                value = "₹0",
                subtitle = "To others",
                icon = Icons.Default.ArrowDownward,
                iconColor = com.daynest.ui.theme.NestBlushPink,
                modifier = Modifier.weight(1f)
            )
            PremiumSummaryCard(
                title = "Others Owe You",
                value = "₹0",
                subtitle = "To receive",
                icon = Icons.Default.ArrowUpward,
                iconColor = NestSageGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(spacing.md))

        PremiumSectionHeader(title = "Person-wise Balances")

        // Member balances (placeholder - would be populated from group)
        PremiumEmptyState(
            icon = Icons.Default.People,
            title = "No group selected",
            message = "Select a group to see person-wise balances.",
            actionLabel = "View Groups",
            onAction = onViewGroups
        )

        Spacer(modifier = Modifier.height(spacing.md))

        // Suggested settlements
        PremiumSectionHeader(title = "Suggested Settlements")

        PremiumEmptyState(
            icon = Icons.Default.Payment,
            title = "No settlements needed",
            message = "All balances are settled.",
            actionLabel = null,
            onAction = null
        )

        Spacer(modifier = Modifier.height(spacing.xxl))
    }
}
