В проекте есть серверное приложение в модуле /server, и есть мобильные приложения для Android и iOS.
Самая очевидная проблема для меня сейчас состоит в следующем - они никак не взаимодействуют друг с
другом. То есть да, я могу запустить сервер, могу запустить мобилки, но нет никакого взаимодействия.
Помоги мне составить план реализации того, как это можно исправить. То есть чего хочу я:
1. Мобиальные приложения по прежнему могут работать без сервера, но если при подключении они его не находят,
   то они где то в UI просто отображают, что статус оффлайн, в остальном работают как и сейчас.
2. если же при запуске приложения обнаруживают, что они могут подключиться к моему серверу, и обменяться данными,
   то они просто в UI отображают статус онлайн

# Этап 1: Подготовка сервера (Health Check)
Прежде чем мобилки смогут понять, доступен ли сервер, сервер должен уметь отвечать на простейший запрос.
В вашем модуле /server (скорее всего, там используется серверный Ktor) создайте простой эндпоинт:
```kotlin
// На сервере (например, в Routing.kt)
routing {
    get("/api/ping") {
        call.respondText("pong", status = HttpStatusCode.OK)
    }
}
```

# Этап 2: Настройка Ktor Client в общем модуле
Вам нужно добавить зависимости для сетевых запросов в ваш файл build.gradle.kts (в модуле composeApp
или shared, где у вас общая логика):
```kotlin
// build.gradle.kts (commonMain)
implementation("io.ktor:ktor-client-core:2.3.8") // Используйте актуальную версию
implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
```
(Также не забудьте добавить движки для платформ: ktor-client-okhttp для Android и ktor-client-darwin для iOS).

# Этап 3: Создание класса для мониторинга статуса
В commonMain создайте класс, который будет проверять доступность сервера и хранить состояние.
Идеальный инструмент для этого — StateFlow из корутин.

```kotlin
// commonMain/.../network/NetworkMonitor.kt
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ServerStatus {
    CONNECTING, ONLINE, OFFLINE
}

class NetworkMonitor(
    private val httpClient: HttpClient,
    private val serverUrl: String = "http://192.168.x.x:8080" // Замените на ваш IP
) {
    private val _status = MutableStateFlow(ServerStatus.CONNECTING)
    val status: StateFlow<ServerStatus> = _status.asStateFlow()

    fun checkServerAvailability(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                // Пытаемся достучаться до сервера
                val response = httpClient.get("$serverUrl/api/ping")
                if (response.status.value in 200..299) {
                    _status.value = ServerStatus.ONLINE
                } else {
                    _status.value = ServerStatus.OFFLINE
                }
            } catch (e: Exception) {
                // Если нет интернета или сервер выключен, упадет exception
                _status.value = ServerStatus.OFFLINE
            }
        }
    }
}

```

Важно: для тестирования на реальных устройствах/эмуляторах используйте локальный IP-адрес вашего
компьютера (например, 192.168.1.5), а не localhost, так как для эмулятора Android localhost — это сам эмулятор.

# Этап 4: Интеграция в Compose Multiplatform (UI)
Теперь мы можем использовать этот статус в вашем файле App.kt.

```kotlin
// На сервере (например, в Routing.kt)
// App.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// В реальном проекте это должно инжектироваться через DI (например, Koin)
val httpClient = HttpClient()
val networkMonitor = remember { NetworkMonitor(httpClient) }

@Composable
@Preview
fun App() {
    MaterialTheme {
        val coroutineScope = rememberCoroutineScope()
        // Наблюдаем за статусом сервера
        val serverStatus by networkMonitor.status.collectAsState()

        // Запускаем проверку при старте приложения
        LaunchedEffect(Unit) {
            networkMonitor.checkServerAvailability(coroutineScope)
        }

        Scaffold(
            topBar = {
                ServerStatusIndicator(serverStatus)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Главный экран трекера задач")
                // Здесь ваш основной UI, который работает независимо от интернета
            }
        }
    }
}

@Composable
fun ServerStatusIndicator(status: ServerStatus) {
    val (color, text) = when (status) {
        ServerStatus.CONNECTING -> Color.Gray to "Подключение..."
        ServerStatus.ONLINE -> Color.Green to "Сервер доступен (Online)"
        ServerStatus.OFFLINE -> Color.Red to "Работаем локально (Offline)"
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
```
