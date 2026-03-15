package ru.den.writes.code.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var showContent by mutableStateOf(false)
        private set

    fun toggleContent() {
        showContent = !showContent
    }
}
