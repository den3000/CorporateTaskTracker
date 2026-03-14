package ru.den.writes.code

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import corporatetasktracker.composeapp.generated.resources.Res
import corporatetasktracker.composeapp.generated.resources.compose_multiplatform
import io.ktor.client.HttpClient
import org.jetbrains.compose.resources.painterResource
import ru.den.writes.code.network.NetworkMonitor
import ru.den.writes.code.network.ServerStatus

val httpClient = HttpClient()
val networkMonitor = NetworkMonitor(httpClient)

@Composable
@Preview
fun App() {
    MaterialTheme {
        val serverStatus by networkMonitor.observeStatus().collectAsState(initial = ServerStatus.CONNECTING)

        Scaffold(
            topBar = {
                ServerStatusIndicator(serverStatus)
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

@Composable
fun ServerStatusIndicator(status: ServerStatus) {
    val (color, text) = when (status) {
        ServerStatus.CONNECTING -> Color.Gray to "Подключение к серверу..."
        ServerStatus.ONLINE -> Color(0xFF4CAF50) to "Сервер доступен (Online)"
        ServerStatus.OFFLINE -> Color(0xFFF44336) to "Работаем локально (Offline)"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}