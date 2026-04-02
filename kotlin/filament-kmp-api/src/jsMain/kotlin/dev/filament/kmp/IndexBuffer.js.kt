package dev.filament.kmp

actual class IndexBuffer {
    actual fun getIndexCount(): Int = TODO("Not yet implemented")

    actual fun setBuffer(engine: Engine, buffer: Any) {
        TODO("Not yet implemented")
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        TODO("Not yet implemented")
    }

    actual fun setBuffer(
        engine: Engine,
        buffer: Any,
        destOffsetInBytes: Int,
        count: Int,
        handler: Any?,
        callback: (() -> Unit)?,
    ) {
        TODO("Not yet implemented")
    }

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }

    actual class Builder {
        actual fun indexCount(indexCount: Int): Builder = TODO("Not yet implemented")

        actual fun bufferType(indexType: IndexType): Builder = TODO("Not yet implemented")

        actual fun build(engine: Engine): IndexBuffer = TODO("Not yet implemented")

        actual enum class IndexType {
            USHORT,
            UINT,
        }
    }
}

