package ru.den.writes.code.di

import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.den.writes.code.ServerStatusViewModel
import ru.den.writes.code.network.NetworkMonitor
import ru.den.writes.code.ui.main.MainViewModel

val appModule = module {
    // Сеть
    single { HttpClient() }
    single { NetworkMonitor(get()) }

    // ViewModels
    viewModelOf(::ServerStatusViewModel)
    viewModelOf(::MainViewModel)
}
