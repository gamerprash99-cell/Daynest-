package com.daynest.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Spa
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.daynest.ui.expense.ExpenseGroupDetailScreen
import com.daynest.ui.habits.HabitsScreen
import com.daynest.ui.home.HomeScreen
import com.daynest.ui.journal.JournalScreen
import com.daynest.ui.settings.SettingsScreen
import com.daynest.ui.tasks.TasksScreen
import com.daynest.ui.theme.NestLavender
import com.daynest.ui.theme.NestOnSurface
import com.daynest.ui.theme.NestOnSurfaceVariant
import com.daynest.ui.theme.NestOutlineVariant
import com.daynest.ui.theme.NestSurface
import com.daynest.ui.expense.AddExpenseScreen
import com.daynest.ui.expense.ExpenseBalancesScreen
import com.daynest.ui.expense.ExpenseDashboardScreen
import com.daynest.ui.expense.ExpenseGroupsScreen
import com.daynest.ui.habits.HabitHeatmapScreen
import com.daynest.ui.habits.HabitListViewScreen
import com.daynest.ui.journal.JournalCalendarScreen
import com.daynest.ui.journal.JournalEditorScreen
import com.daynest.ui.settings.AboutScreen
import com.daynest.ui.settings.AppLockScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(userName: String) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry = navController.currentBackStackEntryAsState().value
            val currentRoute = backStackEntry?.destination
            PremiumBottomNavigation(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(ROUTE_HOME) {
                HomeScreen(
                    userName = userName,
                    onOpenPlanner = {
                        navController.navigate(ROUTE_PLANNER) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenJournal = {
                        navController.navigate(ROUTE_JOURNAL) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenHabits = {
                        navController.navigate(ROUTE_HABITS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenExpenses = {
                        navController.navigate(ROUTE_EXPENSES) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenSettings = { navController.navigate(ROUTE_SETTINGS) }
                )
            }
            composable(ROUTE_PLANNER) { TasksScreen() }
            composable(ROUTE_JOURNAL) {
                JournalScreen(
                    onOpenCalendar = { navController.navigate(ROUTE_JOURNAL_CALENDAR) }
                )
            }
            composable(ROUTE_JOURNAL_CALENDAR) {
                JournalCalendarScreen(
                    onOpenEntry = { entryId -> navController.navigate(journalEditorRoute(entryId)) },
                    onNewEntry = { navController.navigate(journalEditorRoute(null)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = ROUTE_JOURNAL_EDITOR,
                arguments = listOf(navArgument("entryId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getLong("entryId") ?: -1L
                JournalEditorScreen(
                    entryId = entryId.takeIf { it >= 0 },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(ROUTE_HABITS) { HabitsScreen() }
            composable(ROUTE_HABIT_HEATMAP) {
                val viewModel: com.daynest.ui.habits.HabitsViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = com.daynest.ui.LocalViewModelFactory.current
                    )
                val state by viewModel.uiState.collectAsState()
                HabitHeatmapScreen(
                    habits = state.habits,
                    onHabitClick = { /* selection handled internally by the heatmap screen */ }
                )
            }
            composable(ROUTE_HABIT_LIST) {
                val viewModel: com.daynest.ui.habits.HabitsViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = com.daynest.ui.LocalViewModelFactory.current
                    )
                val state by viewModel.uiState.collectAsState()
                HabitListViewScreen(
                    habits = state.habits,
                    onToggle = { habitId -> viewModel.toggleToday(habitId) },
                    onClick = { /* row tap: editing happens from the Habits card screen for now */ },
                    onDelete = { habit -> viewModel.delete(habit) }
                )
            }
            composable(ROUTE_EXPENSES) {
                ExpenseDashboardScreen(
                    onAddExpense = { navController.navigate(ROUTE_EXPENSE_ADD) },
                    onOpenSettings = { navController.navigate(ROUTE_SETTINGS) }
                )
            }
            composable(ROUTE_EXPENSE_ADD) {
                AddExpenseScreen(onBack = { navController.popBackStack() })
            }
            composable(ROUTE_EXPENSE_GROUPS) {
                ExpenseGroupsScreen(
                    onOpenGroup = { groupId ->
                        navController.navigate("expense_group_detail/$groupId")
                    }
                )
            }
            composable(
                route = ROUTE_EXPENSE_GROUP_DETAIL,
                arguments = listOf(navArgument("groupId") { type = NavType.LongType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getLong("groupId")
                ExpenseGroupDetailScreen(
                    groupId = groupId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(ROUTE_EXPENSE_BALANCES) {
                ExpenseBalancesScreen(
                    onBack = { navController.popBackStack() },
                    onViewGroups = { navController.navigate(ROUTE_EXPENSE_GROUPS) }
                )
            }
            composable(ROUTE_SETTINGS) { SettingsScreen() }
            composable(ROUTE_APP_LOCK) { AppLockScreen() }
            composable(ROUTE_ABOUT) { AboutScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumBottomNavigation(
    navController: androidx.navigation.NavHostController,
    currentRoute: androidx.navigation.NavDestination?
) {
    val spacing = com.daynest.ui.theme.LocalDaynestSpacing.current
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.9f)
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = NestLavender
                )
            },
            label = {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.labelLarge,
                    color = NestLavender,
                    fontWeight = FontWeight.SemiBold
                )
            }
        )
    }
}
