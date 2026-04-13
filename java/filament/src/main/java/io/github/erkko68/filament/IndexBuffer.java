package io.github.erkko68.filament;

import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.util.concurrent.Executor;

public class IndexBuffer {
    private long mNativeObject;

    private IndexBuffer(long nativeIndexBuffer) {
        mNativeObject = nativeIndexBuffer;
    }

    public static class Builder {
        private final long mNativeBuilder;

        public enum IndexType {
            USHORT,
            UINT
        }

        public Builder() {
            mNativeBuilder = nCreateBuilder();
        }

        public Builder indexCount(int indexCount) {
            nBuilderIndexCount(mNativeBuilder, indexCount);
            return this;
        }

        public Builder bufferType(IndexType indexType) {
            nBuilderBufferType(mNativeBuilder, indexType.ordinal());
            return this;
        }

        public IndexBuffer build(Engine engine) {
            long nativeIndexBuffer = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeIndexBuffer == 0) throw new IllegalStateException("Couldn't create IndexBuffer");
            return new IndexBuffer(nativeIndexBuffer);
        }
    }

    public int getIndexCount() {
        return nGetIndexCount(getNativeObject());
    }

    public void setBuffer(Engine engine, Buffer buffer) {
        setBuffer(engine, buffer, 0, 0, null, null);
    }

    public void setBuffer(Engine engine, Buffer buffer, int destOffsetInBytes, int count, Executor executor, Runnable callback) {
        int result = nSetBuffer(getNativeObject(), engine.getNativeObject(),
                buffer, buffer.remaining(),
                destOffsetInBytes, count == 0 ? buffer.remaining() : count, executor, callback);
        if (result < 0) {
            throw new BufferOverflowException();
        }
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed IndexBuffer");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderIndexCount(long nativeBuilder, int indexCount);
    private static native void nBuilderBufferType(long nativeBuilder, int indexType);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);

    private static native int nGetIndexCount(long nativeIndexBuffer);
    private static native int nSetBuffer(long nativeIndexBuffer, long nativeEngine, Buffer buffer, int remaining, int destOffsetInBytes, int count, Object handler, Runnable callback);
}
