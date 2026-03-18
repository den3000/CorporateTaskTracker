package ru.den.writes.code.ui.tasks

import androidx.lifecycle.ViewModel

class TaskDetailViewModel(
    val taskId: Int
) : ViewModel() {
    // В будущем здесь будет загрузка данных конкретной задачи по ID с сервера
}
