package dev.filament.kmp

import com.google.android.filament.Colors as AndroidColors
import com.google.android.filament.Material as AndroidMaterial
import java.nio.Buffer

actual class Material internal constructor(
    internal var androidMaterial: AndroidMaterial?,
) {
    actual fun compile(priority: CompilerPriorityQueue, variants: Int, handler: Any?, callback: (() -> Unit)?) {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        material.compile(priority.toAndroid(), variants, handler, callback?.let { Runnable { it() } })
    }

    actual fun createInstance(): MaterialInstance {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return MaterialInstance(material.createInstance())
    }

    actual fun createInstance(name: String): MaterialInstance {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return MaterialInstance(material.createInstance(name))
    }

    actual fun getDefaultInstance(): MaterialInstance {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return MaterialInstance(material.defaultInstance)
    }

    actual fun getName(): String {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.name
    }

    actual fun getShading(): Shading {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.shading.toKmp()
    }

    actual fun getInterpolation(): Interpolation {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.interpolation.toKmp()
    }

    actual fun getBlendingMode(): BlendingMode {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.blendingMode.toKmp()
    }

    actual fun getTransparencyMode(): TransparencyMode {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.transparencyMode.toKmp()
    }

    actual fun getRefractionMode(): RefractionMode {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.refractionMode.toKmp()
    }

    actual fun getRefractionType(): RefractionType {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.refractionType.toKmp()
    }

    actual fun getReflectionMode(): ReflectionMode {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.reflectionMode.toKmp()
    }

    actual fun getFeatureLevel(): Int {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.featureLevel.ordinal
    }

    actual fun getVertexDomain(): VertexDomain {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.vertexDomain.toKmp()
    }

    actual fun getCullingMode(): CullingMode {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.cullingMode.toKmp()
    }

    actual fun isColorWriteEnabled(): Boolean {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.isColorWriteEnabled
    }

    actual fun isDepthWriteEnabled(): Boolean {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.isDepthWriteEnabled
    }

    actual fun isDepthCullingEnabled(): Boolean {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.isDepthCullingEnabled
    }

    actual fun isDoubleSided(): Boolean {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.isDoubleSided
    }

    actual fun isAlphaToCoverageEnabled(): Boolean {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.isAlphaToCoverageEnabled
    }

    actual fun getMaskThreshold(): Float {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.maskThreshold
    }

    actual fun getSpecularAntiAliasingVariance(): Float {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.specularAntiAliasingVariance
    }

    actual fun getSpecularAntiAliasingThreshold(): Float {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.specularAntiAliasingThreshold
    }

    actual fun getRequiredAttributes(): Set<VertexBuffer.VertexAttribute> {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.requiredAttributes.mapTo(linkedSetOf()) { VertexBuffer.VertexAttribute.valueOf(it.name) }
    }

    actual fun getParameterCount(): Int {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.parameterCount
    }

    actual fun getParameters(): List<Parameter> {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.parameters.map {
            Parameter(
                it.name,
                Parameter.Type.valueOf(it.type.name),
                Parameter.Precision.valueOf(it.precision.name),
                it.count,
            )
        }
    }

    actual fun hasParameter(name: String): Boolean {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.hasParameter(name)
    }

    actual fun getParameterTransformName(samplerName: String): String {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.getParameterTransformName(samplerName)
    }

    actual fun setDefaultParameter(name: String, x: Boolean) {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        material.setDefaultParameter(name, x)
    }

    actual fun setDefaultParameter(name: String, x: Float) {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        material.setDefaultParameter(name, x)
    }

    actual fun setDefaultParameter(name: String, x: Int) {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        material.setDefaultParameter(name, x)
    }

    actual fun setDefaultParameter(name: String, x: Float, y: Float) {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        material.setDefaultParameter(name, x, y)
    }

    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float) {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        material.setDefaultParameter(name, x, y, z)
    }

    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float, w: Float) {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        material.setDefaultParameter(name, x, y, z, w)
    }

    actual fun setDefaultParameter(name: String, type: Any, x: Float, y: Float, z: Float, w: Float) {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        when (type) {
            is Colors.RgbaType -> material.setDefaultParameter(name, type.toAndroid(), x, y, z, w)
            is Colors.RgbType -> material.setDefaultParameter(name, type.toAndroid(), x, y, z)
            else -> throw IllegalArgumentException("Unsupported type for setDefaultParameter: ${type::class.simpleName}")
        }
    }

    actual fun getNativeObject(): Long {
        val material = requireNotNull(androidMaterial) { "Calling method on destroyed Material" }
        return material.nativeObject
    }

    actual internal fun invalidate() {
        androidMaterial = null
    }

    actual class Builder {
        private val androidBuilder = AndroidMaterial.Builder()

        actual fun payload(buffer: Any, size: Int): Builder {
            androidBuilder.payload(buffer as Buffer, size)
            return this
        }

        actual fun sphericalHarmonicsBandCount(shBandCount: Int): Builder {
            androidBuilder.sphericalHarmonicsBandCount(shBandCount)
            return this
        }

        actual fun shadowSamplingQuality(quality: ShadowSamplingQuality): Builder {
            androidBuilder.shadowSamplingQuality(quality.toAndroid())
            return this
        }

        actual fun uboBatching(mode: UboBatchingMode): Builder {
            androidBuilder.uboBatching(mode.toAndroid())
            return this
        }

        actual fun build(engine: Engine): Material {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return Material(androidBuilder.build(androidEngine))
        }

        actual enum class ShadowSamplingQuality {
            HARD,
            LOW,
        }
    }

    actual enum class Shading {
        UNLIT,
        LIT,
        SUBSURFACE,
        CLOTH,
        SPECULAR_GLOSSINESS,
    }

    actual enum class Interpolation {
        SMOOTH,
        FLAT,
    }

    actual enum class BlendingMode {
        OPAQUE,
        TRANSPARENT,
        ADD,
        MASKED,
        FADE,
        MULTIPLY,
        SCREEN,
    }

    actual enum class TransparencyMode {
        DEFAULT,
        TWO_PASSES_ONE_SIDE,
        TWO_PASSES_TWO_SIDES,
    }

    actual enum class RefractionMode {
        NONE,
        CUBEMAP,
        SCREEN_SPACE,
    }

    actual enum class RefractionType {
        SOLID,
        THIN,
    }

    actual enum class ReflectionMode {
        DEFAULT,
        SCREEN_SPACE,
    }

    actual enum class VertexDomain {
        OBJECT,
        WORLD,
        VIEW,
        DEVICE,
    }

    actual enum class CullingMode {
        NONE,
        FRONT,
        BACK,
        FRONT_AND_BACK,
    }

    actual enum class CompilerPriorityQueue {
        CRITICAL,
        HIGH,
        LOW,
    }

    actual enum class UboBatchingMode {
        DEFAULT,
        DISABLED,
    }

    actual class Parameter actual constructor(
        actual val name: String,
        actual val type: Type,
        actual val precision: Precision,
        actual val count: Int,
    ) {
        actual enum class Type {
            BOOL,
            BOOL2,
            BOOL3,
            BOOL4,
            FLOAT,
            FLOAT2,
            FLOAT3,
            FLOAT4,
            INT,
            INT2,
            INT3,
            INT4,
            UINT,
            UINT2,
            UINT3,
            UINT4,
            MAT3,
            MAT4,
            SAMPLER_2D,
            SAMPLER_2D_ARRAY,
            SAMPLER_CUBEMAP,
            SAMPLER_EXTERNAL,
            SAMPLER_3D,
            SUBPASS_INPUT,
        }

        actual enum class Precision {
            LOW,
            MEDIUM,
            HIGH,
            DEFAULT,
        }
    }
}

