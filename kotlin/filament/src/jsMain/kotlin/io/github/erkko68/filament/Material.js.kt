import io.github.erkko68.filament.js.Material as JSMaterial

actual class Material(internal val jsMaterial: JSMaterial) {
    actual fun compile(
        priority: CompilerPriorityQueue,
        variants: Int,
        handler: Any?,
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

    actual fun getShading(): Shading {
        return Shading.LIT
    }

    actual fun getInterpolation(): Interpolation {
        return Interpolation.SMOOTH
    }

    actual fun getBlendingMode(): BlendingMode {
        return BlendingMode.OPAQUE
    }

    actual fun getTransparencyMode(): TransparencyMode {
        return TransparencyMode.DEFAULT
    }

    actual fun getRefractionMode(): RefractionMode {
        return RefractionMode.NONE
    }

    actual fun getRefractionType(): RefractionType {
        return RefractionType.SOLID
    }

    actual fun getReflectionMode(): ReflectionMode {
        return ReflectionMode.DEFAULT
    }

    actual fun getVertexDomain(): VertexDomain {
        return VertexDomain.OBJECT
    }

    actual fun getCullingMode(): CullingMode {
        return CullingMode.BACK
    }

    actual fun isColorWriteEnabled(): Boolean {
        return true
    }

    actual fun isDepthWriteEnabled(): Boolean {
        return true
    }

    actual fun isDepthCullingEnabled(): Boolean {
        return true
    }

    actual fun isDoubleSided(): Boolean {
        return false
    }

    actual fun isAlphaToCoverageEnabled(): Boolean {
        return false
    }

    actual fun getMaskThreshold(): Float {
        return 0.4f
    }

    actual fun getSpecularAntiAliasingVariance(): Float {
        return 0.0f
    }

    actual fun getSpecularAntiAliasingThreshold(): Float {
        return 0.0f
    }

    actual fun getFeatureLevel(): Engine.FeatureLevel {
        return Engine.FeatureLevel.FEATURE_LEVEL_1
    }

    actual fun getParameterCount(): Int {
        return 0
    }

    actual fun getParameters(): List<Parameter> {
        return emptyList()
    }

    actual fun getRequiredAttributes(): Set<VertexBuffer.VertexAttribute> {
        return emptySet()
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
        private var _uboBatchingMode: UboBatchingMode = UboBatchingMode.DEFAULT

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
            _uboBatchingMode = mode
            return this
        }

        actual fun build(engine: Engine): Material {
            val payload = _payload ?: throw IllegalStateException("Material payload must be set")
            
            val options = js("{}")
            options.uboBatching = when(_uboBatchingMode) {
                UboBatchingMode.DEFAULT -> io.github.erkko68.filament.js.Material_UboBatchingMode.DEFAULT
                UboBatchingMode.DISABLED -> io.github.erkko68.filament.js.Material_UboBatchingMode.DISABLED
            }

            // In JS, engine.createMaterial takes a BufferReference (which can be a Uint8Array)
            return Material(engine.jsEngine.createMaterial(payload, options))
        }

        actual enum class ShadowSamplingQuality { HARD, LOW }
    }
}