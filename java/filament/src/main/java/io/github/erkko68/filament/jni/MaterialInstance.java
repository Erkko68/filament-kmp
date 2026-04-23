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

/**
 * An instance of a {@link Material}.
 */
public class MaterialInstance {
    private Material mMaterial;
    private long mNativeObject;
    private long mNativeMaterial;

    public enum BooleanElement { BOOL, BOOL2, BOOL3, BOOL4 }
    public enum IntElement { INT, INT2, INT3, INT4 }
    public enum FloatElement { FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, MAT4 }

    public enum StencilOperation { KEEP, ZERO, REPLACE, INCR_CLAMP, INCR_WRAP, DECR_CLAMP, DECR_WRAP, INVERT }
    public enum StencilFace { FRONT, BACK, FRONT_AND_BACK }
    static final int[] sStencilFaceMapping = {0x1, 0x2, 0x3};

    public MaterialInstance(@NotNull Engine engine, long nativeMaterialInstance) {
        mNativeObject = nativeMaterialInstance;
        mNativeMaterial = nGetMaterial(mNativeObject);
    }

    public MaterialInstance(@NotNull Material material, long nativeMaterialInstance) {
        mMaterial = material;
        mNativeMaterial = material.getNativeObject();
        mNativeObject = nativeMaterialInstance;
    }

    public MaterialInstance(long nativeMaterialInstance) {
        mNativeObject = nativeMaterialInstance;
        mNativeMaterial = nGetMaterial(mNativeObject);
    }

    @NotNull public static MaterialInstance duplicate(@NotNull MaterialInstance other, @Nullable String name) {
        long nativeInstance = nDuplicate(other.mNativeObject, name);
        if (nativeInstance == 0) throw new IllegalStateException("Couldn't duplicate MaterialInstance");
        return new MaterialInstance(other.getMaterial(), nativeInstance);
    }

    @NotNull public Material getMaterial() {
        if (mMaterial == null) mMaterial = new Material(mNativeMaterial);
        return mMaterial;
    }

    @NotNull public String getName() { return nGetName(getNativeObject()); }

    public void setParameter(@NotNull String name, boolean x) { nSetParameterBool(getNativeObject(), name, x); }
    public void setParameter(@NotNull String name, float x) { nSetParameterFloat(getNativeObject(), name, x); }
    public void setParameter(@NotNull String name, int x) { nSetParameterInt(getNativeObject(), name, x); }
    public void setParameter(@NotNull String name, boolean x, boolean y) { nSetParameterBool2(getNativeObject(), name, x, y); }
    public void setParameter(@NotNull String name, float x, float y) { nSetParameterFloat2(getNativeObject(), name, x, y); }
    public void setParameter(@NotNull String name, int x, int y) { nSetParameterInt2(getNativeObject(), name, x, y); }
    public void setParameter(@NotNull String name, boolean x, boolean y, boolean z) { nSetParameterBool3(getNativeObject(), name, x, y, z); }
    public void setParameter(@NotNull String name, float x, float y, float z) { nSetParameterFloat3(getNativeObject(), name, x, y, z); }
    public void setParameter(@NotNull String name, int x, int y, int z) { nSetParameterInt3(getNativeObject(), name, x, y, z); }
    public void setParameter(@NotNull String name, boolean x, boolean y, boolean z, boolean w) { nSetParameterBool4(getNativeObject(), name, x, y, z, w); }
    public void setParameter(@NotNull String name, float x, float y, float z, float w) { nSetParameterFloat4(getNativeObject(), name, x, y, z, w); }
    public void setParameter(@NotNull String name, int x, int y, int z, int w) { nSetParameterInt4(getNativeObject(), name, x, y, z, w); }

    public void setParameter(@NotNull String name, @NotNull Colors.RgbType type, float r, float g, float b) {
        float[] color = Colors.toLinear(type, r, g, b);
        nSetParameterFloat3(getNativeObject(), name, color[0], color[1], color[2]);
    }

