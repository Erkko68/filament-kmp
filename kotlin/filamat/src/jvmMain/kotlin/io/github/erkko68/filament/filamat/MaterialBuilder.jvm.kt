package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import io.github.erkko68.filament.filamat.jni.MaterialBuilder as JniMaterialBuilder
import io.github.erkko68.filament.filamat.jni.MaterialPackage as JniMaterialPackage

actual class MaterialBuilder actual constructor() {
    private val jni = JniMaterialBuilder()

    actual enum class Shading {
        UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS;
        internal fun toJni() = JniMaterialBuilder.Shading.values()[ordinal]
    }

    actual enum class Interpolation {
        SMOOTH, FLAT;
        internal fun toJni() = JniMaterialBuilder.Interpolation.values()[ordinal]
    }

    actual enum class UniformType {
        BOOL, BOOL2, BOOL3, BOOL4, FLOAT, FLOAT2, FLOAT3, FLOAT4, INT, INT2, INT3, INT4, UINT, UINT2, UINT3, UINT4, MAT3, MAT4;
        internal fun toJni() = JniMaterialBuilder.UniformType.values()[ordinal]
    }

    actual enum class SamplerType {
        SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D, SAMPLER_CUBEMAP_ARRAY;
        internal fun toJni() = JniMaterialBuilder.SamplerType.values()[ordinal]
    }

    actual enum class SamplerFormat {
        INT, UINT, FLOAT, SHADOW;
        internal fun toJni() = JniMaterialBuilder.SamplerFormat.values()[ordinal]
    }

    actual enum class ParameterPrecision {
        LOW, MEDIUM, HIGH, DEFAULT;
        internal fun toJni() = JniMaterialBuilder.ParameterPrecision.values()[ordinal]
    }

    actual enum class Variable {
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4;
        internal fun toJni() = JniMaterialBuilder.Variable.values()[ordinal]
    }

    actual enum class BlendingMode {
        OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN, CUSTOM;
        internal fun toJni() = JniMaterialBuilder.BlendingMode.values()[ordinal]
    }

    actual enum class VertexDomain {
        OBJECT, WORLD, VIEW, DEVICE;
        internal fun toJni() = JniMaterialBuilder.VertexDomain.values()[ordinal]
    }

    actual enum class CullingMode {
        NONE, FRONT, BACK, FRONT_AND_BACK;
        internal fun toJni() = JniMaterialBuilder.CullingMode.values()[ordinal]
    }

    actual enum class TransparencyMode {
        DEFAULT, TWO_PASSES_ONE_SIDE, TWO_PASSES_TWO_SIDES;
        internal fun toJni() = JniMaterialBuilder.TransparencyMode.values()[ordinal]
    }

    actual enum class MaterialDomain {
        SURFACE, POST_PROCESS;
        internal fun toJni() = JniMaterialBuilder.MaterialDomain.values()[ordinal]
    }

    actual enum class SpecularAmbientOcclusion {
        NONE, SIMPLE, BENT_NORMALS;
        internal fun toJni() = JniMaterialBuilder.SpecularAmbientOcclusion.values()[ordinal]
    }

    actual enum class RefractionMode {
        NONE, CUBEMAP, SCREEN_SPACE;
        internal fun toJni() = JniMaterialBuilder.RefractionMode.values()[ordinal]
    }

    actual enum class ReflectionMode {
        DEFAULT, SCREEN_SPACE;
        internal fun toJni() = JniMaterialBuilder.ReflectionMode.values()[ordinal]
    }

    actual enum class RefractionType {
        SOLID, THIN;
        internal fun toJni() = JniMaterialBuilder.RefractionType.values()[ordinal]
    }

    actual enum class Platform {
        DESKTOP, MOBILE, ALL;
        internal fun toJni() = JniMaterialBuilder.Platform.values()[ordinal]
    }

    actual enum class TargetApi {
        OPENGL, VULKAN, METAL, WEBGPU, ALL;
        internal fun toJni() = JniMaterialBuilder.TargetApi.values()[ordinal]
    }

    actual enum class Optimization {
        NONE, PREPROCESSOR, SIZE, PERFORMANCE;
        internal fun toJni() = JniMaterialBuilder.Optimization.values()[ordinal]
    }

    actual companion object {
        actual fun init() = JniMaterialBuilder.init()
        actual fun shutdown() = JniMaterialBuilder.shutdown()
    }

    actual fun build(): MaterialPackage = MaterialPackage(jni.build())
    actual fun name(name: String): MaterialBuilder { jni.name(name); return this }
    actual fun materialDomain(domain: MaterialDomain): MaterialBuilder { jni.materialDomain(domain.toJni()); return this }
    actual fun shading(shading: Shading): MaterialBuilder { jni.shading(shading.toJni()); return this }
    actual fun interpolation(interpolation: Interpolation): MaterialBuilder { jni.interpolation(interpolation.toJni()); return this }
    actual fun uniformParameter(type: UniformType, name: String): MaterialBuilder { jni.uniformParameter(type.toJni(), name); return this }
    actual fun uniformParameter(type: UniformType, precision: ParameterPrecision, name: String): MaterialBuilder { jni.uniformParameter(type.toJni(), precision.toJni(), name); return this }
    actual fun uniformParameterArray(type: UniformType, size: Int, name: String): MaterialBuilder { jni.uniformParameterArray(type.toJni(), size, name); return this }
    actual fun uniformParameterArray(type: UniformType, size: Int, precision: ParameterPrecision, name: String): MaterialBuilder { jni.uniformParameterArray(type.toJni(), size, precision.toJni(), name); return this }
    actual fun samplerParameter(type: SamplerType, format: SamplerFormat, precision: ParameterPrecision, name: String): MaterialBuilder { jni.samplerParameter(type.toJni(), format.toJni(), precision.toJni(), name); return this }
    actual fun variable(variable: Variable, name: String): MaterialBuilder { jni.variable(variable.toJni(), name); return this }
    
    actual fun require(attribute: VertexAttribute): MaterialBuilder {
        jni.require(JniMaterialBuilder.VertexAttribute.values()[attribute.ordinal])
        return this
    }
    
    actual fun material(code: String): MaterialBuilder { jni.material(code); return this }
    actual fun materialVertex(code: String): MaterialBuilder { jni.materialVertex(code); return this }
    actual fun blending(mode: BlendingMode): MaterialBuilder { jni.blending(mode.toJni()); return this }
    actual fun postLightingBlending(mode: BlendingMode): MaterialBuilder { jni.postLightingBlending(mode.toJni()); return this }
    actual fun vertexDomain(vertexDomain: VertexDomain): MaterialBuilder { jni.vertexDomain(vertexDomain.toJni()); return this }
    actual fun culling(mode: CullingMode): MaterialBuilder { jni.culling(mode.toJni()); return this }
    actual fun colorWrite(enable: Boolean): MaterialBuilder { jni.colorWrite(enable); return this }
    actual fun depthWrite(enable: Boolean): MaterialBuilder { jni.depthWrite(enable); return this }
    actual fun depthCulling(enable: Boolean): MaterialBuilder { jni.depthCulling(enable); return this }
    actual fun doubleSided(doubleSided: Boolean): MaterialBuilder { jni.doubleSided(doubleSided); return this }
    actual fun maskThreshold(threshold: Float): MaterialBuilder { jni.maskThreshold(threshold); return this }
    actual fun alphaToCoverage(enable: Boolean): MaterialBuilder { jni.alphaToCoverage(enable); return this }
    actual fun shadowMultiplier(shadowMultiplier: Boolean): MaterialBuilder { jni.shadowMultiplier(shadowMultiplier); return this }
    actual fun transparentShadow(transparentShadow: Boolean): MaterialBuilder { jni.transparentShadow(transparentShadow); return this }
    actual fun specularAntiAliasing(specularAntiAliasing: Boolean): MaterialBuilder { jni.specularAntiAliasing(specularAntiAliasing); return this }
    actual fun specularAntiAliasingVariance(variance: Float): MaterialBuilder { jni.specularAntiAliasingVariance(variance); return this }
    actual fun specularAntiAliasingThreshold(threshold: Float): MaterialBuilder { jni.specularAntiAliasingThreshold(threshold); return this }
    actual fun refractionMode(mode: RefractionMode): MaterialBuilder { jni.refractionMode(mode.toJni()); return this }
    actual fun reflectionMode(mode: ReflectionMode): MaterialBuilder { jni.reflectionMode(mode.toJni()); return this }
    actual fun refractionType(type: RefractionType): MaterialBuilder { jni.refractionType(type.toJni()); return this }
    actual fun clearCoatIorChange(clearCoatIorChange: Boolean): MaterialBuilder { jni.clearCoatIorChange(clearCoatIorChange); return this }
    actual fun flipUV(flipUV: Boolean): MaterialBuilder { jni.flipUV(flipUV); return this }
    actual fun customSurfaceShading(customSurfaceShading: Boolean): MaterialBuilder { jni.customSurfaceShading(customSurfaceShading); return this }
    actual fun multiBounceAmbientOcclusion(multiBounceAO: Boolean): MaterialBuilder { jni.multiBounceAmbientOcclusion(multiBounceAO); return this }
    actual fun specularAmbientOcclusion(specularAO: SpecularAmbientOcclusion): MaterialBuilder { jni.specularAmbientOcclusion(specularAO.toJni()); return this }
    actual fun transparencyMode(mode: TransparencyMode): MaterialBuilder { jni.transparencyMode(mode.toJni()); return this }
    actual fun platform(platform: Platform): MaterialBuilder { jni.platform(platform.toJni()); return this }
    actual fun targetApi(api: TargetApi): MaterialBuilder { jni.targetApi(api.toJni()); return this }
    actual fun optimization(optimization: Optimization): MaterialBuilder { jni.optimization(optimization.toJni()); return this }
    actual fun variantFilter(variantFilter: Int): MaterialBuilder { jni.variantFilter(variantFilter); return this }
    actual fun useLegacyMorphing(): MaterialBuilder { jni.useLegacyMorphing(); return this }
}
