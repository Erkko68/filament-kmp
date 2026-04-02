package dev.filament.kmp

actual class BufferObject {
    actual fun getByteCount(): Int = TODO("Not yet implemented")

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
        actual fun size(byteCount: Int): Builder = TODO("Not yet implemented")

        actual fun bindingType(bindingType: BindingType): Builder = TODO("Not yet implemented")

        actual fun build(engine: Engine): BufferObject = TODO("Not yet implemented")

        actual enum class BindingType {
            VERTEX,
        }
    }
}

