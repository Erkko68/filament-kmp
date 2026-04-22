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

import io.github.erkko68.filament.jni.internal.NativeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

public class Texture {
    private long mNativeObject;

    public Texture(long nativeTexture) {
        mNativeObject = nativeTexture;
    }

    public enum Sampler {
        SAMPLER_2D,
        SAMPLER_2D_ARRAY,
        SAMPLER_CUBEMAP,
        SAMPLER_EXTERNAL,
        SAMPLER_3D
    }

    public enum InternalFormat {
        R8, R8_SNORM, R8UI, R8I, STENCIL8,
        R16F, R16UI, R16I,
        RG8, RG8_SNORM, RG8UI, RG8I,
        RGB565, RGB9_E5, RGB5_A1,
        RGBA4,
        DEPTH16,
        RGB8, SRGB8, RGB8_SNORM, RGB8UI, RGB8I,
        DEPTH24,
        R32F, R32UI, R32I,
        RG16F, RG16UI, RG16I,
        R11F_G11F_B10F,
        RGBA8, SRGB8_A8, RGBA8_SNORM,
        UNUSED,
        RGB10_A2, RGBA8UI, RGBA8I,
        DEPTH32F, DEPTH24_STENCIL8, DEPTH32F_STENCIL8,
        RGB16F, RGB16UI, RGB16I,
        RG32F, RG32UI, RG32I,
        RGBA16F, RA16UI, RGBA16I,
        RGB32F, RGB32UI, RGB32I,
        RGBA32F, RGBA32UI, RGBA32I,
        EAC_R11, EAC_R11_SIGNED, EAC_RG11, EAC_RG11_SIGNED,
        ETC2_RGB8, ETC2_SRGB8,
        ETC2_RGB8_A1, ETC2_SRGB8_A1,
        ETC2_EAC_RGBA8, ETC2_EAC_SRGBA8,
        DXT1_RGB, DXT1_RGBA, DXT3_RGBA, DXT5_RGBA,
        DXT1_SRGB, DXT1_SRGBA, DXT3_SRGBA, DXT5_SRGBA,
        RGBA_ASTC_4x4, RGBA_ASTC_5x4, RGBA_ASTC_5x5, RGBA_ASTC_6x5, RGBA_ASTC_6x6,
        RGBA_ASTC_8x5, RGBA_ASTC_8x6, RGBA_ASTC_8x8, RGBA_ASTC_10x5, RGBA_ASTC_10x6,
        RGBA_ASTC_10x8, RGBA_ASTC_10x10, RGBA_ASTC_12x10, RGBA_ASTC_12x12,
        SRGB8_ALPHA8_ASTC_4x4, SRGB8_ALPHA8_ASTC_5x4, SRGB8_ALPHA8_ASTC_5x5, SRGB8_ALPHA8_ASTC_6x5,
        SRGB8_ALPHA8_ASTC_6x6, SRGB8_ALPHA8_ASTC_8x5, SRGB8_ALPHA8_ASTC_8x6, SRGB8_ALPHA8_ASTC_8x8,
        SRGB8_ALPHA8_ASTC_10x5, SRGB8_ALPHA8_ASTC_10x6, SRGB8_ALPHA8_ASTC_10x8, SRGB8_ALPHA8_ASTC_10x10,
        SRGB8_ALPHA8_ASTC_12x10, SRGB8_ALPHA8_ASTC_12x12,
        RED_RGTC1, SIGNED_RED_RGTC1, RED_GREEN_RGTC2, SIGNED_RED_GREEN_RGTC2,
        RGB_BPTC_SIGNED_FLOAT, RGB_BPTC_UNSIGNED_FLOAT, RGBA_BPTC_UNORM, SRGB_ALPHA_BPTC_UNORM
    }

    public enum CubemapFace {
        POSITIVE_X, NEGATIVE_X, POSITIVE_Y, NEGATIVE_Y, POSITIVE_Z, NEGATIVE_Z
    }

    public enum Format {
        R, R_INTEGER, RG, RG_INTEGER, RGB, RGB_INTEGER, RGBA, RGBA_INTEGER,
        UNUSED, DEPTH_COMPONENT, DEPTH_STENCIL, STENCIL_INDEX, ALPHA
    }

