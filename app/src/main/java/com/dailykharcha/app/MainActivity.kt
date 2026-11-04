package com.dailykharcha.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.dailykharcha.app.ui.navigation.AppNavHost
import com.dailykharcha.app.ui.theme.DailyKharchaTheme
import com.dailykharcha.app.ui.viewmodel.ExpenseViewModel
import com.dailykharcha.app.ui.viewmodel.ExpenseViewModelFactory

/**
 * Single Activity entry point hosting Jetpack Compose UI.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: ExpenseViewModel by viewModels {
        val app = application as DailyKharchaApplication
        ExpenseViewModelFactory(app.expenseRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DailyKharchaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavHost(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
