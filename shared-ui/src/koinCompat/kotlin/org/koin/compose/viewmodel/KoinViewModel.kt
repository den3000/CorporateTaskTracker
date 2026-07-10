package org.koin.compose.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatformTools
import kotlin.reflect.KClass

/**
 * Полифил koinViewModel для Аврора ОС.
 *
 * Настоящий koin-compose-viewmodel (io.insert-koin) для Авроры не опубликован, поэтому
 * воспроизводим его поведение поверх доступного androidx-слоя: кэшируем ViewModel в
 * [androidx.lifecycle.ViewModelStore] владельца (nav back-stack entry) через
 * [ViewModelProvider]. Это даёт и стабильность между рекомпозициями, и корректный
 * onCleared() (отмену viewModelScope) при уходе с экрана.
 *
 * Фолбэк: VM выше навигации (например, в корне App или в TopAppBar) не имеют
 * [LocalViewModelStoreOwner] — для них берём инстанс из Koin и стабилизируем через remember
 * (без lifecycle-очистки, но и без пересоздания на каждой рекомпозиции; такие VM всё равно
 * живут всё время работы приложения).
 */
@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified T : ViewModel> koinViewModel(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T {
    val kClass = T::class
    val owner = LocalViewModelStoreOwner.current

    if (owner != null) {
        val factory = object : ViewModelProvider.Factory {
            override fun <VM : ViewModel> create(modelClass: KClass<VM>, extras: CreationExtras): VM {
                @Suppress("UNCHECKED_CAST")
                return KoinPlatformTools.defaultContext().get()
                    .get<T>(kClass, qualifier, parameters) as VM
            }
        }
        return ViewModelProvider.create(owner.viewModelStore, factory)[kClass]
    }

    return remember {
        KoinPlatformTools.defaultContext().get().get<T>(kClass, qualifier, parameters)
    }
}
