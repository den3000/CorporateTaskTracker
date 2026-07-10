package ru.den.writes.code

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

@Composable
fun HandleStatusBarOffset(content: @Composable (() -> Unit)) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp)
    ) {
        content()
    }
}

fun main() = application {
    // Десктопный application {} под Авророй не предоставляет LocalViewModelStoreOwner
    // (на Android его даёт Activity, на iOS — ViewController). Настоящий koinViewModel()
    // требует owner уже на корне App (SettingsViewModel выше навигации) — без него
    // краш "No ViewModelStoreOwner". Даём корневой owner здесь.
    CompositionLocalProvider(
        LocalViewModelStoreOwner provides remember {
            object : ViewModelStoreOwner {
                override val viewModelStore = ViewModelStore()
            }
        }
    ) {
        HandleStatusBarOffset {
            App()
        }
    }
}
