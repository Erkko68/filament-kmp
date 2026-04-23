/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.erkko68.filament.jni;

import io.github.erkko68.filament.jni.proguard.UsedByNative;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.github.erkko68.filament.jni.internal.NativeRegistry;
import java.lang.ref.Cleaner;

/**
 * LightManager allows you to create a light source in the scene, such as a sun or street lights.
 */
public class LightManager {
    private static final Type[] sTypeValues = Type.values();
    private long mNativeObject;

    LightManager(long nativeLightManager) {
        mNativeObject = nativeLightManager;
    }

    public int getComponentCount() { return nGetComponentCount(mNativeObject); }

    public boolean hasComponent(@io.github.erkko68.filament.jni.Entity int entity) { return nHasComponent(mNativeObject, entity); }

    @io.github.erkko68.filament.jni.proguard.EntityInstance
    public int getInstance(@io.github.erkko68.filament.jni.Entity int entity) { return nGetInstance(mNativeObject, entity); }

    public void destroy(@io.github.erkko68.filament.jni.Entity int entity) { nDestroy(mNativeObject, entity); }

    public enum Type { SUN, DIRECTIONAL, POINT, FOCUSED_SPOT, SPOT }

    public static class ShadowOptions {
        public int mapSize = 1024;
        public int shadowCascades = 1;
        @NotNull public float[] cascadeSplitPositions = { 0.125f, 0.25f, 0.50f };
        public float constantBias = 0.001f;
        public float normalBias = 1.0f;
        public float shadowFar = 0.0f;
        public float shadowNearHint = 1.0f;
        public float shadowFarHint = 100.0f;
        public boolean stable = false;
        public boolean lispsm = false;
        public float polygonOffsetConstant = 0.5f;
        public float polygonOffsetSlope = 2.0f;
        public boolean screenSpaceContactShadows = false;
        public int stepCount = 8;
        public float maxShadowDistance = 0.3f;
        public boolean elvsm = false;
        public float blurWidth = 0.0f;
        public float shadowBulbRadius = 0.02f;
        @NotNull public float[] transform = { 0.0f, 0.0f, 0.0f, 1.0f };
    }

    public static class ShadowCascades {
        public static void computeUniformSplits(@NotNull float[] splitPositions, int cascades) { nComputeUniformSplits(splitPositions, cascades); }
        public static void computeLogSplits(@NotNull float[] splitPositions, int cascades, float near, float far) { nComputeLogSplits(splitPositions, cascades, near, far); }
        public static void computePracticalSplits(@NotNull float[] splitPositions, int cascades, float near, float far, float lambda) { nComputePracticalSplits(splitPositions, cascades, near, far, lambda); }
    }

    public static final float EFFICIENCY_INCANDESCENT = 0.0220f;
    public static final float EFFICIENCY_HALOGEN      = 0.0707f;
    public static final float EFFICIENCY_FLUORESCENT  = 0.0878f;
    public static final float EFFICIENCY_LED          = 0.1171f;

    public static class Builder {
        private final long mNativeBuilder;
        private final Cleaner.Cleanable mCleanable;

        public Builder(@NotNull Type type) {
            mNativeBuilder = nCreateBuilder(type.ordinal());
            mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        @NotNull public Builder lightChannel(int channel, boolean enable) { nBuilderLightChannel(mNativeBuilder, channel, enable); return this; }
        @NotNull public Builder castShadows(boolean enabled) { nBuilderCastShadows(mNativeBuilder, enabled); return this; }
        @NotNull public Builder shadowOptions(@NotNull ShadowOptions options) {
            nBuilderShadowOptions(mNativeBuilder, options.mapSize, options.shadowCascades, options.cascadeSplitPositions, options.constantBias, options.normalBias, options.shadowFar, options.shadowNearHint, options.shadowFarHint, options.stable, options.lispsm, options.polygonOffsetConstant, options.polygonOffsetSlope, options.screenSpaceContactShadows, options.stepCount, options.maxShadowDistance, options.elvsm, options.blurWidth, options.shadowBulbRadius, options.transform);
            return this;
        }
        @NotNull public Builder castLight(boolean enabled) { nBuilderCastLight(mNativeBuilder, enabled); return this; }
        @NotNull public Builder position(float x, float y, float z) { nBuilderPosition(mNativeBuilder, x, y, z); return this; }
        @NotNull public Builder direction(float x, float y, float z) { nBuilderDirection(mNativeBuilder, x, y, z); return this; }
        @NotNull public Builder color(float linearR, float linearG, float linearB) { nBuilderColor(mNativeBuilder, linearR, linearG, linearB); return this; }
        @NotNull public Builder intensity(float intensity) { nBuilderIntensity(mNativeBuilder, intensity); return this; }
        @NotNull public Builder intensity(float watts, float efficiency) { nBuilderIntensityWatts(mNativeBuilder, watts, efficiency); return this; }
        @NotNull public Builder intensityCandela(float intensity) { nBuilderIntensityCandela(mNativeBuilder, intensity); return this; }
        @NotNull public Builder falloff(float radius) { nBuilderFalloff(mNativeBuilder, radius); return this; }
        @NotNull public Builder spotLightCone(float inner, float outer) { nBuilderSpotLightCone(mNativeBuilder, inner, outer); return this; }
        @NotNull public Builder sunAngularRadius(float angularRadius) { nBuilderAngularRadius(mNativeBuilder, angularRadius); return this; }
        @NotNull public Builder sunHaloSize(float haloSize) { nBuilderHaloSize(mNativeBuilder, haloSize); return this; }
        @NotNull public Builder sunHaloFalloff(float haloFalloff) { nBuilderHaloFalloff(mNativeBuilder, haloFalloff); return this; }

        public void build(@NotNull Engine engine, @io.github.erkko68.filament.jni.Entity int entity) {
            if (!nBuilderBuild(mNativeBuilder, engine.getNativeObject(), entity)) {
                throw new IllegalStateException("Couldn't create Light component for entity " + entity);
            }
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeObject;
            BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
            @Override public void run() { nDestroyBuilder(mNativeObject); }
        }
    }

