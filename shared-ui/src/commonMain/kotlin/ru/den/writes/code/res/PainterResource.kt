package ru.den.writes.code.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource

/**
 * Drop-in replacement for `org.jetbrains.compose.resources.painterResource`, used across the shared
 * UI so call sites stay canonical: `painterResource(Res.drawable.some_icon)`.
 *
 * Why it exists: the Aurora Compose fork's `components-resources` routes an Android Vector Drawable
 * (`.xml`) through Skia's SVGDOM (built for SVG), so `<vector android:pathData>` renders empty / a
 * blank screen. This polyfill keeps Android/iOS on the native painter and, on Aurora, resolves the
 * drawable's bytes itself (via the public `getDrawableResourceBytes`) and dispatches by content
 * signature — raster (PNG/JPEG/WebP/BMP) -> `BitmapPainter`, `<svg>` -> Skia SVGDOM, otherwise Android
 * vector XML -> `ImageVector` (for which `Icon(tint = …)` and intrinsic dp sizes work).
 *
 * The resource file is resolved from [resource] internally; no path string is needed at call sites.
 */
@Composable
expect fun painterResource(resource: DrawableResource): Painter
