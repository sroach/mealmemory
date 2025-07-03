package gy.roach.health.mealmemory.camera

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.*
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSObject

/**
 * iOS implementation of CameraManager using UIImagePickerController.
 */
actual class CameraManager {
    private var onPhotoTakenCallback: ((String?) -> Unit)? = null
    // Store the delegate as a property to prevent garbage collection
    private var imagePickerDelegate: ImagePickerDelegate? = null

    actual fun takePhoto(onResult: (String?) -> Unit) {
        this.onPhotoTakenCallback = onResult

        // Check camera permission first
        if (hasPermission()) {
            openCamera()
        } else {
            requestPermission { granted ->
                if (granted) {
                    openCamera()
                } else {
                    onResult(null)
                }
            }
        }
    }

    actual fun hasPermission(): Boolean {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        return status == AVAuthorizationStatusAuthorized
    }

    actual fun requestPermission(onResult: (Boolean) -> Unit) {
        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
            onResult(granted)
        }
    }

    private fun openCamera() {
        println("DEBUG: openCamera called")
        val sourceType = if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
            println("DEBUG: Camera is available, using camera source type")
            UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        } else {
            // Fallback to photo library if camera is not available (simulator)
            println("DEBUG: Camera is not available, falling back to photo library")
            UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        }

        // Create and configure image picker
        println("DEBUG: Creating UIImagePickerController")
        val picker = UIImagePickerController()
        picker.sourceType = sourceType
        picker.allowsEditing = false

        println("DEBUG: Creating and setting ImagePickerDelegate")
        // Create a new delegate and store it in the property to prevent garbage collection
        imagePickerDelegate = ImagePickerDelegate { imagePath ->
            println("DEBUG: ImagePickerDelegate callback received with imagePath: $imagePath")
            // Ensure callback is invoked on the main thread
            NSOperationQueue.mainQueue.addOperationWithBlock {
                println("DEBUG: Invoking callback on main thread")
                onPhotoTakenCallback?.invoke(imagePath)
            }
        }
        picker.delegate = imagePickerDelegate

        // Only set camera-specific properties if using camera
        if (sourceType == UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera) {
            println("DEBUG: Setting camera device to rear")
            picker.cameraDevice = UIImagePickerControllerCameraDevice.UIImagePickerControllerCameraDeviceRear
        }

        // Present the picker
        println("DEBUG: Getting rootViewController")
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        println("DEBUG: rootViewController: $rootViewController")

        // Make sure everything is properly configured before presenting
        if (rootViewController != null && picker.delegate != null) {
            println("DEBUG: Presenting UIImagePickerController")
            rootViewController.presentViewController(picker, animated = true) {
                println("DEBUG: UIImagePickerController presented successfully")
            }
        } else {
            println("DEBUG: Cannot present UIImagePickerController - rootViewController or delegate is null")
            // If we can't present the picker, call the callback with null to avoid hanging
            // Ensure callback is invoked on the main thread
            NSOperationQueue.mainQueue.addOperationWithBlock {
                println("DEBUG: Invoking null callback on main thread")
                onPhotoTakenCallback?.invoke(null)
            }
        }
    }
}

/**
 * Delegate class to handle UIImagePickerController callbacks
 */
