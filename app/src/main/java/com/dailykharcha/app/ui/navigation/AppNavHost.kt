package com.dailykharcha.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dailykharcha.app.ui.screens.add_edit.AddEditExpenseScreen
import com.dailykharcha.app.ui.screens.dashboard.DashboardScreen
import com.dailykharcha.app.ui.screens.history.HistoryScreen
import com.dailykharcha.app.ui.viewmodel.ExpenseViewModel

/**
 * Top-level Jetpack Compose NavHost connecting all screens with smooth animated transitions.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(250)) },
        exitTransition = { fadeOut(animationSpec = tween(250)) }
    ) {
        // 1. Dashboard Screen
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToAddExpense = {
                    navController.navigate(Screen.AddExpense.route)
                },
                onNavigateToEditExpense = { id ->
                    navController.navigate(Screen.EditExpense.createRoute(id))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        // 2. Add Expense Screen
        composable(
            route = Screen.AddExpense.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(300)
                )
            }
        ) {
            AddEditExpenseScreen(
                expenseId = null,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Edit Expense Screen
        composable(
            route = Screen.EditExpense.route,
            arguments = listOf(
                navArgument("expenseId") { type = NavType.LongType }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getLong("expenseId")
            AddEditExpenseScreen(
                expenseId = expenseId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 4. Expense History Screen
        composable(
            route = Screen.History.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddExpense = {
                    navController.navigate(Screen.AddExpense.route)
                },
                onNavigateToEditExpense = { id ->
                    navController.navigate(Screen.EditExpense.createRoute(id))
                }
            )
        }
    }
}