    public void setParameter(@NotNull String name, @NotNull Colors.RgbaType type, float r, float g, float b, float a) {
        float[] color = Colors.toLinear(type, r, g, b, a);
        nSetParameterFloat4(getNativeObject(), name, color[0], color[1], color[2], color[3]);
    }

    public void setParameter(@NotNull String name, @NotNull Texture texture, @NotNull TextureSampler sampler) {
        nSetParameterTexture(getNativeObject(), name, texture.getNativeObject(), (int) sampler.getSampler());
    }

    public void setParameter(@NotNull String name, @NotNull BooleanElement type, @NotNull boolean[] v, int offset, int count) {
        nSetBooleanParameterArray(getNativeObject(), name, type.ordinal(), v, offset, count);
    }

    public void setParameter(@NotNull String name, @NotNull IntElement type, @NotNull int[] v, int offset, int count) {
        nSetIntParameterArray(getNativeObject(), name, type.ordinal(), v, offset, count);
    }

    public void setParameter(@NotNull String name, @NotNull FloatElement type, @NotNull float[] v, int offset, int count) {
        nSetFloatParameterArray(getNativeObject(), name, type.ordinal(), v, offset, count);
    }

    public void setScissor(int left, int bottom, int width, int height) { nSetScissor(getNativeObject(), left, bottom, width, height); }
    public void unsetScissor() { nUnsetScissor(getNativeObject()); }

    public void setPolygonOffset(float scale, float constant) { nSetPolygonOffset(getNativeObject(), scale, constant); }

    public void setMaskThreshold(float threshold) { nSetMaskThreshold(getNativeObject(), threshold); }
    public float getMaskThreshold() { return nGetMaskThreshold(getNativeObject()); }

    public void setSpecularAntiAliasingVariance(float variance) { nSetSpecularAntiAliasingVariance(getNativeObject(), variance); }
    public float getSpecularAntiAliasingVariance() { return nGetSpecularAntiAliasingVariance(getNativeObject()); }

    public void setSpecularAntiAliasingThreshold(float threshold) { nSetSpecularAntiAliasingThreshold(getNativeObject(), threshold); }
    public float getSpecularAntiAliasingThreshold() { return nGetSpecularAntiAliasingThreshold(getNativeObject()); }

    public void setDoubleSided(boolean doubleSided) { nSetDoubleSided(getNativeObject(), doubleSided); }
    public void setTransparencyMode(@NotNull Material.TransparencyMode mode) { nSetTransparencyMode(getNativeObject(), mode.ordinal()); }
    public boolean isDoubleSided() { return nIsDoubleSided(getNativeObject()); }
    @NotNull public Material.TransparencyMode getTransparencyMode() { return Material.EnumCache.sTransparencyModeValues[nGetTransparencyMode(getNativeObject())]; }

    public void setCullingMode(@NotNull Material.CullingMode mode) { nSetCullingMode(getNativeObject(), mode.ordinal()); }
    public void setCullingMode(@NotNull Material.CullingMode colorPassCullingMode, @NotNull Material.CullingMode shadowPassCullingMode) {
        nSetCullingModeSeparate(getNativeObject(), colorPassCullingMode.ordinal(), shadowPassCullingMode.ordinal());
    }
    @NotNull public Material.CullingMode getCullingMode() { return Material.EnumCache.sCullingModeValues[nGetCullingMode(getNativeObject())]; }
    @NotNull public Material.CullingMode getShadowCullingMode() { return Material.EnumCache.sCullingModeValues[nGetShadowCullingMode(getNativeObject())]; }

    public void setColorWrite(boolean enable) { nSetColorWrite(getNativeObject(), enable); }
    public boolean isColorWriteEnabled() { return nIsColorWriteEnabled(getNativeObject()); }

    public void setDepthWrite(boolean enable) { nSetDepthWrite(getNativeObject(), enable); }
    public boolean isDepthWriteEnabled() { return nIsDepthWriteEnabled(getNativeObject()); }