    public void setLightChannel(int instance, int channel, boolean enable) { nSetLightChannel(mNativeObject, instance, channel, enable); }
    public boolean getLightChannel(int instance, int channel) { return nGetLightChannel(mNativeObject, instance, channel); }

    public void setShadowOptions(int instance, @NotNull ShadowOptions options) {
        nSetShadowOptions(mNativeObject, instance, options.mapSize, options.shadowCascades, options.cascadeSplitPositions, options.constantBias, options.normalBias, options.shadowFar, options.shadowNearHint, options.shadowFarHint, options.stable, options.lispsm, options.polygonOffsetConstant, options.polygonOffsetSlope, options.screenSpaceContactShadows, options.stepCount, options.maxShadowDistance, options.elvsm, options.blurWidth, options.shadowBulbRadius, options.transform);
    }

    public void setCastShadows(int instance, boolean enabled) { nSetCastShadows(mNativeObject, instance, enabled); }
    public boolean isCastingShadows(int instance) { return nIsCastingShadows(mNativeObject, instance); }

    public void setShadowCaster(int instance, boolean enabled) { nSetCastShadows(mNativeObject, instance, enabled); }
    public boolean isShadowCaster(int instance) { return nIsCastingShadows(mNativeObject, instance); }


    public void setIntensity(int instance, float intensity) { nSetIntensity(mNativeObject, instance, intensity); }
    public void setIntensityCandela(int instance, float intensity) { nSetIntensityCandela(mNativeObject, instance, intensity); }
    public float getIntensity(int instance) { return nGetIntensity(mNativeObject, instance); }

    public void setColor(int instance, float r, float g, float b) { nSetColor(mNativeObject, instance, r, g, b); }
    public void getColor(int instance, @NotNull float[] out) { nGetColor(mNativeObject, instance, out); }

    public void setDirection(int instance, float x, float y, float z) { nSetDirection(mNativeObject, instance, x, y, z); }
    public void getDirection(int instance, @NotNull float[] out) { nGetDirection(mNativeObject, instance, out); }

    public void setPosition(int instance, float x, float y, float z) { nSetPosition(mNativeObject, instance, x, y, z); }
    public void getPosition(int instance, @NotNull float[] out) { nGetPosition(mNativeObject, instance, out); }

    public void setFalloff(int instance, float falloff) { nSetFalloff(mNativeObject, instance, falloff); }
    public float getFalloff(int instance) { return nGetFalloff(mNativeObject, instance); }

    public void setSpotLightCone(int instance, float inner, float outer) { nSetSpotLightCone(mNativeObject, instance, inner, outer); }
    public float getInnerConeAngle(int instance) { return nGetInnerConeAngle(mNativeObject, instance); }
    public float getOuterConeAngle(int instance) { return nGetOuterConeAngle(mNativeObject, instance); }

    public void setSunAngularRadius(int instance, float radius) { nSetSunAngularRadius(mNativeObject, instance, radius); }
    public float getSunAngularRadius(int instance) { return nGetSunAngularRadius(mNativeObject, instance); }

    public void setSunHaloSize(int instance, float size) { nSetSunHaloSize(mNativeObject, instance, size); }
    public float getSunHaloSize(int instance) { return nGetSunHaloSize(mNativeObject, instance); }

    public void setSunHaloFalloff(int instance, float falloff) { nSetSunHaloFalloff(mNativeObject, instance, falloff); }
    public float getSunHaloFalloff(int instance) { return nGetSunHaloFalloff(mNativeObject, instance); }

    @NotNull public Type getType(int instance) { return sTypeValues[nGetType(mNativeObject, instance)]; }

    public boolean isDirectional(int instance) { return nIsDirectional(mNativeObject, instance); }
    public boolean isPointLight(int instance) { return nIsPointLight(mNativeObject, instance); }
    public boolean isSpotLight(int instance) { return nIsSpotLight(mNativeObject, instance); }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Calling method on destroyed LightManager");
        return mNativeObject;
    }

