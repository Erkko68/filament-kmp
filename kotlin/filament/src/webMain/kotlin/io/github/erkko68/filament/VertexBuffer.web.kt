package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class VertexBuffer @InternalFilamentApi constructor(internal var nativeHandle: Int) {
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
            FilaVertexBufferBuilder_vertexCount(nativeBuilder, vertexCount)
            return this
        }
        actual fun bufferCount(bufferCount: Int): Builder {
            FilaVertexBufferBuilder_bufferCount(nativeBuilder, bufferCount)
            return this
        }
        actual fun enableBufferObjects(enabled: Boolean): Builder {
            FilaVertexBufferBuilder_enableBufferObjects(nativeBuilder, enabled)
            return this
        }
        actual fun attribute(attribute: VertexAttribute, bufferIndex: Int, attributeType: AttributeType, byteOffset: Int, byteStride: Int): Builder {
            FilaVertexBufferBuilder_attribute(
                nativeBuilder,
                attribute.ordinal,
                bufferIndex,
                attributeType.ordinal,
                byteOffset,
                byteStride
            )
            return this
        }
        actual fun normalized(attribute: VertexAttribute, enabled: Boolean): Builder {
            FilaVertexBufferBuilder_normalized(nativeBuilder, attribute.ordinal, enabled)
            return this
        }
        actual fun build(engine: Engine): VertexBuffer {
            val handle = FilaVertexBufferBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaVertexBufferBuilder_destroy(nativeBuilder)
            return VertexBuffer(handle)
        }
    }

    actual val vertexCount: Int get() = FilaVertexBuffer_getVertexCount(nativeHandle).toInt()
    
    actual fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray) {
        setBufferAt(engine, bufferIndex, data, 0, 0, null)
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray, destOffsetInBytes: Int, count: Int) {
        setBufferAt(engine, bufferIndex, data, destOffsetInBytes, count, null)
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)?) {
        val upload = fila.upload(data, if (count > 0) count else data.size, callback)
        FilaVertexBuffer_setBufferAt(nativeHandle, engine.nativeHandle, bufferIndex, upload.ptr, upload.size, destOffsetInBytes, 0, upload.callback, upload.userData)
    }

    actual fun setBufferObjectAt(engine: Engine, bufferIndex: Int, bufferObject: BufferObject) {
        FilaVertexBuffer_setBufferObjectAt(nativeHandle, engine.nativeHandle, bufferIndex, bufferObject.nativeHandle)
    }
}
