package io.github.erkko68.filament

import com.google.android.filament.BufferObject as AndroidBufferObject

actual class BufferObject internal constructor(val nativeBufferObject: AndroidBufferObject) {
    actual enum class BindingType {
        VERTEX,
        UNIFORM,
        SHADER_STORAGE;
        internal fun toNative(): AndroidBufferObject.Builder.BindingType {
            return when (this) {
                VERTEX -> AndroidBufferObject.Builder.BindingType.VERTEX
                else -> AndroidBufferObject.Builder.BindingType.VERTEX // Android only supports VERTEX for now
            }
        }
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidBufferObject.Builder()

        actual fun size(byteCount: Int): Builder {
            nativeBuilder.size(byteCount)
            return this
        }

        actual fun bindingType(bindingType: BindingType): Builder {
            nativeBuilder.bindingType(bindingType.toNative())
            return this
        }

        actual fun build(engine: Engine): BufferObject {
            return BufferObject(nativeBuilder.build(engine.nativeEngine))
        }
    }

    actual fun getByteCount(): Int = nativeBufferObject.byteCount

    actual fun setBuffer(engine: Engine, data: ByteArray) {
        val byteBuffer = java.nio.ByteBuffer.allocateDirect(data.size).apply {
            order(java.nio.ByteOrder.nativeOrder())
            put(data)
            flip()
        }
        nativeBufferObject.setBuffer(engine.nativeEngine, byteBuffer)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int) {
        val byteBuffer = java.nio.ByteBuffer.allocateDirect(data.size).apply {
            order(java.nio.ByteOrder.nativeOrder())
            put(data)
            flip()
        }
        nativeBufferObject.setBuffer(engine.nativeEngine, byteBuffer, destOffsetInBytes, count)
    }

    actual fun setBuffer(
        engine: Engine,
        data: ByteArray,
        destOffsetInBytes: Int,
        count: Int,
        callback: (() -> Unit)?
    ) {
        val runnable = if (callback != null) Runnable { callback() } else null
        val byteBuffer = java.nio.ByteBuffer.allocateDirect(data.size).apply {
            order(java.nio.ByteOrder.nativeOrder())
            put(data)
            flip()
        }
        nativeBufferObject.setBuffer(
            engine.nativeEngine,
            byteBuffer,
            destOffsetInBytes,
            count,
            Runnable::run,
            runnable
        )
    }
}
