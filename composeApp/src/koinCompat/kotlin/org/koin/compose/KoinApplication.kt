package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import org.koin.compose.application.CompositionKoinApplicationLoader
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.scope.Scope
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication
import org.koin.mp.KoinPlatform


@OptIn(KoinInternalApi::class)
@Composable
fun KoinApplication(
    application: KoinAppDeclaration, //Better to directly use KoinConfiguration class
    content: @Composable () -> Unit
) {
    val koin = rememberKoinApplication(application)
    CompositionLocalProvider(
        LocalKoinApplication provides ComposeContextWrapper(koin) { getDefaultKoinContext() },
        LocalKoinScope provides ComposeContextWrapper(koin.scopeRegistry.rootScope) { getDefaultRootScope() },
        content = content
    )
}

@Composable
@KoinInternalApi
inline fun rememberKoinApplication(noinline koinAppDeclaration: KoinAppDeclaration): Koin {
    val wrapper = remember(koinAppDeclaration) {
        CompositionKoinApplicationLoader(koinApplication(koinAppDeclaration))
    }
    return wrapper.koin ?: error("Koin context has not been initialized in rememberKoinApplication")
}

@OptIn(KoinInternalApi::class)
val LocalKoinApplication: ProvidableCompositionLocal<ComposeContextWrapper<Koin>> =
    compositionLocalOf { ComposeContextWrapper(getDefaultKoinContext()) { getDefaultKoinContext() } }

@OptIn(KoinInternalApi::class)
val LocalKoinScope: ProvidableCompositionLocal<ComposeContextWrapper<Scope>> =
    compositionLocalOf { ComposeContextWrapper(getDefaultRootScope()) { getDefaultRootScope() } }

private fun getDefaultKoinContext() = KoinPlatform.getKoin()

@OptIn(KoinInternalApi::class)
private fun getDefaultRootScope() = KoinPlatform.getKoin().scopeRegistry.rootScope