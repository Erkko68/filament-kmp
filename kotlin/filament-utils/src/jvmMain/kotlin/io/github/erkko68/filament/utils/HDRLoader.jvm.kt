package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.bytes
import io.github.erkko68.filament.confined
import io.github.erkko68.filament.ffm.FilamentC

actual object HDRLoader {
    actual fun createTexture(engine: Engine, buffer: ByteArray, internalFormat: Texture.InternalFormat): Texture? = confined { a ->
        val handle = FilamentC.FilaHDRLoader_createTexture(
            engine.nativeHandle, a.bytes(buffer), buffer.size.toLong(), internalFormat.ordinal,
        )
        handle?.let { Texture(it) }
    }
}
