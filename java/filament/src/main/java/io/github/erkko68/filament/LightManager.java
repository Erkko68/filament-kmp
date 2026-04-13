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

    public static class ShadowOptions {
        public int mapSize = 1024;
        public int shadowCascades = 1;
        public float[] cascadeSplitPositions = { 0.125f, 0.25f, 0.50f };
        public float constantBias = 0.001f;
        public float normalBias = 1.0f;
        public float shadowFar = 0.0f;
        public float shadowNearHint = 1.0f;
        public float shadowFarHint = 100.0f;
        public boolean stable = false;
        public boolean lispsm = true;
        public float polygonOffsetConstant = 0.5f;
        public float polygonOffsetSlope = 2.0f;
        public boolean screenSpaceContactShadows = false;
        public int stepCount = 8;
        public float maxShadowDistance = 0.3f;
        public boolean elvsm = false;
        public float blurWidth = 0.0f;
        public float shadowBulbRadius = 0.02f;
        public float[] transform = { 0.0f, 0.0f, 0.0f, 1.0f };
    }

    public static class ShadowCascades {
        public static void computeUniformSplits(float[] splitPositions, int cascades) {
            nComputeUniformSplits(splitPositions, cascades);
        }
        public static void computeLogSplits(float[] splitPositions, int cascades, float near, float far) {
            nComputeLogSplits(splitPositions, cascades, near, far);
        }
        public static void computePracticalSplits(float[] splitPositions, int cascades, float near, float far, float lambda) {
            nComputePracticalSplits(splitPositions, cascades, near, far, lambda);
        }
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

        public Builder shadowOptions(ShadowOptions options) {
            nBuilderShadowOptions(mNativeBuilder,
                options.mapSize, options.shadowCascades, options.cascadeSplitPositions,
                options.constantBias, options.normalBias, options.shadowFar, options.shadowNearHint,
                options.shadowFarHint, options.stable, options.lispsm,
                options.polygonOffsetConstant, options.polygonOffsetSlope,
                options.screenSpaceContactShadows,
                options.stepCount, options.maxShadowDistance,
                options.elvsm, options.blurWidth, options.shadowBulbRadius, options.transform);
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

        public Builder position(float x, float y, float z) {
            nBuilderPosition(mNativeBuilder, x, y, z);
            return this;
        }

        public Builder falloff(float radius) {
            nBuilderFalloff(mNativeBuilder, radius);
            return this;
        }

        public Builder spotLightCone(float inner, float outer) {
            nBuilderSpotLightCone(mNativeBuilder, inner, outer);
            return this;
        }

        public Builder sunAngularRadius(float radius) {
            nBuilderSunAngularRadius(mNativeBuilder, radius);
            return this;
        }

        public Builder sunHaloSize(float size) {
            nBuilderSunHaloSize(mNativeBuilder, size);
            return this;
        }

        public Builder sunHaloFalloff(float falloff) {
            nBuilderSunHaloFalloff(mNativeBuilder, falloff);
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
    private static native void nBuilderShadowOptions(long nativeBuilder,
        int mapSize, int shadowCascades, float[] cascadeSplitPositions,
        float constantBias, float normalBias, float shadowFar, float shadowNearHint,
        float shadowFarHint, boolean stable, boolean lispsm,
        float polygonOffsetConstant, float polygonOffsetSlope,
        boolean screenSpaceContactShadows,
        int stepCount, float maxShadowDistance,
        boolean elvsm, float blurWidth, float shadowBulbRadius, float[] transform);
    private static native void nBuilderColor(long nativeBuilder, float r, float g, float b);
    private static native void nBuilderIntensity(long nativeBuilder, float intensity);
    private static native void nBuilderDirection(long nativeBuilder, float x, float y, float z);
    private static native void nBuilderPosition(long nativeBuilder, float x, float y, float z);
    private static native void nBuilderFalloff(long nativeBuilder, float radius);
    private static native void nBuilderSpotLightCone(long nativeBuilder, float inner, float outer);
    private static native void nBuilderSunAngularRadius(long nativeBuilder, float radius);
    private static native void nBuilderSunHaloSize(long nativeBuilder, float size);
    private static native void nBuilderSunHaloFalloff(long nativeBuilder, float falloff);
    private static native void nBuilderBuild(long nativeBuilder, long nativeEngine, int entity);

    private static native void nComputeUniformSplits(float[] splitPositions, int cascades);
    private static native void nComputeLogSplits(float[] splitPositions, int cascades, float near, float far);
    private static native void nComputePracticalSplits(float[] splitPositions, int cascades, float near, float far, float lambda);
}
