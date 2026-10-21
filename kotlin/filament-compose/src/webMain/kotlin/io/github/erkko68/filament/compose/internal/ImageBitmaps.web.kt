package io.github.erkko68.filament.compose.internal

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo

internal actual fun imageBitmapOf(width: Int, height: Int, rgba: ByteArray): ImageBitmap =
    Image.makeRaster(ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.PREMUL), rgba, width * 4).toComposeImageBitmap()
