package ru.den.writes.code.ui.tasks

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import ru.den.writes.code.data.repository.LocalTaskRepository
import ru.den.writes.code.domain.model.Task
import ru.den.writes.code.domain.model.TaskPriority

class TaskDetailViewModel(
    taskJson: String?,
    private val repository: LocalTaskRepository
) : ViewModel() {

    private val task: Task? = taskJson?.let { 
        try { Json.decodeFromString<Task>(it) } catch (e: Exception) { null } 
    }
    val taskId: Int = task?.id ?: 0

    val taskTitle: StateFlow<String>
        field = MutableStateFlow(task?.title ?: "")

    val taskDescription: StateFlow<String>
        field = MutableStateFlow(task?.description ?: "")

    val isCompleted: StateFlow<Boolean>
        field = MutableStateFlow(task?.isCompleted ?: false)

    val taskPriority: StateFlow<TaskPriority>
        field = MutableStateFlow(task?.priority ?: TaskPriority.MEDIUM)

    fun onTitleChange(newTitle: String) {
        taskTitle.value = newTitle
    }

    fun onDescriptionChange(newDescription: String) {
        taskDescription.value = newDescription
    }

    fun onCompletionChange(completed: Boolean) {
        isCompleted.value = completed
    }

    fun onPriorityChange(priority: TaskPriority) {
        taskPriority.value = priority
    }

    fun saveTask(): Task {
        return Task(
            id = taskId,
            title = taskTitle.value,
            description = taskDescription.value,
            isCompleted = isCompleted.value,
            priority = taskPriority.value
        )
    }
}