    private static native int nGetComponentCount(long nativeLightManager);
    private static native boolean nHasComponent(long nativeLightManager, int entity);
    private static native int nGetInstance(long nativeLightManager, int entity);
    private static native void nDestroy(long nativeLightManager, int entity);

    private static native void nSetIntensity(long nativeLightManager, int instance, float intensity);
    private static native void nSetIntensityCandela(long nativeLightManager, int instance, float intensity);
    private static native float nGetIntensity(long nativeLightManager, int instance);
    private static native void nSetColor(long nativeLightManager, int instance, float r, float g, float b);
    private static native void nGetColor(long nativeLightManager, int instance, float[] out);
    private static native void nSetDirection(long nativeLightManager, int instance, float x, float y, float z);
    private static native void nGetDirection(long nativeLightManager, int instance, float[] out);
    private static native void nSetPosition(long nativeLightManager, int instance, float x, float y, float z);
    private static native void nGetPosition(long nativeLightManager, int instance, float[] out);
    private static native void nSetFalloff(long nativeLightManager, int instance, float falloff);
    private static native float nGetFalloff(long nativeLightManager, int instance);
    private static native void nSetSpotLightCone(long nativeLightManager, int instance, float inner, float outer);
    private static native void nSetSunAngularRadius(long nativeLightManager, int instance, float radius);
    private static native float nGetSunAngularRadius(long nativeLightManager, int instance);
    private static native void nSetSunHaloSize(long nativeLightManager, int instance, float size);
    private static native float nGetSunHaloSize(long nativeLightManager, int instance);
    private static native void nSetSunHaloFalloff(long nativeLightManager, int instance, float falloff);
    private static native float nGetSunHaloFalloff(long nativeLightManager, int instance);

    private static native float nGetInnerConeAngle(long nativeLightManager, int instance);
    private static native float nGetOuterConeAngle(long nativeLightManager, int instance);

    private static native void nSetLightChannel(long nativeLightManager, int instance, int channel, boolean enable);
    private static native boolean nGetLightChannel(long nativeLightManager, int instance, int channel);
    private static native void nSetShadowOptions(long nativeLightManager, int instance, int mapSize, int shadowCascades, float[] cascadeSplitPositions, float constantBias, float normalBias, float shadowFar, float shadowNearHint, float shadowFarHint, boolean stable, boolean lispsm, float polygonOffsetConstant, float polygonOffsetSlope, boolean screenSpaceContactShadows, int stepCount, float maxShadowDistance, boolean elvsm, float blurWidth, float shadowBulbRadius, float[] transform);
    private static native void nSetCastShadows(long nativeLightManager, int instance, boolean enabled);
    private static native boolean nIsCastingShadows(long nativeLightManager, int instance);
    private static native int nGetType(long nativeLightManager, int instance);
    private static native boolean nIsDirectional(long nativeLightManager, int instance);
    private static native boolean nIsPointLight(long nativeLightManager, int instance);
    private static native boolean nIsSpotLight(long nativeLightManager, int instance);

    private static native long nCreateBuilder(int type);
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderLightChannel(long nativeBuilder, int channel, boolean enable);
    private static native void nBuilderCastShadows(long nativeBuilder, boolean enabled);
    private static native void nBuilderShadowOptions(long nativeBuilder, int mapSize, int shadowCascades, float[] cascadeSplitPositions, float constantBias, float normalBias, float shadowFar, float shadowNearHint, float shadowFarHint, boolean stable, boolean lispsm, float polygonOffsetConstant, float polygonOffsetSlope, boolean screenSpaceContactShadows, int stepCount, float maxShadowDistance, boolean elvsm, float blurWidth, float shadowBulbRadius, float[] transform);
    private static native void nBuilderCastLight(long nativeBuilder, boolean enabled);
    private static native void nBuilderPosition(long nativeBuilder, float x, float y, float z);
    private static native void nBuilderDirection(long nativeBuilder, float x, float y, float z);
    private static native void nBuilderColor(long nativeBuilder, float r, float g, float b);
    private static native void nBuilderIntensity(long nativeBuilder, float intensity);
    private static native void nBuilderIntensityWatts(long nativeBuilder, float watts, float efficiency);
    private static native void nBuilderIntensityCandela(long nativeBuilder, float intensity);
    private static native void nBuilderFalloff(long nativeBuilder, float radius);
    private static native void nBuilderSpotLightCone(long nativeBuilder, float inner, float outer);
    private static native void nBuilderAngularRadius(long nativeBuilder, float radius);
    private static native void nBuilderHaloSize(long nativeBuilder, float size);
    private static native void nBuilderHaloFalloff(long nativeBuilder, float falloff);
    private static native boolean nBuilderBuild(long nativeBuilder, long nativeEngine, int entity);

    private static native void nComputeUniformSplits(float[] splitPositions, int cascades);
    private static native void nComputeLogSplits(float[] splitPositions, int cascades, float near, float far);
    private static native void nComputePracticalSplits(float[] splitPositions, int cascades, float near, float far, float lambda);
}
