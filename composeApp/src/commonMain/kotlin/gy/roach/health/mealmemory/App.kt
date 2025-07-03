package gy.roach.health.mealmemory

import androidx.compose.runtime.*
import gy.roach.health.mealmemory.data.MealRepository
import gy.roach.health.mealmemory.ui.HomeScreen
import gy.roach.health.mealmemory.ui.MealHistoryScreen
import gy.roach.health.mealmemory.ui.theme.IOSTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

sealed class Screen {
    object Home : Screen()
    object History : Screen()
}

@Composable
@Preview
fun App() {
    IOSTheme {
        val mealRepository = remember { MealRepository() }
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

        when (currentScreen) {
            Screen.Home -> {
                HomeScreen(
                    mealRepository = mealRepository,
                    onNavigateToHistory = { currentScreen = Screen.History },
                    onAddMeal = {
                        // This will be handled by MealScreenWithErrorHandling
                    }
                )
            }
            Screen.History -> {
                MealHistoryScreen(
                    mealRepository = mealRepository,
                    onNavigateBack = { currentScreen = Screen.Home }
                )
            }
        }
    }
}
