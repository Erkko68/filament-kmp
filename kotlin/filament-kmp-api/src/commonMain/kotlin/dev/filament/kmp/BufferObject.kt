package dev.filament.kmp

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
    fun setBuffer(engine: Engine, buffer: Any)
    fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int)
    fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int, handler: Any? = null, callback: (() -> Unit)? = null)
}
