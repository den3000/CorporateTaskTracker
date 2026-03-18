package ru.den.writes.code.ui.tasks

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ru.den.writes.code.domain.model.Task
import ru.den.writes.code.domain.model.TaskPriority

class TaskListViewModel : ViewModel() {

    val tasks: StateFlow<List<Task>>
        field = MutableStateFlow(emptyList())

    init {
        loadFakeTasks()
    }

    private fun loadFakeTasks() {
        val fakeData = listOf(
            Task(
                id = 1,
                title = "Подготовить отчет за квартал",
                description = "Собрать данные по продажам и маркетингу за Q3. Сделать презентацию.",
                isCompleted = false,
                priority = TaskPriority.HIGH
            ),
            Task(
                id = 2,
                title = "Обновить сертификаты сервера",
                description = "Текущие SSL сертификаты истекают через неделю. Нужно обновить на всех нодах.",
                isCompleted = false,
                priority = TaskPriority.HIGH
            ),
            Task(
                id = 3,
                title = "Ревью кода (PR #421)",
                description = "Проверить изменения в модуле авторизации от Саши.",
                isCompleted = true,
                priority = TaskPriority.MEDIUM
            ),
            Task(
                id = 4,
                title = "Заказать кофе в офис",
                description = "Зерна закончились, нужно заказать 5кг арабики.",
                isCompleted = false,
                priority = TaskPriority.LOW
            )
        )
        
        tasks.value = fakeData
    }

    fun toggleTaskCompletion(taskId: Int) {
        val mutableFlow = tasks as MutableStateFlow<List<Task>>
        mutableFlow.update { currentList ->
            currentList.map { task ->
                if (task.id == taskId) {
                    task.copy(isCompleted = !task.isCompleted)
                } else {
                    task
                }
            }
        }
    }

    fun addOrUpdateTask(task: Task) {
        val mutableFlow = tasks
        mutableFlow.update { currentList ->
            val index = currentList.indexOfFirst { it.id == task.id }
            if (index != -1) {
                val newList = currentList.toMutableList()
                newList[index] = task
                newList
            } else {
                val newId = if (task.id == 0) (currentList.maxOfOrNull { it.id } ?: 0) + 1 else task.id
                currentList + task.copy(id = newId)
            }
        }
    }
}
