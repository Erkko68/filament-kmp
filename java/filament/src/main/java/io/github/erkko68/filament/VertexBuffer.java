package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NioUtils;

import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.util.concurrent.Executor;

public class VertexBuffer {
    private long mNativeObject;

    private VertexBuffer(long nativeVertexBuffer) {
        mNativeObject = nativeVertexBuffer;
    }

    public enum VertexAttribute {
        POSITION,
        TANGENTS,
        COLOR,
        UV0,
        UV1,
        BONE_INDICES,
        BONE_WEIGHTS,
        UNUSED,
        CUSTOM0,
        CUSTOM1,
        CUSTOM2,
        CUSTOM3,
        CUSTOM4,
        CUSTOM5,
        CUSTOM6,
        CUSTOM7
    }

    public enum AttributeType {
        BYTE, BYTE2, BYTE3, BYTE4,
        UBYTE, UBYTE2, UBYTE3, UBYTE4,
        SHORT, SHORT2, SHORT3, SHORT4,
        USHORT, USHORT2, USHORT3, USHORT4,
        INT, UINT,
        FLOAT, FLOAT2, FLOAT3, FLOAT4,
        HALF, HALF2, HALF3, HALF4
    }

    public static class Builder {
        private final long mNativeBuilder;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
        }

        public Builder vertexCount(int vertexCount) {
            nBuilderVertexCount(mNativeBuilder, vertexCount);
            return this;
        }

        public Builder enableBufferObjects(boolean enabled) {
            nBuilderEnableBufferObjects(mNativeBuilder, enabled);
            return this;
        }

        public Builder bufferCount(int bufferCount) {
            nBuilderBufferCount(mNativeBuilder, bufferCount);
            return this;
        }

        public Builder attribute(VertexAttribute attribute, int bufferIndex, AttributeType attributeType, int byteOffset, int byteStride) {
            nBuilderAttribute(mNativeBuilder, attribute.ordinal(), bufferIndex, attributeType.ordinal(), byteOffset, byteStride);
            return this;
        }

        public Builder attribute(VertexAttribute attribute, int bufferIndex, AttributeType attributeType) {
            return attribute(attribute, bufferIndex, attributeType, 0, 0);
        }

        public Builder normalized(VertexAttribute attribute, boolean enabled) {
            nBuilderNormalized(mNativeBuilder, attribute.ordinal(), enabled);
            return this;
        }

        public VertexBuffer build(Engine engine) {
            long nativeVertexBuffer = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeVertexBuffer == 0) throw new IllegalStateException("Couldn't create VertexBuffer");
            return new VertexBuffer(nativeVertexBuffer);
        }

        // Note: In a production app, use a Cleaner or Finalizer to nDestroyBuilder
    }

    public int getVertexCount() {
        return nGetVertexCount(getNativeObject());
    }

    public void setBufferAt(Engine engine, int bufferIndex, Buffer buffer) {
        setBufferAt(engine, bufferIndex, buffer, 0, 0, null, null);
    }

    public void setBufferAt(Engine engine, int bufferIndex, Buffer buffer, int destOffsetInBytes, int count, Executor executor, Runnable callback) {
        int result = nSetBufferAt(getNativeObject(), engine.getNativeObject(), bufferIndex,
                buffer, buffer.remaining(),
                destOffsetInBytes, count == 0 ? buffer.remaining() : count, executor, callback);
        if (result < 0) {
            throw new BufferOverflowException();
        }
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed VertexBuffer");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderVertexCount(long nativeBuilder, int vertexCount);
    private static native void nBuilderEnableBufferObjects(long nativeBuilder, boolean enabled);
    private static native void nBuilderBufferCount(long nativeBuilder, int bufferCount);
    private static native void nBuilderAttribute(long nativeBuilder, int attribute, int bufferIndex, int attributeType, int byteOffset, int byteStride);
    private static native void nBuilderNormalized(long nativeBuilder, int attribute, boolean normalized);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);

    private static native int nGetVertexCount(long nativeVertexBuffer);
    private static native int nSetBufferAt(long nativeVertexBuffer, long nativeEngine, int bufferIndex, Buffer buffer, int remaining, int destOffsetInBytes, int count, Object handler, Runnable callback);
}
