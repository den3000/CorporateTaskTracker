package ru.den.writes.code.ui.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import ru.den.writes.code.domain.model.Task
import ru.den.writes.code.domain.model.TaskPriority
import ru.den.writes.code.ui.theme.AppTheme

@Composable
fun TaskItem(
    task: Task,
    onToggleCompletion: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            // Чекбокс
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleCompletion() }
            )

            Spacer(modifier = Modifier.Companion.width(8.dp))

            // Тексты (Заголовок и описание)
            Column(modifier = Modifier.Companion.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) TextDecoration.Companion.LineThrough else TextDecoration.Companion.None,
                    maxLines = 1,
                    overflow = TextOverflow.Companion.Ellipsis
                )

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.Companion.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Companion.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.Companion.width(8.dp))

            // Индикатор приоритета (маленькая цветная точка)
            PriorityIndicator(priority = task.priority)
        }
    }
}

@Preview
@Composable
fun TaskItemPreview() {
    AppTheme {
        TaskItem(
            task = Task(
                id = 1,
                title = "Выполнить тестовое задание",
                description = "Нужно сделать все по ТЗ до конца недели",
                isCompleted = false,
                priority = TaskPriority.HIGH
            ),
            onToggleCompletion = {},
            onClick = {}
        )
    }
}

@Preview
@Composable
fun TaskItemCompletedPreview() {
    AppTheme {
        TaskItem(
            task = Task(
                id = 2,
                title = "Купить продукты",
                description = "Хлеб, молоко, яйца",
                isCompleted = true,
                priority = TaskPriority.MEDIUM
            ),
            onToggleCompletion = {},
            onClick = {}
        )
    }
}

@Preview
@Composable
fun TaskItemPreviewDark() {
    AppTheme(true) {
        TaskItem(
            task = Task(
                id = 1,
                title = "Выполнить тестовое задание",
                description = "Нужно сделать все по ТЗ до конца недели",
                isCompleted = false,
                priority = TaskPriority.HIGH
            ),
            onToggleCompletion = {},
            onClick = {}
        )
    }
}

@Preview
@Composable
fun TaskItemCompletedPreviewDark() {
    AppTheme(true) {
        TaskItem(
            task = Task(
                id = 2,
                title = "Купить продукты",
                description = "Хлеб, молоко, яйца",
                isCompleted = true,
                priority = TaskPriority.MEDIUM
            ),
            onToggleCompletion = {},
            onClick = {}
        )
    }
}