package gy.roach.health.mealmemory.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MealRepository {
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals.asStateFlow()

    fun addMeal(photoPath: String, feeling: Feeling = Feeling.NEUTRAL) {
        val newMeal = Meal(
            id = Uuid.random().toString(),
            photoPath = photoPath,
            feeling = feeling,
            timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
        _meals.value = _meals.value + newMeal
    }

    fun updateMealFeeling(mealId: String, newFeeling: Feeling) {
        _meals.value = _meals.value.map { meal ->
            if (meal.id == mealId) meal.copy(feeling = newFeeling) else meal
        }
    }

    fun getTodaysMeals(): List<Meal> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return _meals.value.filter { meal ->
            meal.timestamp.date == today
        }
    }

    fun getMealsForWeek(startDate: LocalDate): List<Meal> {
        val endDate = startDate.plus(DatePeriod(days = 6))
        return _meals.value.filter { meal ->
            meal.timestamp.date >= startDate && meal.timestamp.date <= endDate
        }
    }

    fun getCurrentWeekMeals(): List<Meal> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val sunday = today.minus(DatePeriod(days = today.dayOfWeek.ordinal))
        return getMealsForWeek(sunday)
    }
}