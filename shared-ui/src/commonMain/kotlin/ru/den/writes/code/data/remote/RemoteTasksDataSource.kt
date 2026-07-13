package ru.den.writes.code.data.remote

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.den.writes.code.domain.model.Task
import ru.den.writes.code.network.BaseUrlProvider

class RemoteTasksDataSource(
    private val httpClient: HttpClient,
    private val baseUrlProvider: BaseUrlProvider,
) {
    private val serverUrl: String
        get() = baseUrlProvider.baseUrl

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    // Поток для наблюдения за задачами на сервере
    val tasks: StateFlow<List<Task>>
        field = MutableStateFlow<List<Task>>(emptyList())

    // / Отправляем задачу на сервер
    suspend fun addTask(task: Task) = withContext(Dispatchers.IO) {
        httpClient.post("$serverUrl/api/tasks") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(task))
        }
    }

    // / Загружаем текущие задачи с сервера и обновляем поток
    suspend fun updateTasks() {
        try {
            tasks.value = getTasks()
        } catch (_: Exception) {
            // Если ошибка сети, оставляем текущее значение потока
        }
    }

    // / Удаляем задачу с сервера
    suspend fun deleteTask(id: Int) = withContext(Dispatchers.IO) {
        httpClient.delete("$serverUrl/api/tasks/$id")
    }

    // / Загружаем текущие задачи с сервера
    private suspend fun getTasks(): List<Task> = withContext(Dispatchers.IO) {
        val response = httpClient.get("$serverUrl/api/tasks")
        val text = response.bodyAsText()
        if (text.isBlank()) return@withContext emptyList()
        json.decodeFromString<List<Task>>(text)
    }
}
