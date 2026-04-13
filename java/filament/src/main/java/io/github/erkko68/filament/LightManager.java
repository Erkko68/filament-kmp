package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NativeRegistry;
import java.lang.ref.Cleaner;

public class LightManager {
    private long mNativeObject;

    LightManager(long nativeLightManager) {
        mNativeObject = nativeLightManager;
    }

    public enum Type {
        SUN,
        DIRECTIONAL,
        POINT,
        FOCUSED_SPOT,
        SPOT
    }

    public static class Builder {
        private final long mNativeBuilder;
        private final Cleaner.Cleanable mCleanable;

        public Builder(Type type) {
            mNativeBuilder = nCreateBuilder(type.ordinal());
            mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        public Builder castShadows(boolean enabled) {
            nBuilderCastShadows(mNativeBuilder, enabled);
            return this;
        }

        public Builder color(float r, float g, float b) {
            nBuilderColor(mNativeBuilder, r, g, b);
            return this;
        }

        public Builder intensity(float intensity) {
            nBuilderIntensity(mNativeBuilder, intensity);
            return this;
        }

        public Builder direction(float x, float y, float z) {
            nBuilderDirection(mNativeBuilder, x, y, z);
            return this;
        }

        public void build(Engine engine, int entity) {
            nBuilderBuild(mNativeBuilder, engine.getNativeObject(), entity);
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeObject;
            BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
            @Override public void run() { nDestroyBuilder(mNativeObject); }
        }
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed LightManager");
        }
        return mNativeObject;
    }

    private static native long nCreateBuilder(int type);
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderCastShadows(long nativeBuilder, boolean enabled);
    private static native void nBuilderColor(long nativeBuilder, float r, float g, float b);
    private static native void nBuilderIntensity(long nativeBuilder, float intensity);
    private static native void nBuilderDirection(long nativeBuilder, float x, float y, float z);
    private static native void nBuilderBuild(long nativeBuilder, long nativeEngine, int entity);
}
