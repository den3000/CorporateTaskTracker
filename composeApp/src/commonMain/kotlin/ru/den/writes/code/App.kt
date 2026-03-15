package ru.den.writes.code

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import corporatetasktracker.composeapp.generated.resources.Res
import corporatetasktracker.composeapp.generated.resources.compose_multiplatform
import io.ktor.client.HttpClient
import org.jetbrains.compose.resources.painterResource
import ru.den.writes.code.network.NetworkMonitor
import ru.den.writes.code.ui.components.ServerStatusIndicator

val httpClient = HttpClient()
val networkMonitor = NetworkMonitor(httpClient)

// Инициализируем ViewModel глобально (пока без DI).
// В будущем это лучше делать через Koin или другие DI-фреймворки.
val serverStatusViewModel = ServerStatusViewModel(networkMonitor)

@Composable
@Preview
fun App() {
    MaterialTheme {
        Scaffold(
            topBar = {
                ServerStatusIndicator(viewModel = serverStatusViewModel)
            }
        ) { paddingValues ->
            var showContent by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .safeContentPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = { showContent = !showContent }) {
                    Text("Click me!")
                }
                AnimatedVisibility(showContent) {
                    val greeting = remember { Greeting().greet() }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting")
                    }
                }
            }
        }
    }
}
