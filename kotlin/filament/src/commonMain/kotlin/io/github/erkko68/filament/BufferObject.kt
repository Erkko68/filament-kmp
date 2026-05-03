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

    val byteCount: Int
    fun setBuffer(engine: Engine, data: ByteArray)
    fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int)
    fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)? = null)
}
