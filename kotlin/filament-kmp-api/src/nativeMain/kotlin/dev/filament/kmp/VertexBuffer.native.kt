package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class VertexBuffer internal constructor(
    internal val nativeObject: CPointer<FilaVertexBuffer>
) {
    actual enum class Attribute {
        POSITION, TANGENTS, COLOR, UV0, UV1, BONE_INDICES, BONE_WEIGHTS,
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4, CUSTOM5, CUSTOM6, CUSTOM7;

        internal fun toNative(): FilaVertexAttribute = when (this) {
            POSITION -> FILA_VERTEX_ATTRIBUTE_POSITION
            TANGENTS -> FILA_VERTEX_ATTRIBUTE_TANGENTS
            COLOR -> FILA_VERTEX_ATTRIBUTE_COLOR
            UV0 -> FILA_VERTEX_ATTRIBUTE_UV0
            UV1 -> FILA_VERTEX_ATTRIBUTE_UV1
            BONE_INDICES -> FILA_VERTEX_ATTRIBUTE_BONE_INDICES
            BONE_WEIGHTS -> FILA_VERTEX_ATTRIBUTE_BONE_WEIGHTS
            CUSTOM0 -> FILA_VERTEX_ATTRIBUTE_CUSTOM0
            CUSTOM1 -> FILA_VERTEX_ATTRIBUTE_CUSTOM1
            CUSTOM2 -> FILA_VERTEX_ATTRIBUTE_CUSTOM2
            CUSTOM3 -> FILA_VERTEX_ATTRIBUTE_CUSTOM3
            CUSTOM4 -> FILA_VERTEX_ATTRIBUTE_CUSTOM4
            CUSTOM5 -> FILA_VERTEX_ATTRIBUTE_CUSTOM5
            CUSTOM6 -> FILA_VERTEX_ATTRIBUTE_CUSTOM6
            CUSTOM7 -> FILA_VERTEX_ATTRIBUTE_CUSTOM7
        }
    }

    actual enum class AttributeType {
        BYTE, BYTE2, BYTE3, BYTE4,
        UBYTE, UBYTE2, UBYTE3, UBYTE4,
        SHORT, SHORT2, SHORT3, SHORT4,
        USHORT, USHORT2, USHORT3, USHORT4,
        INT, UINT,
        FLOAT, FLOAT2, FLOAT3, FLOAT4,
        HALF, HALF2, HALF3, HALF4;

        internal fun toNative(): FilaAttributeType = when (this) {
            BYTE -> FILA_ATTRIBUTE_TYPE_BYTE
            BYTE2 -> FILA_ATTRIBUTE_TYPE_BYTE2
            BYTE3 -> FILA_ATTRIBUTE_TYPE_BYTE3
            BYTE4 -> FILA_ATTRIBUTE_TYPE_BYTE4
            UBYTE -> FILA_ATTRIBUTE_TYPE_UBYTE
            UBYTE2 -> FILA_ATTRIBUTE_TYPE_UBYTE2
            UBYTE3 -> FILA_ATTRIBUTE_TYPE_UBYTE3
            UBYTE4 -> FILA_ATTRIBUTE_TYPE_UBYTE4
            SHORT -> FILA_ATTRIBUTE_TYPE_SHORT
            SHORT2 -> FILA_ATTRIBUTE_TYPE_SHORT2
            SHORT3 -> FILA_ATTRIBUTE_TYPE_SHORT3
            SHORT4 -> FILA_ATTRIBUTE_TYPE_SHORT4
            USHORT -> FILA_ATTRIBUTE_TYPE_USHORT
            USHORT2 -> FILA_ATTRIBUTE_TYPE_USHORT2
            USHORT3 -> FILA_ATTRIBUTE_TYPE_USHORT3
            USHORT4 -> FILA_ATTRIBUTE_TYPE_USHORT4
            INT -> FILA_ATTRIBUTE_TYPE_INT
            UINT -> FILA_ATTRIBUTE_TYPE_UINT
            FLOAT -> FILA_ATTRIBUTE_TYPE_FLOAT
            FLOAT2 -> FILA_ATTRIBUTE_TYPE_FLOAT2
            FLOAT3 -> FILA_ATTRIBUTE_TYPE_FLOAT3
            FLOAT4 -> FILA_ATTRIBUTE_TYPE_FLOAT4
            HALF -> FILA_ATTRIBUTE_TYPE_HALF
            HALF2 -> FILA_ATTRIBUTE_TYPE_HALF2
            HALF3 -> FILA_ATTRIBUTE_TYPE_HALF3
            HALF4 -> FILA_ATTRIBUTE_TYPE_HALF4
        }
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaVertexBufferBuilder_create()!!

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

        actual fun attribute(attribute: Attribute, bufferIndex: Int, attributeType: AttributeType, byteOffset: Int, byteStride: Int): Builder {
            FilaVertexBufferBuilder_attribute(nativeBuilder, attribute.toNative(), bufferIndex.toUByte(), attributeType.toNative(), byteOffset.toUInt(), byteStride.toUByte())
            return this
        }

        actual fun normalized(attribute: Attribute): Builder {
            FilaVertexBufferBuilder_normalized(nativeBuilder, attribute.toNative(), true)
            return this
        }

        actual fun build(engine: Engine): VertexBuffer {
            val nativeVertexBuffer = FilaVertexBufferBuilder_build(nativeBuilder, engine.nativeObject)
                ?: throw IllegalStateException("Couldn't create VertexBuffer")
            FilaVertexBufferBuilder_destroy(nativeBuilder)
            return VertexBuffer(nativeVertexBuffer)
        }
    }

    actual fun getVertexCount(): Int = FilaVertexBuffer_getVertexCount(nativeObject).toInt()

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Buffer) {
        setBufferAt(engine, bufferIndex, buffer, 0, 0, null, null)
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Buffer, destOffsetInBytes: Int, count: Int) {
        setBufferAt(engine, bufferIndex, buffer, destOffsetInBytes, count, null, null)
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Buffer, destOffsetInBytes: Int, count: Int, handler: Any?, callback: Runnable?) {
        val bufferPtr = buffer.nativePointer() ?: return
        val sizeInBytes = if (count > 0) count * buffer.elementSize() else (buffer.limit() - buffer.position()) * buffer.elementSize()
        
        // Handling callbacks in C-wrapper requires a helper that manages the Runnable.
        // For now, we pass null if no callback is provided.
        FilaVertexBuffer_setBufferAt(nativeObject, engine.nativeObject, bufferIndex.toUByte(), bufferPtr, sizeInBytes.toULong(), destOffsetInBytes.toUInt(), null, null, null)
    }

    actual fun setBufferObjectAt(engine: Engine, bufferIndex: Int, bufferObject: BufferObject) {
        FilaVertexBuffer_setBufferObjectAt(nativeObject, engine.nativeObject, bufferIndex.toUByte(), bufferObject.nativeObject)
    }
}
