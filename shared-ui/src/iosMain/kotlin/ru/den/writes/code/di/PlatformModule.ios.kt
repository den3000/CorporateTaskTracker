package ru.den.writes.code.di

import org.koin.dsl.module
import ru.den.writes.code.data.local.getDatabaseBuilder

actual fun platformModule() = module {
    single {
        getDatabaseBuilder()
    }
}
