package gy.roach.health.mealmemory.platform

import java.io.File

actual class FileManager {
    actual fun readFileBytes(path: String): ByteArray? {
        return try {
            val file = File(path)
            if (file.exists()) file.readBytes() else null
        } catch (e: Exception) {
            null
        }
    }

    actual fun fileExists(path: String): Boolean {
        return try {
            File(path).exists()
        } catch (e: Exception) {
            false
        }
    }

    actual fun getFileName(path: String): String {
        return try {
            File(path).name
        } catch (e: Exception) {
            path.substringAfterLast('/')
        }
    }
}