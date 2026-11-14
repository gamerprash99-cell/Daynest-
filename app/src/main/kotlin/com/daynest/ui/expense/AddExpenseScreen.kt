package com.daynest.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import com.daynest.ui.components.PremiumSectionHeader
import com.daynest.ui.theme.LocalDaynestSpacing
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(onBack: () -> Unit = {}) {
    val viewModel: ExpenseViewModel = viewModel(factory = LocalViewModelFactory.current)
    val spacing = LocalDaynestSpacing.current
    val uiState by viewModel.uiState.collectAsState()

    var isIncome by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val categories = uiState.categories
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var note by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var showCategoryPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(spacing.md)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Add Transaction",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = {
                if (title.isNotBlank() && amount.isNotBlank() && selectedCategoryId != null) {
                    val amountPaise = (amount.toDoubleOrNull() ?: 0.0).times(100).toLong()
                    viewModel.saveExpense(
                        title = title,
                        amountPaise = amountPaise,
                        categoryId = selectedCategoryId!!,
                        date = selectedDate.toEpochDay(),
                        note = note.takeIf { it.isNotBlank() },
                        paymentMethod = paymentMethod,
                        isIncome = isIncome
                    )
                    onBack()
                }
            }) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
        }
        Spacer(modifier = Modifier.height(spacing.md))

        // Type selector
        Text("Type", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(spacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            listOf(false to "Expense", true to "Income").forEach { (isInc, label) ->
                val selected = isIncome == isInc
                androidx.compose.material3.FilterChip(
                    selected = selected,
                    onClick = { isIncome = isInc },
                    label = { Text(label) },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NestLavender,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(spacing.md))

        // Title
        PremiumTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            placeholder = { Text("e.g. Groceries, Salary, Coffee") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(spacing.md))

        // Amount
        PremiumTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (₹)") },
            placeholder = { Text("0.00") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(spacing.md))

        // Category
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Category", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.width(1.dp))
        }
        Spacer(modifier = Modifier.height(spacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCategoryId?.let { id ->
                    categories.firstOrNull { it.id.toString() == id }?.name ?: "Select category"
                } ?: "Select category",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(1.dp))
            IconButton(onClick = { showCategoryPicker = true }) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Select category")
            }
        }

        if (showCategoryPicker) {
            CategoryPickerSheet(
                categories = categories.filter { it.isIncome == isIncome },
                onCategorySelected = {
                    selectedCategoryId = it.id.toString()
                    showCategoryPicker = false
                },
                onDismiss = { showCategoryPicker = false }
            )
        }

        Spacer(modifier = Modifier.height(spacing.md))

        // Date
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = "Date")
            Spacer(modifier = Modifier.width(spacing.sm))
            Text(
                text = selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(spacing.md))

        // Note
        PremiumTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (optional)") },
            maxLines = 2,
            singleLine = false
        )
        Spacer(modifier = Modifier.height(spacing.md))

        // Payment method
        Text("Payment Method", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(spacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            listOf("Cash", "Card", "UPI", "Bank Transfer").forEach { method ->
                androidx.compose.material3.FilterChip(
                    selected = paymentMethod == method,
                    onClick = { paymentMethod = method },
                    label = { Text(method) },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NestLavender,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        // Save button
        androidx.compose.material3.Button(
            onClick = {
                if (title.isNotBlank() && amount.isNotBlank() && selectedCategoryId != null) {
                    val amountPaise = (amount.toDouble() * 100).toLong()
                    viewModel.saveExpense(
                        title = title,
                        amountPaise = amountPaise,
                        categoryId = selectedCategoryId!!,
                        date = selectedDate.toEpochDay(),
                        note = note.takeIf { it.isNotBlank() },
                        paymentMethod = paymentMethod,
                        isIncome = isIncome
                    )
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.medium,
            elevation = androidx.compose.material3.ButtonDefaults.elevatedButtonElevation(2.dp)
        ) {
            Text(
                text = "Save Transaction",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            )
        }
        Spacer(modifier = Modifier.height(spacing.lg))
    }
}

@Composable
private fun CategoryPickerSheet(
    categories: List<com.daynest.data.local.expense.ExpenseCategoryEntity>,
    onCategorySelected: (com.daynest.data.local.expense.ExpenseCategoryEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = LocalDaynestSpacing.current

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.md, vertical = spacing.md)
    ) {
        Text(
            text = "Select Category",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(spacing.sm))

        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            items(categories, key = { it.id }) { category ->
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onCategorySelected(category) }),
                    shape = com.daynest.ui.theme.DaynestShapes.medium,
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.sm, horizontal = spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.icon,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(spacing.sm))
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
