package org.jetbrains.compose.resources

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import org.jetbrains.compose.resources.vector.xmldom.parse

internal actual class SvgElement

internal actual fun SvgElement.toSvgPainter(density: Density): Painter {
    TODO("Not yet implemented")
}

internal actual fun ByteArray.toImageBitmap(
    resourceDensity: Int,
    targetDensity: Int
): ImageBitmap {
    TODO("Not yet implemented")
}

internal actual fun ByteArray.toXmlElement() = parse(decodeToString())

internal actual fun ByteArray.toSvgElement(): SvgElement {
    TODO("Not yet implemented")
}