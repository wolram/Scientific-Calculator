package com.vibecoding.calculator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vibecoding.calculator.ui.screens.FinancialCalculatorScreen
import com.vibecoding.calculator.ui.screens.GraphingCalculatorScreen
import com.vibecoding.calculator.ui.screens.MainMenuScreen
import com.vibecoding.calculator.ui.screens.ScientificCalculatorScreen

sealed class Screen(val route: String) {
    object Menu : Screen("menu")
    object Scientific : Screen("scientific")
    object Financial : Screen("financial")
    object Graphing : Screen("graphing")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Menu.route) {
        composable(Screen.Menu.route) {
            MainMenuScreen(
                onNavigateToScientific = { navController.navigate(Screen.Scientific.route) },
                onNavigateToFinancial = { navController.navigate(Screen.Financial.route) },
                onNavigateToGraphing = { navController.navigate(Screen.Graphing.route) }
            )
        }

        composable(Screen.Scientific.route) {
            ScientificCalculatorScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Financial.route) {
            FinancialCalculatorScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Graphing.route) {
            GraphingCalculatorScreen(onBack = { navController.popBackStack() })
        }
    }
}
