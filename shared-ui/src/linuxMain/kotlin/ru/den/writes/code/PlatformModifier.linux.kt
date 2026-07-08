package ru.den.writes.code

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalWindowInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.auroraos.kmp.keyboard.maliit.Keyboard

private fun forceFetchKbdHeight(): Flow<Int> {
    return flow {
        while (true) {
            if (Keyboard.isOpen()) {
                val height = Keyboard.height().toInt()
                emit(height)
            } else {
                emit(0)
            }
            delay(100)
        }
    }
}

actual fun Modifier.fillMaxSizeModifierWithKbdHandling() = composed {
    val kbdHeight = forceFetchKbdHeight().collectAsState(0).value
    val windowHeight = LocalWindowInfo.current.containerSize.height

    val fraction = if (kbdHeight != 0)
        (windowHeight - kbdHeight).toFloat() / windowHeight
    else
        1.0f

    this
        .fillMaxWidth()
        .fillMaxHeight(fraction)
}
