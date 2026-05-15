package ru.den.writes.code.data.remote

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import ru.den.writes.code.domain.model.Task
import ru.den.writes.code.domain.model.TaskPriority
import ru.den.writes.code.network.BaseUrlProvider

class RemoteTaskDataSource(
    private val httpClient: HttpClient,
    private val baseUrlProvider: BaseUrlProvider
) {
    private val serverUrl: String
        get() = baseUrlProvider.baseUrl

    // Note: In Phase 2, the server returns tasks.toString() instead of JSON
    // We use a dirty regex parser for GET, but use proper JSON for POST.

    suspend fun getTasks(): List<Task> {
        val response = httpClient.get("$serverUrl/api/tasks")
        val text = response.bodyAsText()
        
        // Dirty parsing for Phase 2 (since server returns task.toString())
        return parseTasksFromToString(text)
    }

    suspend fun addTask(task: Task) {
        val json = Json.encodeToString(task)
        httpClient.post("$serverUrl/api/tasks") {
            contentType(ContentType.Application.Json)
            setBody(json)
        }
    }

    suspend fun deleteTask(id: Int) {
        httpClient.delete("$serverUrl/api/tasks/$id")
    }

    private fun parseTasksFromToString(text: String): List<Task> {
        val tasks = mutableListOf<Task>()
        val cleaned = text.trim().removeSurrounding("[", "]")
        if (cleaned.isEmpty()) return emptyList()

        val taskStrings = cleaned.split("), ")
        
        for (s in taskStrings) {
            val normalized = if (s.endsWith(")")) s else "$s)"
            
            val id = extractField(normalized, "id")?.toIntOrNull() ?: 0
            val title = extractField(normalized, "title") ?: "Untitled"
            val description = extractField(normalized, "description") ?: ""
            val isCompleted = extractField(normalized, "isCompleted")?.toBoolean() ?: false
            val priority = extractField(normalized, "priority")?.let { 
                runCatching { TaskPriority.valueOf(it) }.getOrDefault(TaskPriority.MEDIUM) 
            } ?: TaskPriority.MEDIUM

            tasks.add(Task(id, title, description, isCompleted = isCompleted, priority = priority))
        }
        return tasks
    }

    private fun extractField(input: String, field: String): String? {
        val regex = "$field=([^,)]+)".toRegex()
        val match = regex.find(input)
        return match?.groupValues?.get(1)
    }
}
