package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import com.google.android.filament.utils.HDRLoader as AndroidHDRLoader

actual object HDRLoader {
    actual fun createTexture(engine: Engine, buffer: ByteArray, internalFormat: Texture.InternalFormat): Texture? {
        val byteBuffer = ByteBuffer.allocateDirect(buffer.size).apply {
            order(ByteOrder.nativeOrder())
            put(buffer)
            rewind()
        }
        val androidTexture = AndroidHDRLoader.createTexture(engine.nativeEngine, byteBuffer)
        return androidTexture?.let { Texture(it) }
    }

    init {
        Utils.init()
    }
}
