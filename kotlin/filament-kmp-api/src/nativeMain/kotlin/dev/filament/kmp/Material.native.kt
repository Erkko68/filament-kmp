@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaMaterial
import cnames.structs.FilaMaterial_Builder

actual class Material internal constructor(internal val nativeHandle: CPointer<FilaMaterial>) {
    private val mDefaultInstance: MaterialInstance by lazy { MaterialInstance(this, FilaMaterial_getDefaultInstance(nativeHandle)!!) }
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
        private val nativeBuilder = FilaMaterial_Builder_create()!!

        actual enum class ShadowSamplingQuality { HARD, LOW }

        actual fun payload(buffer: Any, size: Int): Builder = apply {
            FilaMaterial_Builder_package(nativeBuilder, buffer.interpretAsCPointer(), size.toULong())
        }

        actual fun sphericalHarmonicsBandCount(shBandCount: Int): Builder = apply {
            FilaMaterial_Builder_sphericalHarmonicsBandCount(nativeBuilder, shBandCount)
        }

        actual fun shadowSamplingQuality(quality: ShadowSamplingQuality): Builder = apply {
            FilaMaterial_Builder_shadowSamplingQuality(nativeBuilder, quality.ordinal.toUInt())
        }

        actual fun uboBatching(mode: UboBatchingMode): Builder = apply {
            FilaMaterial_Builder_uboBatching(nativeBuilder, mode.ordinal.toUInt())
        }

        actual fun build(engine: Engine): Material {
            val material = FilaMaterial_Builder_build(nativeBuilder, engine.nativeHandle)
            FilaMaterial_Builder_destroy(nativeBuilder)
            return Material(material!!)
        }
    }

    actual fun compile(
        priority: CompilerPriorityQueue,
        variants: Int,
        handler: Any?,
        callback: (() -> Unit)?
    ) {
        val stableRef = if (callback != null) StableRef.create(callback) else null
        FilaMaterial_compile(
            nativeHandle,
            priority.ordinal.toUInt(),
            variants.toUInt(),
            handler.interpretAsCPointer(),
            staticCFunction { m, data ->
                val cb = data?.asStableRef<() -> Unit>()?.get()
                cb?.invoke()
                data?.asStableRef<() -> Unit>()?.dispose()
            },
            stableRef?.asCPointer()
        )
    }

    actual fun createInstance(): MaterialInstance = MaterialInstance(this, FilaMaterial_createInstance(nativeHandle)!!)
    actual fun createInstance(name: String): MaterialInstance = MaterialInstance(this, FilaMaterial_createInstanceWithName(nativeHandle, name)!!)
    actual fun getDefaultInstance(): MaterialInstance = mDefaultInstance

    actual fun getName(): String = FilaMaterial_getName(nativeHandle)?.toKString() ?: ""
    actual fun getShading(): Shading = Shading.entries[FilaMaterial_getShading(nativeHandle).toInt()]
    actual fun getInterpolation(): Interpolation = Interpolation.entries[FilaMaterial_getInterpolation(nativeHandle).toInt()]
    actual fun getBlendingMode(): BlendingMode = BlendingMode.entries[FilaMaterial_getBlendingMode(nativeHandle).toInt()]
    actual fun getTransparencyMode(): TransparencyMode = TransparencyMode.entries[FilaMaterial_getTransparencyMode(nativeHandle).toInt()]
    actual fun getRefractionMode(): RefractionMode = RefractionMode.entries[FilaMaterial_getRefractionMode(nativeHandle).toInt()]
    actual fun getRefractionType(): RefractionType = RefractionType.entries[FilaMaterial_getRefractionType(nativeHandle).toInt()]
    actual fun getReflectionMode(): ReflectionMode = ReflectionMode.entries[FilaMaterial_getReflectionMode(nativeHandle).toInt()]
    actual fun getVertexDomain(): VertexDomain = VertexDomain.entries[FilaMaterial_getVertexDomain(nativeHandle).toInt()]
    actual fun getCullingMode(): CullingMode = CullingMode.entries[FilaMaterial_getCullingMode(nativeHandle).toInt()]
    actual fun isColorWriteEnabled(): Boolean = FilaMaterial_isColorWriteEnabled(nativeHandle)
    actual fun isDepthWriteEnabled(): Boolean = FilaMaterial_isDepthWriteEnabled(nativeHandle)
    actual fun isDepthCullingEnabled(): Boolean = FilaMaterial_isDepthCullingEnabled(nativeHandle)
    actual fun isDoubleSided(): Boolean = FilaMaterial_isDoubleSided(nativeHandle)
    actual fun isAlphaToCoverageEnabled(): Boolean = FilaMaterial_isAlphaToCoverageEnabled(nativeHandle)
    actual fun getMaskThreshold(): Float = FilaMaterial_getMaskThreshold(nativeHandle)
    actual fun getSpecularAntiAliasingVariance(): Float = FilaMaterial_getSpecularAntiAliasingVariance(nativeHandle)
    actual fun getSpecularAntiAliasingThreshold(): Float = FilaMaterial_getSpecularAntiAliasingThreshold(nativeHandle)
    actual fun getFeatureLevel(): Engine.FeatureLevel = Engine.FeatureLevel.entries[FilaMaterial_getFeatureLevel(nativeHandle).toInt()]
    actual fun getParameterCount(): Int = FilaMaterial_getParameterCount(nativeHandle).toInt()

    actual fun getRequiredAttributes(): Set<VertexBuffer.VertexAttribute> {
        val bitmask = FilaMaterial_getRequiredAttributes(nativeHandle)
        val attributes = mutableSetOf<VertexBuffer.VertexAttribute>()
        VertexBuffer.VertexAttribute.entries.forEach { attr ->
            if ((bitmask and (1u shl attr.ordinal)) != 0u) {
                attributes.add(attr)
            }
        }
        return attributes
    }
}

private fun Any?.interpretAsCPointer(): CPointer<out CPointed>? {
    return if (this is CPointer<*>) this.reinterpret() else null
}
