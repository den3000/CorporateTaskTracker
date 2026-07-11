package ru.den.writes.code.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.den.writes.code.generated.resources.Res
import ru.den.writes.code.generated.resources.cloud_alert_24px
import ru.den.writes.code.generated.resources.cloud_off_24px
import org.koin.compose.viewmodel.koinViewModel
import ru.den.writes.code.res.painterResource
import ru.den.writes.code.ServerStatusViewModel
import ru.den.writes.code.network.ServerStatus

@Composable
fun ServerStatusIndicator(
    modifier: Modifier = Modifier,
    viewModel: ServerStatusViewModel = koinViewModel()
) {
    val status by viewModel.status.collectAsState()
    
    when (status) {
        ServerStatus.CONNECTING -> {
            Icon(
                painter = painterResource(Res.drawable.cloud_alert_24px),
                contentDescription = "Подключение к серверу",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = modifier.size(24.dp)
            )
        }
        ServerStatus.OFFLINE -> {
            Icon(
                painter = painterResource(Res.drawable.cloud_off_24px),
                contentDescription = "Нет соединения с сервером",
                tint = MaterialTheme.colorScheme.error,
                modifier = modifier.size(24.dp)
            )
        }
        ServerStatus.ONLINE -> {
            // Если онлайн - ничего не рисуем (занимает 0 пикселей)
        }
    }
}
