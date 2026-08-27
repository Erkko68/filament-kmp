@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaMaterial

actual class Material @InternalFilamentApi constructor(internal var nativeHandle: CPointer<FilaMaterial>?) {
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

    actual object UserVariantFilterBit {
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
        // Set in payload(): a non-empty blob that isn't a compiled .filamat. build() rejects it before
        // calling Filament's parser, which would otherwise panic uncatchably (see isValidFilamatPayload).
        private var payloadInvalid = false
        actual enum class ShadowSamplingQuality { HARD, LOW }

        actual fun payload(data: ByteArray): Builder = apply {
            if (data.isNotEmpty()) {
                payloadInvalid = !isValidFilamatPayload(data)
                data.usePinned { pinned ->
                    FilaMaterial_Builder_package(nativeBuilder, pinned.addressOf(0).reinterpret<ByteVar>(), data.size.toULong())
                }
            } else {
                payloadInvalid = false
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
            if (payloadInvalid) {
                FilaMaterial_Builder_destroy(nativeBuilder)
                throw IllegalArgumentException(
                    "Failed to build material — the payload is not a valid compiled .filamat",
                )
            }
            val handle = FilaMaterial_Builder_build(nativeBuilder, engine.nativeHandle)
            FilaMaterial_Builder_destroy(nativeBuilder)
            if (handle == null) {
                throw IllegalArgumentException(
                    "Failed to build material — the payload is not a valid compiled .filamat",
                )
            }
            return Material(handle)
        }
    }

    actual fun compile(priority: CompilerPriorityQueue, variants: Int, callback: (() -> Unit)?) {
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
    actual val defaultInstance: MaterialInstance get() = MaterialInstance(FilaMaterial_getDefaultInstance(nativeHandle))

    actual val name: String get() = FilaMaterial_getName(nativeHandle)?.toKString() ?: ""
    actual val shading: Shading get() = Shading.values()[FilaMaterial_getShading(nativeHandle).toInt()]
    actual val interpolation: Interpolation get() = Interpolation.values()[FilaMaterial_getInterpolation(nativeHandle).toInt()]
    actual val blendingMode: BlendingMode get() = BlendingMode.values()[FilaMaterial_getBlendingMode(nativeHandle).toInt()]
    actual val transparencyMode: TransparencyMode get() = TransparencyMode.values()[FilaMaterial_getTransparencyMode(nativeHandle).toInt()]
    actual val refractionMode: RefractionMode get() = RefractionMode.values()[FilaMaterial_getRefractionMode(nativeHandle)]
    actual val refractionType: RefractionType get() = RefractionType.values()[FilaMaterial_getRefractionType(nativeHandle)]
    actual val reflectionMode: ReflectionMode get() = ReflectionMode.values()[FilaMaterial_getReflectionMode(nativeHandle)]
    actual val vertexDomain: VertexDomain get() = VertexDomain.values()[FilaMaterial_getVertexDomain(nativeHandle).toInt()]
    actual val cullingMode: CullingMode get() = CullingMode.values()[FilaMaterial_getCullingMode(nativeHandle).toInt()]
    
    actual val isColorWriteEnabled: Boolean get() = FilaMaterial_isColorWriteEnabled(nativeHandle)
    actual val isDepthWriteEnabled: Boolean get() = FilaMaterial_isDepthWriteEnabled(nativeHandle)
    actual val isDepthCullingEnabled: Boolean get() = FilaMaterial_isDepthCullingEnabled(nativeHandle)
    actual val isDoubleSided: Boolean get() = FilaMaterial_isDoubleSided(nativeHandle)
    actual val isAlphaToCoverageEnabled: Boolean get() = FilaMaterial_isAlphaToCoverageEnabled(nativeHandle)
    
    actual val maskThreshold: Float get() = FilaMaterial_getMaskThreshold(nativeHandle)
    actual val specularAntiAliasingVariance: Float get() = FilaMaterial_getSpecularAntiAliasingVariance(nativeHandle)
    actual val specularAntiAliasingThreshold: Float get() = FilaMaterial_getSpecularAntiAliasingThreshold(nativeHandle)
    actual val featureLevel: Engine.FeatureLevel get() = Engine.FeatureLevel.entries[FilaMaterial_getFeatureLevel(nativeHandle).toInt()]
    
    actual val parameterCount: Int get() = FilaMaterial_getParameterCount(nativeHandle).toInt()

    actual val parameters: List<Parameter> get() = memScoped {
        val count = parameterCount
        if (count == 0) return emptyList()
        val infoArray = allocArray<FilaMaterialParameterInfo>(count)
        val actualCount = FilaMaterial_getParameters(nativeHandle, infoArray, count.toUInt())
        (0 until actualCount.toInt()).map { i ->
            val info = infoArray[i]
            Parameter(
                info.name?.toKString() ?: "",
                Parameter.Type.values()[info.type.toInt()],
                Parameter.Precision.values()[info.precision.toInt()],
                info.count.toInt()
            )
        }
    }

    actual val requiredAttributes: Set<VertexBuffer.VertexAttribute> get() {
        val bitset = FilaMaterial_getRequiredAttributes(nativeHandle)
        val result = mutableSetOf<VertexBuffer.VertexAttribute>()
        VertexBuffer.VertexAttribute.entries.forEach { attr ->
            if ((bitset and (1u shl attr.ordinal)) != 0u) {
                result.add(attr)
            }
        }
        return result
    }

    actual fun hasParameter(name: String): Boolean = FilaMaterial_hasParameter(nativeHandle, name)
    actual fun getParameterTransformName(samplerName: String): String? = FilaMaterial_getParameterTransformName(nativeHandle, samplerName)?.toKString()
    actual fun setDefaultParameter(name: String, value: Boolean) = FilaMaterial_setDefaultParameter_bool(nativeHandle, name, value)
    actual fun setDefaultParameter(name: String, value: Float) = FilaMaterial_setDefaultParameter_float(nativeHandle, name, value)
    actual fun setDefaultParameter(name: String, value: Int) = FilaMaterial_setDefaultParameter_int(nativeHandle, name, value)
    actual fun setDefaultParameter(name: String, x: Float, y: Float) = FilaMaterial_setDefaultParameter_float2(nativeHandle, name, x, y)
    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float) = FilaMaterial_setDefaultParameter_float3(nativeHandle, name, x, y, z)
    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float, w: Float) = FilaMaterial_setDefaultParameter_float4(nativeHandle, name, x, y, z, w)
}
