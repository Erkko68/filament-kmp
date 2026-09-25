package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.wasm.*
import io.github.erkko68.filament.nativeObject

actual object HDRLoader {
    actual fun createTexture(engine: Engine, buffer: ByteArray, internalFormat: Texture.InternalFormat): Texture? {
        val handle = buffer.usePinned { pinned ->
            FilaHDRLoader_createTexture(
                engine.nativeObject,
                pinned,
                buffer.size,
                internalFormat.ordinal
            )
        }
        return handle.takeIf { it != 0 }?.let { Texture(it) }
    }
}
