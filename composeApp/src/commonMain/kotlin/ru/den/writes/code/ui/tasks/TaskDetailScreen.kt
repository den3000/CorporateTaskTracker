package ru.den.writes.code.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaskDetailScreen(
    viewModel: TaskDetailViewModel,
    paddingValues: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {}
) {
    val title by viewModel.taskTitle.collectAsState()
    val description by viewModel.taskDescription.collectAsState()

    val isNewTask = viewModel.taskId <= 0
    val screenTitle = if (isNewTask) "Новая задача" else "Редактирование задачи #${viewModel.taskId}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .padding(16.dp)
    ) {
        Text(
            text = screenTitle,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Поле ввода: Заголовок
        OutlinedTextField(
            value = title,
            onValueChange = { viewModel.onTitleChange(it) },
            label = { Text("Название задачи") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поле ввода: Описание
        OutlinedTextField(
            value = description,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = { Text("Подробное описание") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f)) // Сдвигаем кнопку вниз

        // Кнопка сохранения
        Button(
            onClick = {
                viewModel.saveTask()
                onBack() // Возвращаемся назад после сохранения
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = title.isNotBlank() // Нельзя сохранить задачу без названия
        ) {
            Text(if (isNewTask) "Создать" else "Сохранить изменения")
        }
    }
}
