package ru.den.writes.code.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.den.writes.code.generated.resources.Res
import ru.den.writes.code.generated.resources.app_name
import ru.den.writes.code.generated.resources.arrow_back_24px
import ru.den.writes.code.generated.resources.content_desc_back
import ru.den.writes.code.generated.resources.content_desc_settings
import ru.den.writes.code.generated.resources.settings_24px
import ru.den.writes.code.generated.resources.title_settings
import ru.den.writes.code.generated.resources.title_task_detail
import ru.den.writes.code.generated.resources.title_tasks
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.den.writes.code.navigation.SettingsRoute
import ru.den.writes.code.navigation.TaskDetailRoute
import ru.den.writes.code.navigation.TaskListRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val canPop = navController.previousBackStackEntry != null
    val isSettingsScreen = currentDestination?.contains(SettingsRoute::class.simpleName ?: "") == true

    // Определяем заголовок экрана
    val titleText = when {
        currentDestination?.contains(TaskListRoute::class.simpleName ?: "") == true -> stringResource(Res.string.title_tasks)
        currentDestination?.contains(TaskDetailRoute::class.simpleName ?: "") == true -> stringResource(Res.string.title_task_detail)
        currentDestination?.contains(SettingsRoute::class.simpleName ?: "") == true -> stringResource(Res.string.title_settings)
        else -> stringResource(Res.string.app_name)
    }

    TopAppBar(
        title = {
            Text(text = titleText)
        },
        navigationIcon = {
            if (canPop) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back_24px),
                        contentDescription = stringResource(Res.string.content_desc_back),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ServerStatusIndicator(modifier = Modifier.padding(end = 16.dp))

                if (!isSettingsScreen) {
                    IconButton(onClick = { navController.navigate(SettingsRoute) }) {
                        Icon(
                            painter = painterResource(Res.drawable.settings_24px),
                            contentDescription = stringResource(Res.string.content_desc_settings),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
