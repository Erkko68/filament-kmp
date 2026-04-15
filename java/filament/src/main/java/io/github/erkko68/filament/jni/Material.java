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
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * A Filament Material defines the visual appearance of an object.
 */
public class Material {
    static final class EnumCache {
        static final Shading[] sShadingValues = Shading.values();
        static final Interpolation[] sInterpolationValues = Interpolation.values();
        static final BlendingMode[] sBlendingModeValues = BlendingMode.values();
        static final RefractionMode[] sRefractionModeValues = RefractionMode.values();
        static final RefractionType[] sRefractionTypeValues = RefractionType.values();
        static final ReflectionMode[] sReflectionModeValues = ReflectionMode.values();
        static final Engine.FeatureLevel[] sFeatureLevelValues = Engine.FeatureLevel.values();
        static final VertexDomain[] sVertexDomainValues = VertexDomain.values();
        static final CullingMode[] sCullingModeValues = CullingMode.values();
        static final VertexBuffer.VertexAttribute[] sVertexAttributeValues = VertexBuffer.VertexAttribute.values();
        static final TransparencyMode[] sTransparencyModeValues = TransparencyMode.values();
    }

    private long mNativeObject;
    private final MaterialInstance mDefaultInstance;
    private Set<VertexBuffer.VertexAttribute> mRequiredAttributes;

    public enum Shading { UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS }
    public enum Interpolation { SMOOTH, FLAT }
    public enum BlendingMode { OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN }
    public enum TransparencyMode { DEFAULT, TWO_PASSES_ONE_SIDE, TWO_PASSES_TWO_SIDES }
    public enum RefractionMode { NONE, CUBEMAP, SCREEN_SPACE }
    public enum RefractionType { SOLID, THIN }
    public enum ReflectionMode { DEFAULT, SCREEN_SPACE }
    public enum VertexDomain { OBJECT, WORLD, VIEW, DEVICE }
    public enum CullingMode { NONE, FRONT, BACK, FRONT_AND_BACK }
    public enum CompilerPriorityQueue { CRITICAL, HIGH, LOW }
    public enum UboBatchingMode { DEFAULT, DISABLED }

    public static class Parameter {
        public enum Type { BOOL, BOOL2, BOOL3, BOOL4, FLOAT, FLOAT2, FLOAT3, FLOAT4, INT, INT2, INT3, INT4, UINT, UINT2, UINT3, UINT4, MAT3, MAT4, SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D, SUBPASS_INPUT }
        public enum Precision { LOW, MEDIUM, HIGH, DEFAULT }
        @NotNull public final String name;
        @NotNull public final Type type;
        @NotNull public final Precision precision;
        public final int count;
        private Parameter(@NotNull String name, @NotNull Type type, @NotNull Precision precision, int count) {
            this.name = name; this.type = type; this.precision = precision; this.count = count;
        }
        @UsedByNative("Material.cpp")
        private static void add(@NotNull List<Parameter> parameters, @NotNull String name, int type, int precision, int count) {
            parameters.add(new Parameter(name, Type.values()[type], Precision.values()[precision], count));
        }
    }

    public Material(long nativeMaterial) {
        mNativeObject = nativeMaterial;
        mDefaultInstance = new MaterialInstance(this, nGetDefaultInstance(nativeMaterial));
    }

    public static class Builder {
        public enum ShadowSamplingQuality { HARD, LOW }
        private Buffer mBuffer;
        private int mSize;
        private int mShBandCount = 3;
        private ShadowSamplingQuality mShadowSamplingQuality = ShadowSamplingQuality.LOW;
        private UboBatchingMode mUboBatchingMode = UboBatchingMode.DEFAULT;

        @NotNull public Builder payload(@NotNull Buffer buffer, int size) { mBuffer = buffer; mSize = size; return this; }
        @NotNull public Builder sphericalHarmonicsBandCount(int shBandCount) { mShBandCount = shBandCount; return this; }
        @NotNull public Builder shadowSamplingQuality(@NotNull ShadowSamplingQuality quality) { mShadowSamplingQuality = quality; return this; }
        @NotNull public Builder uboBatching(@NotNull UboBatchingMode mode) { mUboBatchingMode = mode; return this; }

