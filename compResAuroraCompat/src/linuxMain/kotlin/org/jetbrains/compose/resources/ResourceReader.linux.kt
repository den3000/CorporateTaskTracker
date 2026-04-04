package org.jetbrains.compose.resources

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal

internal actual fun getPlatformResourceReader(): ResourceReader {
    TODO("Not yet implemented")
}

internal actual val ProvidableCompositionLocal<ResourceReader>.currentOrPreview: ResourceReader
    @Composable
    get() = TODO("Not yet implemented")