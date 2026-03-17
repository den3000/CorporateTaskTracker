package ru.den.writes.code.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    // Определяем заголовок экрана
    val titleText = when {
        currentDestination?.contains(TaskListRoute::class.simpleName ?: "") == true -> "Задачи"
        currentDestination?.contains(TaskDetailRoute::class.simpleName ?: "") == true -> "Детали задачи"
        currentDestination?.contains(SettingsRoute::class.simpleName ?: "") == true -> "Настройки"
        else -> "Corporate Task Tracker"
    }

    Column {
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
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}
