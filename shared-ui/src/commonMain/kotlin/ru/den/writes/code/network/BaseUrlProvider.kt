package ru.den.writes.code.network

import ru.den.writes.code.getPlatform
import ru.den.writes.code.config.AppConfig

class BaseUrlProvider {
    // В зависимости от платформы используем нужный адрес для эмулятора/симулятора
    // или общий IP для реальных устройств из файла config.properties.
    val baseUrl: String
        get() = if (AppConfig.SERVER_IP.isBlank() || AppConfig.SERVER_IP.contains("X")) {
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
}
