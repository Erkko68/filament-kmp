package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NativeRegistry;
import java.lang.ref.Cleaner;
import java.nio.Buffer;

public class SkinningBuffer {
    private long mNativeObject;

    private SkinningBuffer(long nativeSkinningBuffer) {
        mNativeObject = nativeSkinningBuffer;
    }

    public static class Builder {
        private final long mNativeBuilder;
        private final Cleaner.Cleanable mCleanable;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        public Builder boneCount(int boneCount) {
            nBuilderBoneCount(mNativeBuilder, boneCount);
            return this;
        }

        public Builder initialize(boolean initialize) {
            nBuilderInitialize(mNativeBuilder, initialize);
            return this;
        }

        public SkinningBuffer build(Engine engine) {
            long nativeObject = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeObject == 0) throw new IllegalStateException("Couldn't create SkinningBuffer");
            return new SkinningBuffer(nativeObject);
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeObject;
            BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
            @Override public void run() { nDestroyBuilder(mNativeObject); }
        }
    }

    public void setBonesAsMatrices(Engine engine, Buffer matrices, int boneCount, int offset) {
        int result = nSetBonesAsMatrices(getNativeObject(), engine.getNativeObject(),
                matrices, matrices.remaining(), boneCount, offset);
        if (result < 0) throw new java.nio.BufferOverflowException();
    }

    public void setBonesAsQuaternions(Engine engine, Buffer quaternions, int boneCount, int offset) {
        int result = nSetBonesAsQuaternions(getNativeObject(), engine.getNativeObject(),
                quaternions, quaternions.remaining(), boneCount, offset);
        if (result < 0) throw new java.nio.BufferOverflowException();
    }

    public int getBoneCount() {
        return nGetBoneCount(getNativeObject());
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed SkinningBuffer");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderBoneCount(long nativeBuilder, int boneCount);
    private static native void nBuilderInitialize(long nativeBuilder, boolean initialize);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);

    private static native int nSetBonesAsMatrices(long nativeBuffer, long nativeEngine, Buffer matrices, int remaining, int boneCount, int offset);
    private static native int nSetBonesAsQuaternions(long nativeBuffer, long nativeEngine, Buffer quaternions, int remaining, int boneCount, int offset);
    private static native int nGetBoneCount(long nativeBuffer);
}
