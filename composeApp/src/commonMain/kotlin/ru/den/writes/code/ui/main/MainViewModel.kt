package ru.den.writes.code.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var showContent by mutableStateOf(false)
        private set

    // Состояние темы. 
    // Null означает, что мы используем системную тему.
    // True - принудительно темная, False - принудительно светлая.
    var isDarkThemeOverride by mutableStateOf<Boolean?>(null)
        private set

    fun toggleContent() {
        showContent = !showContent
    }

    fun toggleTheme(currentSystemIsDark: Boolean) {
        // Если ручное переопределение еще не задано, инвертируем системную тему.
        // Если уже задано - инвертируем его.
        val currentIsDark = isDarkThemeOverride ?: currentSystemIsDark
        isDarkThemeOverride = !currentIsDark
    }
}
