@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.utils

import io.github.erkko68.Engine
import io.github.erkko68.Texture
import io.github.erkko68.utils.cinterop.*
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
