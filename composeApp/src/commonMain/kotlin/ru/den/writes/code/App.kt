package ru.den.writes.code

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import ru.den.writes.code.di.appModule
import ru.den.writes.code.ui.components.ServerStatusIndicator
import ru.den.writes.code.ui.settings.SettingsScreen
import ru.den.writes.code.ui.settings.SettingsViewModel
import ru.den.writes.code.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        val settingsViewModel: SettingsViewModel = koinViewModel()
        val useDarkTheme = settingsViewModel.isDarkThemeOverride
            .collectAsState()
            .value ?: isSystemInDarkTheme()

        AppTheme(darkTheme = useDarkTheme) {
            Scaffold(
                topBar = {
                    ServerStatusIndicator()
                }
            ) { paddingValues ->
                // Временно показываем SettingsScreen напрямую.
                // В Этапе 4 здесь появится NavHost, который свяжет 
                // TaskListScreen, TaskDetailScreen и SettingsScreen.
                SettingsScreen(
                    viewModel = settingsViewModel,
                    paddingValues = paddingValues,
                    isDarkTheme = useDarkTheme
                )
            }
        }
    }
}
