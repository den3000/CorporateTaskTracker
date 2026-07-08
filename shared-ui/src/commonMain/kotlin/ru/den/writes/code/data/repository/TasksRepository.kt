package ru.den.writes.code.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import ru.den.writes.code.data.local.toDomain
import ru.den.writes.code.data.local.toEntity
import ru.den.writes.code.data.remote.RemoteTasksDataSource
import ru.den.writes.code.domain.model.Task
import ru.den.writes.code.network.NetworkMonitor
import ru.den.writes.code.ui.tasks.TaskItem

/**
 * Основной репозиторий данных, объединяющий локальные и удалённые источники.
 * Отвечает за синхронизацию данных между локальной БД и сервером.
 */
class TasksRepository(
    private val localDataSource: LocalTasksDataSource,
    private val remoteDataSource: RemoteTasksDataSource,
    private val networkMonitor: NetworkMonitor,
) {
    /**
     * Подписываемся на поток задач.
     * Сначала загружаем локальные данные, затем синхронизируем с сервером (если есть сеть).
     */
    fun subscribeAllTasks(): Flow<List<Task>> {

        return combine(
            localDataSource.subscribeAllTasks(),
            remoteDataSource.tasks
        ) { localTasks, remoteTasks ->
            syncWithServer(localTasks, remoteTasks)
        }
    }

    /**
     * Запишем задачу в локальную БД.
     */
    suspend fun upsertTask(task: Task) {
        localDataSource.upsertTask(task)

        if (networkMonitor.isOnline()) {
            try {
                remoteDataSource.addTask(task)
            } catch (e: Exception) {
                println("PAM TASKS REPOSITORY > upsertTask exception: $e")
            }
        }
    }

    /**
     * Удаляем задачу: сначала с сервера, затем из локальной БД.
     */
    suspend fun deleteTask(task: Task) {
        // Удаляем с сервера (если есть сеть)
        if (networkMonitor.isOnline()) {
            try {
                remoteDataSource.deleteTask(task.id)
            } catch (e: Exception) {
                println("PAM TASKS REPOSITORY > deleteTask exception: $e")
            }
        }
        
        // Удаляем из локальной БД
        localDataSource.deleteTask(task)
    }

    /**
     * Получаем задачу по ID.
     */
    suspend fun getTaskById(id: Int): Task? {
        return localDataSource.getTaskById(id)
    }

    suspend fun forceSync() {
        remoteDataSource.updateTasks()
    }

    /**
     * Синхронизируем локальные данные с серверными.
     * @param localTasks все задачи из локальной БД
     * @param remoteTasks все задачи с сервера
     * @return обновлённый список задач
     */
    private fun syncWithServer(
        localTasks: List<Task>,
        remoteTasks: List<Task>
    ): List<Task> {
        // Преобразуем удалённые задачи в Map по ID для быстрого поиска
        val remoteTasksMap = remoteTasks.associateBy { it.id }

        // Обновляем локальные задачи данными с сервера
        return localTasks.map { localTask ->
            val remoteTask = remoteTasksMap[localTask.id]
            remoteTask ?: localTask // Если задачи нет на сервере, оставляем локальную
        }.sortedBy { it.id }
    }
}