    public void setStencilWrite(boolean enable) { nSetStencilWrite(getNativeObject(), enable); }
    public boolean isStencilWriteEnabled() { return nIsStencilWriteEnabled(getNativeObject()); }

    public void setDepthCulling(boolean enable) { nSetDepthCulling(getNativeObject(), enable); }
    public void setDepthFunc(@NotNull TextureSampler.CompareFunction func) { nSetDepthFunc(getNativeObject(), func.ordinal()); }
    public boolean isDepthCullingEnabled() { return nIsDepthCullingEnabled(getNativeObject()); }
    @NotNull public TextureSampler.CompareFunction getDepthFunc() { return TextureSampler.sCompareFunctionValues[nGetDepthFunc(getNativeObject())]; }

    public void setStencilCompareFunction(@NotNull TextureSampler.CompareFunction func, @NotNull StencilFace face) {
        nSetStencilCompareFunction(getNativeObject(), func.ordinal(), sStencilFaceMapping[face.ordinal()]);
    }
    public void setStencilCompareFunction(@NotNull TextureSampler.CompareFunction func) { setStencilCompareFunction(func, StencilFace.FRONT_AND_BACK); }

    public void setStencilOpStencilFail(@NotNull StencilOperation op, @NotNull StencilFace face) {
        nSetStencilOpStencilFail(getNativeObject(), op.ordinal(), sStencilFaceMapping[face.ordinal()]);
    }
    public void setStencilOpStencilFail(@NotNull StencilOperation op) { setStencilOpStencilFail(op, StencilFace.FRONT_AND_BACK); }

    public void setStencilOpDepthFail(@NotNull StencilOperation op, @NotNull StencilFace face) {
        nSetStencilOpDepthFail(getNativeObject(), op.ordinal(), sStencilFaceMapping[face.ordinal()]);
    }
    public void setStencilOpDepthFail(@NotNull StencilOperation op) { setStencilOpDepthFail(op, StencilFace.FRONT_AND_BACK); }

    public void setStencilOpDepthStencilPass(@NotNull StencilOperation op, @NotNull StencilFace face) {
        nSetStencilOpDepthStencilPass(getNativeObject(), op.ordinal(), sStencilFaceMapping[face.ordinal()]);
    }
    public void setStencilOpDepthStencilPass(@NotNull StencilOperation op) { setStencilOpDepthStencilPass(op, StencilFace.FRONT_AND_BACK); }

    public void setStencilReference(int value, @NotNull StencilFace face) {
        nSetStencilReference(getNativeObject(), value, sStencilFaceMapping[face.ordinal()]);
    }
    public void setStencilReference(int value) { setStencilReference(value, StencilFace.FRONT_AND_BACK); }

    public void setStencilReferenceValue(int value, @NotNull StencilFace face) {
        setStencilReference(value, face);
    }
    public void setStencilReferenceValue(int value) {
        setStencilReference(value, StencilFace.FRONT_AND_BACK);
    }

    public void setStencilReadMask(int mask, @NotNull StencilFace face) {
        nSetStencilReadMask(getNativeObject(), mask, sStencilFaceMapping[face.ordinal()]);
    }
    public void setStencilReadMask(int mask) { setStencilReadMask(mask, StencilFace.FRONT_AND_BACK); }

