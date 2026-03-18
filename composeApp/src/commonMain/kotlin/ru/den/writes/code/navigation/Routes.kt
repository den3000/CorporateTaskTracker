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
 * Принимает опциональный аргумент сериализованной задачи [taskJson].
 * Если он null или пустой - значит мы создаем новую задачу.
 */
@Serializable
data class TaskDetailRoute(val taskJson: String?)

/**
 * Экран настроек (бывший MainScreen).
 * Не принимает аргументов.
 */
@Serializable
object SettingsRoute
