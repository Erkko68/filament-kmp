package io.github.erkko68.filament

import io.github.erkko68.filament.ffm.FilamentC
import io.github.erkko68.filament.ffm.FilaMaterialParameterInfo
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

actual class Material @InternalFilamentApi constructor(internal var nativeHandle: MemorySegment?) {
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
        private val nativeBuilder = FilamentC.FilaMaterial_Builder_create()
        // Filament's C++ Material::Builder::package() only stores a pointer — the
        // payload data must stay valid until build() is called.  A confined arena
        // would free the memory immediately, causing a use-after-free crash.  We
        // keep the arena alive here and close it once build() has consumed the data.
        private var payloadArena: Arena? = null
        // Set in payload(): a non-empty blob that isn't a compiled .filamat. build() rejects it before
        // calling Filament's parser, which would otherwise panic uncatchably (see isValidFilamatPayload).
        private var payloadInvalid = false
        actual enum class ShadowSamplingQuality { HARD, LOW }

        actual fun payload(data: ByteArray): Builder = apply {
            // Close any previously set payload arena (in case payload() is called twice).
            payloadArena?.close()
            if (data.isNotEmpty()) {
                payloadInvalid = !isValidFilamatPayload(data)
                val arena = Arena.ofConfined()
                payloadArena = arena
                FilamentC.FilaMaterial_Builder_package(nativeBuilder, arena.bytes(data), data.size.toLong())
            } else {
                payloadArena = null
                payloadInvalid = false
            }
        }
        actual fun sphericalHarmonicsBandCount(shBandCount: Int): Builder = apply {
            FilamentC.FilaMaterial_Builder_sphericalHarmonicsBandCount(nativeBuilder, shBandCount)
        }
        actual fun shadowSamplingQuality(quality: ShadowSamplingQuality): Builder = apply {
            FilamentC.FilaMaterial_Builder_shadowSamplingQuality(nativeBuilder, quality.ordinal)
        }
        actual fun uboBatching(mode: UboBatchingMode): Builder = apply {
            FilamentC.FilaMaterial_Builder_uboBatching(nativeBuilder, mode.ordinal)
        }
        actual fun build(engine: Engine): Material {
            try {
                if (payloadInvalid) {
                    throw IllegalArgumentException(
                        "Failed to build material — the payload is not a valid compiled .filamat",
                    )
                }
                val handle = FilamentC.FilaMaterial_Builder_build(nativeBuilder, engine.nativeHandle)
                if (handle.isNullPtr()) {
                    throw IllegalArgumentException(
                        "Failed to build material — the payload is not a valid compiled .filamat",
                    )
                }
                return Material(handle)
            } finally {
                payloadArena?.close()
                payloadArena = null
                FilamentC.FilaMaterial_Builder_destroy(nativeBuilder)
            }
        }
    }

    actual fun compile(priority: CompilerPriorityQueue, variants: Int, callback: (() -> Unit)?) {
        if (callback == null) {
            FilamentC.FilaMaterial_compile(nativeHandle, priority.ordinal, variants, NULL, NULL, NULL)
        } else {
            val userData = Completions.register(callback)
            FilamentC.FilaMaterial_compile(nativeHandle, priority.ordinal, variants, NULL, Completions.materialCompileStub, userData)
        }
    }

    actual fun createInstance(): MaterialInstance = MaterialInstance(FilamentC.FilaMaterial_createInstance(nativeHandle))
    actual fun createInstance(name: String): MaterialInstance = confined { arena -> MaterialInstance(FilamentC.FilaMaterial_createInstanceWithName(nativeHandle, arena.cstr(name))) }
    actual val defaultInstance: MaterialInstance get() = MaterialInstance(FilamentC.FilaMaterial_getDefaultInstance(nativeHandle))

    actual val name: String get() = FilamentC.FilaMaterial_getName(nativeHandle).let { if (it.isNullPtr()) "" else it.cString() }
    actual val shading: Shading get() = Shading.values()[FilamentC.FilaMaterial_getShading(nativeHandle)]
    actual val interpolation: Interpolation get() = Interpolation.values()[FilamentC.FilaMaterial_getInterpolation(nativeHandle)]
    actual val blendingMode: BlendingMode get() = BlendingMode.values()[FilamentC.FilaMaterial_getBlendingMode(nativeHandle)]
    actual val transparencyMode: TransparencyMode get() = TransparencyMode.values()[FilamentC.FilaMaterial_getTransparencyMode(nativeHandle)]
    actual val refractionMode: RefractionMode get() = RefractionMode.values()[FilamentC.FilaMaterial_getRefractionMode(nativeHandle)]
    actual val refractionType: RefractionType get() = RefractionType.values()[FilamentC.FilaMaterial_getRefractionType(nativeHandle)]
    actual val reflectionMode: ReflectionMode get() = ReflectionMode.values()[FilamentC.FilaMaterial_getReflectionMode(nativeHandle)]
    actual val vertexDomain: VertexDomain get() = VertexDomain.values()[FilamentC.FilaMaterial_getVertexDomain(nativeHandle)]
    actual val cullingMode: CullingMode get() = CullingMode.values()[FilamentC.FilaMaterial_getCullingMode(nativeHandle)]

    actual val isColorWriteEnabled: Boolean get() = FilamentC.FilaMaterial_isColorWriteEnabled(nativeHandle)
    actual val isDepthWriteEnabled: Boolean get() = FilamentC.FilaMaterial_isDepthWriteEnabled(nativeHandle)
    actual val isDepthCullingEnabled: Boolean get() = FilamentC.FilaMaterial_isDepthCullingEnabled(nativeHandle)
    actual val isDoubleSided: Boolean get() = FilamentC.FilaMaterial_isDoubleSided(nativeHandle)
    actual val isAlphaToCoverageEnabled: Boolean get() = FilamentC.FilaMaterial_isAlphaToCoverageEnabled(nativeHandle)

    actual val maskThreshold: Float get() = FilamentC.FilaMaterial_getMaskThreshold(nativeHandle)
    actual val specularAntiAliasingVariance: Float get() = FilamentC.FilaMaterial_getSpecularAntiAliasingVariance(nativeHandle)
    actual val specularAntiAliasingThreshold: Float get() = FilamentC.FilaMaterial_getSpecularAntiAliasingThreshold(nativeHandle)
    actual val featureLevel: Engine.FeatureLevel get() = Engine.FeatureLevel.entries[FilamentC.FilaMaterial_getFeatureLevel(nativeHandle)]

    actual val parameterCount: Int get() = FilamentC.FilaMaterial_getParameterCount(nativeHandle)

    actual val parameters: List<Parameter> get() {
        val count = parameterCount
        if (count == 0) return emptyList()
        return confined { arena ->
            val arr = FilaMaterialParameterInfo.allocateArray(count.toLong(), arena)
            val actualCount = FilamentC.FilaMaterial_getParameters(nativeHandle, arr, count)
            (0 until actualCount).map { i ->
                val info = FilaMaterialParameterInfo.asSlice(arr, i.toLong())
                val namePtr = FilaMaterialParameterInfo.name(info)
                Parameter(
                    if (namePtr.isNullPtr()) "" else namePtr.cString(),
                    Parameter.Type.values()[FilaMaterialParameterInfo.type(info).toInt() and 0xFF],
                    Parameter.Precision.values()[FilaMaterialParameterInfo.precision(info).toInt() and 0xFF],
                    FilaMaterialParameterInfo.count(info)
                )
            }
        }
    }

    actual val requiredAttributes: Set<VertexBuffer.VertexAttribute> get() {
        val bitset = FilamentC.FilaMaterial_getRequiredAttributes(nativeHandle)
        val result = mutableSetOf<VertexBuffer.VertexAttribute>()
        VertexBuffer.VertexAttribute.entries.forEach { attr ->
            if ((bitset and (1 shl attr.ordinal)) != 0) {
                result.add(attr)
            }
        }
        return result
    }

    actual fun hasParameter(name: String): Boolean = confined { arena -> FilamentC.FilaMaterial_hasParameter(nativeHandle, arena.cstr(name)) }
    actual fun getParameterTransformName(samplerName: String): String? = confined { arena ->
        FilamentC.FilaMaterial_getParameterTransformName(nativeHandle, arena.cstr(samplerName)).let { if (it.isNullPtr()) null else it.cString() }
    }
    actual fun setDefaultParameter(name: String, value: Boolean) = confined { arena -> FilamentC.FilaMaterial_setDefaultParameter_bool(nativeHandle, arena.cstr(name), value) }
    actual fun setDefaultParameter(name: String, value: Float) = confined { arena -> FilamentC.FilaMaterial_setDefaultParameter_float(nativeHandle, arena.cstr(name), value) }
    actual fun setDefaultParameter(name: String, value: Int) = confined { arena -> FilamentC.FilaMaterial_setDefaultParameter_int(nativeHandle, arena.cstr(name), value) }
    actual fun setDefaultParameter(name: String, x: Float, y: Float) = confined { arena -> FilamentC.FilaMaterial_setDefaultParameter_float2(nativeHandle, arena.cstr(name), x, y) }
    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float) = confined { arena -> FilamentC.FilaMaterial_setDefaultParameter_float3(nativeHandle, arena.cstr(name), x, y, z) }
    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float, w: Float) = confined { arena -> FilamentC.FilaMaterial_setDefaultParameter_float4(nativeHandle, arena.cstr(name), x, y, z, w) }
}