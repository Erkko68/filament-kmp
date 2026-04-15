package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.jni.TextureLoader as JniTextureLoader
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual object TextureLoader {
    actual enum class TextureType {
        COLOR, NORMAL, DATA;
        internal fun toJni() = JniTextureLoader.TextureType.values()[ordinal]
    }

    actual fun loadTexture(engine: Engine, buffer: ByteArray, type: TextureType): Texture? {
        val byteBuffer = ByteBuffer.allocateDirect(buffer.size).order(ByteOrder.nativeOrder())
        byteBuffer.put(buffer)
        byteBuffer.rewind()
        
        val jniTexture = JniTextureLoader.loadTexture(engine.nativeEngine.getNativeObject(), byteBuffer, type.toJni().ordinal)
        return jniTexture.takeIf { it != 0L }?.let { Texture(io.github.erkko68.filament.jni.Texture(it)) }
    }
}
