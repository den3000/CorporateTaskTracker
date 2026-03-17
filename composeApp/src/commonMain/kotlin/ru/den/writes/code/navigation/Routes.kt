package ru.den.writes.code.navigation

import kotlinx.serialization.Serializable

// ==========================================
// Маршруты приложения (Type-Safe Navigation)
// ==========================================

/**
 * Главный экран со списком задач.
 * Не принимает аргументов, поэтому это object.
 */
@Serializable
object TaskListRoute

/**
 * Экран деталей задачи.
 * Принимает обязательный аргумент [taskId], поэтому это data class.
 */
@Serializable
data class TaskDetailRoute(val taskId: Int)

/**
 * Экран настроек (бывший MainScreen).
 * Не принимает аргументов.
 */
@Serializable
object SettingsRoute
