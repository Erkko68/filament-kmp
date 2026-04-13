@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaMaterial

actual class Material internal constructor(internal var nativeHandle: CPointer<FilaMaterial>?) {
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
            actual val DIRECTIONAL_LIGHTING = 0x01
            actual val DYNAMIC_LIGHTING = 0x02
            actual val SHADOW_RECEIVER = 0x04
            actual val SKINNING = 0x08
            actual val FOG = 0x10
            actual val VSM = 0x20
            actual val SSR = 0x40
            actual val STE = 0x80
            actual val ALL = 0xFF
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
        private val nativeBuilder = FilaMaterial_Builder_create()
        actual enum class ShadowSamplingQuality { HARD, LOW }

        actual fun payload(data: ByteArray): Builder = apply {
            if (data.isNotEmpty()) {
                data.usePinned { pinned ->
                    FilaMaterial_Builder_package(nativeBuilder, pinned.addressOf(0).reinterpret<ByteVar>(), data.size.toULong())
                }
            }
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
            val handle = FilaMaterial_Builder_build(nativeBuilder, engine.nativeHandle)
            FilaMaterial_Builder_destroy(nativeBuilder)
            return Material(handle)
        }
    }

    actual fun compile(priority: CompilerPriorityQueue, variants: Int, handler: Any?, callback: (() -> Unit)?) {
        if (callback == null) {
            FilaMaterial_compile(nativeHandle, priority.ordinal.toUInt(), variants.toUInt(), null, null, null)
        } else {
            val stableRef = StableRef.create(callback)
            val callbackWrapper = staticCFunction { _: CPointer<FilaMaterial>?, user: COpaquePointer? ->
                val ref = user!!.asStableRef<(() -> Unit)>()
                val cb = ref.get()
                cb.invoke()
                ref.dispose()
            }
            FilaMaterial_compile(nativeHandle, priority.ordinal.toUInt(), variants.toUInt(), null, callbackWrapper, stableRef.asCPointer())
        }
    }

    actual fun createInstance(): MaterialInstance = MaterialInstance(FilaMaterial_createInstance(nativeHandle))
    actual fun createInstance(name: String): MaterialInstance = MaterialInstance(FilaMaterial_createInstanceWithName(nativeHandle, name))
    actual fun getDefaultInstance(): MaterialInstance = MaterialInstance(FilaMaterial_getDefaultInstance(nativeHandle))

    actual fun getName(): String = FilaMaterial_getName(nativeHandle)?.toKString() ?: ""
    actual fun getShading(): Shading = Shading.values()[FilaMaterial_getShading(nativeHandle).toInt()]
    actual fun getInterpolation(): Interpolation = Interpolation.values()[FilaMaterial_getInterpolation(nativeHandle).toInt()]
    actual fun getBlendingMode(): BlendingMode = BlendingMode.values()[FilaMaterial_getBlendingMode(nativeHandle).toInt()]
    actual fun getTransparencyMode(): TransparencyMode = TransparencyMode.values()[FilaMaterial_getTransparencyMode(nativeHandle).toInt()]
    actual fun getRefractionMode(): RefractionMode = RefractionMode.values()[FilaMaterial_getRefractionMode(nativeHandle)]
    actual fun getRefractionType(): RefractionType = RefractionType.values()[FilaMaterial_getRefractionType(nativeHandle)]
    actual fun getReflectionMode(): ReflectionMode = ReflectionMode.values()[FilaMaterial_getReflectionMode(nativeHandle)]
    actual fun getVertexDomain(): VertexDomain = VertexDomain.values()[FilaMaterial_getVertexDomain(nativeHandle).toInt()]
    actual fun getCullingMode(): CullingMode = CullingMode.values()[FilaMaterial_getCullingMode(nativeHandle).toInt()]
    
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

    actual fun getParameters(): List<Parameter> = memScoped {
        val count = getParameterCount()
        if (count == 0) return emptyList()
        val infoArray = allocArray<FilaMaterialParameterInfo>(count)
        val actualCount = FilaMaterial_getParameters(nativeHandle, infoArray, count.toUInt())
        (0 until actualCount.toInt()).map { i ->
            val info = infoArray[i]
            Parameter(
                info.name?.toKString() ?: "",
                Parameter.Type.values()[info.type],
                Parameter.Precision.values()[info.precision.toInt()],
                info.count.toInt()
            )
        }
    }

    actual fun getRequiredAttributes(): Set<VertexBuffer.VertexAttribute> {
        val bitset = FilaMaterial_getRequiredAttributes(nativeHandle)
        val result = mutableSetOf<VertexBuffer.VertexAttribute>()
        VertexBuffer.VertexAttribute.entries.forEach { attr ->
            if ((bitset and (1u shl attr.ordinal)) != 0u) {
                result.add(attr)
            }
        }
        return result
    }
}
