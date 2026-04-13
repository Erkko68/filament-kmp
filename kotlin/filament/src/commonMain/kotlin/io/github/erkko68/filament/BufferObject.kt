package io.github.erkko68.filament

expect class BufferObject {
    enum class BindingType {
        VERTEX,
        UNIFORM,
        SHADER_STORAGE,
    }

    class Builder() {
        fun size(byteCount: Int): Builder
        fun bindingType(bindingType: BindingType): Builder
        fun build(engine: Engine): BufferObject
    }

    fun getByteCount(): Int
    fun setBuffer(engine: Engine, data: ByteArray)
    fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int)
    fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int, handler: Any? = null, callback: (() -> Unit)? = null)
}
