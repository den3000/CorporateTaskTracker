package ru.den.writes.code.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ru.den.writes.code.domain.model.Task
import ru.den.writes.code.domain.model.TaskPriority
import androidx.compose.ui.tooling.preview.Preview
import ru.den.writes.code.ui.theme.AppTheme

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = koinViewModel(),
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToTask: (Int) -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()

    TaskListContent(
        tasks = tasks,
        paddingValues = paddingValues,
        onToggleCompletion = { viewModel.toggleTaskCompletion(it) },
        onNavigateToTask = onNavigateToTask
    )
}

@Composable
fun TaskListContent(
    tasks: List<Task>,
    paddingValues: PaddingValues = PaddingValues(),
    onToggleCompletion: (Int) -> Unit,
    onNavigateToTask: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
    ) {
        if (tasks.isEmpty()) {
            Text(
                text = "Список задач пуст",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = tasks,
                    key = { it.id }
                ) { task ->
                    TaskItem(
                        task = task,
                        onToggleCompletion = { onToggleCompletion(task.id) },
                        onClick = { onNavigateToTask(task.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { onNavigateToTask(0) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить задачу"
            )
        }
    }
}

@Preview
@Composable
fun TaskListScreenPreview() {
    val sampleTasks = listOf(
        Task(1, "Выполнить тестовое задание", "Нужно сделать все по ТЗ", false, TaskPriority.HIGH),
        Task(2, "Купить продукты", "Хлеб, молоко, яйца", true, TaskPriority.MEDIUM),
        Task(3, "Прочитать книгу", "Чистый код", false, TaskPriority.LOW)
    )
    AppTheme {
        TaskListContent(
            tasks = sampleTasks,
            onToggleCompletion = {},
            onNavigateToTask = {}
        )
    }
}

@Preview
@Composable
fun TaskListScreenPreviewDark() {
    val sampleTasks = listOf(
        Task(1, "Выполнить тестовое задание", "Нужно сделать все по ТЗ", false, TaskPriority.HIGH),
        Task(2, "Купить продукты", "Хлеб, молоко, яйца", true, TaskPriority.MEDIUM),
        Task(3, "Прочитать книгу", "Чистый код", false, TaskPriority.LOW)
    )
    AppTheme(true) {
        TaskListContent(
            tasks = sampleTasks,
            onToggleCompletion = {},
            onNavigateToTask = {}
        )
    }
}