package dev.filament.kmp

import com.google.android.filament.BufferObject as AndroidBufferObject
import java.nio.Buffer

actual class BufferObject internal constructor(val nativeBufferObject: AndroidBufferObject) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidBufferObject.Builder()

        actual enum class BindingType { VERTEX }

        actual fun size(byteCount: Int): Builder {
            nativeBuilder.size(byteCount)
            return this
        }
        actual fun bindingType(bindingType: BindingType): Builder {
            nativeBuilder.bindingType(AndroidBufferObject.Builder.BindingType.values()[bindingType.ordinal])
            return this
        }
        actual fun build(engine: Engine): BufferObject = BufferObject(nativeBuilder.build(engine.nativeEngine))
    }

    actual fun getByteCount(): Int = nativeBufferObject.byteCount
    actual fun setBuffer(engine: Engine, buffer: Any) {
        nativeBufferObject.setBuffer(engine.nativeEngine, buffer as Buffer)
    }
    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        nativeBufferObject.setBuffer(engine.nativeEngine, buffer as Buffer, destOffsetInBytes, count)
    }
}
