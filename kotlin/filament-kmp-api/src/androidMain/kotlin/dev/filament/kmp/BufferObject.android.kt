package dev.filament.kmp

import java.nio.Buffer
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

    actual fun setBuffer(engine: Engine, buffer: Any) {
        if (buffer is Buffer) {
            nativeBufferObject.setBuffer(engine.nativeEngine, buffer)
        }
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        if (buffer is Buffer) {
            nativeBufferObject.setBuffer(engine.nativeEngine, buffer, destOffsetInBytes, count)
        }
    }
}
