package ru.den.writes.code.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.den.writes.code.domain.model.TaskPriority
import androidx.compose.ui.tooling.preview.Preview
import ru.den.writes.code.ui.theme.AppTheme

@Composable
fun TaskDetailScreen(
    viewModel: TaskDetailViewModel,
    paddingValues: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val title by viewModel.taskTitle.collectAsState()
    val description by viewModel.taskDescription.collectAsState()
    val isCompleted by viewModel.isCompleted.collectAsState()
    val priority by viewModel.taskPriority.collectAsState()

    TaskDetailContent(
        taskId = viewModel.taskId,
        title = title,
        description = description,
        isCompleted = isCompleted,
        priority = priority,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onCompletionChange = viewModel::onCompletionChange,
        onPriorityChange = viewModel::onPriorityChange,
        onSave = {
            scope.launch {
                viewModel.saveTask()
                onBack()
            }
        },
        paddingValues = paddingValues
    )
}

@Composable
fun TaskDetailContent(
    taskId: Int,
    title: String,
    description: String,
    isCompleted: Boolean,
    priority: TaskPriority,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCompletionChange: (Boolean) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    onSave: () -> Unit,
    paddingValues: PaddingValues = PaddingValues()
) {
    val isNewTask = taskId <= 0
    val screenTitle = if (isNewTask) "Новая задача" else "Редактирование задачи #${taskId}"

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
            onValueChange = onTitleChange,
            label = { Text("Название задачи") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поле ввода: Описание
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Подробное описание") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Задача выполнена",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Switch(
                checked = isCompleted,
                onCheckedChange = onCompletionChange
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Приоритет",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskPriority.entries.forEach { p ->
                FilterChip(
                    selected = priority == p,
                    onClick = { onPriorityChange(p) },
                    label = { Text(p.name) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Сдвигаем кнопку вниз

        // Кнопка сохранения
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = title.isNotBlank() // Нельзя сохранить задачу без названия
        ) {
            Text(if (isNewTask) "Создать" else "Сохранить изменения")
        }
    }
}

@Preview
@Composable
fun TaskDetailScreenPreview() {
    AppTheme {
        TaskDetailContent(
            taskId = 1,
            title = "Пример задачи",
            description = "Это пример описания задачи для превью",
            isCompleted = false,
            priority = TaskPriority.MEDIUM,
            onTitleChange = {},
            onDescriptionChange = {},
            onCompletionChange = {},
            onPriorityChange = {},
            onSave = {}
        )
    }
}

@Preview
@Composable
fun NewTaskDetailScreenPreview() {
    AppTheme(true) {
        TaskDetailContent(
            taskId = 0,
            title = "",
            description = "",
            isCompleted = false,
            priority = TaskPriority.MEDIUM,
            onTitleChange = {},
            onDescriptionChange = {},
            onCompletionChange = {},
            onPriorityChange = {},
            onSave = {}
        )
    }
}
