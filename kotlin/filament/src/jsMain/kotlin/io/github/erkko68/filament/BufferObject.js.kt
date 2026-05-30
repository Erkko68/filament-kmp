package io.github.erkko68.filament

import io.github.erkko68.filament.js.BufferObject_BindingType
import io.github.erkko68.filament.js.BufferObject as JSBufferObject

actual class BufferObject(internal val jsBufferObject: JSBufferObject) {
    actual val byteCount: Int get() = jsBufferObject.getByteCount().toInt()

    private fun ByteArray.toUint8Array(): org.khronos.webgl.Uint8Array {
        val int8 = org.khronos.webgl.Int8Array(size)
        forEachIndexed { i, b -> int8.asDynamic()[i] = b }
        return org.khronos.webgl.Uint8Array(int8.buffer)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray) {
        jsBufferObject.setBuffer(engine.jsEngine, data.toUint8Array())
    }

    actual fun setBuffer(
        engine: Engine,
        data: ByteArray,
        destOffsetInBytes: Int,
        count: Int
    ) {
        val clippedData = if (count < data.size) data.sliceArray(0 until count) else data
        jsBufferObject.setBuffer(engine.jsEngine, clippedData.toUint8Array(), destOffsetInBytes)
    }

    actual fun setBuffer(
        engine: Engine,
        data: ByteArray,
        destOffsetInBytes: Int,
        count: Int,
        callback: (() -> Unit)?
    ) {
        setBuffer(engine, data, destOffsetInBytes, count)
        callback?.invoke()
    }

    actual enum class BindingType { VERTEX, UNIFORM, SHADER_STORAGE }
    actual class Builder {
        private val jsBuilder = JSBufferObject.Builder()

        actual fun size(byteCount: Int): Builder {
            jsBuilder.size(byteCount)
            return this
        }

        actual fun bindingType(bindingType: BindingType): Builder {
            // JS bindings only support VERTEX; UNIFORM and SHADER_STORAGE are unsupported
            jsBuilder.bindingType(BufferObject_BindingType.VERTEX)
            return this
        }

        actual fun build(engine: Engine): BufferObject {
            // filament.js installs no `.build` wrapper on BufferObject$Builder, so call
            // the raw embind `_build` and delete the builder ourselves.
            val obj = jsBuilder._build(engine.jsEngine)
            jsBuilder.delete()
            return BufferObject(obj)
        }
    }
}