private class ImagePickerDelegate(
    private val onImageSelected: (String?) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    @Suppress("CONFLICTING_OVERLOADS")
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        println("DEBUG: imagePickerController called with info: $didFinishPickingMediaWithInfo")

        // Try different ways to access the image
        println("DEBUG: Available keys in info: ${didFinishPickingMediaWithInfo.keys}")

        // Try using the string constant
        var image = didFinishPickingMediaWithInfo["UIImagePickerControllerOriginalImage"] as? UIImage

        // If that didn't work, try other possible keys
        if (image == null) {
            // Try lowercase first letter (Kotlin/Native sometimes does this)
            image = didFinishPickingMediaWithInfo["uIImagePickerControllerOriginalImage"] as? UIImage
        }

        if (image == null) {
            // Try without the prefix
            image = didFinishPickingMediaWithInfo["OriginalImage"] as? UIImage
        }

        println("DEBUG: Image extracted: ${image != null}")

        // First dismiss the view controller to ensure UI is responsive
        picker.dismissViewControllerAnimated(true) {
            println("DEBUG: Picker dismissed successfully")

            // Then process the image
            if (image != null) {
                println("DEBUG: About to call saveImageToDocuments")
                val imagePath = saveImageToDocuments(image)
                println("DEBUG: saveImageToDocuments returned: $imagePath")
                onImageSelected(imagePath)
            } else {
                println("DEBUG: Image was null, returning null")
                onImageSelected(null)
            }
        }
    }

    // Add alternative method name that might match the Objective-C selector
    fun imagePickerController_didFinishPickingMediaWithInfo(
        picker: UIImagePickerController,
        info: Map<Any?, *>
    ) {
        println("DEBUG: imagePickerController_didFinishPickingMediaWithInfo called with info: $info")

        // Try different ways to access the image
        println("DEBUG: Available keys in info: ${info.keys}")

        // Try using the string constant
        var image = info["UIImagePickerControllerOriginalImage"] as? UIImage

        // If that didn't work, try other possible keys
        if (image == null) {
            // Try lowercase first letter (Kotlin/Native sometimes does this)
            image = info["uIImagePickerControllerOriginalImage"] as? UIImage
        }

        if (image == null) {
            // Try without the prefix
            image = info["OriginalImage"] as? UIImage
        }

        println("DEBUG: Image extracted: ${image != null}")

        // First dismiss the view controller to ensure UI is responsive
        picker.dismissViewControllerAnimated(true) {
            println("DEBUG: Picker dismissed successfully")

            // Then process the image
            if (image != null) {
                println("DEBUG: About to call saveImageToDocuments")
                val imagePath = saveImageToDocuments(image)
                println("DEBUG: saveImageToDocuments returned: $imagePath")
                onImageSelected(imagePath)
            } else {
                println("DEBUG: Image was null, returning null")
                onImageSelected(null)
            }
        }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        println("DEBUG: imagePickerControllerDidCancel called")
        picker.dismissViewControllerAnimated(true) {
            println("DEBUG: Picker dismissed after cancel")
            // Ensure callback is invoked on the main thread
            NSOperationQueue.mainQueue.addOperationWithBlock {
                println("DEBUG: Invoking null callback on main thread after cancel")
                onImageSelected(null)
            }
        }
    }

    // Add alternative method name that might match the Objective-C selector
    fun imagePickerController_didCancel(picker: UIImagePickerController) {
        println("DEBUG: imagePickerController_didCancel called")
        picker.dismissViewControllerAnimated(true) {
            println("DEBUG: Picker dismissed after cancel")
            // Ensure callback is invoked on the main thread
            NSOperationQueue.mainQueue.addOperationWithBlock {
                println("DEBUG: Invoking null callback on main thread after cancel")
                onImageSelected(null)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun saveImageToDocuments(image: UIImage): String? {
        println("DEBUG: saveImageToDocuments started")
        return try {
            // Get documents directory
            println("DEBUG: Getting documents directory")
            val documentsPath = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory,
                NSUserDomainMask,
                true
            ).firstOrNull() as? String

            println("DEBUG: documentsPath: $documentsPath")
            if (documentsPath == null) {
                println("DEBUG: documentsPath is null, returning null")
                return null
            }

            // Create MealMemory directory if it doesn't exist
            val mealMemoryDir = "$documentsPath/MealMemory"
            println("DEBUG: mealMemoryDir: $mealMemoryDir")
            val fileManager = NSFileManager.defaultManager

            if (!fileManager.fileExistsAtPath(mealMemoryDir)) {
                println("DEBUG: Creating directory at $mealMemoryDir")
                fileManager.createDirectoryAtPath(
                    mealMemoryDir,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null
                )
            }

            // Generate unique filename
            println("DEBUG: Generating filename")
            val dateFormatter = NSDateFormatter()
            dateFormatter.setDateFormat("yyyyMMdd_HHmmss")
            val timeStamp = dateFormatter.stringFromDate(NSDate())
            val fileName = "MEAL_${timeStamp}.jpg"
            val filePath = "$mealMemoryDir/$fileName"
            println("DEBUG: filePath: $filePath")

            // Convert UIImage to JPEG data and save
            println("DEBUG: Converting image to JPEG")
            val imageData = UIImageJPEGRepresentation(image, 0.8) // 80% quality

            if (imageData == null) {
                println("DEBUG: Failed to convert image to JPEG data")
                return null
            }

            println("DEBUG: imageData size: ${imageData.length()} bytes")

            println("DEBUG: Writing file to disk")
            val success = imageData.writeToFile(filePath, atomically = true)
            println("DEBUG: Write success: $success")

            if (success) {
                println("DEBUG: Returning filePath: $filePath")
                filePath
            } else {
                println("DEBUG: Write failed, returning null")
                null
            }
        } catch (e: Exception) {
            println("DEBUG: Exception in saveImageToDocuments: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
