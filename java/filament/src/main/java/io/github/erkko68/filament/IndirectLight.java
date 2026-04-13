package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NativeRegistry;
import java.lang.ref.Cleaner;

public class IndirectLight {
    private long mNativeObject;

    public IndirectLight(long nativeIndirectLight) {
        mNativeObject = nativeIndirectLight;
    }

    public static class Builder {
        private final long mNativeBuilder;
        private final Cleaner.Cleanable mCleanable;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        public Builder reflections(Texture cubemap) {
            nBuilderReflections(mNativeBuilder, cubemap.getNativeObject());
            return this;
        }

        public Builder irradiance(int bands, float[] sh) {
            nIrradiance(mNativeBuilder, bands, sh);
            return this;
        }

        public Builder radiance(int bands, float[] sh) {
            nRadiance(mNativeBuilder, bands, sh);
            return this;
        }

        public Builder irradiance(Texture cubemap) {
            nIrradianceAsTexture(mNativeBuilder, cubemap.getNativeObject());
            return this;
        }

        public Builder intensity(float envIntensity) {
            nIntensity(mNativeBuilder, envIntensity);
            return this;
        }

        public Builder rotation(float[] rotation) {
            if (rotation.length < 9) throw new IllegalArgumentException("Rotation must be at least 9 floats");
            nRotation(mNativeBuilder, rotation[0], rotation[1], rotation[2], rotation[3], rotation[4], rotation[5], rotation[6], rotation[7], rotation[8]);
            return this;
        }

        public IndirectLight build(Engine engine) {
            long nativeObject = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeObject == 0) throw new IllegalStateException("Couldn't create IndirectLight");
            return new IndirectLight(nativeObject);
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeObject;
            BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
            @Override public void run() { nDestroyBuilder(mNativeObject); }
        }
    }

    public void setIntensity(float intensity) {
        nSetIntensity(getNativeObject(), intensity);
    }

    public float getIntensity() {
        return nGetIntensity(getNativeObject());
    }

    public void setRotation(float[] rotation) {
        if (rotation.length < 9) throw new IllegalArgumentException("Rotation must be at least 9 floats");
        nSetRotation(getNativeObject(), rotation[0], rotation[1], rotation[2], rotation[3], rotation[4], rotation[5], rotation[6], rotation[7], rotation[8]);
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed IndirectLight");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);

    private static native void nBuilderReflections(long nativeBuilder, long nativeTexture);
    private static native void nIrradiance(long nativeBuilder, int bands, float[] sh);
    private static native void nRadiance(long nativeBuilder, int bands, float[] sh);
    private static native void nIrradianceAsTexture(long nativeBuilder, long nativeTexture);
    private static native void nIntensity(long nativeBuilder, float envIntensity);
    private static native void nRotation(long nativeBuilder, float v0, float v1, float v2, float v3, float v4, float v5, float v6, float v7, float v8);

    private static native void nSetIntensity(long nativeIndirectLight, float intensity);
    private static native float nGetIntensity(long nativeIndirectLight);
    private static native void nSetRotation(long nativeIndirectLight, float v0, float v1, float v2, float v3, float v4, float v5, float v6, float v7, float v8);
}
