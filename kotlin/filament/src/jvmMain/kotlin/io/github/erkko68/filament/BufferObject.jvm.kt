package io.github.erkko68.filament

import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class BufferObject(val nativeBufferObject: io.github.erkko68.filament.jni.BufferObject) {
    actual enum class BindingType {
        VERTEX, UNIFORM, SHADER_STORAGE;
        internal fun toJni(): io.github.erkko68.filament.jni.BufferObject.Builder.BindingType = when (this) {
            VERTEX -> io.github.erkko68.filament.jni.BufferObject.Builder.BindingType.VERTEX
            // JVM JNI bindings currently expose only VERTEX.
            else -> throw UnsupportedOperationException("BindingType $this is not supported by JVM JNI BufferObject.Builder")
        }
    }

    actual class Builder actual constructor() {
        private val jni = io.github.erkko68.filament.jni.BufferObject.Builder()

        actual fun size(byteCount: Int): Builder {
            jni.size(byteCount)
            return this
        }

        actual fun bindingType(bindingType: BindingType): Builder {
            jni.bindingType(bindingType.toJni())
            return this
        }

        actual fun build(engine: Engine): BufferObject = 
            BufferObject(jni.build(engine.nativeEngine))
    }

    actual fun getByteCount(): Int = nativeBufferObject.byteCount

    actual fun setBuffer(engine: Engine, data: ByteArray) {
        val buffer = ByteBuffer.allocateDirect(data.size).order(ByteOrder.nativeOrder())
        buffer.put(data)
        buffer.rewind()
        nativeBufferObject.setBuffer(engine.nativeEngine, buffer)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int) {
        val buffer = ByteBuffer.allocateDirect(data.size).order(ByteOrder.nativeOrder())
        buffer.put(data)
        buffer.rewind()
        nativeBufferObject.setBuffer(engine.nativeEngine, buffer, destOffsetInBytes, count)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)?) {
        val buffer = ByteBuffer.allocateDirect(data.size).order(ByteOrder.nativeOrder())
        buffer.put(data)
        buffer.rewind()
        val runnable: java.lang.Runnable? = if (callback != null) Runnable { callback() } else null
        nativeBufferObject.setBuffer(engine.nativeEngine, buffer, destOffsetInBytes, count, null, runnable)
    }
}
