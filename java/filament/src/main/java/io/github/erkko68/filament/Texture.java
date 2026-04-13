package io.github.erkko68.filament;

import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.util.concurrent.Executor;

public class Texture {
    private long mNativeObject;

    private Texture(long nativeTexture) {
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
        // Core formats
        R8, R8_SNORM, R8UI, R8I, STENCIL8,
        R16F, R16UI, R16I,
        R32F, R32UI, R32I,
        RG8, RG8_SNORM, RG8UI, RG8I,
        RG16F, RG16UI, RG16I,
        RG32F, RG32UI, RG32I,
        RGB8, RGB8_SNORM, RGB8UI, RGB8I,
        RGB16F, RGB16UI, RGB16I,
        RGB32F, RGB32UI, RGB32I,
        RGBA8, RGBA8_SNORM, RGBA8UI, RGBA8I,
        RGBA16F, RA16UI, RGBA16I,
        RGBA32F, RGBA32UI, RGBA32I,
        
        // Depth/Stencil
        DEPTH16, DEPTH24, DEPTH32F, DEPTH24_STENCIL8, DEPTH32F_STENCIL8,

        // Sized formats
        RGB565, RGB5_A1, RGBA4, RGB10_A2,

        // Compressed
        EAC_R11, EAC_R11_SIGNED, EAC_RG11, EAC_RG11_SIGNED,
        ETC2_RGB8, ETC2_SRGB8, ETC2_RGB8_A1, ETC2_SRGB8_A1, ETC2_RGBA8, ETC2_SRGB8_A8,
        ASTC_4x4, ASTC_5x4, ASTC_5x5, ASTC_6x5, ASTC_6x6, ASTC_8x5, ASTC_8x6, ASTC_8x8,
        ASTC_10x5, ASTC_10x6, ASTC_10x8, ASTC_10x10, ASTC_12x10, ASTC_12x12,
        ASTC_3x3x3, ASTC_4x3x3, ASTC_4x4x3, ASTC_4x4x4, ASTC_5x4x4, ASTC_5x5x4, ASTC_5x5x5,
        SRGB8_ETC2, SRGB8_ALPHA8_ETC2, SRGB8_PUNCHTHROUGH_ALPHA1_ETC2,
        RGBA_ASTC_4x4, RGBA_ASTC_5x4, RGBA_ASTC_5x5, RGBA_ASTC_6x5, RGBA_ASTC_6x6,
        RGBA_ASTC_8x5, RGBA_ASTC_8x6, RGBA_ASTC_8x8, RGBA_ASTC_10x5, RGBA_ASTC_10x6,
        RGBA_ASTC_10x8, RGBA_ASTC_10x10, RGBA_ASTC_12x10, RGBA_ASTC_12x12,
    }

    public enum Format {
        R, R_INTEGER, RG, RG_INTEGER, RGB, RGB_INTEGER, RGBA, RGBA_INTEGER,
        UNUSED, DEPTH_COMPONENT, DEPTH_STENCIL, STENCIL_INDEX
    }

    public enum Type {
        UBYTE, BYTE, USHORT, SHORT, UINT, INT, HALF, FLOAT,
        UINT_10F_11F_11F_REV, USHORT_565, USHORT_5551, USHORT_4444, UINT_24_8, UINT_10_10_10_2,
        COMPRESSED
    }

    public enum Usage {
        DEFAULT(0x1),
        COLOR_ATTACHMENT(0x2),
        DEPTH_ATTACHMENT(0x4),
        STENCIL_ATTACHMENT(0x8),
        UPLOADABLE(0x10),
        SAMPLEABLE(0x20);

        private final int value;
        Usage(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    public static class Builder {
        private final long mNativeBuilder;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
        }

        public Builder width(int width) {
            nBuilderWidth(mNativeBuilder, width);
            return this;
        }

        public Builder height(int height) {
            nBuilderHeight(mNativeBuilder, height);
            return this;
        }

        public Builder depth(int depth) {
            nBuilderDepth(mNativeBuilder, depth);
            return this;
        }

        public Builder levels(int levels) {
            nBuilderLevels(mNativeBuilder, levels);
            return this;
        }

        public Builder sampler(Sampler sampler) {
            nBuilderSampler(mNativeBuilder, sampler.ordinal());
            return this;
        }

        public Builder format(InternalFormat format) {
            nBuilderFormat(mNativeBuilder, format.ordinal());
            return this;
        }

        public Builder usage(int usage) {
            nBuilderUsage(mNativeBuilder, usage);
            return this;
        }

        public Texture build(Engine engine) {
            long nativeTexture = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeTexture == 0) throw new IllegalStateException("Couldn't create Texture");
            return new Texture(nativeTexture);
        }
    }

    public int getWidth(int level) {
        return nGetWidth(getNativeObject(), level);
    }

    public int getHeight(int level) {
        return nGetHeight(getNativeObject(), level);
    }

    public int getDepth(int level) {
        return nGetDepth(getNativeObject(), level);
    }

    public int getLevels() {
        return nGetLevels(getNativeObject());
    }

    public void setImage(Engine engine, int level, PixelBufferDescriptor buffer) {
        setImage(getNativeObject(), engine.getNativeObject(), level,
                0, 0, 0, getWidth(level), getHeight(level), getDepth(level),
                buffer.storage, buffer.storage.remaining(),
                buffer.left, buffer.top, buffer.type.ordinal(), buffer.alignment, buffer.stride, buffer.format.ordinal(),
                buffer.executor, buffer.callback);
    }

    public static class PixelBufferDescriptor {
        public Buffer storage;
        public int left;
        public int top;
        public Type type;
        public int alignment = 1;
        public int stride;
        public Format format;
        public Executor executor;
        public Runnable callback;

        public PixelBufferDescriptor(Buffer storage, Format format, Type type) {
            this(storage, format, type, 1, 0, 0, null, null);
        }

        public PixelBufferDescriptor(Buffer storage, Format format, Type type, int alignment, int left, int top, Executor executor, Runnable callback) {
            this.storage = storage;
            this.format = format;
            this.type = type;
            this.alignment = alignment;
            this.left = left;
            this.top = top;
            this.executor = executor;
            this.callback = callback;
        }
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed Texture");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderWidth(long nativeBuilder, int width);
    private static native void nBuilderHeight(long nativeBuilder, int height);
    private static native void nBuilderDepth(long nativeBuilder, int depth);
    private static native void nBuilderLevels(long nativeBuilder, int levels);
    private static native void nBuilderSampler(long nativeBuilder, int sampler);
    private static native void nBuilderFormat(long nativeBuilder, int format);
    private static native void nBuilderUsage(long nativeBuilder, int usage);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);

    private static native int nGetWidth(long nativeTexture, int level);
    private static native int nGetHeight(long nativeTexture, int level);
    private static native int nGetDepth(long nativeTexture, int level);
    private static native int nGetLevels(long nativeTexture);

    private static native int setImage(long nativeTexture, long nativeEngine, int level,
                                        int xoffset, int yoffset, int zoffset, int width, int height, int depth,
                                        Buffer storage, int remaining,
                                        int left, int top, int type, int alignment, int stride, int format,
                                        Object handler, Runnable callback);
}
