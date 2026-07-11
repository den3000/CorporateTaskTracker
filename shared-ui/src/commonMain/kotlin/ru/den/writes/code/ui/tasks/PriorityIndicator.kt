package ru.den.writes.code.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.den.writes.code.domain.model.TaskPriority
import ru.den.writes.code.ui.theme.AppTheme

@Composable
fun PriorityIndicator(priority: TaskPriority) {
    val color = when (priority) {
        TaskPriority.HIGH -> MaterialTheme.colorScheme.error
        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primaryContainer
        TaskPriority.LOW -> MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = Modifier
            .size(12.dp)
            .background(color, shape = CircleShape),
    )
}

@Preview
@Composable
fun PriorityIndicatorHighPreview() {
    AppTheme {
        PriorityIndicator(priority = TaskPriority.HIGH)
    }
}

@Preview
@Composable
fun PriorityIndicatorMediumPreview() {
    AppTheme {
        PriorityIndicator(priority = TaskPriority.MEDIUM)
    }
}

@Preview
@Composable
fun PriorityIndicatorLowPreview() {
    AppTheme {
        PriorityIndicator(priority = TaskPriority.LOW)
    }
}

@Preview
@Composable
fun PriorityIndicatorHighPreviewDark() {
    AppTheme(darkTheme = true) {
        PriorityIndicator(priority = TaskPriority.HIGH)
    }
}

@Preview
@Composable
fun PriorityIndicatorMediumPreviewDark() {
    AppTheme(darkTheme = true) {
        PriorityIndicator(priority = TaskPriority.MEDIUM)
    }
}

@Preview
@Composable
fun PriorityIndicatorLowPreviewDark() {
    AppTheme(darkTheme = true) {
        PriorityIndicator(priority = TaskPriority.LOW)
    }
}
