package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import java.nio.ByteBuffer

actual object TextureLoader {
    actual enum class TextureType {
        COLOR,
        NORMAL,
        DATA
    }

    actual fun loadTexture(engine: Engine, buffer: ByteArray, type: TextureType): Texture? {
        val googleType = com.google.android.filament.utils.TextureLoader.TextureType.values()[type.ordinal]
        val byteBuffer = ByteBuffer.wrap(buffer)
        return com.google.android.filament.utils.TextureLoader.loadTexture(
            engine.nativeEngine,
            byteBuffer,
            googleType
        )?.let { Texture(it) }
    }
}
