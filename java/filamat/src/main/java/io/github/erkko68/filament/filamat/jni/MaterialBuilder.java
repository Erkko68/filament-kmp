package io.github.erkko68.filament.filamat.jni;

import io.github.erkko68.filament.jni.Engine;
import io.github.erkko68.filament.jni.internal.NativeRegistry;

import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MaterialBuilder {
    private static boolean sInitialized = false;

    private final long mNativeBuilder;
    private final Cleaner.Cleanable mCleanable;

    public enum Shading {
        UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS
    }

    public enum Interpolation {
        SMOOTH, FLAT
    }

    public enum BlendingMode {
        OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN
    }

    public enum MaterialDomain {
        SURFACE, POST_PROCESS
    }

    public enum VertexDomain {
        OBJECT, WORLD, VIEW, DEVICE
    }

    public enum CullingMode {
        NONE, FRONT, BACK, FRONT_AND_BACK
    }

    public enum TransparencyMode {
        DEFAULT, TWO_PASSES_ONE_SIDE, TWO_PASSES_TWO_SIDES
    }

    public enum SpecularAmbientOcclusion {
        NONE, SIMPLE, BENT_NORMALS
    }

    public enum RefractionMode {
        NONE, CUBEMAP, SCREEN_SPACE
    }

    public enum ReflectionMode {
        DEFAULT, SCREEN_SPACE
    }

    public enum RefractionType {
        SOLID, THIN
    }

    public enum Platform {
        DESKTOP, MOBILE, ALL
    }

    public enum Optimization {
        NONE, PREPROCESSOR, SIZE, PERFORMANCE
    }

    public enum UniformType {
        BOOL, BOOL2, BOOL3, BOOL4,
        FLOAT, FLOAT2, FLOAT3, FLOAT4,
        INT, INT2, INT3, INT4,
        UINT, UINT2, UINT3, UINT4,
        MAT3, MAT4
    }

    public enum SamplerType {
        SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D
    }

    public enum SamplerFormat {
        INT, UINT, FLOAT, SHADOW
    }

    public enum ParameterPrecision {
        LOW, MEDIUM, HIGH, DEFAULT
    }

    public enum Variable {
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3
    }

    public enum VertexAttribute {
        POSITION, TANGENTS, COLOR, UV0, UV1, BONE_INDICES, BONE_WEIGHTS, UNUSED,
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4, CUSTOM5, CUSTOM6, CUSTOM7
    }

    public enum TargetApi {
        OPENGL(1), VULKAN(2), METAL(4), WEBGPU(8), ALL(7); // prebuilt compiled without FILAMENT_SUPPORTS_WEBGPU; ALL =
                                                           // OpenGL|Vulkan|Metal

        final int bit;

        TargetApi(int bit) {
            this.bit = bit;
        }
    }

    public static synchronized void init() {
        if (sInitialized) return;
        // All JNI symbols live in the combined filament-jni dylib loaded here.
        io.github.erkko68.filament.jni.Filament.init();
        nMaterialBuilderInit();
        sInitialized = true;
    }

    public static void shutdown() {
        nMaterialBuilderShutdown();
        sInitialized = false;
    }

    public MaterialBuilder() {
        mNativeBuilder = nCreateMaterialBuilder();
        mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
    }

    public MaterialBuilder name(@NotNull String name) {
        nMaterialBuilderName(mNativeBuilder, name);
        return this;
    }

    public MaterialBuilder shading(@NotNull Shading shading) {
        nMaterialBuilderShading(mNativeBuilder, shading.ordinal());
        return this;
    }

    public MaterialBuilder interpolation(@NotNull Interpolation interpolation) {
        nMaterialBuilderInterpolation(mNativeBuilder, interpolation.ordinal());
        return this;
    }

    public MaterialBuilder blending(@NotNull BlendingMode mode) {
        nMaterialBuilderBlending(mNativeBuilder, mode.ordinal());
        return this;
    }

    public MaterialBuilder postLightingBlending(@NotNull BlendingMode mode) {
        nMaterialBuilderPostLightingBlending(mNativeBuilder, mode.ordinal());
        return this;
    }

    public MaterialBuilder materialDomain(@NotNull MaterialDomain domain) {
        nMaterialBuilderMaterialDomain(mNativeBuilder, domain.ordinal());
        return this;
    }

    public MaterialBuilder vertexDomain(@NotNull VertexDomain domain) {
        nMaterialBuilderVertexDomain(mNativeBuilder, domain.ordinal());
        return this;
    }

    public MaterialBuilder material(@NotNull String code) {
        nMaterialBuilderMaterial(mNativeBuilder, code);
        return this;
    }

    public MaterialBuilder materialVertex(@NotNull String code) {
        nMaterialBuilderMaterialVertex(mNativeBuilder, code);
        return this;
    }

    public MaterialBuilder targetApi(@NotNull TargetApi api) {
        nMaterialBuilderTargetApi(mNativeBuilder, api.bit);
        return this;
    }

    public MaterialBuilder optimization(@NotNull Optimization optimization) {
        nMaterialBuilderOptimization(mNativeBuilder, optimization.ordinal());
        return this;
    }

    public MaterialBuilder platform(@NotNull Platform platform) {
        nMaterialBuilderPlatform(mNativeBuilder, platform.ordinal());
        return this;
    }

    public MaterialBuilder uniformParameter(@NotNull UniformType type, @NotNull String name) {
        nMaterialBuilderUniformParameter(mNativeBuilder, type.ordinal(), ParameterPrecision.DEFAULT.ordinal(), name);
        return this;
    }

    public MaterialBuilder uniformParameter(@NotNull UniformType type, @NotNull ParameterPrecision precision,
            @NotNull String name) {
        nMaterialBuilderUniformParameter(mNativeBuilder, type.ordinal(), precision.ordinal(), name);
        return this;
    }

    public MaterialBuilder uniformParameterArray(@NotNull UniformType type, int size, @NotNull String name) {
        return uniformParameterArray(type, size, ParameterPrecision.DEFAULT, name);
    }

    public MaterialBuilder uniformParameterArray(@NotNull UniformType type, int size,
            @NotNull ParameterPrecision precision, @NotNull String name) {
        nMaterialBuilderUniformParameterArray(mNativeBuilder, type.ordinal(), size, precision.ordinal(), name);
        return this;
    }

    public MaterialBuilder samplerParameter(@NotNull SamplerType type, @NotNull SamplerFormat format,
            @NotNull ParameterPrecision precision, @NotNull String name) {
        nMaterialBuilderSamplerParameter(mNativeBuilder, type.ordinal(), format.ordinal(), precision.ordinal(), name);
        return this;
    }

    public MaterialBuilder variable(@NotNull Variable variable, @NotNull String name) {
        nMaterialBuilderVariable(mNativeBuilder, variable.ordinal(), name);
        return this;
    }

    public MaterialBuilder require(@NotNull VertexAttribute attribute) {
        nMaterialBuilderRequire(mNativeBuilder, attribute.ordinal());
        return this;
    }

    public MaterialBuilder colorWrite(boolean enable) {
        nMaterialBuilderColorWrite(mNativeBuilder, enable);
        return this;
    }

    public MaterialBuilder depthWrite(boolean enable) {
        nMaterialBuilderDepthWrite(mNativeBuilder, enable);
        return this;
    }

    public MaterialBuilder depthCulling(boolean enable) {
        nMaterialBuilderDepthCulling(mNativeBuilder, enable);
        return this;
    }

    public MaterialBuilder doubleSided(boolean doubleSided) {
        nMaterialBuilderDoubleSided(mNativeBuilder, doubleSided);
        return this;
    }

    public MaterialBuilder culling(@NotNull CullingMode mode) {
        nMaterialBuilderCulling(mNativeBuilder, mode.ordinal());
        return this;
    }

    public MaterialBuilder transparencyMode(@NotNull TransparencyMode mode) {
        nMaterialBuilderTransparencyMode(mNativeBuilder, mode.ordinal());
        return this;
    }

    public MaterialBuilder maskThreshold(float threshold) {
        nMaterialBuilderMaskThreshold(mNativeBuilder, threshold);
        return this;
    }

    public MaterialBuilder alphaToCoverage(boolean enable) {
        nMaterialBuilderAlphaToCoverage(mNativeBuilder, enable);
        return this;
    }

    public MaterialBuilder shadowMultiplier(boolean enable) {
        nMaterialBuilderShadowMultiplier(mNativeBuilder, enable);
        return this;
    }

    public MaterialBuilder transparentShadow(boolean enable) {
        nMaterialBuilderTransparentShadow(mNativeBuilder, enable);
        return this;
    }

    public MaterialBuilder specularAntiAliasing(boolean enable) {
        nMaterialBuilderSpecularAntiAliasing(mNativeBuilder, enable);
        return this;
    }

    public MaterialBuilder specularAntiAliasingVariance(float variance) {
        nMaterialBuilderSpecularAntiAliasingVariance(mNativeBuilder, variance);
        return this;
    }

    public MaterialBuilder specularAntiAliasingThreshold(float threshold) {
        nMaterialBuilderSpecularAntiAliasingThreshold(mNativeBuilder, threshold);
        return this;
    }

    public MaterialBuilder refractionMode(@NotNull RefractionMode mode) {
        nMaterialBuilderRefractionMode(mNativeBuilder, mode.ordinal());
        return this;
    }

    public MaterialBuilder reflectionMode(@NotNull ReflectionMode mode) {
        nMaterialBuilderReflectionMode(mNativeBuilder, mode.ordinal());
        return this;
    }

    public MaterialBuilder refractionType(@NotNull RefractionType type) {
        nMaterialBuilderRefractionType(mNativeBuilder, type.ordinal());
        return this;
    }

    public MaterialBuilder clearCoatIorChange(boolean enable) {
        nMaterialBuilderClearCoatIorChange(mNativeBuilder, enable);
        return this;
    }

    public MaterialBuilder flipUV(boolean enable) {
        nMaterialBuilderFlipUV(mNativeBuilder, enable);
        return this;
    }

    public MaterialBuilder customSurfaceShading(boolean enable) {
        nMaterialBuilderCustomSurfaceShading(mNativeBuilder, enable);
        return this;
    }

    public MaterialBuilder multiBounceAmbientOcclusion(boolean enable) {
        nMaterialBuilderMultiBounceAmbientOcclusion(mNativeBuilder, enable);
        return this;
    }

    public MaterialBuilder specularAmbientOcclusion(@NotNull SpecularAmbientOcclusion sao) {
        nMaterialBuilderSpecularAmbientOcclusion(mNativeBuilder, sao.ordinal());
        return this;
    }

    public MaterialBuilder variantFilter(int variantFilter) {
        nMaterialBuilderVariantFilter(mNativeBuilder, variantFilter);
        return this;
    }

    public MaterialBuilder useLegacyMorphing() {
        nMaterialBuilderUseLegacyMorphing(mNativeBuilder);
        return this;
    }

    public MaterialPackage build() {
        return build(null);
    }

    public MaterialPackage build(@Nullable Engine engine) {
        long jobSystem = 0;
        if (engine != null) {
            // We'll need to add a way to get the native job system from Engine in
            // nBuilderBuild
            // But for now we allow passing engine to find its native counterpart if
            // possible.
        }
        long nativePackage = nBuilderBuild(mNativeBuilder, engine != null ? engine.getNativeObject() : 0);
        if (nativePackage == 0)
            return new MaterialPackage(null, false);

        byte[] data = nGetPackageBytes(nativePackage);
        boolean valid = nGetPackageIsValid(nativePackage);
        nDestroyPackage(nativePackage);

        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.order(ByteOrder.nativeOrder());
        buffer.put(data);
        buffer.flip();

        return new MaterialPackage(buffer, valid);
    }

    private static class BuilderCleanup implements Runnable {
        private final long mNativeObject;

        BuilderCleanup(long nativeObject) {
            mNativeObject = nativeObject;
        }

        @Override
        public void run() {
            nDestroyMaterialBuilder(mNativeObject);
        }
    }

    private static native void nMaterialBuilderInit();

    private static native void nMaterialBuilderShutdown();

    private static native long nCreateMaterialBuilder();

    private static native void nDestroyMaterialBuilder(long nativeBuilder);

    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);

    private static native byte[] nGetPackageBytes(long nativePackage);

    private static native boolean nGetPackageIsValid(long nativePackage);

    private static native void nDestroyPackage(long nativePackage);

    private static native void nMaterialBuilderName(long nativeBuilder, String name);

    private static native void nMaterialBuilderShading(long nativeBuilder, int shading);

    private static native void nMaterialBuilderInterpolation(long nativeBuilder, int interpolation);

    private static native void nMaterialBuilderBlending(long nativeBuilder, int mode);

    private static native void nMaterialBuilderPostLightingBlending(long nativeBuilder, int mode);

    private static native void nMaterialBuilderMaterialDomain(long nativeBuilder, int domain);

    private static native void nMaterialBuilderVertexDomain(long nativeBuilder, int domain);

    private static native void nMaterialBuilderMaterial(long nativeBuilder, String code);

    private static native void nMaterialBuilderMaterialVertex(long nativeBuilder, String code);

    private static native void nMaterialBuilderTargetApi(long nativeBuilder, int api);

    private static native void nMaterialBuilderOptimization(long nativeBuilder, int optimization);

    private static native void nMaterialBuilderPlatform(long nativeBuilder, int platform);

    private static native void nMaterialBuilderUniformParameter(long nativeBuilder, int type, int precision,
            String name);

    private static native void nMaterialBuilderUniformParameterArray(long nativeBuilder, int type, int size,
            int precision, String name);

    private static native void nMaterialBuilderSamplerParameter(long nativeBuilder, int type, int format, int precision,
            String name);

    private static native void nMaterialBuilderVariable(long nativeBuilder, int variable, String name);

    private static native void nMaterialBuilderRequire(long nativeBuilder, int attribute);

    private static native void nMaterialBuilderColorWrite(long nativeBuilder, boolean enable);

    private static native void nMaterialBuilderDepthWrite(long nativeBuilder, boolean enable);

    private static native void nMaterialBuilderDepthCulling(long nativeBuilder, boolean enable);

    private static native void nMaterialBuilderDoubleSided(long nativeBuilder, boolean doubleSided);

    private static native void nMaterialBuilderCulling(long nativeBuilder, int mode);

    private static native void nMaterialBuilderTransparencyMode(long nativeBuilder, int mode);

    private static native void nMaterialBuilderMaskThreshold(long nativeBuilder, float threshold);

    private static native void nMaterialBuilderAlphaToCoverage(long nativeBuilder, boolean enable);

    private static native void nMaterialBuilderShadowMultiplier(long nativeBuilder, boolean enable);

    private static native void nMaterialBuilderTransparentShadow(long nativeBuilder, boolean enable);

    private static native void nMaterialBuilderSpecularAntiAliasing(long nativeBuilder, boolean enable);

    private static native void nMaterialBuilderSpecularAntiAliasingVariance(long nativeBuilder, float variance);

    private static native void nMaterialBuilderSpecularAntiAliasingThreshold(long nativeBuilder, float threshold);

    private static native void nMaterialBuilderRefractionMode(long nativeBuilder, int mode);

    private static native void nMaterialBuilderReflectionMode(long nativeBuilder, int mode);

    private static native void nMaterialBuilderRefractionType(long nativeBuilder, int type);

    private static native void nMaterialBuilderClearCoatIorChange(long nativeBuilder, boolean enable);

    private static native void nMaterialBuilderFlipUV(long nativeBuilder, boolean enable);

    private static native void nMaterialBuilderCustomSurfaceShading(long nativeBuilder, boolean enable);

    private static native void nMaterialBuilderMultiBounceAmbientOcclusion(long nativeBuilder, boolean enable);

    private static native void nMaterialBuilderSpecularAmbientOcclusion(long nativeBuilder, int sao);

    private static native void nMaterialBuilderVariantFilter(long nativeBuilder, int variantFilter);

    private static native void nMaterialBuilderUseLegacyMorphing(long nativeBuilder);
}
