package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.wasm.*
import io.github.erkko68.filament.nativeObject

actual object TextureLoader {
    actual enum class TextureType {
        COLOR,
        NORMAL,
        DATA
    }

    actual fun loadTexture(engine: Engine, buffer: ByteArray, type: TextureType): Texture? {
        val handle = buffer.usePinned { pinned ->
            FilaTextureLoader_loadTexture(
                engine.nativeObject,
                pinned,
                buffer.size,
                type == TextureType.COLOR // sRGB if COLOR
            )
        }
        return handle.takeIf { it != 0 }?.let { Texture(it) }
    }
}
