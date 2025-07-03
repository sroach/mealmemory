package gy.roach.health.mealmemory.platform

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.posix.memcpy

actual class FileManager {
    @OptIn(ExperimentalForeignApi::class)
    actual fun readFileBytes(path: String): ByteArray? {
        return try {
            val data = NSData.dataWithContentsOfFile(path)
            data?.let {
                val length = it.length.toInt()
                val result = ByteArray(length)
                result.usePinned { pinned ->
                    memcpy(pinned.addressOf(0), it.bytes, length.toULong())
                }
                result
            }
        } catch (e: Exception) {
            null
        }
    }

    actual fun fileExists(path: String): Boolean {
        return try {
            NSFileManager.defaultManager.fileExistsAtPath(path)
        } catch (e: Exception) {
            false
        }
    }

    actual fun getFileName(path: String): String {
        return try {
            path.substringAfterLast('/')
        } catch (e: Exception) {
            path
        }
    }
}
