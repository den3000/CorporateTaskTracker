package ru.den.writes.code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import ru.den.writes.code.network.NetworkMonitor
import ru.den.writes.code.network.ServerStatus

class ServerStatusViewModel(
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    val status: StateFlow<ServerStatus> = networkMonitor.observeStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ServerStatus.CONNECTING,
        )
}