    public void setStencilWriteMask(int mask, @NotNull StencilFace face) {
        nSetStencilWriteMask(getNativeObject(), mask, sStencilFaceMapping[face.ordinal()]);
    }
    public void setStencilWriteMask(int mask) { setStencilWriteMask(mask, StencilFace.FRONT_AND_BACK); }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Calling method on destroyed MaterialInstance");
        return mNativeObject;
    }

    void clearNativeObject() { mNativeObject = 0; }

    private static native long nGetMaterial(long nativeInstance);
    private static native String nGetName(long nativeInstance);
    private static native long nDuplicate(long nativeInstance, String name);

    private static native void nSetParameterBool(long nativeInstance, String name, boolean x);
    private static native void nSetParameterFloat(long nativeInstance, String name, float x);
    private static native void nSetParameterInt(long nativeInstance, String name, int x);
    private static native void nSetParameterBool2(long nativeInstance, String name, boolean x, boolean y);
    private static native void nSetParameterFloat2(long nativeInstance, String name, float x, float y);
    private static native void nSetParameterInt2(long nativeInstance, String name, int x, int y);
    private static native void nSetParameterBool3(long nativeInstance, String name, boolean x, boolean y, boolean z);
    private static native void nSetParameterFloat3(long nativeInstance, String name, float x, float y, float z);
    private static native void nSetParameterInt3(long nativeInstance, String name, int x, int y, int z);
    private static native void nSetParameterBool4(long nativeInstance, String name, boolean x, boolean y, boolean z, boolean w);
    private static native void nSetParameterFloat4(long nativeInstance, String name, float x, float y, float z, float w);
    private static native void nSetParameterInt4(long nativeInstance, String name, int x, int y, int z, int w);
    private static native void nSetParameterTexture(long nativeInstance, String name, long nativeTexture, int sampler);
    private static native void nSetBooleanParameterArray(long nativeInstance, String name, int type, boolean[] v, int offset, int count);
    private static native void nSetIntParameterArray(long nativeInstance, String name, int type, int[] v, int offset, int count);
    private static native void nSetFloatParameterArray(long nativeInstance, String name, int type, float[] v, int offset, int count);

    private static native void nSetScissor(long nativeInstance, int left, int bottom, int width, int height);
    private static native void nUnsetScissor(long nativeInstance);
    private static native void nSetPolygonOffset(long nativeInstance, float scale, float constant);
    private static native void nSetMaskThreshold(long nativeInstance, float threshold);
    private static native float nGetMaskThreshold(long nativeInstance);
    private static native void nSetSpecularAntiAliasingVariance(long nativeInstance, float variance);
    private static native float nGetSpecularAntiAliasingVariance(long nativeInstance);
    private static native void nSetSpecularAntiAliasingThreshold(long nativeInstance, float threshold);
    private static native float nGetSpecularAntiAliasingThreshold(long nativeInstance);
    private static native void nSetDoubleSided(long nativeInstance, boolean doubleSided);
    private static native void nSetTransparencyMode(long nativeInstance, int mode);
    private static native boolean nIsDoubleSided(long nativeInstance);
    private static native int nGetTransparencyMode(long nativeInstance);
    private static native void nSetCullingMode(long nativeInstance, int mode);
    private static native void nSetCullingModeSeparate(long nativeInstance, int colorMode, int shadowMode);
    private static native int nGetCullingMode(long nativeInstance);
    private static native int nGetShadowCullingMode(long nativeInstance);
    private static native void nSetColorWrite(long nativeInstance, boolean enable);
    private static native boolean nIsColorWriteEnabled(long nativeInstance);
    private static native void nSetDepthWrite(long nativeInstance, boolean enable);
    private static native boolean nIsDepthWriteEnabled(long nativeInstance);
    private static native void nSetStencilWrite(long nativeInstance, boolean enable);
    private static native boolean nIsStencilWriteEnabled(long nativeInstance);
    private static native void nSetDepthCulling(long nativeInstance, boolean enable);
    private static native void nSetDepthFunc(long nativeInstance, int func);
    private static native boolean nIsDepthCullingEnabled(long nativeInstance);
    private static native int nGetDepthFunc(long nativeInstance);
    private static native void nSetStencilCompareFunction(long nativeInstance, int func, int face);
    private static native void nSetStencilOpStencilFail(long nativeInstance, int op, int face);
    private static native void nSetStencilOpDepthFail(long nativeInstance, int op, int face);
    private static native void nSetStencilOpDepthStencilPass(long nativeInstance, int op, int face);
    private static native void nSetStencilReference(long nativeInstance, int value, int face);
    private static native void nSetStencilReadMask(long nativeInstance, int mask, int face);
    private static native void nSetStencilWriteMask(long nativeInstance, int mask, int face);
}
