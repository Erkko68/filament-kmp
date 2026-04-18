package io.github.erkko68.filament

import io.github.erkko68.filament.js.BufferObject as JSBufferObject
import io.github.erkko68.filament.js.`BufferObject_Builder` as JSBufferObjectBuilder
import io.github.erkko68.filament.js.BufferObject_BindingType

actual class BufferObject(internal val jsBufferObject: JSBufferObject) {
    actual fun getByteCount(): Int {
        return jsBufferObject.getByteCount().toInt()
    }

    actual fun setBuffer(engine: Engine, data: ByteArray) {
        jsBufferObject.setBuffer(engine.jsEngine, data.asDynamic(), 0)
    }

    actual fun setBuffer(
        engine: Engine,
        data: ByteArray,
        destOffsetInBytes: Int,
        count: Int
    ) {
        val clippedData = if (count < data.size) data.sliceArray(0 until count) else data
        jsBufferObject.setBuffer(engine.jsEngine, clippedData.asDynamic(), destOffsetInBytes)
    }

    actual fun setBuffer(
        engine: Engine,
        data: ByteArray,
        destOffsetInBytes: Int,
        count: Int,
        handler: Any?,
        callback: (() -> Unit)?
    ) {
        setBuffer(engine, data, destOffsetInBytes, count)
        callback?.invoke()
    }

    actual enum class BindingType { VERTEX, UNIFORM, SHADER_STORAGE }
    actual class Builder {
        private val jsBuilder = JSBufferObjectBuilder()

        actual fun size(byteCount: Int): Builder {
            jsBuilder.size(byteCount)
            return this
        }

        actual fun bindingType(bindingType: BindingType): Builder {
            jsBuilder.bindingType(when (bindingType) {
                BindingType.VERTEX -> BufferObject_BindingType.VERTEX
                BindingType.UNIFORM -> BufferObject_BindingType.UNIFORM
                BindingType.SHADER_STORAGE -> BufferObject_BindingType.SHADER_STORAGE
            })
            return this
        }

        actual fun build(engine: Engine): BufferObject {
            return BufferObject(jsBuilder.build(engine.jsEngine))
        }
    }
}