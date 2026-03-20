package ru.den.writes.code.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import corporatetasktracker.composeapp.generated.resources.Res
import corporatetasktracker.composeapp.generated.resources.priority_high
import corporatetasktracker.composeapp.generated.resources.priority_low
import corporatetasktracker.composeapp.generated.resources.priority_medium
import org.jetbrains.compose.resources.stringResource
import ru.den.writes.code.domain.model.TaskPriority

@Composable
fun PriorityBadge(
    priority: TaskPriority,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor = when (priority) {
        TaskPriority.HIGH -> MaterialTheme.colorScheme.error
        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primaryContainer
        TaskPriority.LOW -> MaterialTheme.colorScheme.secondary
    }
    
    val textColor = contentColorFor(backgroundColor)

    val label = when (priority) {
        TaskPriority.HIGH -> stringResource(Res.string.priority_high)
        TaskPriority.MEDIUM -> stringResource(Res.string.priority_medium)
        TaskPriority.LOW -> stringResource(Res.string.priority_low)
    }

    var boxModifier = modifier.clip(RoundedCornerShape(8.dp))
    if (onClick != null) {
        boxModifier = boxModifier.clickable { onClick() }
    }
    boxModifier = boxModifier
        .background(backgroundColor)
        .padding(horizontal = 12.dp, vertical = 6.dp)

    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}
