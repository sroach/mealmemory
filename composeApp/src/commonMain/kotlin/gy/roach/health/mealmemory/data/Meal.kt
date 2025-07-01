package gy.roach.health.mealmemory.data

import kotlinx.datetime.LocalDateTime

enum class Feeling {
    GOOD, BAD, NAUSEOUS, NEUTRAL
}

data class Meal(
    val id: String,
    val photoPath: String, // Path to the meal photo
    val feeling: Feeling,
    val timestamp: LocalDateTime
)
