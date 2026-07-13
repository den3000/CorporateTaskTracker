package ru.den.writes.code

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController

actual fun Modifier.fillMaxSizeModifierWithKbdHandling() = this
    .fillMaxSize()
    .imePadding()

// PascalCase is required: Xcode consumes this factory as `MainViewController()`.
@Suppress("ktlint:standard:function-naming")
fun MainViewController() = ComposeUIViewController { App() }
