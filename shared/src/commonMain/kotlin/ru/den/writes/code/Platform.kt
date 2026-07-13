package ru.den.writes.code

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
