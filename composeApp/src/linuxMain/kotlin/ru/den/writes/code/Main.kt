package ru.den.writes.code

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application

fun main() = application {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp) // Стандартный отступ для статус-бара и навигационной панели
    ) {
        App()
    }
}