        @NotNull public Material build(@NotNull Engine engine) {
            long nativeMaterial = nBuilderBuild(engine.getNativeObject(), mBuffer, mSize, mShBandCount, mShadowSamplingQuality.ordinal(), mUboBatchingMode.ordinal());
            if (nativeMaterial == 0) throw new IllegalStateException("Couldn't create Material");
            return new Material(nativeMaterial);
        }
        private static native long nBuilderBuild(long nativeEngine, Buffer buffer, int size, int shBandCount, int shadowQuality, int uboBatching);
    }

    public void compile(@NotNull CompilerPriorityQueue priority, int variants, @Nullable Object handler, @Nullable Runnable callback) {
        nCompile(getNativeObject(), priority.ordinal(), variants, handler, callback);
    }

    @NotNull public MaterialInstance createInstance() {
        long nativeInstance = nCreateInstance(getNativeObject());
        if (nativeInstance == 0) throw new IllegalStateException("Couldn't create MaterialInstance");
        return new MaterialInstance(this, nativeInstance);
    }

    @NotNull public MaterialInstance createInstance(@NotNull String name) {
        long nativeInstance = nCreateInstanceWithName(getNativeObject(), name);
        if (nativeInstance == 0) throw new IllegalStateException("Couldn't create MaterialInstance");
        return new MaterialInstance(this, nativeInstance);
    }

    @NotNull public MaterialInstance getDefaultInstance() { return mDefaultInstance; }

    public String getName() { return nGetName(getNativeObject()); }
    public Shading getShading() { return EnumCache.sShadingValues[nGetShading(getNativeObject())]; }
    public Interpolation getInterpolation() { return EnumCache.sInterpolationValues[nGetInterpolation(getNativeObject())]; }
    public BlendingMode getBlendingMode() { return EnumCache.sBlendingModeValues[nGetBlendingMode(getNativeObject())]; }
    public TransparencyMode getTransparencyMode() { return EnumCache.sTransparencyModeValues[nGetTransparencyMode(getNativeObject())]; }
    public RefractionMode getRefractionMode() { return EnumCache.sRefractionModeValues[nGetRefractionMode(getNativeObject())]; }
    public RefractionType getRefractionType() { return EnumCache.sRefractionTypeValues[nGetRefractionType(getNativeObject())]; }
    public ReflectionMode getReflectionMode() { return EnumCache.sReflectionModeValues[nGetReflectionMode(getNativeObject())]; }
    public Engine.FeatureLevel getFeatureLevel() { return EnumCache.sFeatureLevelValues[nGetFeatureLevel(getNativeObject())]; }
    public VertexDomain getVertexDomain() { return EnumCache.sVertexDomainValues[nGetVertexDomain(getNativeObject())]; }
    public CullingMode getCullingMode() { return EnumCache.sCullingModeValues[nGetCullingMode(getNativeObject())]; }
    public boolean isColorWriteEnabled() { return nIsColorWriteEnabled(getNativeObject()); }
    public boolean isDepthWriteEnabled() { return nIsDepthWriteEnabled(getNativeObject()); }
    public boolean isDepthCullingEnabled() { return nIsDepthCullingEnabled(getNativeObject()); }
    public boolean isDoubleSided() { return nIsDoubleSided(getNativeObject()); }
    public boolean isAlphaToCoverageEnabled() { return nIsAlphaToCoverageEnabled(getNativeObject()); }
    public float getMaskThreshold() { return nGetMaskThreshold(getNativeObject()); }
    public float getSpecularAntiAliasingVariance() { return nGetSpecularAntiAliasingVariance(getNativeObject()); }
    public float getSpecularAntiAliasingThreshold() { return nGetSpecularAntiAliasingThreshold(getNativeObject()); }

