package ru.den.writes.code.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource as composePainterResource

// iOS (skiko) parses Android vector XML natively — delegate to the standard painter.
@Composable
actual fun painterResource(resource: DrawableResource): Painter = composePainterResource(resource)
