package ru.den.writes.code.network

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

enum class ServerStatus {
    CONNECTING, ONLINE, OFFLINE
}

class NetworkMonitor(
    private val httpClient: HttpClient,
    private val baseUrlProvider: BaseUrlProvider,
) {
    private val serverUrl: String
        get() = baseUrlProvider.baseUrl

    /**
     * Поток статуса сервера. ВАЖНО: весь опрос уходит на [Dispatchers.IO] через [flowOn],
     * иначе блокирующий сетевой вызов выполняется на UI-потоке (Main) и подвешивает
     * перерисовку/навигацию — особенно на Aurora, где Main-диспетчер строгий.
     */
    fun observeStatus(): Flow<ServerStatus> = flow {
        emit(ServerStatus.CONNECTING)
        while (true) {
            emit(pingOnce())
            // Периодический пинг для обновления статуса
            delay(5000)
        }
    }.flowOn(Dispatchers.IO)

    /** Разовая проверка (используется в репозитории перед записью на сервер). Не на Main. */
    suspend fun isOnline(): Boolean =
        withContext(Dispatchers.IO) { pingOnce() == ServerStatus.ONLINE }

    private suspend fun pingOnce(): ServerStatus = try {
        // Таймаут, чтобы недостижимый сервер не висел до дефолтного curl-таймаута.
        val ok = withTimeoutOrNull(PING_TIMEOUT_MS) {
            httpClient.get("$serverUrl/api/ping").status.value in 200..299
        } == true
        if (ok) ServerStatus.ONLINE else ServerStatus.OFFLINE
    } catch (e: Exception) {
        ServerStatus.OFFLINE
    }

    private companion object {
        const val PING_TIMEOUT_MS = 3000L
    }
}
