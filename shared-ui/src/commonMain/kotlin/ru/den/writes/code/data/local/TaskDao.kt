package ru.den.writes.code.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    
    // Вставка или обновление (если ID совпадает)
    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    // Удаление
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // Получение одной задачи по ID
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskEntity?

    // Получение списка всех задач
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    suspend fun getAllTasks(): List<TaskEntity>

    // Подписка на список всех задач, новые задачи сверху
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun subscribeAllTasks(): Flow<List<TaskEntity>>
}
