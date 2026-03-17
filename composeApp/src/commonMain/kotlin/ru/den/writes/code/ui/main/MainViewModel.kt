package ru.den.writes.code.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    val showContent: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val isDarkThemeOverride: StateFlow<Boolean?>
        field = MutableStateFlow<Boolean?>(null)

    fun toggleContent() {
        showContent.value = !showContent.value
    }

    fun toggleTheme(currentSystemIsDark: Boolean) {
        // Если ручное переопределение еще не задано, инвертируем системную тему.
        // Если уже задано - инвертируем его.
        val currentIsDark = isDarkThemeOverride.value ?: currentSystemIsDark
        isDarkThemeOverride.value = !currentIsDark
    }
}
