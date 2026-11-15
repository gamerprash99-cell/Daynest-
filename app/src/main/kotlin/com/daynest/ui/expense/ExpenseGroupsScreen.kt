package com.daynest.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.daynest.ui.LocalViewModelFactory
import com.daynest.ui.components.*
import com.daynest.ui.components.PremiumEmptyState
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseGroupsScreen(onOpenGroup: (Long) -> Unit = {}) {
    val viewModel: GroupViewModel = viewModel(factory = LocalViewModelFactory.current)
    val state by viewModel.groupsState.collectAsState()
    val spacing = LocalDaynestSpacing.current
    var newGroupName by remember { mutableStateOf("") }
    var showAddGroupSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Split Bills",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage shared expenses with friends",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { showAddGroupSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Group")
            }
        }
        Spacer(modifier = Modifier.height(spacing.md))

        if (state.groups.isEmpty()) {
            PremiumEmptyState(
                icon = Icons.Default.People,
                title = "No groups yet",
                message = "Create a group to split expenses with friends.",
                actionLabel = "Create Group",
                onAction = { showAddGroupSheet = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                items(state.groups, key = { it.id }) { group ->
                    GroupRow(
                        group = group,
                        onClick = { onOpenGroup(group.id) },
                        onDelete = { viewModel.deleteGroup(group) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.xxl))
    }

    // Add group dialog
    if (showAddGroupSheet) {
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = { showAddGroupSheet = false },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.lg)
        ) {
            Text(
                text = "New Group",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.md))

            PremiumTextField(
                value = newGroupName,
                onValueChange = { newGroupName = it },
                label = { Text("Group Name") },
                placeholder = { Text("e.g. Dinner with Friends") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(spacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                androidx.compose.material3.TextButton(
                    onClick = {
                        newGroupName = ""
                        showAddGroupSheet = false
                    }
                ) {
                    Text(text = "Cancel")
                }
                androidx.compose.material3.Button(
                    onClick = {
                        if (newGroupName.isNotBlank()) {
                            viewModel.upsertGroup(
                                com.daynest.data.local.expense.ExpenseGroupEntity(
                                    name = newGroupName.trim()
                                )
                            )
                            newGroupName = ""
                            showAddGroupSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Create Group")
                }
            }
            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
    }
}

@Composable
private fun GroupRow(
    group: com.daynest.data.local.expense.ExpenseGroupEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val spacing = LocalDaynestSpacing.current
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
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(NestLavender.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = group.name.first().uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.width(spacing.sm))
            Column(Modifier.weight(1f)) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Group expenses",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete group")
            }
        }
    }
}
