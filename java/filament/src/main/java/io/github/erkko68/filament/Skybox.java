package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NativeRegistry;
import java.lang.ref.Cleaner;

public class Skybox {
    private long mNativeObject;

    public Skybox(long nativeSkybox) {
        mNativeObject = nativeSkybox;
    }

    public static class Builder {
        private final long mNativeBuilder;
        private final Cleaner.Cleanable mCleanable;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        public Builder environment(Texture cubemap) {
            nBuilderEnvironment(mNativeBuilder, cubemap.getNativeObject());
            return this;
        }

        public Builder showSun(boolean show) {
            nBuilderShowSun(mNativeBuilder, show);
            return this;
        }

        public Builder intensity(float envIntensity) {
            nBuilderIntensity(mNativeBuilder, envIntensity);
            return this;
        }

        public Builder color(float r, float g, float b, float a) {
            nBuilderColor(mNativeBuilder, r, g, b, a);
            return this;
        }

        public Skybox build(Engine engine) {
            long nativeObject = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeObject == 0) throw new IllegalStateException("Couldn't create Skybox");
            return new Skybox(nativeObject);
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeObject;
            BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
            @Override public void run() { nDestroyBuilder(mNativeObject); }
        }
    }

    public void setColor(float r, float g, float b, float a) {
        nSetColor(getNativeObject(), r, g, b, a);
    }

    public float getIntensity() {
        return nGetIntensity(getNativeObject());
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed Skybox");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeSkyboxBuilder);
    private static native void nBuilderEnvironment(long nativeSkyboxBuilder, long nativeTexture);
    private static native void nBuilderShowSun(long nativeSkyboxBuilder, boolean show);
    private static native void nBuilderIntensity(long nativeSkyboxBuilder, float intensity);
    private static native void nBuilderColor(long nativeSkyboxBuilder, float r, float g, float b, float a);
    private static native long nBuilderBuild(long nativeSkyboxBuilder, long nativeEngine);

    private static native float nGetIntensity(long nativeSkybox);
    private static native void nSetColor(long nativeSkybox, float r, float g, float b, float a);
}
