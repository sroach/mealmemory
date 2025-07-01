package gy.roach.health.mealmemory.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gy.roach.health.mealmemory.data.Meal
import gy.roach.health.mealmemory.data.MealRepository
import gy.roach.health.mealmemory.ui.components.*

@Composable
fun MealHistoryScreen(
    mealRepository: MealRepository,
    onNavigateBack: () -> Unit
) {
    val weekMeals by remember { derivedStateOf { mealRepository.getCurrentWeekMeals() } }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // iOS-style Navigation Bar with back button
        IOSNavigationBar(
            title = "Meal History",
            leadingIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                "Past Week's Meals",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (weekMeals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No meals recorded this week.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(weekMeals.sortedByDescending { it.timestamp }) { meal ->
                        IOSMealHistoryCard(
                            meal = meal,
                            onFeelingChange = { newFeeling ->
                                mealRepository.updateMealFeeling(meal.id, newFeeling)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IOSMealHistoryCard(
    meal: Meal,
    onFeelingChange: (gy.roach.health.mealmemory.data.Feeling) -> Unit
) {
    var showFeelingActionSheet by remember { mutableStateOf(false) }

    IOSCard(
        onClick = { showFeelingActionSheet = true }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "${meal.timestamp.date}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${meal.timestamp.hour}:${meal.timestamp.minute.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IOSFeelingChip(
                feeling = meal.feeling,
                onClick = { showFeelingActionSheet = true }
            )
        }
    }

    if (showFeelingActionSheet) {
        IOSFeelingActionSheet(
            currentFeeling = meal.feeling,
            onFeelingSelected = { newFeeling ->
                onFeelingChange(newFeeling)
                showFeelingActionSheet = false
            },
            onDismiss = { showFeelingActionSheet = false }
        )
    }
}