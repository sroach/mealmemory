package gy.roach.health.mealmemory.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

actual class CameraManager {
    private var context: Context? = null
    private var activity: Activity? = null
    private var currentPhotoPath: String? = null
    private var onPhotoTaken: ((String?) -> Unit)? = null

    // Camera launcher
    private var cameraLauncher: ActivityResultLauncher<Intent>? = null

    // Permission launcher
    private var permissionLauncher: ActivityResultLauncher<String>? = null

    fun initialize(activity: Activity) {
        this.activity = activity
        this.context = activity

        // Initialize camera launcher
        if (activity is androidx.activity.ComponentActivity) {
            cameraLauncher = activity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    onPhotoTaken?.invoke(currentPhotoPath)
                } else {
                    onPhotoTaken?.invoke(null)
                }
            }

            // Initialize permission launcher
            permissionLauncher = activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    launchCamera()
                } else {
                    onPhotoTaken?.invoke(null)
                }
            }
        }
    }

    actual fun takePhoto(onResult: (String?) -> Unit) {
        this.onPhotoTaken = onResult

        if (hasPermission()) {
            launchCamera()
        } else {
            requestPermission { granted ->
                if (granted) {
                    launchCamera()
                } else {
                    onResult(null)
                }
            }
        }
    }

    actual fun hasPermission(): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        } ?: false
    }

    actual fun requestPermission(onResult: (Boolean) -> Unit) {
        permissionLauncher?.launch(Manifest.permission.CAMERA) ?: onResult(false)
    }

    private fun launchCamera() {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            context?.let { ctx ->
                takePictureIntent.resolveActivity(ctx.packageManager)?.let {
                    val photoFile = createImageFile()
                    photoFile?.let { file ->
                        val photoURI = FileProvider.getUriForFile(
                            ctx,
                            "${ctx.packageName}.fileprovider",
                            file
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        cameraLauncher?.launch(takePictureIntent)
                    }
                }
            }
        } catch (e: Exception) {
            onPhotoTaken?.invoke(null)
        }
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "MEAL_$timeStamp"
            val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val image = File.createTempFile(imageFileName, ".jpg", storageDir)
            currentPhotoPath = image.absolutePath
            image
        } catch (e: Exception) {
            null
        }
    }
}
