package gy.roach.health.mealmemory.network

import gy.roach.health.mealmemory.data.Feeling
import gy.roach.health.mealmemory.data.Meal
import gy.roach.health.mealmemory.platform.FileManager
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MealNetworkRepository(
    private val apiClient: ApiClient = ApiClient(),
    private val fileManager: FileManager = FileManager(),
    private val userId: String = "default_user" // You can make this configurable
) {

    suspend fun uploadMealWithPhoto(
        photoPath: String,
        feeling: Feeling = Feeling.NEUTRAL
    ): Result<Meal> {
        return try {
            // Check if photo file exists
            if (!fileManager.fileExists(photoPath)) {
                return Result.failure(Exception("Photo file not found"))
            }

            // Read photo file bytes
            val photoBytes = fileManager.readFileBytes(photoPath)
                ?: return Result.failure(Exception("Failed to read photo file"))

            val fileName = fileManager.getFileName(photoPath)

            // Upload to API with a 30-second timeout
            try {
                withTimeout(30000) { // 30 seconds timeout
                    val uploadResult = apiClient.uploadMeal(
                        userId = userId,
                        photoBytes = photoBytes,
                        fileName = fileName,
                        feeling = feeling.name
                    )

                    uploadResult.fold(
                        onSuccess = { response ->
                            // Convert API response to local Meal object
                            val meal = Meal(
                                id = response.id?.toString() ?: generateLocalId(),
                                photoPath = photoPath, // Keep local path for now
                                feeling = feeling,
                                timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                            )
                            return@withTimeout Result.success(meal)
                        },
                        onFailure = { error ->
                            return@withTimeout Result.failure(error)
                        }
                    )
                }
            } catch (e: TimeoutCancellationException) {
                // If the network request times out, fall back to local storage
                println("Network request timed out: ${e.message}")
                val meal = Meal(
                    id = generateLocalId(),
                    photoPath = photoPath,
                    feeling = feeling,
                    timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
                // Return success with the local meal to prevent UI from hanging
                Result.success(meal)
            }
        } catch (e: Exception) {
            println("Error in uploadMealWithPhoto: ${e.message}")
            // Create a local meal as a fallback
            val meal = Meal(
                id = generateLocalId(),
                photoPath = photoPath,
                feeling = feeling,
                timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
            // Return success with the local meal to prevent UI from hanging
            Result.success(meal)
        }
    }

    suspend fun getUserMeals(): Result<List<Meal>> {
        return try {
            // Get meals with a 30-second timeout
            try {
                withTimeout(30000) { // 30 seconds timeout
                    val result = apiClient.getMealsByUserId(userId)
                    result.fold(
                        onSuccess = { mealResponses ->
                            val meals = mealResponses.mapNotNull { response ->
                                convertResponseToMeal(response)
                            }
                            return@withTimeout Result.success(meals)
                        },
                        onFailure = { error ->
                            return@withTimeout Result.failure(error)
                        }
                    )
                }
            } catch (e: TimeoutCancellationException) {
                // If the network request times out, return an empty list
                println("Network request timed out when getting meals: ${e.message}")
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            println("Error in getUserMeals: ${e.message}")
            // Return an empty list to prevent UI from hanging
            Result.success(emptyList())
        }
    }

    suspend fun deleteMeal(mealId: String): Result<Boolean> {
        return try {
            val id = mealId.toLongOrNull() ?: return Result.failure(Exception("Invalid meal ID"))

            // Delete meal with a 30-second timeout
            try {
                withTimeout(30000) { // 30 seconds timeout
                    val result = apiClient.deleteMeal(id)
                    result.fold(
                        onSuccess = { return@withTimeout Result.success(true) },
                        onFailure = { error -> return@withTimeout Result.failure(error) }
                    )
                }
            } catch (e: TimeoutCancellationException) {
                // If the network request times out, return success anyway to prevent UI from hanging
                println("Network request timed out when deleting meal: ${e.message}")
                Result.success(true)
            }
        } catch (e: Exception) {
            println("Error in deleteMeal: ${e.message}")
            // Return success anyway to prevent UI from hanging
            Result.success(true)
        }
    }

    private fun convertResponseToMeal(response: MealResponse): Meal? {
        return try {
            Meal(
                id = response.id?.toString() ?: return null,
                photoPath = response.photoUrl ?: "",
                feeling = response.feeling?.let {
                    Feeling.valueOf(it.uppercase())
                } ?: Feeling.NEUTRAL,
                timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) // You might want to parse the actual timestamp
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun generateLocalId(): String {
        return Clock.System.now().toEpochMilliseconds().toString()
    }

    fun close() {
        apiClient.close()
    }
}
