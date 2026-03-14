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
) {
    // В зависимости от платформы используем нужный адрес для эмулятора/симулятора
    // или общий IP для реальных устройств из файла config.properties.
    private val serverUrl: String = if (AppConfig.SERVER_IP.isBlank() || AppConfig.SERVER_IP.contains("X")) {
        // Режим симулятора/эмулятора (если IP не был изменен)
        if (getPlatform().name.contains("Android")) {
            "http://10.0.2.2:8080" // Android Emulator
        } else {
            "http://127.0.0.1:8080" // iOS Simulator (или localhost)
        }
    } else {
        // Режим реального устройства в локальной Wi-Fi сети
        "http://${AppConfig.SERVER_IP}:8080"
    }

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