    public enum Type {
        UBYTE, BYTE, USHORT, SHORT, UINT, INT, HALF, FLOAT,
        COMPRESSED, UINT_10F_11F_11F_REV, USHORT_565
    }

    public enum Swizzle {
        SUBSTITUTE_ZERO, SUBSTITUTE_ONE, CHANNEL_0, CHANNEL_1, CHANNEL_2, CHANNEL_3
    }

    public static class PixelBufferDescriptor {
        public Buffer storage;
        public Type type;
        public int alignment = 1;
        public int left = 0;
        public int top = 0;
        public int stride = 0;
        public Format format;
        public Object handler;
        public Runnable callback;

        public PixelBufferDescriptor(@NotNull Buffer storage, @NotNull Format format, @NotNull Type type, int alignment, int left, int top, int stride, @Nullable Object handler, @Nullable Runnable callback) {
            this.storage = storage;
            this.type = type;
            this.alignment = alignment;
            this.left = left;
            this.top = top;
            this.stride = stride;
            this.format = format;
            this.handler = handler;
            this.callback = callback;
        }

        public PixelBufferDescriptor(@NotNull Buffer storage, @NotNull Format format, @NotNull Type type) {
            this(storage, format, type, 1, 0, 0, 0, null, null);
        }

        public PixelBufferDescriptor(@NotNull Buffer storage, @NotNull Format format, @NotNull Type type, int alignment) {
            this(storage, format, type, alignment, 0, 0, 0, null, null);
        }

        public void setCallback(@Nullable Object handler, @Nullable Runnable callback) {
            this.handler = handler;
            this.callback = callback;
        }

        public static int computeDataSize(@NotNull Format format, @NotNull Type type, int stride, int height, int alignment) {
            if (type == Type.COMPRESSED) return 0;
            int n = 0;
            switch (format) {
                case R: case R_INTEGER: case DEPTH_COMPONENT: case ALPHA: n = 1; break;
                case RG: case RG_INTEGER: case DEPTH_STENCIL: case STENCIL_INDEX: n = 2; break;
                case RGB: case RGB_INTEGER: n = 3; break;
                case RGBA: case RGBA_INTEGER: n = 4; break;
                default: throw new IllegalStateException("unsupported format");
            }
            int bpp = n;
            switch (type) {
                case USHORT: case SHORT: case HALF: bpp *= 2; break;
                case UINT: case INT: case FLOAT: bpp *= 4; break;
                case UINT_10F_11F_11F_REV: bpp = 4; break;
                case USHORT_565: bpp = 2; break;
            }
            int bpr = (bpp * stride + (alignment - 1)) & -alignment;
            return bpr * height;
        }
    }

    public static class PrefilterOptions {
        public int sampleCount = 8;
        public boolean mirror = true;
    }

    public static class Builder {
        private final long mNativeBuilder;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeBuilder;
            BuilderCleanup(long nativeBuilder) { mNativeBuilder = nativeBuilder; }
            @Override public void run() { nDestroyBuilder(mNativeBuilder); }
        }

        @NotNull public Builder width(int width) { nBuilderWidth(mNativeBuilder, width); return this; }
        @NotNull public Builder height(int height) { nBuilderHeight(mNativeBuilder, height); return this; }
        @NotNull public Builder depth(int depth) { nBuilderDepth(mNativeBuilder, depth); return this; }
        @NotNull public Builder levels(int levels) { nBuilderLevels(mNativeBuilder, levels); return this; }
        @NotNull public Builder sampler(@NotNull Sampler sampler) { nBuilderSampler(mNativeBuilder, sampler.ordinal()); return this; }
        @NotNull public Builder format(@NotNull InternalFormat format) { nBuilderFormat(mNativeBuilder, format.ordinal()); return this; }
        @NotNull public Builder usage(int usage) { nBuilderUsage(mNativeBuilder, usage); return this; }
        @NotNull public Builder swizzle(@NotNull Swizzle r, @NotNull Swizzle g, @NotNull Swizzle b, @NotNull Swizzle a) { nBuilderSwizzle(mNativeBuilder, r.ordinal(), g.ordinal(), b.ordinal(), a.ordinal()); return this; }
        @NotNull public Builder samples(int samples) { nBuilderSamples(mNativeBuilder, samples); return this; }
        @NotNull public Builder importTexture(long id) { nBuilderImportTexture(mNativeBuilder, id); return this; }
        @NotNull public Builder external() { nBuilderExternal(mNativeBuilder); return this; }

