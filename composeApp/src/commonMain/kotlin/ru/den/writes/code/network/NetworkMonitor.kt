package ru.den.writes.code.network

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.den.writes.code.getPlatform
import ru.den.writes.code.config.AppConfig

enum class ServerStatus {
    CONNECTING, ONLINE, OFFLINE
}

class NetworkMonitor(
    private val httpClient: HttpClient,
    private val baseUrlProvider: BaseUrlProvider,
) {
    private val serverUrl: String
        get() = baseUrlProvider.baseUrl

    fun observeStatus(): Flow<ServerStatus> = flow {
        emit(ServerStatus.CONNECTING)
        while (true) {
            val nextStatus = try {
                println("PAM Pinging server... $serverUrl")
                val response = httpClient.get("$serverUrl/api/ping")
                if (response.status.value in 200..299) {
                    ServerStatus.ONLINE
                } else {
                    ServerStatus.OFFLINE
                }
            } catch (e: Exception) {
                // Если сервер выключен или нет связи - упадёт с ошибкой
                ServerStatus.OFFLINE
            }

            emit(nextStatus)

            // Периодический пинг каждые 5 секунд для обновления статуса
            delay(5000)
        }
    }
}
