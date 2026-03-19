package ru.den.writes.code.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.den.writes.code.data.repository.LocalTaskRepository
import ru.den.writes.code.domain.model.Task
import ru.den.writes.code.domain.model.TaskPriority

class TaskDetailViewModel(
    val taskId: Int,
    private val repository: LocalTaskRepository
) : ViewModel() {

    val taskTitle: StateFlow<String>
        field = MutableStateFlow("")

    val taskDescription: StateFlow<String>
        field = MutableStateFlow("")

    val isCompleted: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val taskPriority: StateFlow<TaskPriority>
        field = MutableStateFlow(TaskPriority.MEDIUM)

    init {
        if (taskId > 0) {
            viewModelScope.launch {
                val task = repository.getTaskById(taskId)
                task?.let {
                    taskTitle.value = it.title
                    taskDescription.value = it.description
                    isCompleted.value = it.isCompleted
                    taskPriority.value = it.priority
                }
            }
        }
    }

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

    suspend fun saveTask() {
        val task = Task(
            id = if (taskId > 0) taskId else 0,
            title = taskTitle.value,
            description = taskDescription.value,
            isCompleted = isCompleted.value,
            priority = taskPriority.value
        )
        repository.upsertTask(task)
    }
}
