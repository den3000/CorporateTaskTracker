package ru.den.writes.code.ui.tasks

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import ru.den.writes.code.domain.model.Task
import ru.den.writes.code.domain.model.TaskPriority

class TaskDetailViewModel(
    taskJson: String?
) : ViewModel() {

    private val task: Task? = taskJson?.let { 
        try { Json.decodeFromString<Task>(it) } catch (e: Exception) { null } 
    }
    private val taskId: Int = task?.id ?: 0

    val taskTitle: StateFlow<String>
        field = MutableStateFlow(task?.title ?: "")

    val taskDescription: StateFlow<String>
        field = MutableStateFlow(task?.description ?: "")

    fun onTitleChange(newTitle: String) {
        taskTitle.value = newTitle
    }

    fun onDescriptionChange(newDescription: String) {
        taskDescription.value = newDescription
    }

    fun saveTask(): Task {
        return Task(
            id = taskId,
            title = taskTitle.value,
            description = taskDescription.value,
            isCompleted = task?.isCompleted ?: false,
            priority = task?.priority ?: TaskPriority.MEDIUM
        )
    }
}
