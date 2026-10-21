package io.github.erkko68.filament.compose.internal

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.nio.ByteBuffer

internal actual fun imageBitmapOf(width: Int, height: Int, rgba: ByteArray): ImageBitmap =
    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply { copyPixelsFromBuffer(ByteBuffer.wrap(rgba)) }.asImageBitmap()
