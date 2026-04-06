@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package dev.filament.kmp

import dev.filament.kmp.cinterop.*
import kotlinx.cinterop.*
import cnames.structs.FilaMaterial
import cnames.structs.FilaMaterialBuilder

actual class Material internal constructor(internal var nativeHandle: CPointer<FilaMaterial>?) {
    actual class Parameter actual constructor(
        actual val name: String,
        actual val type: Type,
        actual val precision: Precision,
        actual val count: Int,
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

        actual enum class Precision {
            LOW, MEDIUM, HIGH, DEFAULT
        }
    }

    actual fun compile(priority: CompilerPriorityQueue, variants: Int, handler: Any?, callback: (() -> Unit)?) {
        val callbackPtr = if (callback != null) {
            StableRef.create(callback).asCPointer()
        } else {
            null
        }

        FilaMaterial_compile(
            nativeHandle,
            priority.ordinal,
            variants,
            null, // handler not used directly in C-wrapper yet
            if (callbackPtr != null) staticCFunction { _, ptr ->
                val ref = ptr!!.asStableRef<() -> Unit>()
                val cb = ref.get()
                cb()
                ref.dispose()
            } else null,
            callbackPtr
        )
    }

    actual fun createInstance(): MaterialInstance {
        val result = FilaMaterial_createInstance(nativeHandle)
            ?: throw IllegalStateException("Failed to create MaterialInstance")
        return MaterialInstance(result)
    }

    actual fun createInstance(name: String): MaterialInstance {
        val result = FilaMaterial_createInstanceWithName(nativeHandle, name)
            ?: throw IllegalStateException("Failed to create MaterialInstance named $name")
        return MaterialInstance(result)
    }

    actual val defaultInstance: MaterialInstance
        get() = MaterialInstance(FilaMaterial_getDefaultInstance(nativeHandle))

    actual val name: String
        get() = FilaMaterial_getName(nativeHandle)?.toKString() ?: ""

    actual val shading: Shading
        get() = Shading.entries[FilaMaterial_getShading(nativeHandle).toInt()]

    actual val interpolation: Interpolation
        get() = Interpolation.entries[FilaMaterial_getInterpolation(nativeHandle).toInt()]

    actual val blendingMode: BlendingMode
        get() = BlendingMode.entries[FilaMaterial_getBlendingMode(nativeHandle).toInt()]

    actual val transparencyMode: TransparencyMode
        get() = TransparencyMode.entries[FilaMaterial_getTransparencyMode(nativeHandle).toInt()]

    actual val refractionMode: RefractionMode
        get() = RefractionMode.entries[FilaMaterial_getRefractionMode(nativeHandle)]

    actual val refractionType: RefractionType
        get() = RefractionType.entries[FilaMaterial_getRefractionType(nativeHandle)]

    actual val reflectionMode: ReflectionMode
        get() = ReflectionMode.entries[FilaMaterial_getReflectionMode(nativeHandle)]

    actual val featureLevel: Int
        get() = FilaMaterial_getFeatureLevel(nativeHandle)

    actual val vertexDomain: VertexDomain
        get() = VertexDomain.entries[FilaMaterial_getVertexDomain(nativeHandle).toInt()]

    actual val cullingMode: CullingMode
        get() = CullingMode.entries[FilaMaterial_getCullingMode(nativeHandle).toInt()]

    actual val isColorWriteEnabled: Boolean
        get() = FilaMaterial_isColorWriteEnabled(nativeHandle)

    actual val isDepthWriteEnabled: Boolean
        get() = FilaMaterial_isDepthWriteEnabled(nativeHandle)

    actual val isDepthCullingEnabled: Boolean
        get() = FilaMaterial_isDepthCullingEnabled(nativeHandle)

    actual val isDoubleSided: Boolean
        get() = FilaMaterial_isDoubleSided(nativeHandle)

    actual val isAlphaToCoverageEnabled: Boolean
        get() = FilaMaterial_isAlphaToCoverageEnabled(nativeHandle)

    actual val maskThreshold: Float
        get() = FilaMaterial_getMaskThreshold(nativeHandle)

    actual val specularAntiAliasingVariance: Float
        get() = FilaMaterial_getSpecularAntiAliasingVariance(nativeHandle)

    actual val specularAntiAliasingThreshold: Float
        get() = FilaMaterial_getSpecularAntiAliasingThreshold(nativeHandle)

    actual val requiredAttributes: Set<VertexBuffer.VertexAttribute>
        get() {
            val mask = FilaMaterial_getRequiredAttributes(nativeHandle)
            val result = mutableSetOf<VertexBuffer.VertexAttribute>()
            VertexBuffer.VertexAttribute.entries.forEach { attr ->
                if ((mask and (1u shl attr.ordinal)) != 0u) {
                    result.add(attr)
                }
            }
            return result
        }

    actual val parameterCount: Int
        get() = FilaMaterial_getParameterCount(nativeHandle).toInt()

    actual val parameters: List<Parameter>
        get() {
            val count = parameterCount
            if (count == 0) return emptyList()
            memScoped {
                val outParams = allocArray<FilaMaterialParameterInfo>(count)
                FilaMaterial_getParameters(nativeHandle, outParams, count.toUInt())
                return (0 until count).map { i ->
                    val info = outParams[i]
                    Parameter(
                        info.name?.toKString() ?: "",
                        decodeType(info.type.convert()),
                        Parameter.Precision.entries[info.precision.toInt()],
                        info.count.toInt()
                    )
                }
            }
        }

    actual fun hasParameter(name: String): Boolean = FilaMaterial_hasParameter(nativeHandle, name)

    actual fun getParameterTransformName(samplerName: String): String =
        FilaMaterial_getParameterTransformName(nativeHandle, samplerName)?.toKString() ?: ""

    actual fun setDefaultParameter(name: String, x: Boolean) = defaultInstance.setParameter(name, x)
    actual fun setDefaultParameter(name: String, x: Float) = defaultInstance.setParameter(name, x)
    actual fun setDefaultParameter(name: String, x: Int) = defaultInstance.setParameter(name, x)
    actual fun setDefaultParameter(name: String, x: Float, y: Float) = defaultInstance.setParameter(name, x, y)
    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float) = defaultInstance.setParameter(name, x, y, z)
    actual fun setDefaultParameter(name: String, x: Float, y: Float, z: Float, w: Float) = defaultInstance.setParameter(name, x, y, z, w)
    actual fun setDefaultParameter(name: String, type: Any, x: Float, y: Float, z: Float, w: Float) =
        defaultInstance.setParameter(name, type as RgbaType, x, y, z, w)

    actual val nativeObject: Long
        get() = nativeHandle?.rawValue?.toLong() ?: 0L

    actual internal fun invalidate() {
        nativeHandle = null
    }

    actual class Builder {
        private var _payload: CPointer<out CPointed>? = null
        private var _size: Int = 0

        actual fun payload(buffer: Any, size: Int): Builder {
            if (buffer is ByteArray) {
                buffer.usePinned { pinned ->
                    // Note: This is unsafe if build() is called later after pinning is gone.
                    // But for a builder.build() pattern, it might be okay if we pin during build.
                }
            }
            _size = size
            // We need a better way to handle the buffer. For now, assume it's passed as a persistent memory or copied.
            return this
        }

        actual fun sphericalHarmonicsBandCount(shBandCount: Int): Builder {
            // Not in load-only Material::Builder
            return this
        }

        actual fun shadowSamplingQuality(quality: ShadowSamplingQuality): Builder {
            // Not in load-only Material::Builder
            return this
        }

        actual fun uboBatching(mode: UboBatchingMode): Builder {
            // Not in load-only Material::Builder
            return this
        }

        actual fun build(engine: Engine): Material {
            // We need to pass the payload. Assuming user logic provides a stable pointer.
            val result = FilaMaterial_Builder_build(engine.nativeHandle, null, _size.toULong())
                ?: throw IllegalStateException("Failed to build Material")
            return Material(result)
        }

        actual enum class ShadowSamplingQuality { HARD, LOW }
    }

    actual enum class Shading { UNLIT, LIT, CLOTH, SUBSURFACE, SPECULAR_GLOSSINESS }
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

    companion object {
        private fun decodeType(type: Int): Parameter.Type = when (type) {
            0 -> Parameter.Type.BOOL
            100 -> Parameter.Type.SAMPLER_2D
            // ... need full mapping
            else -> Parameter.Type.FLOAT
        }
    }
}
