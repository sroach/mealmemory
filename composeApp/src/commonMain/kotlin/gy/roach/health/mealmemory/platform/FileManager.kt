package gy.roach.health.mealmemory.platform

expect class FileManager() {
    fun readFileBytes(path: String): ByteArray?
    fun fileExists(path: String): Boolean
    fun getFileName(path: String): String
}