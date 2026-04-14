@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.cinterop.*
import kotlinx.cinterop.*

actual object TextureLoader {
    actual enum class TextureType {
        COLOR,
        NORMAL,
        DATA
    }

    actual fun loadTexture(engine: Engine, buffer: ByteArray, type: TextureType): Texture? {
        val handle = buffer.usePinned { pinned ->
            FilaTextureLoader_loadTexture(
                engine.nativeHandle,
                pinned.addressOf(0),
                buffer.size.toULong(),
                type == TextureType.COLOR // sRGB if COLOR
            )
        }
        return handle?.let { Texture(it) }
    }
}
