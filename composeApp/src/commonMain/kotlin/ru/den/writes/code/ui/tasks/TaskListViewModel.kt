package ru.den.writes.code.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.den.writes.code.data.repository.LocalTaskRepository
import ru.den.writes.code.domain.model.Task

class TaskListViewModel(
    private val repository: LocalTaskRepository
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = repository.subscribeAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    fun toggleTaskCompletion(taskId: Int) = viewModelScope.launch {
        repository
            .getTaskById(taskId)
            ?.let { repository.upsertTask(it.copy(isCompleted = !it.isCompleted)) }
    }

    fun addOrUpdateTask(task: Task) = viewModelScope.launch {
        repository.upsertTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }

}
