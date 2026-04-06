package dev.filament.kmp

expect class BufferObject {
    class Builder() {
        enum class BindingType { VERTEX }
        fun size(byteCount: Int): Builder
        fun bindingType(bindingType: BindingType): Builder
        fun build(engine: Engine): BufferObject
    }

    fun getByteCount(): Int
    fun setBuffer(engine: Engine, buffer: Any)
    fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int)
}
