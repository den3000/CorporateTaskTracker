package ru.den.writes.code.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import corporatetasktracker.composeapp.generated.resources.Res
import corporatetasktracker.composeapp.generated.resources.server_status_connecting
import corporatetasktracker.composeapp.generated.resources.server_status_offline
import corporatetasktracker.composeapp.generated.resources.server_status_online
import org.jetbrains.compose.resources.stringResource
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
    val (color, textRes) = when (status) {
        ServerStatus.CONNECTING -> Color.Gray to Res.string.server_status_connecting
        ServerStatus.ONLINE -> Color.Green to Res.string.server_status_online
        ServerStatus.OFFLINE -> Color.Red to Res.string.server_status_offline
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .windowInsetsPadding(WindowInsets.statusBars),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(textRes),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
