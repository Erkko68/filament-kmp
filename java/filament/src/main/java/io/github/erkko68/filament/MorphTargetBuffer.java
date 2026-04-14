package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NativeRegistry;
import java.lang.ref.Cleaner;
import java.nio.Buffer;

public class MorphTargetBuffer {
    private long mNativeObject;

    private MorphTargetBuffer(long nativeMorphTargetBuffer) {
        mNativeObject = nativeMorphTargetBuffer;
    }

    public static class Builder {
        private final long mNativeBuilder;
        private final Cleaner.Cleanable mCleanable;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        public Builder vertexCount(int vertexCount) {
            nBuilderVertexCount(mNativeBuilder, vertexCount);
            return this;
        }

        public Builder count(int count) {
            nBuilderCount(mNativeBuilder, count);
            return this;
        }

        public Builder withPositions(boolean enabled) {
            nBuilderWithPositions(mNativeBuilder, enabled);
            return this;
        }

        public Builder withTangents(boolean enabled) {
            nBuilderWithTangents(mNativeBuilder, enabled);
            return this;
        }

        public Builder enableCustomMorphing(boolean enabled) {
            nBuilderEnableCustomMorphing(mNativeBuilder, enabled);
            return this;
        }

        public MorphTargetBuffer build(Engine engine) {
            long nativeObject = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeObject == 0) throw new IllegalStateException("Couldn't create MorphTargetBuffer");
            return new MorphTargetBuffer(nativeObject);
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeObject;
            BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
            @Override public void run() { nDestroyBuilder(mNativeObject); }
        }
    }

    public void setPositionsAt(Engine engine, int targetIndex, Buffer positions, int offsetInBytes) {
        nSetPositionsAt(getNativeObject(), engine.getNativeObject(), targetIndex, positions, positions.remaining(), offsetInBytes);
    }

    public void setTangentsAt(Engine engine, int targetIndex, Buffer tangents, int offsetInBytes) {
        nSetTangentsAt(getNativeObject(), engine.getNativeObject(), targetIndex, tangents, tangents.remaining(), offsetInBytes);
    }

    public int getVertexCount() {
        return nGetVertexCount(getNativeObject());
    }

    public int getCount() {
        return nGetCount(getNativeObject());
    }

    public boolean hasPositions() {
        return nHasPositions(getNativeObject());
    }

    public boolean hasTangents() {
        return nHasTangents(getNativeObject());
    }

    public boolean isCustomMorphingEnabled() {
        return nIsCustomMorphingEnabled(getNativeObject());
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed MorphTargetBuffer");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderVertexCount(long nativeBuilder, int vertexCount);
    private static native void nBuilderCount(long nativeBuilder, int count);
    private static native void nBuilderWithPositions(long nativeBuilder, boolean enabled);
    private static native void nBuilderWithTangents(long nativeBuilder, boolean enabled);
    private static native void nBuilderEnableCustomMorphing(long nativeBuilder, boolean enabled);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);

    private static native void nSetPositionsAt(long nativeBuffer, long nativeEngine, int index, Buffer positions, int remaining, int offset);
    private static native void nSetTangentsAt(long nativeBuffer, long nativeEngine, int index, Buffer tangents, int remaining, int offset);
    private static native int nGetVertexCount(long nativeBuffer);
    private static native int nGetCount(long nativeBuffer);
    private static native boolean nHasPositions(long nativeBuffer);
    private static native boolean nHasTangents(long nativeBuffer);
    private static native boolean nIsCustomMorphingEnabled(long nativeBuffer);
}
