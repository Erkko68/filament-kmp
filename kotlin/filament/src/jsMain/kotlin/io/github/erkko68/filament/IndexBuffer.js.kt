package io.github.erkko68.filament

actual class IndexBuffer {
    actual fun getIndexCount(): Int {
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

    actual class Builder {
        actual fun indexCount(indexCount: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun bufferType(indexType: IndexType): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(engine: Engine): IndexBuffer {
            TODO("Not yet implemented")
        }

        actual enum class IndexType { USHORT, UINT }
    }
}