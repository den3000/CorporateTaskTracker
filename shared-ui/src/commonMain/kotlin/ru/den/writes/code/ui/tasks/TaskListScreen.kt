package ru.den.writes.code.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.den.writes.code.resources.Res
import ru.den.writes.code.resources.content_desc_back
import ru.den.writes.code.resources.empty_task_list
import ru.den.writes.code.resources.settings_24px
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ru.den.writes.code.domain.model.Task
import ru.den.writes.code.domain.model.TaskPriority
import ru.den.writes.code.ui.theme.AppTheme

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = koinViewModel(),
    onNavigateToTask: (Int) -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    TaskListContent(
        tasks = tasks,
        onToggleCompletion = { viewModel.toggleTaskCompletion(it) },
        onNavigateToTask = onNavigateToTask,
        onDeleteTask = { viewModel.deleteTask(it) },
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshTasks() },
    )
}

@Composable
fun TaskListContent(
    tasks: List<Task>,
    onToggleCompletion: (Int) -> Unit,
    onNavigateToTask: (Int) -> Unit,
    onDeleteTask: (Task) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (tasks.isEmpty()) {
                item { EmptyStateItem() }
                return@LazyColumn
            }

            items(
                items = tasks,
                key = { it.id }
            ) { task ->
                DismissibleTaskItem(
                    task = task,
                    onToggleCompletion = onToggleCompletion,
                    onNavigateToTask = onNavigateToTask,
                    onDeleteTask = onDeleteTask
                )
            }
        }
    }
}

/**
 * Компонент для отображения состояния, когда список пуст.
 */
@Composable
private fun LazyItemScope.EmptyStateItem() {
    Box(
        modifier = Modifier.fillParentMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.empty_task_list),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Компонент карточки задачи с поддержкой Swipe-to-Dismiss
 */
@Composable
private fun DismissibleTaskItem(
    task: Task,
    onToggleCompletion: (Int) -> Unit,
    onNavigateToTask: (Int) -> Unit,
    onDeleteTask: (Task) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.background
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(Res.drawable.settings_24px),
                    contentDescription = stringResource(Res.string.content_desc_back),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        onDismiss = { onDeleteTask(task) }
    ) {
        TaskItem(
            task = task,
            onToggleCompletion = { onToggleCompletion(task.id) },
            onClick = { onNavigateToTask(task.id) }
        )
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
            onNavigateToTask = {},
            onDeleteTask = {},
            isRefreshing = true,
            onRefresh = {},
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
            onNavigateToTask = {},
            onDeleteTask = {},
            isRefreshing = false,
            onRefresh = {},
        )
    }
}