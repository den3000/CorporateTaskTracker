package ru.den.writes.code.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import corporatetasktracker.composeapp.generated.resources.Res
import corporatetasktracker.composeapp.generated.resources.priority_high
import corporatetasktracker.composeapp.generated.resources.priority_low
import corporatetasktracker.composeapp.generated.resources.priority_medium
import corporatetasktracker.composeapp.generated.resources.select_priority
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import ru.den.writes.code.domain.model.TaskPriority
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

@OptIn(ExperimentalMaterial3Api::class)
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

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .safeContentPadding()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Приоритет",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                PriorityBadge(
                    priority = priority,
                    onClick = {
                        focusManager.clearFocus()
                        showBottomSheet = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Кнопка сохранения
        Button(
            onClick = {
                focusManager.clearFocus()
                onSave()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = title.isNotBlank() // Нельзя сохранить задачу без названия
        ) {
            Text(if (isNewTask) "Создать" else "Сохранить изменения")
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, top = 8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.select_priority),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                TaskPriority.entries.forEach { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onPriorityChange(p)
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val label = when (p) {
                            TaskPriority.HIGH -> stringResource(Res.string.priority_high)
                            TaskPriority.MEDIUM -> stringResource(Res.string.priority_medium)
                            TaskPriority.LOW -> stringResource(Res.string.priority_low)
                        }
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        PriorityIndicator(priority = p)
                    }
                }
            }
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