        @NotNull public Texture build(@NotNull Engine engine) {
            long nativeTexture = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeTexture == 0) throw new IllegalStateException("Couldn't create Texture");
            return new Texture(nativeTexture);
        }
    }

    public int getWidth(int level) { return nGetWidth(getNativeObject(), level); }
    public int getHeight(int level) { return nGetHeight(getNativeObject(), level); }
    public int getDepth(int level) { return nGetDepth(getNativeObject(), level); }
    public int getLevels() { return nGetLevels(getNativeObject()); }
    public Sampler getTarget() { return Sampler.values()[nGetTarget(getNativeObject())]; }
    public InternalFormat getFormat() { return InternalFormat.values()[nGetFormat(getNativeObject())]; }

    public void setImage(@NotNull Engine engine, int level, @NotNull PixelBufferDescriptor buffer) {
        nSetImage(getNativeObject(), engine.getNativeObject(), level, 0, 0, 0, getWidth(level), getHeight(level), getDepth(level),
                buffer.storage, buffer.storage.remaining(), buffer.left, buffer.top, buffer.type.ordinal(), buffer.alignment, buffer.stride, buffer.format.ordinal(),
                buffer.handler, buffer.callback);
    }

    public void setImage(@NotNull Engine engine, int level, int x, int y, int width, int height, @NotNull PixelBufferDescriptor buffer) {
        nSetImage(getNativeObject(), engine.getNativeObject(), level, x, y, 0, width, height, 1,
                buffer.storage, buffer.storage.remaining(), buffer.left, buffer.top, buffer.type.ordinal(), buffer.alignment, buffer.stride, buffer.format.ordinal(),
                buffer.handler, buffer.callback);
    }

    public void setImage(@NotNull Engine engine, int level, int x, int y, int z, int width, int height, int depth, @NotNull PixelBufferDescriptor buffer) {
        nSetImage(getNativeObject(), engine.getNativeObject(), level, x, y, z, width, height, depth,
                buffer.storage, buffer.storage.remaining(), buffer.left, buffer.top, buffer.type.ordinal(), buffer.alignment, buffer.stride, buffer.format.ordinal(),
                buffer.handler, buffer.callback);
    }

    public void setCubemapLayer(@NotNull Engine engine, int level, @NotNull CubemapFace face, @NotNull PixelBufferDescriptor buffer) {
        nSetImage(getNativeObject(), engine.getNativeObject(), level, 0, 0, face.ordinal(), getWidth(level), getHeight(level), 1,
                buffer.storage, buffer.storage.remaining(), buffer.left, buffer.top, buffer.type.ordinal(), buffer.alignment, buffer.stride, buffer.format.ordinal(),
                buffer.handler, buffer.callback);
    }

    public void generateMipmaps(@NotNull Engine engine) { nGenerateMipmaps(getNativeObject(), engine.getNativeObject()); }
    public void generatePrefilterMipmap(@NotNull Engine engine, @NotNull PixelBufferDescriptor buffer, @NotNull int[] offsets, @NotNull PrefilterOptions options) {
        nGeneratePrefilterMipmap(getNativeObject(), engine.getNativeObject(), buffer.storage, buffer.storage.remaining(), buffer.left, buffer.top, buffer.type.ordinal(), buffer.alignment, buffer.stride, buffer.format.ordinal(), buffer.handler, buffer.callback, offsets, options.sampleCount, options.mirror);
    }

    public static boolean isTextureFormatSupported(@NotNull Engine engine, @NotNull InternalFormat format) { return nIsTextureFormatSupported(engine.getNativeObject(), format.ordinal()); }
    public static boolean isTextureFormatMipmappable(@NotNull Engine engine, @NotNull InternalFormat format) { return nIsTextureFormatMipmappable(engine.getNativeObject(), format.ordinal()); }
    public static boolean isTextureSwizzleSupported(@NotNull Engine engine) { return nIsTextureSwizzleSupported(engine.getNativeObject()); }

    public static boolean validatePixelFormatAndType(@NotNull InternalFormat internalFormat, @NotNull Format pixelDataFormat, @NotNull Type pixelDataType) {
        return nValidatePixelFormatAndType(internalFormat.ordinal(), pixelDataFormat.ordinal(), pixelDataType.ordinal());
    }

    public static int getMaxTextureSize(@NotNull Engine engine, Sampler type) {
        return nGetMaxTextureSize(engine.getNativeObject(), type.ordinal());
    }

    public static int getMaxArrayTextureLayers(@NotNull Engine engine) {
        return nGetMaxArrayTextureLayers(engine.getNativeObject());
    }

    public void setExternalStream(@NotNull Engine engine, @NotNull Stream stream) {
        nSetExternalStream(getNativeObject(), engine.getNativeObject(), stream.getNativeObject());
    }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Calling method on destroyed Texture");
        return mNativeObject;
    }

    void clearNativeObject() { mNativeObject = 0; }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderWidth(long nativeBuilder, int width);
    private static native void nBuilderHeight(long nativeBuilder, int height);
    private static native void nBuilderDepth(long nativeBuilder, int depth);
    private static native void nBuilderLevels(long nativeBuilder, int levels);
    private static native void nBuilderSampler(long nativeBuilder, int sampler);
    private static native void nBuilderFormat(long nativeBuilder, int format);
    private static native void nBuilderUsage(long nativeBuilder, int usage);
    private static native void nBuilderSwizzle(long nativeBuilder, int r, int g, int b, int a);
    private static native void nBuilderSamples(long nativeBuilder, int samples);
    private static native void nBuilderImportTexture(long nativeBuilder, long id);
    private static native void nBuilderExternal(long nativeBuilder);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);

    private static native int nGetWidth(long nativeTexture, int level);
    private static native int nGetHeight(long nativeTexture, int level);
    private static native int nGetDepth(long nativeTexture, int level);
    private static native int nGetLevels(long nativeTexture);
    private static native int nGetTarget(long nativeTexture);
    private static native int nGetFormat(long nativeTexture);

    private static native void nSetImage(long nativeTexture, long nativeEngine, int level, int x, int y, int z, int w, int h, int d, Buffer storage, int remaining, int left, int top, int type, int alignment, int stride, int format, Object handler, Runnable callback);
    private static native void nGenerateMipmaps(long nativeTexture, long nativeEngine);
    private static native void nGeneratePrefilterMipmap(long nativeTexture, long nativeEngine, Buffer storage, int remaining, int left, int top, int type, int alignment, int stride, int format, Object handler, Runnable callback, int[] offsets, int sampleCount, boolean mirror);

    private static native boolean nIsTextureFormatSupported(long nativeEngine, int format);
    private static native boolean nIsTextureFormatMipmappable(long nativeEngine, int format);
    private static native boolean nIsTextureSwizzleSupported(long nativeEngine);
    private static native boolean nValidatePixelFormatAndType(int internalFormat, int pixelDataFormat, int pixelDataType);
    private static native int nGetMaxTextureSize(long nativeEngine, int sampler);
    private static native int nGetMaxArrayTextureLayers(long nativeEngine);
    private static native void nSetExternalStream(long nativeTexture, long nativeEngine, long nativeStream);
    // devicePtr: MTLDevice* (macOS) or VkDevice* (Linux/Windows) from Skiko.
    // physDevicePtr: VkPhysicalDevice* (Linux/Windows only; ignored on macOS).
    // Returns the GPU-native handle: MTLTexture* on Metal, VkImage on Vulkan.
    public static native long nCreateSharedTexture(long devicePtr, long physDevicePtr, int width, int height);
    public static native void nReleaseSharedTexture(long handle);
}