internal fun Material.CullingMode.toAndroid(): AndroidMaterial.CullingMode = AndroidMaterial.CullingMode.valueOf(name)

private fun AndroidMaterial.CullingMode.toKmp(): Material.CullingMode = Material.CullingMode.valueOf(name)

private fun Material.CompilerPriorityQueue.toAndroid(): AndroidMaterial.CompilerPriorityQueue =
    AndroidMaterial.CompilerPriorityQueue.valueOf(name)

private fun Material.Builder.ShadowSamplingQuality.toAndroid(): AndroidMaterial.Builder.ShadowSamplingQuality =
    AndroidMaterial.Builder.ShadowSamplingQuality.valueOf(name)

private fun Material.UboBatchingMode.toAndroid(): AndroidMaterial.UboBatchingMode =
    AndroidMaterial.UboBatchingMode.valueOf(name)

private fun AndroidMaterial.Shading.toKmp(): Material.Shading = Material.Shading.valueOf(name)

private fun AndroidMaterial.Interpolation.toKmp(): Material.Interpolation = Material.Interpolation.valueOf(name)

private fun AndroidMaterial.BlendingMode.toKmp(): Material.BlendingMode = Material.BlendingMode.valueOf(name)

private fun AndroidMaterial.TransparencyMode.toKmp(): Material.TransparencyMode = Material.TransparencyMode.valueOf(name)

private fun AndroidMaterial.RefractionMode.toKmp(): Material.RefractionMode = Material.RefractionMode.valueOf(name)

private fun AndroidMaterial.RefractionType.toKmp(): Material.RefractionType = Material.RefractionType.valueOf(name)

private fun AndroidMaterial.ReflectionMode.toKmp(): Material.ReflectionMode = Material.ReflectionMode.valueOf(name)

private fun AndroidMaterial.VertexDomain.toKmp(): Material.VertexDomain = Material.VertexDomain.valueOf(name)

private fun Colors.RgbaType.toAndroid(): AndroidColors.RgbaType = when (this) {
    Colors.RgbaType.SRGB -> AndroidColors.RgbaType.SRGB
    Colors.RgbaType.LINEAR -> AndroidColors.RgbaType.LINEAR
    Colors.RgbaType.PREMULTIPLIED_SRGB -> AndroidColors.RgbaType.PREMULTIPLIED_SRGB
    Colors.RgbaType.PREMULTIPLIED_LINEAR -> AndroidColors.RgbaType.PREMULTIPLIED_LINEAR
}

private fun Colors.RgbType.toAndroid(): AndroidColors.RgbType = when (this) {
    Colors.RgbType.SRGB -> AndroidColors.RgbType.SRGB
    Colors.RgbType.LINEAR -> AndroidColors.RgbType.LINEAR
}
