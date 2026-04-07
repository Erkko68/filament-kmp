package dev.filament.kmp

import com.google.android.filament.Material as AndroidMaterial
import java.nio.Buffer
import java.util.BitSet

actual class Material internal constructor(internal val nativeMaterial: AndroidMaterial) {
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

    actual class Builder actual constructor() {
        private val androidBuilder = AndroidMaterial.Builder()

        actual enum class ShadowSamplingQuality { HARD, LOW }

        actual fun payload(buffer: Any, size: Int): Builder {
            androidBuilder.payload(buffer as Buffer, size)
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
            // Android SDK might not expose this yet in the same way, or it's a newer feature
            return this
        }

        actual fun build(engine: Engine): Material {
            return Material(androidBuilder.build(engine.nativeEngine))
        }
    }

    actual fun compile(
        priority: CompilerPriorityQueue,
        variants: Int,
        handler: Any?,
        callback: (() -> Unit)?
    ) {
        nativeMaterial.compile(
            AndroidMaterial.CompilerPriorityQueue.values()[priority.ordinal],
            variants,
            handler as? android.os.Handler, // Cast to Android Handler
            callback
        )
    }

    actual fun createInstance(): MaterialInstance = MaterialInstance(nativeMaterial.createInstance())
    actual fun createInstance(name: String): MaterialInstance = MaterialInstance(nativeMaterial.createInstance(name))
    actual fun getDefaultInstance(): MaterialInstance = MaterialInstance(nativeMaterial.getDefaultInstance())

    actual fun getName(): String = nativeMaterial.name
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
    actual fun getFeatureLevel(): Engine.FeatureLevel = Engine.FeatureLevel.entries[nativeMaterial.featureLevel.ordinal]
    actual fun getParameterCount(): Int = nativeMaterial.parameterCount

    actual fun getRequiredAttributes(): Set<VertexBuffer.VertexAttribute> {
        val bitset = nativeMaterial.requiredAttributes as java.util.BitSet
        val result = mutableSetOf<VertexBuffer.VertexAttribute>()
        VertexBuffer.VertexAttribute.entries.forEach { attr ->
            if (bitset.get(attr.ordinal)) {
                result.add(attr)
            }
        }
        return result
    }
}
