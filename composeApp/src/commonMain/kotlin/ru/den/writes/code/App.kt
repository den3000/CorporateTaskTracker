package ru.den.writes.code

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import ru.den.writes.code.di.appModule
import ru.den.writes.code.ui.components.ServerStatusIndicator
import ru.den.writes.code.ui.main.MainScreen
import ru.den.writes.code.ui.theme.AppTheme
@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        AppTheme {
            Scaffold(
                topBar = {
                    ServerStatusIndicator()
                }
            ) { paddingValues ->
                MainScreen(
                    paddingValues = paddingValues
                )
            }
        }
    }
}
