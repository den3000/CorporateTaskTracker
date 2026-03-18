package ru.den.writes.code.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
                imageVector = Icons.Default.QuestionMark,
                contentDescription = "Подключение к серверу",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = modifier
            )
        }
        ServerStatus.OFFLINE -> {
            Icon(
                imageVector = Icons.Default.CloudOff,
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
