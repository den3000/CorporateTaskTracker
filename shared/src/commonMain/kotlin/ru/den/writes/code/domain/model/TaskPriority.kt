package ru.den.writes.code.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
}
