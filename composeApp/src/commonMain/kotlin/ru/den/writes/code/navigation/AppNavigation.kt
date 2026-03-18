package ru.den.writes.code.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.den.writes.code.domain.model.Task
import ru.den.writes.code.ui.settings.SettingsScreen
import ru.den.writes.code.ui.settings.SettingsViewModel
import ru.den.writes.code.ui.tasks.TaskDetailScreen
import ru.den.writes.code.ui.tasks.TaskListScreen
import ru.den.writes.code.ui.tasks.TaskListViewModel

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
        composable<TaskListRoute> { backStackEntry ->
            val taskListViewModel: TaskListViewModel = koinViewModel()
            val savedStateHandle = backStackEntry.savedStateHandle
            val taskResultJson = savedStateHandle.get<String>("task_result")
            
            LaunchedEffect(taskResultJson) {
                if (taskResultJson != null) {
                    val task = Json.decodeFromString<Task>(taskResultJson)
                    taskListViewModel.addOrUpdateTask(task)
                    savedStateHandle.remove<String>("task_result")
                }
            }

            TaskListScreen(
                viewModel = taskListViewModel,
                paddingValues = paddingValues,
                onNavigateToTask = { taskJson ->
                    navController.navigate(TaskDetailRoute(taskJson))
                }
            )
        }

        composable<TaskDetailRoute> { backStackEntry ->
            val route: TaskDetailRoute = backStackEntry.toRoute()
            TaskDetailScreen(
                viewModel = koinViewModel { parametersOf(route.taskJson) },
                paddingValues = paddingValues,
                onTaskSaved = { task ->
                    val json = Json.encodeToString(task)
                    navController.previousBackStackEntry?.savedStateHandle?.set("task_result", json)
                    navController.popBackStack()
                }
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                viewModel = settingsViewModel,
                paddingValues = paddingValues,
                isDarkTheme = isDarkTheme,
            )
        }
    }
}
