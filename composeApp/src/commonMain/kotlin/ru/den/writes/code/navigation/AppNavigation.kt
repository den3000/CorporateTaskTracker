package ru.den.writes.code.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.den.writes.code.ui.components.ServerStatusIndicator
import ru.den.writes.code.ui.settings.SettingsScreen
import ru.den.writes.code.ui.settings.SettingsViewModel
import ru.den.writes.code.ui.tasks.TaskDetailScreen
import ru.den.writes.code.ui.tasks.TaskListScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    paddingValues: PaddingValues,
    isDarkTheme: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = TaskListRoute
    ) {
        composable<TaskListRoute> {
            TaskListScreen(
                paddingValues = paddingValues,
                onNavigateToTask = { taskId ->
                    navController.navigate(TaskDetailRoute(taskId))
                },
                onNavigateToSettings = {
                    navController.navigate(SettingsRoute)
                }
            )
        }

        composable<TaskDetailRoute> { backStackEntry ->
            // Type-Safe извлечение аргументов
            val route: TaskDetailRoute = backStackEntry.toRoute()
            
            // Инициализация ViewModel с параметром (инжектим taskId)
            TaskDetailScreen(
                viewModel = koinViewModel { parametersOf(route.taskId) },
                paddingValues = paddingValues
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                viewModel = settingsViewModel,
                paddingValues = paddingValues,
                isDarkTheme = isDarkTheme,
                serverStatusContent = { ServerStatusIndicator() }
            )
        }
    }
}
