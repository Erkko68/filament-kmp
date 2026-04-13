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

    public void setBones(Engine engine, Buffer transforms, int count, int offset) {
        nSetBones(getNativeObject(), engine.getNativeObject(), transforms, transforms.remaining(), count, offset);
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

    private static native void nSetBones(long nativeBuffer, long nativeEngine, Buffer transforms, int remaining, int count, int offset);
    private static native int nGetBoneCount(long nativeBuffer);
}
