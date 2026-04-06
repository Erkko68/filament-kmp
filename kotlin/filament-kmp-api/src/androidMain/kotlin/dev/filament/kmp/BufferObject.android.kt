package dev.filament.kmp

import com.google.android.filament.BufferObject as AndroidBufferObject
import java.nio.Buffer

actual class BufferObject internal constructor(
    internal var androidBufferObject: AndroidBufferObject?,
) {
    actual fun getByteCount(): Int {
        val bufferObject = requireNotNull(androidBufferObject) { "Calling method on destroyed BufferObject" }
        return bufferObject.byteCount
    }

    actual fun setBuffer(engine: Engine, buffer: Any) {
        setBuffer(engine, buffer, 0, 0, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        setBuffer(engine, buffer, destOffsetInBytes, count, null, null)
    }

    actual fun setBuffer(
        engine: Engine,
        buffer: Any,
        destOffsetInBytes: Int,
        count: Int,
        handler: Any?,
        callback: (() -> Unit)?,
    ) {
        val bufferObject = requireNotNull(androidBufferObject) { "Calling method on destroyed BufferObject" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        bufferObject.setBuffer(androidEngine, buffer as Buffer, destOffsetInBytes, count, handler, callback?.let { Runnable { it() } })
    }

    actual val nativeObject: Long
        get() {
        val bufferObject = requireNotNull(androidBufferObject) { "Calling method on destroyed BufferObject" }
        return bufferObject.nativeObject
    }

    actual internal fun invalidate() {
        androidBufferObject = null
    }

    actual class Builder {
        private val androidBuilder = AndroidBufferObject.Builder()

        actual fun size(byteCount: Int): Builder {
            androidBuilder.size(byteCount)
            return this
        }

        actual fun bindingType(bindingType: BindingType): Builder {
            androidBuilder.bindingType(bindingType.toAndroid())
            return this
        }

        actual fun build(engine: Engine): BufferObject {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return BufferObject(androidBuilder.build(androidEngine))
        }

        actual enum class BindingType {
            VERTEX,
        }
    }
}

private fun BufferObject.Builder.BindingType.toAndroid(): AndroidBufferObject.Builder.BindingType = when (this) {
    BufferObject.Builder.BindingType.VERTEX -> AndroidBufferObject.Builder.BindingType.VERTEX
}

