@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
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
    
    actual fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Any) {
        setBufferAt(engine, bufferIndex, buffer, 0, 0, null, null)
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Any, destOffsetInBytes: Int, count: Int) {
        setBufferAt(engine, bufferIndex, buffer, destOffsetInBytes, count, null, null)
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Any, destOffsetInBytes: Int, count: Int, handler: Any?, callback: (() -> Unit)?) {
        val ptr = buffer as? CPointer<*>
        // Callback bridging not yet implemented for Native
        FilaVertexBuffer_setBufferAt(nativeHandle, engine.nativeHandle, bufferIndex.toUByte(), ptr, 0.toULong(), destOffsetInBytes.toUInt(), null, null, null)
    }

    actual fun setBufferObjectAt(engine: Engine, bufferIndex: Int, bufferObject: BufferObject) {
        FilaVertexBuffer_setBufferObjectAt(nativeHandle, engine.nativeHandle, bufferIndex.toUByte(), bufferObject.nativeHandle)
    }
}
