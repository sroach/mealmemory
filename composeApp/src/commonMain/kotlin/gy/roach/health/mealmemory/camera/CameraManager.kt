package gy.roach.health.mealmemory.camera

/**
 * Common interface for camera functionality.
 * Platform-specific implementations will provide the actual functionality.
 */
expect class CameraManager() {
    fun takePhoto(onResult: (String?) -> Unit)
    fun hasPermission(): Boolean
    fun requestPermission(onResult: (Boolean) -> Unit)
}

// commonMain/kotlin/camera/CameraResult.kt
sealed class CameraResult {
    data class Success(val imagePath: String) : CameraResult()
    data class Error(val message: String) : CameraResult()
    object Cancelled : CameraResult()
}