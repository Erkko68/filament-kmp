@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
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

    actual fun getIndexCount(): Int = FilaIndexBuffer_getIndexCount(nativeHandle).toInt()
    
    actual fun setBuffer(engine: Engine, buffer: Any) {
        setBuffer(engine, buffer, 0, 0, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        setBuffer(engine, buffer, destOffsetInBytes, count, null, null)
    }

    private class BufferPinWrapper(val pinned: Pinned<*>, val callback: (() -> Unit)?)

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int, handler: Any?, callback: (() -> Unit)?) {
        var ptr: CPointer<out CPointed>? = null
        var sizeInBytes: ULong = 0.toULong()
        var pinned: Pinned<*>? = null

        when (buffer) {
            is FloatArray -> {
                pinned = buffer.pin()
                ptr = pinned.addressOf(0)
                sizeInBytes = (buffer.size * 4).toULong()
            }
            is ByteArray -> {
                pinned = buffer.pin()
                ptr = pinned.addressOf(0)
                sizeInBytes = buffer.size.toULong()
            }
            is ShortArray -> {
                pinned = buffer.pin()
                ptr = pinned.addressOf(0)
                sizeInBytes = (buffer.size * 2).toULong()
            }
            is IntArray -> {
                pinned = buffer.pin()
                ptr = pinned.addressOf(0)
                sizeInBytes = (buffer.size * 4).toULong()
            }
            is CPointer<*> -> {
                ptr = buffer
                sizeInBytes = 0.toULong()
            }
        }

        if (pinned == null && callback == null) {
            FilaIndexBuffer_setBuffer(nativeHandle, engine.nativeHandle, ptr, sizeInBytes, destOffsetInBytes.toUInt(), null, null, null)
        } else {
            val wrapper = BufferPinWrapper(pinned ?: ByteArray(0).pin(), callback)
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
}
