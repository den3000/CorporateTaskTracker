package ru.den.writes.code.ui.tasks

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TaskDetailViewModel(
    val taskId: Int
) : ViewModel() {

    val taskTitle: StateFlow<String>
        field = MutableStateFlow("")

    val taskDescription: StateFlow<String>
        field = MutableStateFlow("")

    init {
        // Если переданный ID > 0, имитируем загрузку задачи
        if (taskId > 0) {
            loadFakeTaskData(taskId)
        }
    }

    private fun loadFakeTaskData(id: Int) {
        // Заглушка, имитирующая данные с сервера
        taskTitle.value = "Задача №$id"
        taskDescription.value = "Описание загруженной задачи $id"
    }

    fun onTitleChange(newTitle: String) {
        taskTitle.value = newTitle
    }

    fun onDescriptionChange(newDescription: String) {
        taskDescription.value = newDescription
    }

    fun saveTask() {
        // Имитация сохранения задачи: 
        // Создание (если taskId == 0) или Обновление (если taskId > 0)
        println("Saving task. ID: $taskId, Title: ${taskTitle.value}, Desc: ${taskDescription.value}")
    }
}
