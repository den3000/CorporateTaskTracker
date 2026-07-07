package ru.den.writes.code.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.den.writes.code.data.local.TaskDao
import ru.den.writes.code.data.local.toDomain
import ru.den.writes.code.data.local.toEntity
import ru.den.writes.code.domain.model.Task

/**
 * DataSource для работы с локальной базой данных.
 * Отвечает за CRUD операции с локальным хранилищем.
 */
class LocalTasksDataSource(
    private val taskDao: TaskDao
) {
    fun subscribeAllTasks(): Flow<List<Task>> {
        return taskDao
            .subscribeAllTasks()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task.toEntity())
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    suspend fun getTaskById(id: Int): Task? {
        return taskDao.getTaskById(id)?.toDomain()
    }
}
