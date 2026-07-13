package ru.den.writes.code.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.den.writes.code.ui.settings.SettingsScreen
import ru.den.writes.code.ui.settings.SettingsViewModel
import ru.den.writes.code.ui.tasks.TaskDetailScreen
import ru.den.writes.code.ui.tasks.TaskListScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    isDarkTheme: Boolean,
) {
    NavHost(
        navController = navController,
        startDestination = TaskListRoute,
    ) {
        composable<TaskListRoute> {
            TaskListScreen(
                onNavigateToTask = { taskId ->
                    navController.navigate(TaskDetailRoute(taskId))
                },
            )
        }

        composable<TaskDetailRoute> { backStackEntry ->
            val route: TaskDetailRoute = backStackEntry.toRoute()
            TaskDetailScreen(
                viewModel = koinViewModel { parametersOf(route.taskId) },
                onBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                viewModel = settingsViewModel,
                isDarkTheme = isDarkTheme,
            )
        }
    }
}
