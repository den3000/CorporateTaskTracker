package ru.den.writes.code

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.Modifier

actual fun Modifier.fillMaxSizeModifierWithKbdHandling() = this
    .fillMaxSize()
    .imePadding()
