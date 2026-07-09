package ru.den.writes.code.di

import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.den.writes.code.ServerStatusViewModel
import ru.den.writes.code.data.local.AppDatabase
import ru.den.writes.code.data.local.getRoomDatabase
import ru.den.writes.code.data.remote.RemoteTasksDataSource
import ru.den.writes.code.data.repository.LocalTasksDataSource
import ru.den.writes.code.data.repository.TasksRepository
import ru.den.writes.code.network.BaseUrlProvider
import ru.den.writes.code.network.NetworkMonitor
import ru.den.writes.code.ui.settings.SettingsViewModel
import ru.den.writes.code.ui.tasks.TaskDetailViewModel
import ru.den.writes.code.ui.tasks.TaskListViewModel

val appModule = module {
    // Сеть
    single { BaseUrlProvider() }
    single { HttpClient() }
    single { NetworkMonitor(get(), get()) }
    single { RemoteTasksDataSource(get(), get()) }

    // База данных
    single { getRoomDatabase(get()) }
    single { get<AppDatabase>().taskDao() }
    single { LocalTasksDataSource(get()) }

    single { TasksRepository(get(), get(), get()) }

    // ViewModels
    viewModelOf(::ServerStatusViewModel)
    viewModelOf(::SettingsViewModel)
    
    factory { TaskListViewModel(get()) }
    factory { (taskId: Int) -> TaskDetailViewModel(taskId, get()) }
}
