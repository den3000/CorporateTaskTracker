package org.koin.compose.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatformTools

/**
 * Полифил koinViewModel для Аврора ОС.
 *
 * ВАЖНО: [remember] обязателен. Без него get() вызывается на КАЖДОЙ рекомпозиции, а VM
 * зарегистрированы как factory — то есть каждый recompose создавал бы новый экземпляр
 * ViewModel, сбрасывая его состояние (например, tasks.stateIn -> initialValue = emptyList()),
 * из-за чего список мигает пусто/полно, а весь UI «моргает». Настоящий
 * koin-compose-viewmodel кэширует VM в ViewModelStore; здесь достаточно remember на время
 * жизни composable.
 */
@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified T : ViewModel> koinViewModel(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T = remember {
    KoinPlatformTools.defaultContext().get().get<T>(qualifier, parameters)
}
