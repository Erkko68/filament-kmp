package io.github.erkko68.filament.compose.internal

import androidx.compose.ui.graphics.ImageBitmap

/** An image of [rgba]'s premultiplied RGBA8 pixels, rows top-down. */
internal expect fun imageBitmapOf(width: Int, height: Int, rgba: ByteArray): ImageBitmap
