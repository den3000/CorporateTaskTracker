package ru.den.writes.code.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ru.den.writes.code.ServerStatusViewModel
import ru.den.writes.code.network.ServerStatus

@Composable
fun ServerStatusIndicator(
    viewModel: ServerStatusViewModel = koinViewModel()
) {
    val status by viewModel.status.collectAsState()
    ServerStatusIndicatorContent(status)
}

@Composable
private fun ServerStatusIndicatorContent(status: ServerStatus) {
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
