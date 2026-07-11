package ru.den.writes.code.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.den.writes.code.generated.resources.Res
import ru.den.writes.code.generated.resources.add_2_24px
import ru.den.writes.code.generated.resources.content_desc_add_task
import org.jetbrains.compose.resources.stringResource
import ru.den.writes.code.res.painterResource
import ru.den.writes.code.navigation.TaskDetailRoute
import ru.den.writes.code.navigation.TaskListRoute

@Composable
fun AppFab(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val isTaskListScreen = currentDestination?.contains(TaskListRoute::class.simpleName ?: "") == true

    if (isTaskListScreen) {
        FloatingActionButton(
            onClick = {
                navController.navigate(TaskDetailRoute(0))
            },
            modifier = Modifier
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                painter = painterResource(Res.drawable.add_2_24px),
                contentDescription = stringResource(Res.string.content_desc_add_task),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}