package gy.roach.health.mealmemory

import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import gy.roach.health.mealmemory.data.MealRepository
import gy.roach.health.mealmemory.ui.HomeScreen
import gy.roach.health.mealmemory.ui.MealHistoryScreen
import gy.roach.health.mealmemory.ui.theme.IOSTheme

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
                        // TODO: Implement camera/gallery integration
                        // For now, add a placeholder meal
                        mealRepository.addMeal("placeholder_path")
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