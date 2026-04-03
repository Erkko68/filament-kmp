package dev.filament.kmp

actual class Material {
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

    actual fun compile(priority: CompilerPriorityQueue, variants: Int, handler: Any?, callback: (() -> Unit)?) {
        TODO("Not yet implemented")
    }

    actual fun createInstance(): MaterialInstance = TODO("Not yet implemented")

    actual fun createInstance(name: String): MaterialInstance = TODO("Not yet implemented")

    actual fun getDefaultInstance(): MaterialInstance = TODO("Not yet implemented")

    actual fun getName(): String = TODO("Not yet implemented")

    actual fun getShading(): Shading = TODO("Not yet implemented")

    actual fun getInterpolation(): Interpolation = TODO("Not yet implemented")

    actual fun getBlendingMode(): BlendingMode = TODO("Not yet implemented")

    actual fun getTransparencyMode(): TransparencyMode = TODO("Not yet implemented")

    actual fun getRefractionMode(): RefractionMode = TODO("Not yet implemented")

    actual fun getRefractionType(): RefractionType = TODO("Not yet implemented")

    actual fun getReflectionMode(): ReflectionMode = TODO("Not yet implemented")

    actual fun getFeatureLevel(): Int = TODO("Not yet implemented")

    actual fun getVertexDomain(): VertexDomain = TODO("Not yet implemented")

    actual fun getCullingMode(): CullingMode = TODO("Not yet implemented")

    actual fun isColorWriteEnabled(): Boolean = TODO("Not yet implemented")

    actual fun isDepthWriteEnabled(): Boolean = TODO("Not yet implemented")

    actual fun isDepthCullingEnabled(): Boolean = TODO("Not yet implemented")

    actual fun isDoubleSided(): Boolean = TODO("Not yet implemented")

    actual fun isAlphaToCoverageEnabled(): Boolean = TODO("Not yet implemented")

    actual fun getMaskThreshold(): Float = TODO("Not yet implemented")

    actual fun getSpecularAntiAliasingVariance(): Float = TODO("Not yet implemented")

    actual fun getSpecularAntiAliasingThreshold(): Float = TODO("Not yet implemented")

    actual fun getRequiredAttributes(): Set<VertexBuffer.VertexAttribute> = TODO("Not yet implemented")

    actual fun getParameterCount(): Int = TODO("Not yet implemented")

    actual fun getParameters(): List<Parameter> = TODO("Not yet implemented")

    actual fun hasParameter(name: String): Boolean = TODO("Not yet implemented")

    actual fun getParameterTransformName(samplerName: String): String = TODO("Not yet implemented")

    actual fun setDefaultParameter(name: String, x: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun setDefaultParameter(name: String, x: Float) {
        TODO("Not yet implemented")
    }

    actual fun setDefaultParameter(name: String, x: Int) {
        TODO("Not yet implemented")
    }

    actual fun setDefaultParameter(name: String, x: Float, y: Float) {
        TODO("Not yet implemented")
    }

    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float) {
        TODO("Not yet implemented")
    }

    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float, w: Float) {
        TODO("Not yet implemented")
    }

    actual fun setDefaultParameter(name: String, type: Any, x: Float, y: Float, z: Float, w: Float) {
        TODO("Not yet implemented")
    }

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }

    actual class Builder {
        actual fun payload(buffer: Any, size: Int): Builder = TODO("Not yet implemented")

        actual fun sphericalHarmonicsBandCount(shBandCount: Int): Builder = TODO("Not yet implemented")

        actual fun shadowSamplingQuality(quality: ShadowSamplingQuality): Builder = TODO("Not yet implemented")

        actual fun uboBatching(mode: UboBatchingMode): Builder = TODO("Not yet implemented")

        actual fun build(engine: Engine): Material = TODO("Not yet implemented")

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
}
