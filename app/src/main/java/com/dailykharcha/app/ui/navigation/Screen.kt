package com.dailykharcha.app.ui.navigation

/**
 * Navigation routes for Daily Kharcha.
 */
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object AddExpense : Screen("add_expense")
    object EditExpense : Screen("edit_expense/{expenseId}") {
        fun createRoute(expenseId: Long) = "edit_expense/$expenseId"
    }
    object History : Screen("history")
}
