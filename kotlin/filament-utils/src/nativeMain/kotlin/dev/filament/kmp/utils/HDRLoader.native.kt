@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp.utils

import dev.filament.kmp.Engine
import dev.filament.kmp.Texture
import dev.filament.kmp.utils.cinterop.*
import kotlinx.cinterop.*

actual object HDRLoader {
    actual fun createTexture(engine: Engine, buffer: ByteArray, internalFormat: Texture.InternalFormat): Texture? {
        val handle = buffer.usePinned { pinned ->
            FilaHDRLoader_createTexture(
                engine.nativeHandle,
                pinned.addressOf(0),
                buffer.size.toULong(),
                internalFormat.ordinal
            )
        }
        return handle?.let { Texture(it) }
    }
}
