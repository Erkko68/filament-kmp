package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.jni.*
import io.github.erkko68.filament.nativeObject

actual object HDRLoader {
    actual fun createTexture(engine: Engine, buffer: ByteArray, internalFormat: Texture.InternalFormat): Texture? {
        val handle = buffer.usePinned { pinned ->
            FilaHDRLoader_createTexture(
                engine.nativeObject,
                pinned,
                buffer.size.toLong(),
                internalFormat.ordinal
            )
        }
        return handle.takeIf { it != 0L }?.let { Texture(it) }
    }
}
