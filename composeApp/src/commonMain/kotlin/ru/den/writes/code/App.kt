package ru.den.writes.code

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import io.ktor.client.HttpClient
import ru.den.writes.code.network.NetworkMonitor
import ru.den.writes.code.ui.components.ServerStatusIndicator
import ru.den.writes.code.ui.main.MainScreen
import ru.den.writes.code.ui.main.MainViewModel

val httpClient = HttpClient()
val networkMonitor = NetworkMonitor(httpClient)

// Инициализируем ViewModels глобально (пока без DI).
val serverStatusViewModel = ServerStatusViewModel(networkMonitor)
val mainViewModel = MainViewModel()

@Composable
@Preview
fun App() {
    MaterialTheme {
        Scaffold(
            topBar = {
                ServerStatusIndicator(viewModel = serverStatusViewModel)
            }
        ) { paddingValues ->
            MainScreen(
                viewModel = mainViewModel,
                paddingValues = paddingValues
            )
        }
    }
}
