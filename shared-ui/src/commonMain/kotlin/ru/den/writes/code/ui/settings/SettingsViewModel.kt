package ru.den.writes.code.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

    val showContent: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val isDarkThemeOverride: StateFlow<Boolean?>
        field = MutableStateFlow<Boolean?>(null)

    fun toggleContent() {
        showContent.value = !showContent.value
    }

    fun toggleTheme(currentSystemIsDark: Boolean) {
        val currentIsDark = isDarkThemeOverride.value ?: currentSystemIsDark
        isDarkThemeOverride.value = !currentIsDark
    }
}
