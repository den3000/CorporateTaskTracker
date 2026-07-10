package ru.den.writes.code

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalWindowInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import ru.auroraos.kmp.keyboard.maliit.Keyboard

private fun forceFetchKbdHeight(): Flow<Int> = flow {
    while (true) {
        emit(if (Keyboard.isOpen()) Keyboard.height().toInt() else 0)
        delay(100)
    }
}

actual fun Modifier.fillMaxSizeModifierWithKbdHandling(): Modifier = composed {
    val kbdHeight by remember { forceFetchKbdHeight().distinctUntilChanged() }.collectAsState(0)
    val windowHeight = LocalWindowInfo.current.containerSize.height
    val fraction = if (kbdHeight in 1 until windowHeight)
        (windowHeight - kbdHeight).toFloat() / windowHeight
    else
        1.0f

    this
        .fillMaxWidth()
        .fillMaxHeight(fraction)
}
