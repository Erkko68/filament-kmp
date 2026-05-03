@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaIndexBuffer

actual class IndexBuffer internal constructor(internal var nativeHandle: CPointer<FilaIndexBuffer>?) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaIndexBufferBuilder_create()
        actual enum class IndexType { USHORT, UINT }
        actual fun indexCount(indexCount: Int): Builder = apply { FilaIndexBufferBuilder_indexCount(nativeBuilder, indexCount.toUInt()) }
        actual fun bufferType(indexType: IndexType): Builder = apply { 
            FilaIndexBufferBuilder_bufferType(nativeBuilder, indexType.ordinal.toUInt()) 
        }
        actual fun build(engine: Engine): IndexBuffer = IndexBuffer(FilaIndexBufferBuilder_build(nativeBuilder, engine.nativeHandle))
    }

    actual val indexCount: Int get() = FilaIndexBuffer_getIndexCount(nativeHandle).toInt()
    
    actual fun setBuffer(engine: Engine, data: ByteArray) {
        setBuffer(engine, data, 0, 0, null)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int) {
        setBuffer(engine, data, destOffsetInBytes, count, null)
    }

    private class BufferPinWrapper(val pinned: Pinned<*>, val callback: (() -> Unit)?)

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)?) {
        val pinned = data.pin()
        val ptr = pinned.addressOf(0).reinterpret<ByteVar>()
        val sizeInBytes = if (count > 0) count.toULong() else data.size.toULong()

        val wrapper = BufferPinWrapper(pinned, callback)
        val stableRef = StableRef.create(wrapper)
        val callbackWrapper = staticCFunction { _: COpaquePointer?, _: ULong, user: COpaquePointer? ->
            val ref = user!!.asStableRef<BufferPinWrapper>()
            val wrap = ref.get()
            wrap.callback?.invoke()
            wrap.pinned.unpin()
            ref.dispose()
        }
        FilaIndexBuffer_setBuffer(nativeHandle, engine.nativeHandle, ptr, sizeInBytes, destOffsetInBytes.toUInt(), null, callbackWrapper, stableRef.asCPointer())
    }
}
