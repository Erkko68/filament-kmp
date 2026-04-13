package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NativeRegistry;
import java.lang.ref.Cleaner;
import java.nio.Buffer;

public class BufferObject {
    private long mNativeObject;

    private BufferObject(long nativeBufferObject) {
        mNativeObject = nativeBufferObject;
    }

    public static class Builder {
        private final long mNativeBuilder;
        private final Cleaner.Cleanable mCleanable;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        public Builder size(int byteCount) {
            nBuilderSize(mNativeBuilder, byteCount);
            return this;
        }

        public Builder bindingType(BindingType type) {
            nBuilderBindingType(mNativeBuilder, type.ordinal());
            return this;
        }

        public BufferObject build(Engine engine) {
            long nativeObject = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeObject == 0) throw new IllegalStateException("Couldn't create BufferObject");
            return new BufferObject(nativeObject);
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeObject;
            BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
            @Override public void run() { nDestroyBuilder(mNativeObject); }
        }
    }

    public enum BindingType {
        VERTEX,
        UNIFORM
    }

    public void setBuffer(Engine engine, Buffer buffer) {
        setBuffer(engine, buffer, 0, buffer.remaining());
    }

    public void setBuffer(Engine engine, Buffer buffer, int byteOffset, int byteCount) {
        nSetBuffer(getNativeObject(), engine.getNativeObject(), buffer, buffer.remaining(), byteOffset, byteCount);
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed BufferObject");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderSize(long nativeBuilder, int byteCount);
    private static native void nBuilderBindingType(long nativeBuilder, int type);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);
    private static native void nSetBuffer(long nativeBufferObject, long nativeEngine, Buffer buffer, int remaining, int byteOffset, int byteCount);
}
