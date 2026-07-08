package ru.den.writes.code.di

import org.koin.core.module.Module

expect fun platformModule(): Module

val platformModule = platformModule()