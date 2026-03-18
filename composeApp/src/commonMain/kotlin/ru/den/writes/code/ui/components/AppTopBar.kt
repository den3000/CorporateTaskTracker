package ru.den.writes.code.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
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
        currentDestination?.contains(TaskListRoute::class.simpleName ?: "") == true -> "Задачи"
        currentDestination?.contains(TaskDetailRoute::class.simpleName ?: "") == true -> "Детали задачи"
        currentDestination?.contains(SettingsRoute::class.simpleName ?: "") == true -> "Настройки"
        else -> "Corporate Task Tracker"
    }

    TopAppBar(
        title = {
            Text(text = titleText)
        },
        navigationIcon = {
            if (canPop) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад"
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
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки"
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
