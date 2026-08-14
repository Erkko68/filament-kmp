package io.github.erkko68.filament

import io.github.erkko68.filament.web.Shading as JsShading
import io.github.erkko68.filament.web.Interpolation as JsInterpolation
import io.github.erkko68.filament.web.BlendingMode as JsBlendingMode
import io.github.erkko68.filament.web.TransparencyMode as JsTransparencyMode
import io.github.erkko68.filament.web.RefractionMode as JsRefractionMode
import io.github.erkko68.filament.web.RefractionType as JsRefractionType
import io.github.erkko68.filament.web.ReflectionMode as JsReflectionMode
import io.github.erkko68.filament.web.VertexDomain as JsVertexDomain
import io.github.erkko68.filament.web.CullingMode as JsCullingMode
import io.github.erkko68.filament.web.FeatureLevel as JsFeatureLevel
import io.github.erkko68.filament.web.Material_ParameterInfo as JsParameterInfo
import io.github.erkko68.filament.web.Material as JSMaterial
import io.github.erkko68.filament.web.interop.jsNumbers
import org.khronos.webgl.set

actual class Material(internal val jsMaterial: JSMaterial) {
    actual fun compile(
        priority: CompilerPriorityQueue,
        variants: Int,
        callback: (() -> Unit)?
    ) {
    }

    actual fun createInstance(): MaterialInstance {
        return MaterialInstance(jsMaterial.createInstance())
    }

    actual fun createInstance(name: String): MaterialInstance {
        return MaterialInstance(jsMaterial.createNamedInstance(name))
    }

    actual fun getDefaultInstance(): MaterialInstance {
        return MaterialInstance(jsMaterial.getDefaultInstance())
    }

    actual fun getName(): String {
        return jsMaterial.getName()
    }

    actual fun getShading(): Shading = when (jsMaterial.getShading()) {
        JsShading.UNLIT -> Shading.UNLIT
        JsShading.SUBSURFACE -> Shading.SUBSURFACE
        JsShading.CLOTH -> Shading.CLOTH
        JsShading.SPECULAR_GLOSSINESS -> Shading.SPECULAR_GLOSSINESS
        else -> Shading.LIT
    }

    actual fun getInterpolation(): Interpolation = when (jsMaterial.getInterpolation()) {
        JsInterpolation.FLAT -> Interpolation.FLAT
        else -> Interpolation.SMOOTH
    }

    // BlendingMode::CUSTOM has no common-API counterpart; it reports as TRANSPARENT.
    actual fun getBlendingMode(): BlendingMode = when (jsMaterial.getBlendingMode()) {
        JsBlendingMode.TRANSPARENT, JsBlendingMode.CUSTOM -> BlendingMode.TRANSPARENT
        JsBlendingMode.ADD -> BlendingMode.ADD
        JsBlendingMode.MASKED -> BlendingMode.MASKED
        JsBlendingMode.FADE -> BlendingMode.FADE
        JsBlendingMode.MULTIPLY -> BlendingMode.MULTIPLY
        JsBlendingMode.SCREEN -> BlendingMode.SCREEN
        else -> BlendingMode.OPAQUE
    }

    actual fun getTransparencyMode(): TransparencyMode = when (jsMaterial.getTransparencyMode()) {
        JsTransparencyMode.TWO_PASSES_ONE_SIDE -> TransparencyMode.TWO_PASSES_ONE_SIDE
        JsTransparencyMode.TWO_PASSES_TWO_SIDES -> TransparencyMode.TWO_PASSES_TWO_SIDES
        else -> TransparencyMode.DEFAULT
    }

    actual fun getRefractionMode(): RefractionMode = when (jsMaterial.getRefractionMode()) {
        JsRefractionMode.CUBEMAP -> RefractionMode.CUBEMAP
        JsRefractionMode.SCREEN_SPACE -> RefractionMode.SCREEN_SPACE
        else -> RefractionMode.NONE
    }

    actual fun getRefractionType(): RefractionType = when (jsMaterial.getRefractionType()) {
        JsRefractionType.THIN -> RefractionType.THIN
        else -> RefractionType.SOLID
    }

    actual fun getReflectionMode(): ReflectionMode = when (jsMaterial.getReflectionMode()) {
        JsReflectionMode.SCREEN_SPACE -> ReflectionMode.SCREEN_SPACE
        else -> ReflectionMode.DEFAULT
    }

    actual fun getVertexDomain(): VertexDomain = when (jsMaterial.getVertexDomain()) {
        JsVertexDomain.WORLD -> VertexDomain.WORLD
        JsVertexDomain.VIEW -> VertexDomain.VIEW
        JsVertexDomain.DEVICE -> VertexDomain.DEVICE
        else -> VertexDomain.OBJECT
    }

    actual fun getCullingMode(): CullingMode = when (jsMaterial.getCullingMode()) {
        JsCullingMode.NONE -> CullingMode.NONE
        JsCullingMode.FRONT -> CullingMode.FRONT
        JsCullingMode.FRONT_AND_BACK -> CullingMode.FRONT_AND_BACK
        else -> CullingMode.BACK
    }

    actual fun isColorWriteEnabled(): Boolean = jsMaterial.isColorWriteEnabled()

    actual fun isDepthWriteEnabled(): Boolean = jsMaterial.isDepthWriteEnabled()

    actual fun isDepthCullingEnabled(): Boolean = jsMaterial.isDepthCullingEnabled()

    actual fun isDoubleSided(): Boolean = jsMaterial.isDoubleSided()

    actual fun isAlphaToCoverageEnabled(): Boolean = jsMaterial.isAlphaToCoverageEnabled()

    actual fun getMaskThreshold(): Float = jsMaterial.getMaskThreshold().toFloat()

    actual fun getSpecularAntiAliasingVariance(): Float =
        jsMaterial.getSpecularAntiAliasingVariance().toFloat()

    actual fun getSpecularAntiAliasingThreshold(): Float =
        jsMaterial.getSpecularAntiAliasingThreshold().toFloat()

    actual fun getFeatureLevel(): Engine.FeatureLevel = when (jsMaterial.getFeatureLevel()) {
        JsFeatureLevel.FEATURE_LEVEL_2 -> Engine.FeatureLevel.FEATURE_LEVEL_2
        JsFeatureLevel.FEATURE_LEVEL_3 -> Engine.FeatureLevel.FEATURE_LEVEL_3
        else -> Engine.FeatureLevel.FEATURE_LEVEL_1
    }

    actual fun getParameterCount(): Int = jsMaterial.getParameterCount().toInt()

    actual fun getParameters(): List<Parameter> {
        val params = jsMaterial.getParameters()
        return List(params.size) { i ->
            val p = params[i]!!
            Parameter(p.name, p.parameterType(), Parameter.Precision.entries[p.precision], p.count)
        }
    }

    actual fun getRequiredAttributes(): Set<VertexBuffer.VertexAttribute> {
        val bitset = jsMaterial.getRequiredAttributes().toInt()
        return VertexBuffer.VertexAttribute.entries
            .filterTo(mutableSetOf()) { (bitset and (1 shl it.ordinal)) != 0 }
    }

    actual fun hasParameter(name: String): Boolean = jsMaterial.hasParameter(name)

    // setDefaultParameter is setParameter on the default instance, same as the C++ inline.
    actual fun setDefaultParameter(name: String, value: Int) {
        jsMaterial.getDefaultInstance().setIntParameter(name, value.toDouble())
    }

    actual fun setDefaultParameter(name: String, value: Boolean) {
        jsMaterial.getDefaultInstance().setBoolParameter(name, value)
    }

    actual fun setDefaultParameter(name: String, value: Float) {
        jsMaterial.getDefaultInstance().setFloatParameter(name, value.toDouble())
    }

    actual fun setDefaultParameter(name: String, x: Float, y: Float) {
        jsMaterial.getDefaultInstance().setFloat2Parameter(name, jsNumbers(x, y))
    }

    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float) {
        jsMaterial.getDefaultInstance().setFloat3Parameter(name, jsNumbers(x, y, z))
    }

    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float, w: Float) {
        jsMaterial.getDefaultInstance().setFloat4Parameter(name, jsNumbers(x, y, z, w))
    }

    actual fun getParameterTransformName(samplerName: String): String? {
        val name = jsMaterial.getParameterTransformName(samplerName)
        // JS binding returns an empty string when no transform name exists.
        return name.takeIf { it.isNotEmpty() }
    }

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
            actual val DIRECTIONAL_LIGHTING: Int = 1
            actual val DYNAMIC_LIGHTING: Int = 2
            actual val SHADOW_RECEIVER: Int = 4
            actual val SKINNING: Int = 8
            actual val FOG: Int = 16
            actual val VSM: Int = 32
            actual val SSR: Int = 64
            actual val STE: Int = 128
            actual val ALL: Int = 255
        }
    }

    actual class Parameter actual constructor(
        actual val name: String,
        actual val type: Type,
        actual val precision: Precision,
        actual val count: Int
    ) {
        actual enum class Type { BOOL, BOOL2, BOOL3, BOOL4, FLOAT, FLOAT2, FLOAT3, FLOAT4, INT, INT2, INT3, INT4, UINT, UINT2, UINT3, UINT4, MAT3, MAT4, SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D, SUBPASS_INPUT }
        actual enum class Precision { LOW, MEDIUM, HIGH, DEFAULT }
    }

    actual class Builder {
        private var _payload: ByteArray? = null

        actual fun payload(data: ByteArray): Builder {
            _payload = data
            return this
        }

        actual fun sphericalHarmonicsBandCount(shBandCount: Int): Builder {
            return this
        }

        actual fun shadowSamplingQuality(quality: ShadowSamplingQuality): Builder {
            return this
        }

        actual fun uboBatching(mode: UboBatchingMode): Builder {
            return this
        }

        actual fun build(engine: Engine): Material {
            val payload = _payload ?: throw IllegalStateException("Material payload must be set")
            // Convert ByteArray to Uint8Array for JS
            val int8 = org.khronos.webgl.Int8Array(payload.size)
            payload.forEachIndexed { i, b -> int8[i] = b }
            val uint8 = org.khronos.webgl.Uint8Array(int8.buffer, int8.byteOffset, int8.length)
            // A bad payload C++-throws out of embind; map it to a catchable Kotlin exception
            // (matches jvm/native, and keeps the raw JS value out of coroutine machinery).
            val jsMaterial = catchingJsThrows { engine.jsEngine.createMaterial(uint8) }
                ?: throw IllegalArgumentException("Material build failed — not a valid compiled .filamat payload")
            return Material(jsMaterial)
        }

        actual enum class ShadowSamplingQuality { HARD, LOW }
    }
}

// ParameterInfo packs a union: `type`, `samplerType` and `subpassType` live in separate
// enum spaces, selected by isSampler/isSubpass. The common Parameter.Type flattens them —
// UniformType[0..17] then the samplers then SUBPASS_INPUT — and those prefixes match the
// backend enums, so the offsets below are exact.
private fun JsParameterInfo.parameterType(): Material.Parameter.Type {
    val entries = Material.Parameter.Type.entries
    return when {
        isSubpass -> Material.Parameter.Type.SUBPASS_INPUT
        // SamplerType also declares SAMPLER_CUBEMAP_ARRAY, which the common enum lacks and
        // WebGL 2 cannot express; it would run past the sampler range, so it is clamped out.
        isSampler -> entries[
            Material.Parameter.Type.SAMPLER_2D.ordinal +
                (samplerType ?: 0).coerceIn(0, Material.Parameter.Type.SAMPLER_3D.ordinal -
                    Material.Parameter.Type.SAMPLER_2D.ordinal)
        ]
        else -> entries[(type ?: 0).coerceIn(0, Material.Parameter.Type.MAT4.ordinal)]
    }
}
