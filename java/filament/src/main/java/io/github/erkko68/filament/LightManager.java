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

    public int getInstance(int entity) {
        return nGetInstance(mNativeObject, entity);
    }

    public void setIntensity(int instance, float intensity) {
        nSetIntensity(mNativeObject, instance, intensity);
    }

    public void setColor(int instance, float r, float g, float b) {
        nSetColor(mNativeObject, instance, r, g, b);
    }

    public void setDirection(int instance, float x, float y, float z) {
        nSetDirection(mNativeObject, instance, x, y, z);
    }

    public void setPosition(int instance, float x, float y, float z) {
        nSetPosition(mNativeObject, instance, x, y, z);
    }

    public void setFalloff(int instance, float falloff) {
        nSetFalloff(mNativeObject, instance, falloff);
    }

    public void setSunAngularRadius(int instance, float radius) {
        nSetSunAngularRadius(mNativeObject, instance, radius);
    }

    public void setSunHaloSize(int instance, float size) {
        nSetSunHaloSize(mNativeObject, instance, size);
    }

    public void setSunHaloFalloff(int instance, float falloff) {
        nSetSunHaloFalloff(mNativeObject, instance, falloff);
    }

    public void destroy(int entity) {
        nDestroy(mNativeObject, entity);
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed LightManager");
        }
        return mNativeObject;
    }

    private static native int nGetInstance(long nativeLightManager, int entity);
    private static native void nSetIntensity(long nativeLightManager, int instance, float intensity);
    private static native void nSetColor(long nativeLightManager, int instance, float r, float g, float b);
    private static native void nSetDirection(long nativeLightManager, int instance, float x, float y, float z);
    private static native void nSetPosition(long nativeLightManager, int instance, float x, float y, float z);
    private static native void nSetFalloff(long nativeLightManager, int instance, float falloff);
    private static native void nSetSunAngularRadius(long nativeLightManager, int instance, float radius);
    private static native void nSetSunHaloSize(long nativeLightManager, int instance, float size);
    private static native void nSetSunHaloFalloff(long nativeLightManager, int instance, float falloff);
    private static native void nDestroy(long nativeLightManager, int entity);

    private static native long nCreateBuilder(int type);
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderCastShadows(long nativeBuilder, boolean enabled);
    private static native void nBuilderColor(long nativeBuilder, float r, float g, float b);
    private static native void nBuilderIntensity(long nativeBuilder, float intensity);
    private static native void nBuilderDirection(long nativeBuilder, float x, float y, float z);
    private static native void nBuilderBuild(long nativeBuilder, long nativeEngine, int entity);
}
