@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaVertexBuffer

actual class VertexBuffer internal constructor(internal var nativeHandle: CPointer<FilaVertexBuffer>?) {
    actual enum class VertexAttribute {
        POSITION, TANGENTS, COLOR, UV0, UV1, BONE_INDICES, BONE_WEIGHTS, UNUSED,
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4, CUSTOM5, CUSTOM6, CUSTOM7
    }

    actual enum class AttributeType {
        BYTE, BYTE2, BYTE3, BYTE4,
        UBYTE, UBYTE2, UBYTE3, UBYTE4,
        SHORT, SHORT2, SHORT3, SHORT4,
        USHORT, USHORT2, USHORT3, USHORT4,
        INT, UINT,
        FLOAT, FLOAT2, FLOAT3, FLOAT4,
        HALF, HALF2, HALF3, HALF4
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaVertexBufferBuilder_create()

        actual fun vertexCount(vertexCount: Int): Builder {
            FilaVertexBufferBuilder_vertexCount(nativeBuilder, vertexCount.toUInt())
            return this
        }
        actual fun bufferCount(bufferCount: Int): Builder {
            FilaVertexBufferBuilder_bufferCount(nativeBuilder, bufferCount.toUByte())
            return this
        }
        actual fun enableBufferObjects(enabled: Boolean): Builder {
            FilaVertexBufferBuilder_enableBufferObjects(nativeBuilder, enabled)
            return this
        }
        actual fun attribute(attribute: VertexAttribute, bufferIndex: Int, attributeType: AttributeType, byteOffset: Int, byteStride: Int): Builder {
            FilaVertexBufferBuilder_attribute(
                nativeBuilder,
                attribute.ordinal.toUInt(),
                bufferIndex.toUByte(),
                attributeType.ordinal.toUInt(),
                byteOffset.toUInt(),
                byteStride.toUByte()
            )
            return this
        }
        actual fun normalized(attribute: VertexAttribute, enabled: Boolean): Builder {
            FilaVertexBufferBuilder_normalized(nativeBuilder, attribute.ordinal.toUInt(), enabled)
            return this
        }
        actual fun build(engine: Engine): VertexBuffer {
            val handle = FilaVertexBufferBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaVertexBufferBuilder_destroy(nativeBuilder)
            return VertexBuffer(handle)
        }
    }

    actual fun getVertexCount(): Int = FilaVertexBuffer_getVertexCount(nativeHandle).toInt()
    
    actual fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray) {
        setBufferAt(engine, bufferIndex, data, 0, 0, null)
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray, destOffsetInBytes: Int, count: Int) {
        setBufferAt(engine, bufferIndex, data, destOffsetInBytes, count, null)
    }

    private class BufferPinWrapper(val pinned: Pinned<*>, val callback: (() -> Unit)?)

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)?) {
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
        FilaVertexBuffer_setBufferAt(nativeHandle, engine.nativeHandle, bufferIndex.toUByte(), ptr, sizeInBytes, destOffsetInBytes.toUInt(), null, callbackWrapper, stableRef.asCPointer())
    }

    actual fun setBufferObjectAt(engine: Engine, bufferIndex: Int, bufferObject: BufferObject) {
        FilaVertexBuffer_setBufferObjectAt(nativeHandle, engine.nativeHandle, bufferIndex.toUByte(), bufferObject.nativeHandle)
    }
}
