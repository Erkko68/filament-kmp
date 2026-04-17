package io.github.erkko68.filament

actual class BufferObject {
    actual fun getByteCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun setBuffer(engine: Engine, data: ByteArray) {
    }

    actual fun setBuffer(
        engine: Engine,
        data: ByteArray,
        destOffsetInBytes: Int,
        count: Int
    ) {
    }

    actual fun setBuffer(
        engine: Engine,
        data: ByteArray,
        destOffsetInBytes: Int,
        count: Int,
        handler: Any?,
        callback: (() -> Unit)?
    ) {
    }

    actual enum class BindingType { VERTEX, UNIFORM, SHADER_STORAGE }
    actual class Builder {
        actual fun size(byteCount: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun bindingType(bindingType: BindingType): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(engine: Engine): BufferObject {
            TODO("Not yet implemented")
        }
    }
}