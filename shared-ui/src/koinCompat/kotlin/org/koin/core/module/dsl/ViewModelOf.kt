package org.koin.core.module.dsl


import androidx.lifecycle.ViewModel
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

inline fun <reified R : ViewModel> Module.viewModelOf(
    crossinline constructor: () -> R
): KoinDefinition<R> = factory { constructor() }

inline fun <reified R : ViewModel, reified T1> Module.viewModelOf(
    crossinline constructor: (T1) -> R
): KoinDefinition<R> = factory { constructor(get()) }

inline fun <reified R : ViewModel, reified T1, reified T2> Module.viewModelOf(
    crossinline constructor: (T1, T2) -> R
): KoinDefinition<R> = factory { constructor(get(), get()) }