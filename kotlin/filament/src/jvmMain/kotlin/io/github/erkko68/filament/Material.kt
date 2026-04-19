package io.github.erkko68.filament

import java.nio.ByteBuffer

actual class Material(val nativeMaterial: io.github.erkko68.filament.jni.Material) {
    actual enum class Shading { UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS }
    actual enum class Interpolation { SMOOTH, FLAT }
    actual enum class BlendingMode { OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN }
    actual enum class TransparencyMode { DEFAULT, TWO_PASSES_ONE_SIDE, TWO_PASSES_TWO_SIDES }
    actual enum class RefractionMode { NONE, CUBEMAP, SCREEN_SPACE }
    actual enum class RefractionType { SOLID, THIN }
    actual enum class ReflectionMode { DEFAULT, SCREEN_SPACE }
    actual enum class VertexDomain { OBJECT, WORLD, VIEW, DEVICE }
    actual enum class CullingMode { NONE, FRONT, BACK, FRONT_AND_BACK }
    actual enum class CompilerPriorityQueue { CRITICAL, HIGH, LOW }
    actual enum class UboBatchingMode { DEFAULT, DISABLED }
    
    actual class UserVariantFilterBit {
        actual companion object {
            actual val DIRECTIONAL_LIGHTING: Int = 0x01
            actual val DYNAMIC_LIGHTING: Int = 0x02
            actual val SHADOW_RECEIVER: Int = 0x04
            actual val SKINNING: Int = 0x08
            actual val FOG: Int = 0x10
            actual val VSM: Int = 0x20
            actual val SSR: Int = 0x40
            actual val STE: Int = 0x80
            actual val ALL: Int = 0xFF
        }
    }

    actual class Parameter actual constructor(
        name: String,
        type: Type,
        precision: Precision,
        count: Int
    ) {
        actual val name: String = name
        actual val type: Type = type
        actual val precision: Precision = precision
        actual val count: Int = count

        actual enum class Type {
            BOOL, BOOL2, BOOL3, BOOL4,
            FLOAT, FLOAT2, FLOAT3, FLOAT4,
            INT, INT2, INT3, INT4,
            UINT, UINT2, UINT3, UINT4,
            MAT3, MAT4,
            SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D,
            SUBPASS_INPUT
        }

        actual enum class Precision { LOW, MEDIUM, HIGH, DEFAULT }
    }

    actual class Builder actual constructor() {
        private val jni = io.github.erkko68.filament.jni.Material.Builder()

        actual enum class ShadowSamplingQuality { HARD, LOW }

        actual fun payload(data: ByteArray): Builder {
            val buffer = ByteBuffer.allocateDirect(data.size)
            buffer.put(data)
            buffer.rewind()
            jni.payload(buffer, data.size)
            return this
        }

        actual fun sphericalHarmonicsBandCount(shBandCount: Int): Builder {
            jni.sphericalHarmonicsBandCount(shBandCount)
            return this
        }

        actual fun shadowSamplingQuality(quality: ShadowSamplingQuality): Builder {
            jni.shadowSamplingQuality(io.github.erkko68.filament.jni.Material.Builder.ShadowSamplingQuality.values()[quality.ordinal])
            return this
        }

        actual fun uboBatching(mode: UboBatchingMode): Builder {
            jni.uboBatching(io.github.erkko68.filament.jni.Material.UboBatchingMode.values()[mode.ordinal])
            return this
        }

        actual fun build(engine: Engine): Material =
            Material(jni.build(engine.nativeEngine))
    }

    actual fun compile(priority: CompilerPriorityQueue, variants: Int, callback: (() -> Unit)?) {
        val runnable: java.lang.Runnable? = if (callback != null) Runnable { callback() } else null
        nativeMaterial.compile(io.github.erkko68.filament.jni.Material.CompilerPriorityQueue.values()[priority.ordinal], variants, null, runnable)
    }

    actual fun createInstance(): MaterialInstance = MaterialInstance(nativeMaterial.createInstance())
    actual fun createInstance(name: String): MaterialInstance = MaterialInstance(nativeMaterial.createInstance(name))
    actual fun getDefaultInstance(): MaterialInstance = MaterialInstance(nativeMaterial.defaultInstance)
    
    actual fun getName(): String = nativeMaterial.name ?: ""
    actual fun getShading(): Shading = Shading.values()[nativeMaterial.shading.ordinal]
    actual fun getInterpolation(): Interpolation = Interpolation.values()[nativeMaterial.interpolation.ordinal]
    actual fun getBlendingMode(): BlendingMode = BlendingMode.values()[nativeMaterial.blendingMode.ordinal]
    actual fun getTransparencyMode(): TransparencyMode = TransparencyMode.values()[nativeMaterial.transparencyMode.ordinal]
    actual fun getRefractionMode(): RefractionMode = RefractionMode.values()[nativeMaterial.refractionMode.ordinal]
    actual fun getRefractionType(): RefractionType = RefractionType.values()[nativeMaterial.refractionType.ordinal]
    actual fun getReflectionMode(): ReflectionMode = ReflectionMode.values()[nativeMaterial.reflectionMode.ordinal]
    actual fun getVertexDomain(): VertexDomain = VertexDomain.values()[nativeMaterial.vertexDomain.ordinal]
    actual fun getCullingMode(): CullingMode = CullingMode.values()[nativeMaterial.cullingMode.ordinal]
    actual fun isColorWriteEnabled(): Boolean = nativeMaterial.isColorWriteEnabled
    actual fun isDepthWriteEnabled(): Boolean = nativeMaterial.isDepthWriteEnabled
    actual fun isDepthCullingEnabled(): Boolean = nativeMaterial.isDepthCullingEnabled
    actual fun isDoubleSided(): Boolean = nativeMaterial.isDoubleSided
    actual fun isAlphaToCoverageEnabled(): Boolean = nativeMaterial.isAlphaToCoverageEnabled
    actual fun getMaskThreshold(): Float = nativeMaterial.maskThreshold
    actual fun getSpecularAntiAliasingVariance(): Float = nativeMaterial.specularAntiAliasingVariance
    actual fun getSpecularAntiAliasingThreshold(): Float = nativeMaterial.specularAntiAliasingThreshold
    actual fun getFeatureLevel(): Engine.FeatureLevel = Engine.FeatureLevel.values()[nativeMaterial.featureLevel.ordinal]
    
    actual fun getParameterCount(): Int = nativeMaterial.parameterCount
    actual fun getParameters(): List<Parameter> {
        return emptyList()
    }
    
    actual fun getRequiredAttributes(): Set<VertexBuffer.VertexAttribute> {
        val attr = nativeMaterial.requiredAttributes
        val result = mutableSetOf<VertexBuffer.VertexAttribute>()
        attr.forEach { result.add(VertexBuffer.VertexAttribute.values()[it.ordinal]) }
        return result
    }
}
