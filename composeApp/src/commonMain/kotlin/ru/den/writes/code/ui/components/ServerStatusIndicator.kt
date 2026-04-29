package ru.den.writes.code.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import corporatetasktracker.composeapp.generated.resources.Res
import corporatetasktracker.composeapp.generated.resources.cloud_alert_24px
import corporatetasktracker.composeapp.generated.resources.cloud_off_24px
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
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
                modifier = modifier
            )
        }
        ServerStatus.OFFLINE -> {
            Icon(
                painter = painterResource(Res.drawable.cloud_off_24px),
                contentDescription = "Нет соединения с сервером",
                tint = MaterialTheme.colorScheme.error,
                modifier = modifier
            )
        }
        ServerStatus.ONLINE -> {
            // Если онлайн - ничего не рисуем (занимает 0 пикселей)
        }
    }
}
