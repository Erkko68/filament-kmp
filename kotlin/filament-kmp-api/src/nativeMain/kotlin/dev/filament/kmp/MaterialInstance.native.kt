@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package dev.filament.kmp

import dev.filament.kmp.cinterop.*
import kotlinx.cinterop.*
import cnames.structs.FilaMaterialInstance

actual class MaterialInstance internal constructor(internal var nativeHandle: CPointer<FilaMaterialInstance>?) {
    actual enum class StencilOperation {
        KEEP, ZERO, REPLACE, INCR_CLAMP, INCR_WRAP, DECR_CLAMP, DECR_WRAP, INVERT
    }

    actual enum class StencilFace {
        FRONT, BACK, FRONT_AND_BACK
    }

    actual companion object {
        actual fun duplicate(other: MaterialInstance, name: String?): MaterialInstance {
            val result = FilaMaterialInstance_duplicate(other.nativeHandle, name)
                ?: throw IllegalStateException("Failed to duplicate MaterialInstance")
            return MaterialInstance(result)
        }
    }

    actual val material: Material
        get() = Material(FilaMaterialInstance_getMaterial(nativeHandle))

    actual val name: String
        get() = FilaMaterialInstance_getName(nativeHandle)?.toKString() ?: ""

    actual fun setParameter(name: String, x: Float) =
        FilaMaterialInstance_setParameterFloat(nativeHandle, name, x)

    actual fun setParameter(name: String, x: Int) =
        FilaMaterialInstance_setParameterInt(nativeHandle, name, x)

    actual fun setParameter(name: String, x: Float, y: Float) =
        FilaMaterialInstance_setParameterFloat2(nativeHandle, name, x, y)

    actual fun setParameter(name: String, x: Int, y: Int) =
        FilaMaterialInstance_setParameterInt2(nativeHandle, name, x, y)

    actual fun setParameter(name: String, x: Float, y: Float, z: Float) =
        FilaMaterialInstance_setParameterFloat3(nativeHandle, name, x, y, z)

    actual fun setParameter(name: String, x: Int, y: Int, z: Int) =
        FilaMaterialInstance_setParameterInt3(nativeHandle, name, x, y, z)

    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) =
        FilaMaterialInstance_setParameterFloat4(nativeHandle, name, x, y, z, w)

    actual fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) =
        FilaMaterialInstance_setParameterInt4(nativeHandle, name, x, y, z, w)

    actual fun setParameter(name: String, type: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float) =
        FilaMaterialInstance_setParameterRgba(nativeHandle, name, type.ordinal.toUInt(), r, g, b, a)

    actual fun setParameter(name: String, texture: Texture, sampler: TextureSampler) =
        FilaMaterialInstance_setParameterTexture(nativeHandle, name, texture.nativeHandle, sampler.nativeObject.toULong())

    actual fun setScissor(left: Int, bottom: Int, width: Int, height: Int) =
        FilaMaterialInstance_setScissor(nativeHandle, left, bottom, width.toUInt(), height.toUInt())

    actual fun unsetScissor() = FilaMaterialInstance_unsetScissor(nativeHandle)

    actual fun setPolygonOffset(scale: Float, constant: Float) =
        FilaMaterialInstance_setPolygonOffset(nativeHandle, scale, constant)

    actual var maskThreshold: Float
        get() = FilaMaterialInstance_getMaskThreshold(nativeHandle)
        set(value) = FilaMaterialInstance_setMaskThreshold(nativeHandle, value)

    actual var isDoubleSided: Boolean
        get() = FilaMaterialInstance_isDoubleSided(nativeHandle)
        set(value) = FilaMaterialInstance_setDoubleSided(nativeHandle, value)

    actual var transparencyMode: Material.TransparencyMode
        get() = Material.TransparencyMode.entries[FilaMaterialInstance_getTransparencyMode(nativeHandle).toInt()]
        set(value) = FilaMaterialInstance_setTransparencyMode(nativeHandle, value.ordinal.toUInt())

    actual var cullingMode: Material.CullingMode
        get() = Material.CullingMode.entries[FilaMaterialInstance_getCullingMode(nativeHandle).toInt()]
        set(value) = FilaMaterialInstance_setCullingMode(nativeHandle, value.ordinal.toUInt())

    actual fun setCullingMode(colorPassCullingMode: Material.CullingMode, shadowPassCullingMode: Material.CullingMode) =
        FilaMaterialInstance_setCullingModeSeparate(nativeObject.toCPointer(), colorPassCullingMode.ordinal.toUInt(), shadowPassCullingMode.ordinal.toUInt())

    actual val shadowCullingMode: Material.CullingMode
        get() = Material.CullingMode.entries[FilaMaterialInstance_getShadowCullingMode(nativeHandle).toInt()]

    actual var isColorWriteEnabled: Boolean
        get() = FilaMaterialInstance_isColorWriteEnabled(nativeHandle)
        set(value) = FilaMaterialInstance_setColorWrite(nativeHandle, value)

    actual var isDepthWriteEnabled: Boolean
        get() = FilaMaterialInstance_isDepthWriteEnabled(nativeHandle)
        set(value) = FilaMaterialInstance_setDepthWrite(nativeHandle, value)

    actual var isStencilWriteEnabled: Boolean
        get() = FilaMaterialInstance_isStencilWriteEnabled(nativeHandle)
        set(value) = FilaMaterialInstance_setStencilWrite(nativeHandle, value)

    actual var isDepthCullingEnabled: Boolean
        get() = FilaMaterialInstance_isDepthCullingEnabled(nativeHandle)
        set(value) = FilaMaterialInstance_setDepthCulling(nativeHandle, value)

    actual var depthFunc: TextureSampler.CompareFunction
        get() = TextureSampler.CompareFunction.entries[FilaMaterialInstance_getDepthFunc(nativeHandle).toInt()]
        set(value) = FilaMaterialInstance_setDepthFunc(nativeHandle, value.ordinal.toUInt())

    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction, face: StencilFace) =
        FilaMaterialInstance_setStencilCompareFunction(nativeHandle, func.ordinal.toUInt(), face.ordinal.toUInt())

    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction) =
        FilaMaterialInstance_setStencilCompareFunction(nativeHandle, func.ordinal.toUInt(), StencilFace.FRONT_AND_BACK.ordinal.toUInt())

    actual fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace) =
        FilaMaterialInstance_setStencilOpStencilFail(nativeHandle, op.ordinal.toUInt(), face.ordinal.toUInt())

    actual fun setStencilOpStencilFail(op: StencilOperation) =
        FilaMaterialInstance_setStencilOpStencilFail(nativeHandle, op.ordinal.toUInt(), StencilFace.FRONT_AND_BACK.ordinal.toUInt())

    actual fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace) =
        FilaMaterialInstance_setStencilOpDepthFail(nativeHandle, op.ordinal.toUInt(), face.ordinal.toUInt())

    actual fun setStencilOpDepthFail(op: StencilOperation) =
        FilaMaterialInstance_setStencilOpDepthFail(nativeHandle, op.ordinal.toUInt(), StencilFace.FRONT_AND_BACK.ordinal.toUInt())

    actual fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace) =
        FilaMaterialInstance_setStencilOpDepthStencilPass(nativeHandle, op.ordinal.toUInt(), face.ordinal.toUInt())

    actual fun setStencilOpDepthStencilPass(op: StencilOperation) =
        FilaMaterialInstance_setStencilOpDepthStencilPass(nativeHandle, op.ordinal.toUInt(), StencilFace.FRONT_AND_BACK.ordinal.toUInt())

    actual fun setStencilReferenceValue(value: Int, face: StencilFace) =
        FilaMaterialInstance_setStencilReferenceValue(nativeHandle, value.toUInt(), face.ordinal.toUInt())

    actual fun setStencilReferenceValue(value: Int) =
        FilaMaterialInstance_setStencilReferenceValue(nativeHandle, value.toUInt(), StencilFace.FRONT_AND_BACK.ordinal.toUInt())

    actual fun setStencilReadMask(readMask: Int, face: StencilFace) =
        FilaMaterialInstance_setStencilReadMask(nativeHandle, readMask.toUInt(), face.ordinal.toUInt())

    actual fun setStencilReadMask(readMask: Int) =
        FilaMaterialInstance_setStencilReadMask(nativeHandle, readMask.toUInt(), StencilFace.FRONT_AND_BACK.ordinal.toUInt())

    actual fun setStencilWriteMask(writeMask: Int, face: StencilFace) =
        FilaMaterialInstance_setStencilWriteMask(nativeHandle, writeMask.toUInt(), face.ordinal.toUInt())

    actual fun setStencilWriteMask(writeMask: Int) =
        FilaMaterialInstance_setStencilWriteMask(nativeHandle, writeMask.toUInt(), StencilFace.FRONT_AND_BACK.ordinal.toUInt())

    actual val nativeObject: Long
        get() = nativeHandle?.rawValue?.toLong() ?: 0L

    actual internal fun invalidate() {
        nativeHandle = null
    }
}
