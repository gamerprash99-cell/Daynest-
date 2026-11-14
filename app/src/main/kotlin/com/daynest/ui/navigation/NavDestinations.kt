package com.daynest.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// ============================================================================
// NAVIGATION ROUTES
// ============================================================================

const val ROUTE_ONBOARDING = "onboarding"
const val ROUTE_HOME = "home"
const val ROUTE_PLANNER = "planner"
const val ROUTE_JOURNAL = "journal"
const val ROUTE_JOURNAL_CALENDAR = "journal_calendar"
const val ROUTE_JOURNAL_EDITOR = "journal_editor?entryId={entryId}"
fun journalEditorRoute(entryId: Long? = null) = "journal_editor?entryId=${entryId ?: -1L}"
const val ROUTE_HABITS = "habits"
const val ROUTE_HABIT_HEATMAP = "habit_heatmap"
const val ROUTE_HABIT_LIST = "habit_list"
const val ROUTE_EXPENSES = "expenses"
const val ROUTE_EXPENSE_ADD = "expense_add"
const val ROUTE_EXPENSE_GROUPS = "expense_groups"
const val ROUTE_EXPENSE_GROUP_DETAIL = "expense_group_detail/{groupId}"
const val ROUTE_EXPENSE_BALANCES = "expense_balances"
const val ROUTE_SETTINGS = "settings"
const val ROUTE_APP_LOCK = "app_lock"
const val ROUTE_ABOUT = "about"

data class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val iconSelected: ImageVector? = null
)

val bottomDestinations = listOf(
    BottomDestination(
        route = ROUTE_HOME,
        label = "Home",
        icon = Icons.Default.Home,
        iconSelected = Icons.Filled.Home
    ),
    BottomDestination(
        route = ROUTE_PLANNER,
        label = "Planner",
        icon = Icons.Default.CheckCircle,
        iconSelected = Icons.Filled.CheckCircle
    ),
    BottomDestination(
        route = ROUTE_JOURNAL,
        label = "Journal",
        icon = Icons.Default.Book,
        iconSelected = Icons.Filled.Book
    ),
    BottomDestination(
        route = ROUTE_HABITS,
        label = "Habits",
        icon = Icons.Default.Spa,
        iconSelected = Icons.Filled.Spa
    ),
    BottomDestination(
        route = ROUTE_EXPENSES,
        label = "Expenses",
        icon = Icons.Default.AttachMoney,
        iconSelected = Icons.Filled.AttachMoney
    )
)

// Secondary destinations (not in bottom nav, but accessible via screens)
val secondaryDestinations = listOf(
    BottomDestination(ROUTE_SETTINGS, "Settings", Icons.Default.Settings),
    BottomDestination(ROUTE_APP_LOCK, "App Lock", Icons.Default.Person),
    BottomDestination(ROUTE_ABOUT, "About", Icons.Default.Info)
)
