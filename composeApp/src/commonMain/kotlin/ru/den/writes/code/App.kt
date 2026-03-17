package ru.den.writes.code

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import ru.den.writes.code.di.appModule
import ru.den.writes.code.ui.components.ServerStatusIndicator
import ru.den.writes.code.ui.main.MainScreen
import ru.den.writes.code.ui.main.MainViewModel
import ru.den.writes.code.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        val mainViewModel: MainViewModel = koinViewModel()
        val useDarkTheme = mainViewModel.isDarkThemeOverride
            .collectAsState()
            .value ?: isSystemInDarkTheme()

        AppTheme(darkTheme = useDarkTheme) {
            Scaffold(
                topBar = {
                    ServerStatusIndicator()
                }
            ) { paddingValues ->
                MainScreen(
                    viewModel = mainViewModel,
                    paddingValues = paddingValues,
                    isDarkTheme = useDarkTheme
                )
            }
        }
    }
}
