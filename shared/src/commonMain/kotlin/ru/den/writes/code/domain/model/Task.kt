package ru.den.writes.code.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int = 0, // 0 - означает, что задача новая и еще не сохранена
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
)