    public Set<VertexBuffer.VertexAttribute> getRequiredAttributes() {
        if (mRequiredAttributes == null) {
            int bitSet = nGetRequiredAttributes(getNativeObject());
            mRequiredAttributes = EnumSet.noneOf(VertexBuffer.VertexAttribute.class);
            VertexBuffer.VertexAttribute[] values = EnumCache.sVertexAttributeValues;
            for (int i = 0; i < values.length; i++) {
                if ((bitSet & (1 << i)) != 0) mRequiredAttributes.add(values[i]);
            }
            mRequiredAttributes = Collections.unmodifiableSet(mRequiredAttributes);
        }
        return mRequiredAttributes;
    }

    public boolean hasParameter(@NotNull String name) { return nHasParameter(getNativeObject(), name); }

    public List<Parameter> getParameters() {
        List<Parameter> parameters = new ArrayList<>();
        nGetParameters(getNativeObject(), parameters);
        return parameters;
    }

    public int getParameterCount() { return nGetParameterCount(getNativeObject()); }

    public String getParameterTransformName(@NotNull String samplerName) {
        return nGetParameterTransformName(getNativeObject(), samplerName);
    }

    public void setDefaultParameter(@NotNull String name, boolean x) { nSetDefaultParameterBool(getNativeObject(), name, x); }
    public void setDefaultParameter(@NotNull String name, float x) { nSetDefaultParameterFloat(getNativeObject(), name, x); }
    public void setDefaultParameter(@NotNull String name, int x) { nSetDefaultParameterInt(getNativeObject(), name, x); }
    public void setDefaultParameter(@NotNull String name, boolean x, boolean y) { nSetDefaultParameterBool2(getNativeObject(), name, x, y); }
    public void setDefaultParameter(@NotNull String name, float x, float y) { nSetDefaultParameterFloat2(getNativeObject(), name, x, y); }
    public void setDefaultParameter(@NotNull String name, int x, int y) { nSetDefaultParameterInt2(getNativeObject(), name, x, y); }
    public void setDefaultParameter(@NotNull String name, boolean x, boolean y, boolean z) { nSetDefaultParameterBool3(getNativeObject(), name, x, y, z); }
    public void setDefaultParameter(@NotNull String name, float x, float y, float z) { nSetDefaultParameterFloat3(getNativeObject(), name, x, y, z); }
    public void setDefaultParameter(@NotNull String name, int x, int y, int z) { nSetDefaultParameterInt3(getNativeObject(), name, x, y, z); }
    public void setDefaultParameter(@NotNull String name, boolean x, boolean y, boolean z, boolean w) { nSetDefaultParameterBool4(getNativeObject(), name, x, y, z, w); }
    public void setDefaultParameter(@NotNull String name, float x, float y, float z, float w) { nSetDefaultParameterFloat4(getNativeObject(), name, x, y, z, w); }
    public void setDefaultParameter(@NotNull String name, int x, int y, int z, int w) { nSetDefaultParameterInt4(getNativeObject(), name, x, y, z, w); }

    public void setDefaultParameter(@NotNull String name, @NotNull Colors.RgbType type, float r, float g, float b) {
        float[] color = Colors.toLinear(type, r, g, b);
        nSetDefaultParameterFloat3(getNativeObject(), name, color[0], color[1], color[2]);
    }

    public void setDefaultParameter(@NotNull String name, @NotNull Colors.RgbaType type, float r, float g, float b, float a) {
        float[] color = Colors.toLinear(type, r, g, b, a);
        nSetDefaultParameterFloat4(getNativeObject(), name, color[0], color[1], color[2], color[3]);
    }

    public void setDefaultParameter(@NotNull String name, @NotNull MaterialInstance.BooleanElement type, @NotNull boolean[] v, int offset, int count) {
        nSetDefaultBooleanParameterArray(getNativeObject(), name, type.ordinal(), v, offset, count);
    }

    public void setDefaultParameter(@NotNull String name, @NotNull MaterialInstance.IntElement type, @NotNull int[] v, int offset, int count) {
        nSetDefaultIntParameterArray(getNativeObject(), name, type.ordinal(), v, offset, count);
    }

    public void setDefaultParameter(@NotNull String name, @NotNull MaterialInstance.FloatElement type, @NotNull float[] v, int offset, int count) {
        nSetDefaultFloatParameterArray(getNativeObject(), name, type.ordinal(), v, offset, count);
    }

