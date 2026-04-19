package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.jni.HDRLoader as JniHDRLoader
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual object HDRLoader {
    actual fun createTexture(engine: Engine, buffer: ByteArray, internalFormat: Texture.InternalFormat): Texture? {
        val byteBuffer = ByteBuffer.allocateDirect(buffer.size).order(ByteOrder.nativeOrder())
        byteBuffer.put(buffer)
        byteBuffer.rewind()
        
        val jniTexture = JniHDRLoader.createTexture(engine.nativeEngine, byteBuffer, io.github.erkko68.filament.jni.Texture.InternalFormat.values()[internalFormat.ordinal])
        return jniTexture?.let { Texture(it) }
    }
}
