package gy.roach.health.mealmemory.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import gy.roach.health.mealmemory.data.Feeling
import gy.roach.health.mealmemory.data.Meal
import gy.roach.health.mealmemory.data.MealRepository
import gy.roach.health.mealmemory.ui.components.*
import mealmemory.composeapp.generated.resources.Res
import mealmemory.composeapp.generated.resources.compose_multiplatform

@Composable
fun HomeScreen(
    mealRepository: MealRepository,
    onNavigateToHistory: () -> Unit,
    onAddMeal: () -> Unit
) {
    val todaysMeals by remember { derivedStateOf { mealRepository.getTodaysMeals() } }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // iOS-style Navigation Bar
        IOSNavigationBar(
            title = "MealMemory",
            backgroundColor = MaterialTheme.colorScheme.surface
        )

        // Content
        Box(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    "Today's Meals",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (todaysMeals.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No meals recorded today.\nTap + to add your first meal!",
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
                        items(todaysMeals) { meal ->
                            IOSMealCard(
                                meal = meal,
                                onFeelingChange = { newFeeling ->
                                    mealRepository.updateMealFeeling(meal.id, newFeeling)
                                }
                            )
                        }
                    }
                }
            }

            // iOS-style Floating Action Button
            IOSFloatingActionButton(
                onClick = onAddMeal,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Meal")
            }
        }

        // iOS-style Tab Bar
        IOSTabBar {
            IOSTabItem(
                text = "History",
                icon = Icons.Default.History,
                onClick = onNavigateToHistory
            )
        }
    }
}

@Composable
fun IOSMealCard(
    meal: Meal,
    onFeelingChange: (Feeling) -> Unit
) {
    var showFeelingActionSheet by remember { mutableStateOf(false) }

    IOSCard(
        onClick = { showFeelingActionSheet = true }
    ) {
        // Placeholder for meal image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = "Meal photo",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Feeling:",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            IOSFeelingChip(
                feeling = meal.feeling,
                onClick = { showFeelingActionSheet = true }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Time: ${meal.timestamp.hour}:${meal.timestamp.minute.toString().padStart(2, '0')}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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

@Composable
fun IOSFeelingChip(
    feeling: Feeling,
    onClick: () -> Unit
) {
    val (color, text) = when (feeling) {
        Feeling.GOOD -> Color(0xFF34C759) to "GOOD"
        Feeling.BAD -> Color(0xFFFF3B30) to "BAD"
        Feeling.NAUSEOUS -> Color(0xFFAF52DE) to "NAUSEOUS"
        Feeling.NEUTRAL -> Color(0xFF8E8E93) to "NEUTRAL"
    }

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun IOSFeelingActionSheet(
    currentFeeling: Feeling,
    onFeelingSelected: (Feeling) -> Unit,
    onDismiss: () -> Unit
) {
    val actions = Feeling.entries.map { feeling ->
        IOSActionSheetAction(
            title = feeling.name,
            onClick = { onFeelingSelected(feeling) }
        )
    } + IOSActionSheetAction(
        title = "Cancel",
        style = IOSActionStyle.Cancel,
        onClick = { }
    )

    IOSActionSheet(
        title = "How are you feeling?",
        onDismiss = onDismiss,
        actions = actions
    )
}