    public void setDefaultParameter(@NotNull String name, @NotNull Texture texture, @NotNull TextureSampler sampler) {
        nSetDefaultParameterTexture(getNativeObject(), name, texture.getNativeObject(), (int) sampler.getSampler());
    }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Calling method on destroyed Material");
        return mNativeObject;
    }

    void clearNativeObject() { mNativeObject = 0; }

    private static native long nCreateInstance(long nativeMaterial);
    private static native long nCreateInstanceWithName(long nativeMaterial, String name);
    private static native long nGetDefaultInstance(long nativeMaterial);
    private static native String nGetName(long nativeMaterial);
    private static native int nGetShading(long nativeMaterial);
    private static native int nGetInterpolation(long nativeMaterial);
    private static native int nGetBlendingMode(long nativeMaterial);
    private static native int nGetTransparencyMode(long nativeMaterial);
    private static native int nGetRefractionMode(long nativeMaterial);
    private static native int nGetRefractionType(long nativeMaterial);
    private static native int nGetReflectionMode(long nativeMaterial);
    private static native int nGetFeatureLevel(long nativeMaterial);
    private static native int nGetVertexDomain(long nativeMaterial);
    private static native int nGetCullingMode(long nativeMaterial);
    private static native boolean nIsColorWriteEnabled(long nativeMaterial);
    private static native boolean nIsDepthWriteEnabled(long nativeMaterial);
    private static native boolean nIsDepthCullingEnabled(long nativeMaterial);
    private static native boolean nIsDoubleSided(long nativeMaterial);
    private static native boolean nIsAlphaToCoverageEnabled(long nativeMaterial);
    private static native float nGetMaskThreshold(long nativeMaterial);
    private static native float nGetSpecularAntiAliasingVariance(long nativeMaterial);
    private static native float nGetSpecularAntiAliasingThreshold(long nativeMaterial);
    private static native int nGetRequiredAttributes(long nativeMaterial);
    private static native boolean nHasParameter(long nativeMaterial, String name);
    private static native void nGetParameters(long nativeMaterial, List<Parameter> parameters);
    private static native int nGetParameterCount(long nativeMaterial);
    private static native void nCompile(long nativeMaterial, int priority, int variants, Object handler, Runnable callback);

    private static native String nGetParameterTransformName(long nativeMaterial, String samplerName);
    private static native void nSetDefaultParameterBool(long nativeMaterial, String name, boolean x);
    private static native void nSetDefaultParameterFloat(long nativeMaterial, String name, float x);
    private static native void nSetDefaultParameterInt(long nativeMaterial, String name, int x);
    private static native void nSetDefaultParameterBool2(long nativeMaterial, String name, boolean x, boolean y);
    private static native void nSetDefaultParameterFloat2(long nativeMaterial, String name, float x, float y);
    private static native void nSetDefaultParameterInt2(long nativeMaterial, String name, int x, int y);
    private static native void nSetDefaultParameterBool3(long nativeMaterial, String name, boolean x, boolean y, boolean z);
    private static native void nSetDefaultParameterFloat3(long nativeMaterial, String name, float x, float y, float z);
    private static native void nSetDefaultParameterInt3(long nativeMaterial, String name, int x, int y, int z);
    private static native void nSetDefaultParameterBool4(long nativeMaterial, String name, boolean x, boolean y, boolean z, boolean w);
    private static native void nSetDefaultParameterFloat4(long nativeMaterial, String name, float x, float y, float z, float w);
    private static native void nSetDefaultParameterInt4(long nativeMaterial, String name, int x, int y, int z, int w);
    private static native void nSetDefaultBooleanParameterArray(long nativeMaterial, String name, int type, boolean[] v, int offset, int count);
    private static native void nSetDefaultIntParameterArray(long nativeMaterial, String name, int type, int[] v, int offset, int count);
    private static native void nSetDefaultFloatParameterArray(long nativeMaterial, String name, int type, float[] v, int offset, int count);
    private static native void nSetDefaultParameterTexture(long nativeMaterial, String name, long nativeTexture, int sampler);
}
