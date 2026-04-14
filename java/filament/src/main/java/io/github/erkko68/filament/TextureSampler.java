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

package io.github.erkko68.filament;

import org.jetbrains.annotations.NotNull;

public class TextureSampler {

    public enum MinFilter {
        NEAREST,
        LINEAR,
        NEAREST_MIPMAP_NEAREST,
        LINEAR_MIPMAP_NEAREST,
        NEAREST_MIPMAP_LINEAR,
        LINEAR_MIPMAP_LINEAR
    }

    public enum MagFilter {
        NEAREST,
        LINEAR
    }

    public enum WrapMode {
        CLAMP_TO_EDGE,
        REPEAT,
        MIRRORED_REPEAT
    }

    public enum CompareMode {
        NONE,
        COMPARE_TO_TEXTURE
    }

    public enum CompareFunction {
        LESS_EQUAL,
        GREATER_EQUAL,
        LESS,
        GREATER,
        EQUAL,
        NOT_EQUAL,
        ALWAYS,
        NEVER
    }

    // package-private: used by MaterialInstance
    static final MinFilter[] sMinFilterValues = MinFilter.values();
    static final MagFilter[] sMagFilterValues = MagFilter.values();
    static final WrapMode[] sWrapModeValues = WrapMode.values();
    static final CompareMode[] sCompareModeValues = CompareMode.values();
    static final CompareFunction[] sCompareFunctionValues = CompareFunction.values();

    // The sampler state is packed into a long by native code.
    private long mSampler;

    /** Default: LINEAR_MIPMAP_LINEAR min, LINEAR mag, REPEAT wrap */
    public TextureSampler() {
        this(MinFilter.LINEAR_MIPMAP_LINEAR, MagFilter.LINEAR, WrapMode.REPEAT);
    }

    public TextureSampler(@NotNull MagFilter minMag) {
        this(minMag, WrapMode.CLAMP_TO_EDGE);
    }

    public TextureSampler(@NotNull MagFilter minMag, @NotNull WrapMode wrap) {
        this(minFilterFromMagFilter(minMag), minMag, wrap);
    }

    public TextureSampler(@NotNull MinFilter min, @NotNull MagFilter mag, @NotNull WrapMode wrap) {
        this(min, mag, wrap, wrap, wrap);
    }

    public TextureSampler(@NotNull MinFilter min, @NotNull MagFilter mag,
            @NotNull WrapMode s, @NotNull WrapMode t, @NotNull WrapMode r) {
        mSampler = nCreateSampler(min.ordinal(), mag.ordinal(),
                s.ordinal(), t.ordinal(), r.ordinal());
    }

    public TextureSampler(@NotNull CompareMode mode) {
        this(mode, CompareFunction.LESS_EQUAL);
    }

    public TextureSampler(@NotNull CompareMode mode, @NotNull CompareFunction function) {
        mSampler = nCreateCompareSampler(mode.ordinal(), function.ordinal());
    }

    // ---- Getters ----

    @NotNull
    public MinFilter getMinFilter() {
        return sMinFilterValues[nGetMinFilter(mSampler)];
    }

    @NotNull
    public MagFilter getMagFilter() {
        return sMagFilterValues[nGetMagFilter(mSampler)];
    }

    @NotNull
    public WrapMode getWrapModeS() {
        return sWrapModeValues[nGetWrapModeS(mSampler)];
    }

    @NotNull
    public WrapMode getWrapModeT() {
        return sWrapModeValues[nGetWrapModeT(mSampler)];
    }

    @NotNull
    public WrapMode getWrapModeR() {
        return sWrapModeValues[nGetWrapModeR(mSampler)];
    }

    public float getAnisotropy() {
        return nGetAnisotropy(mSampler);
    }

    @NotNull
    public CompareMode getCompareMode() {
        return sCompareModeValues[nGetCompareMode(mSampler)];
    }

    @NotNull
    public CompareFunction getCompareFunction() {
        return sCompareFunctionValues[nGetCompareFunction(mSampler)];
    }

    // ---- Setters ----

    public void setMinFilter(@NotNull MinFilter filter) {
        mSampler = nSetMinFilter(mSampler, filter.ordinal());
    }

    public void setMagFilter(@NotNull MagFilter filter) {
        mSampler = nSetMagFilter(mSampler, filter.ordinal());
    }

    public void setWrapModeS(@NotNull WrapMode mode) {
        mSampler = nSetWrapModeS(mSampler, mode.ordinal());
    }

    public void setWrapModeT(@NotNull WrapMode mode) {
        mSampler = nSetWrapModeT(mSampler, mode.ordinal());
    }

    public void setWrapModeR(@NotNull WrapMode mode) {
        mSampler = nSetWrapModeR(mSampler, mode.ordinal());
    }

    public void setAnisotropy(float anisotropy) {
        mSampler = nSetAnisotropy(mSampler, anisotropy);
    }

    public void setCompareMode(@NotNull CompareMode mode) {
        mSampler = nSetCompareMode(mSampler, mode.ordinal());
    }

    public void setCompareFunction(@NotNull CompareFunction function) {
        mSampler = nSetCompareFunction(mSampler, function.ordinal());
    }

    /** @return the packed native sampler state. */
    public long getSampler() {
        return mSampler;
    }

    private static MinFilter minFilterFromMagFilter(@NotNull MagFilter minMag) {
        switch (minMag) {
            case NEAREST: return MinFilter.NEAREST;
            case LINEAR:
            default:      return MinFilter.LINEAR;
        }
    }

    // ---- Natives ----
    private static native long nCreateSampler(int min, int mag, int s, int t, int r);
    private static native long nCreateCompareSampler(int mode, int function);

    private static native int  nGetMinFilter(long sampler);
    private static native long nSetMinFilter(long sampler, int filter);
    private static native int  nGetMagFilter(long sampler);
    private static native long nSetMagFilter(long sampler, int filter);

    private static native int  nGetWrapModeS(long sampler);
    private static native long nSetWrapModeS(long sampler, int mode);
    private static native int  nGetWrapModeT(long sampler);
    private static native long nSetWrapModeT(long sampler, int mode);
    private static native int  nGetWrapModeR(long sampler);
    private static native long nSetWrapModeR(long sampler, int mode);

    private static native float nGetAnisotropy(long sampler);
    private static native long  nSetAnisotropy(long sampler, float anisotropy);

    private static native int  nGetCompareMode(long sampler);
    private static native long nSetCompareMode(long sampler, int mode);
    private static native int  nGetCompareFunction(long sampler);
    private static native long nSetCompareFunction(long sampler, int function);
}
