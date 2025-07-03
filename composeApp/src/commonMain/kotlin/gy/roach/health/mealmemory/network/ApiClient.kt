package gy.roach.health.mealmemory.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class MealResponse(
    val id: Long? = null,
    val photoUrl: String? = null,
    val feeling: String? = null,
    val timestamp: String? = null
)

class ApiClient(private val baseUrl: String = "https://roach.gy/mealmemory") {
    private val httpClient = HttpClient()

    suspend fun uploadMeal(
        userId: String,
        photoBytes: ByteArray,
        fileName: String,
        feeling: String? = null
    ): Result<MealResponse> {
        return try {
            val response: MealResponse = httpClient.submitFormWithBinaryData(
                url = "$baseUrl/api/v1/meal/upload",
                formData = formData {
                    append("userId", userId)
                    append("feeling", feeling ?: "NEUTRAL")
                    append("feeling", feeling ?: "NEUTRAL")
                    append("metadata", "") // Add metadata (empty string if no metadata)

                    append("image", photoBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    })
                }
            ).body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMealsByUserId(userId: String): Result<List<MealResponse>> {
        return try {
            val response: List<MealResponse> = httpClient.get("$baseUrl/api/v1/meal/user/$userId").body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllMeals(): Result<List<MealResponse>> {
        return try {
            val response: List<MealResponse> = httpClient.get("$baseUrl/api/v1/meal/all").body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMeal(id: Long): Result<MealResponse> {
        return try {
            val response: MealResponse = httpClient.delete("$baseUrl/api/v1/meal/$id").body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun close() {
        httpClient.close()
    }
}