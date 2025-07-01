package gy.roach.health.mealmemory

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform