package io.github.erkko68.filament

import com.google.android.filament.Material as AndroidMaterial
import java.nio.Buffer
import java.util.BitSet

actual class Material @InternalFilamentApi constructor(internal val nativeMaterial: AndroidMaterial) {
    private val mDefaultInstance: MaterialInstance by lazy { MaterialInstance(this, nativeMaterial.defaultInstance) }
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
            actual val DIRECTIONAL_LIGHTING = AndroidMaterial.UserVariantFilterBit.DIRECTIONAL_LIGHTING
            actual val DYNAMIC_LIGHTING = AndroidMaterial.UserVariantFilterBit.DYNAMIC_LIGHTING
            actual val SHADOW_RECEIVER = AndroidMaterial.UserVariantFilterBit.SHADOW_RECEIVER
            actual val SKINNING = AndroidMaterial.UserVariantFilterBit.SKINNING
            actual val FOG = AndroidMaterial.UserVariantFilterBit.FOG
            actual val VSM = AndroidMaterial.UserVariantFilterBit.VSM
            actual val SSR = AndroidMaterial.UserVariantFilterBit.SSR
            actual val STE = AndroidMaterial.UserVariantFilterBit.STE
            actual val ALL = AndroidMaterial.UserVariantFilterBit.ALL
        }
    }

    actual class Parameter actual constructor(
        actual val name: String,
        actual val type: Type,
        actual val precision: Precision,
        actual val count: Int
    ) {
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
        private val androidBuilder = AndroidMaterial.Builder()
        // Set in payload(): a non-empty blob that isn't a compiled .filamat. build() rejects it before
        // calling Filament's parser, which would otherwise panic uncatchably (see isValidFilamatPayload).
        private var payloadInvalid = false

        actual enum class ShadowSamplingQuality { HARD, LOW }

        actual fun payload(data: ByteArray): Builder {
            payloadInvalid = data.isNotEmpty() && !isValidFilamatPayload(data)
            val byteBuffer = java.nio.ByteBuffer.allocateDirect(data.size).apply {
                order(java.nio.ByteOrder.nativeOrder())
                put(data)
                flip()
            }
            androidBuilder.payload(byteBuffer, byteBuffer.remaining())
            return this
        }

        actual fun sphericalHarmonicsBandCount(shBandCount: Int): Builder {
            androidBuilder.sphericalHarmonicsBandCount(shBandCount)
            return this
        }

        actual fun shadowSamplingQuality(quality: ShadowSamplingQuality): Builder {
            androidBuilder.shadowSamplingQuality(AndroidMaterial.Builder.ShadowSamplingQuality.values()[quality.ordinal])
            return this
        }

        actual fun uboBatching(mode: UboBatchingMode): Builder {
            androidBuilder.uboBatching(AndroidMaterial.UboBatchingMode.entries[mode.ordinal])
            return this
        }

        actual fun build(engine: Engine): Material {
            if (payloadInvalid) {
                throw IllegalArgumentException("Failed to build material — the payload is not a valid compiled .filamat")
            }
            return Material(androidBuilder.build(engine.nativeEngine))
        }
    }

    actual fun compile(
        priority: CompilerPriorityQueue,
        variants: Int,
        callback: (() -> Unit)?
    ) {
        nativeMaterial.compile(
            AndroidMaterial.CompilerPriorityQueue.values()[priority.ordinal],
            variants,
            null,
            callback
        )
    }

    actual fun createInstance(): MaterialInstance = MaterialInstance(this, nativeMaterial.createInstance())
    actual fun createInstance(name: String): MaterialInstance = MaterialInstance(this, nativeMaterial.createInstance(name))
    actual val defaultInstance: MaterialInstance get() = mDefaultInstance

    actual val name: String get() = nativeMaterial.name
    actual val shading: Shading get() = Shading.values()[nativeMaterial.shading.ordinal]
    actual val interpolation: Interpolation get() = Interpolation.values()[nativeMaterial.interpolation.ordinal]
    actual val blendingMode: BlendingMode get() = BlendingMode.values()[nativeMaterial.blendingMode.ordinal]
    actual val transparencyMode: TransparencyMode get() = TransparencyMode.values()[nativeMaterial.transparencyMode.ordinal]
    actual val refractionMode: RefractionMode get() = RefractionMode.values()[nativeMaterial.refractionMode.ordinal]
    actual val refractionType: RefractionType get() = RefractionType.values()[nativeMaterial.refractionType.ordinal]
    actual val reflectionMode: ReflectionMode get() = ReflectionMode.values()[nativeMaterial.reflectionMode.ordinal]
    actual val vertexDomain: VertexDomain get() = VertexDomain.values()[nativeMaterial.vertexDomain.ordinal]
    actual val cullingMode: CullingMode get() = CullingMode.values()[nativeMaterial.cullingMode.ordinal]
    actual val isColorWriteEnabled: Boolean get() = nativeMaterial.isColorWriteEnabled
    actual val isDepthWriteEnabled: Boolean get() = nativeMaterial.isDepthWriteEnabled
    actual val isDepthCullingEnabled: Boolean get() = nativeMaterial.isDepthCullingEnabled
    actual val isDoubleSided: Boolean get() = nativeMaterial.isDoubleSided
    actual val isAlphaToCoverageEnabled: Boolean get() = nativeMaterial.isAlphaToCoverageEnabled
    actual val maskThreshold: Float get() = nativeMaterial.maskThreshold
    actual val specularAntiAliasingVariance: Float get() = nativeMaterial.specularAntiAliasingVariance
    actual val specularAntiAliasingThreshold: Float get() = nativeMaterial.specularAntiAliasingThreshold
    actual val featureLevel: Engine.FeatureLevel get() = Engine.FeatureLevel.entries[nativeMaterial.featureLevel.ordinal]
    actual val parameterCount: Int get() = nativeMaterial.parameterCount
    actual val parameters: List<Parameter> get() = nativeMaterial.parameters.map { p ->
        Parameter(
            p.name,
            Parameter.Type.values()[p.type.ordinal],
            Parameter.Precision.values()[p.precision.ordinal],
            p.count
        )
    }

    actual val requiredAttributes: Set<VertexBuffer.VertexAttribute> get() {
        val attrSet = nativeMaterial.requiredAttributes
        val result = mutableSetOf<VertexBuffer.VertexAttribute>()
        // We iterate over our KMP enum entries and check if they exist in the Java set
        VertexBuffer.VertexAttribute.entries.forEach { attr ->
            if (attrSet.any { it.name == attr.name }) {
                result.add(attr)
            }
        }
        return result
    }

    actual fun hasParameter(name: String): Boolean = nativeMaterial.hasParameter(name)
    actual fun getParameterTransformName(samplerName: String): String? = nativeMaterial.getParameterTransformName(samplerName)
    actual fun setDefaultParameter(name: String, value: Boolean) = nativeMaterial.setDefaultParameter(name, value)
    actual fun setDefaultParameter(name: String, value: Float) = nativeMaterial.setDefaultParameter(name, value)
    actual fun setDefaultParameter(name: String, value: Int) = nativeMaterial.setDefaultParameter(name, value)
    actual fun setDefaultParameter(name: String, x: Float, y: Float) = nativeMaterial.setDefaultParameter(name, x, y)
    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float) = nativeMaterial.setDefaultParameter(name, x, y, z)
    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float, w: Float) = nativeMaterial.setDefaultParameter(name, x, y, z, w)
}
