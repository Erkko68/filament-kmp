@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaMaterialInstance

actual class MaterialInstance internal constructor(
    internal val nativeHandle: CPointer<FilaMaterialInstance>?
) {
    actual enum class BooleanElement { BOOL, BOOL2, BOOL3, BOOL4 }
    actual enum class IntElement { INT, INT2, INT3, INT4 }
    actual enum class FloatElement { FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, MAT4 }
    
    actual enum class StencilOperation { KEEP, ZERO, REPLACE, INCR_CLAMP, INCR_WRAP, DECR_CLAMP, DECR_WRAP, INVERT }
    actual enum class StencilFace { FRONT, BACK, FRONT_AND_BACK }
 
    actual companion object {
        actual fun duplicate(other: MaterialInstance, name: String?): MaterialInstance {
            return MaterialInstance(FilaMaterialInstance_duplicate(other.nativeHandle, name))
        }
    }

    actual fun getMaterial(): Material = Material(FilaMaterialInstance_getMaterial(nativeHandle))
    actual fun getName(): String = FilaMaterialInstance_getName(nativeHandle)?.toKString() ?: ""

    actual fun setParameter(name: String, x: Boolean) { FilaMaterialInstance_setParameterBool(nativeHandle, name, x) }
    actual fun setParameter(name: String, x: Float) { FilaMaterialInstance_setParameterFloat(nativeHandle, name, x) }
    actual fun setParameter(name: String, x: Int) { FilaMaterialInstance_setParameterInt(nativeHandle, name, x) }
    actual fun setParameter(name: String, x: Boolean, y: Boolean) { FilaMaterialInstance_setParameterBool2(nativeHandle, name, x, y) }
    actual fun setParameter(name: String, x: Float, y: Float) { FilaMaterialInstance_setParameterFloat2(nativeHandle, name, x, y) }
    actual fun setParameter(name: String, x: Int, y: Int) { FilaMaterialInstance_setParameterInt2(nativeHandle, name, x, y) }
    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean) { FilaMaterialInstance_setParameterBool3(nativeHandle, name, x, y, z) }
    actual fun setParameter(name: String, x: Float, y: Float, z: Float) { FilaMaterialInstance_setParameterFloat3(nativeHandle, name, x, y, z) }
    actual fun setParameter(name: String, x: Int, y: Int, z: Int) { FilaMaterialInstance_setParameterInt3(nativeHandle, name, x, y, z) }
    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean, w: Boolean) { FilaMaterialInstance_setParameterBool4(nativeHandle, name, x, y, z, w) }
    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) { FilaMaterialInstance_setParameterFloat4(nativeHandle, name, x, y, z, w) }
    actual fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) { FilaMaterialInstance_setParameterInt4(nativeHandle, name, x, y, z, w) }
    
    actual fun setParameter(name: String, texture: Texture, sampler: TextureSampler) {
        FilaMaterialInstance_setParameterTexture(nativeHandle, name, texture.nativeHandle, sampler.nativeHandle)
    }
    
    actual fun setParameter(name: String, type: BooleanElement, v: BooleanArray, offset: Int, count: Int) = memScoped {
        val nativeArray = allocArray<BooleanVar>(count)
        for (i in 0 until count) {
            nativeArray[i].value = v[offset + i]
        }
        FilaMaterialInstance_setBooleanParameterArray(nativeHandle, name, (type.ordinal + 1).toUInt(), nativeArray, count.toULong())
    }
    actual fun setParameter(name: String, type: IntElement, v: IntArray, offset: Int, count: Int) {
        val pinned = v.pin()
        try {
            FilaMaterialInstance_setIntParameterArray(nativeHandle, name, (type.ordinal + 1).toUInt(), pinned.addressOf(offset), count.toULong())
        } finally {
            pinned.unpin()
        }
    }
    actual fun setParameter(name: String, type: FloatElement, v: FloatArray, offset: Int, count: Int) {
        val elementSize = when (type) {
            FloatElement.FLOAT -> 1
            FloatElement.FLOAT2 -> 2
            FloatElement.FLOAT3 -> 3
            FloatElement.FLOAT4 -> 4
            FloatElement.MAT3 -> 9
            FloatElement.MAT4 -> 16
        }
        val pinned = v.pin()
        try {
            FilaMaterialInstance_setFloatParameterArray(nativeHandle, name, elementSize.toUInt(), pinned.addressOf(offset), count.toULong())
        } finally {
            pinned.unpin()
        }
    }
    
    actual fun setParameter(name: String, type: Colors.RgbType, r: Float, g: Float, b: Float) {
        val linear = Colors.toLinear(type, r, g, b)
        FilaMaterialInstance_setParameterFloat3(nativeHandle, name, linear[0], linear[1], linear[2])
    }
    actual fun setParameter(name: String, type: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float) {
        val linear = Colors.toLinear(type, r, g, b, a)
        FilaMaterialInstance_setParameterFloat4(nativeHandle, name, linear[0], linear[1], linear[2], linear[3])
    }

    actual fun setScissor(left: Int, bottom: Int, width: Int, height: Int) {
        FilaMaterialInstance_setScissor(nativeHandle, left, bottom, width.toUInt(), height.toUInt())
    }
    actual fun unsetScissor() { FilaMaterialInstance_unsetScissor(nativeHandle) }
    
    actual fun setPolygonOffset(scale: Float, constant: Float) { FilaMaterialInstance_setPolygonOffset(nativeHandle, scale, constant) }
    actual fun setMaskThreshold(threshold: Float) { FilaMaterialInstance_setMaskThreshold(nativeHandle, threshold) }
    actual fun getMaskThreshold(): Float = FilaMaterialInstance_getMaskThreshold(nativeHandle)
    actual fun setSpecularAntiAliasingVariance(variance: Float) { FilaMaterialInstance_setSpecularAntiAliasingVariance(nativeHandle, variance) }
    actual fun getSpecularAntiAliasingVariance(): Float = FilaMaterialInstance_getSpecularAntiAliasingVariance(nativeHandle)
    actual fun setSpecularAntiAliasingThreshold(threshold: Float) { FilaMaterialInstance_setSpecularAntiAliasingThreshold(nativeHandle, threshold) }
    actual fun getSpecularAntiAliasingThreshold(): Float = FilaMaterialInstance_getSpecularAntiAliasingThreshold(nativeHandle)
    actual fun setDoubleSided(doubleSided: Boolean) { FilaMaterialInstance_setDoubleSided(nativeHandle, doubleSided) }
    actual fun isDoubleSided(): Boolean = FilaMaterialInstance_isDoubleSided(nativeHandle)
    
    actual fun setTransparencyMode(mode: Material.TransparencyMode) {
        FilaMaterialInstance_setTransparencyMode(nativeHandle, mode.ordinal.toUInt())
    }
    actual fun getTransparencyMode(): Material.TransparencyMode = Material.TransparencyMode.values()[FilaMaterialInstance_getTransparencyMode(nativeHandle).toInt()]
    
    actual fun setCullingMode(mode: Material.CullingMode) {
        FilaMaterialInstance_setCullingMode(nativeHandle, mode.ordinal.toUInt())
    }
    actual fun setCullingMode(colorPassCullingMode: Material.CullingMode, shadowPassCullingMode: Material.CullingMode) {
        FilaMaterialInstance_setCullingModeSeparate(nativeHandle, colorPassCullingMode.ordinal.toUInt(), shadowPassCullingMode.ordinal.toUInt())
    }
    actual fun getCullingMode(): Material.CullingMode = Material.CullingMode.values()[FilaMaterialInstance_getCullingMode(nativeHandle).toInt()]
    actual fun getShadowCullingMode(): Material.CullingMode = Material.CullingMode.values()[FilaMaterialInstance_getShadowCullingMode(nativeHandle).toInt()]
    
    actual fun setColorWrite(enable: Boolean) { FilaMaterialInstance_setColorWrite(nativeHandle, enable) }
    actual fun isColorWriteEnabled(): Boolean = FilaMaterialInstance_isColorWriteEnabled(nativeHandle)
    actual fun setDepthWrite(enable: Boolean) { FilaMaterialInstance_setDepthWrite(nativeHandle, enable) }
    actual fun isDepthWriteEnabled(): Boolean = FilaMaterialInstance_isDepthWriteEnabled(nativeHandle)
    actual fun setStencilWrite(enable: Boolean) { FilaMaterialInstance_setStencilWrite(nativeHandle, enable) }
    actual fun isStencilWriteEnabled(): Boolean = FilaMaterialInstance_isStencilWriteEnabled(nativeHandle)
    
    actual fun setDepthCulling(enable: Boolean) { FilaMaterialInstance_setDepthCulling(nativeHandle, enable) }
    actual fun setDepthFunc(func: TextureSampler.CompareFunction) {
        FilaMaterialInstance_setDepthFunc(nativeHandle, func.ordinal.toUInt())
    }
    actual fun isDepthCullingEnabled(): Boolean = FilaMaterialInstance_isDepthCullingEnabled(nativeHandle)
    actual fun getDepthFunc(): TextureSampler.CompareFunction = TextureSampler.CompareFunction.values()[FilaMaterialInstance_getDepthFunc(nativeHandle).toInt()]
    
    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction, face: StencilFace) {
        FilaMaterialInstance_setStencilCompareFunction(nativeHandle, func.ordinal.toUInt(), face.native)
    }
    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction) {
        FilaMaterialInstance_setStencilCompareFunction(nativeHandle, func.ordinal.toUInt(), StencilFace.FRONT_AND_BACK.native)
    }
    actual fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace) {
        FilaMaterialInstance_setStencilOpStencilFail(nativeHandle, op.ordinal.toUInt(), face.native)
    }
    actual fun setStencilOpStencilFail(op: StencilOperation) {
        FilaMaterialInstance_setStencilOpStencilFail(nativeHandle, op.ordinal.toUInt(), StencilFace.FRONT_AND_BACK.native)
    }
    actual fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace) {
        FilaMaterialInstance_setStencilOpDepthFail(nativeHandle, op.ordinal.toUInt(), face.native)
    }
    actual fun setStencilOpDepthFail(op: StencilOperation) {
        FilaMaterialInstance_setStencilOpDepthFail(nativeHandle, op.ordinal.toUInt(), StencilFace.FRONT_AND_BACK.native)
    }
    actual fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace) {
        FilaMaterialInstance_setStencilOpDepthStencilPass(nativeHandle, op.ordinal.toUInt(), face.native)
    }
    actual fun setStencilOpDepthStencilPass(op: StencilOperation) {
        FilaMaterialInstance_setStencilOpDepthStencilPass(nativeHandle, op.ordinal.toUInt(), StencilFace.FRONT_AND_BACK.native)
    }
    
    actual fun setStencilReferenceValue(value: Int, face: StencilFace) {
        FilaMaterialInstance_setStencilReferenceValue(nativeHandle, value.toUInt(), face.native)
    }
    actual fun setStencilReferenceValue(value: Int) {
        FilaMaterialInstance_setStencilReferenceValue(nativeHandle, value.toUInt(), StencilFace.FRONT_AND_BACK.native)
    }
    actual fun setStencilReadMask(readMask: Int, face: StencilFace) {
        FilaMaterialInstance_setStencilReadMask(nativeHandle, readMask.toUInt(), face.native)
    }
    actual fun setStencilReadMask(readMask: Int) {
        FilaMaterialInstance_setStencilReadMask(nativeHandle, readMask.toUInt(), StencilFace.FRONT_AND_BACK.native)
    }
    actual fun setStencilWriteMask(writeMask: Int, face: StencilFace) {
        FilaMaterialInstance_setStencilWriteMask(nativeHandle, writeMask.toUInt(), face.native)
    }
    actual fun setStencilWriteMask(writeMask: Int) {
        FilaMaterialInstance_setStencilWriteMask(nativeHandle, writeMask.toUInt(), StencilFace.FRONT_AND_BACK.native)
    }
}

private val MaterialInstance.StencilFace.native: UInt
    get() = when (this) {
        MaterialInstance.StencilFace.FRONT -> 1u
        MaterialInstance.StencilFace.BACK -> 2u
        MaterialInstance.StencilFace.FRONT_AND_BACK -> 3u
    }

private fun Any.usePinned(block: (Pinned<*>) -> Unit) {
    when (this) {
        is ByteArray -> this.pin().let { try { block(it) } finally { it.unpin() } }
        is FloatArray -> this.pin().let { try { block(it) } finally { it.unpin() } }
        is ShortArray -> this.pin().let { try { block(it) } finally { it.unpin() } }
        is IntArray -> this.pin().let { try { block(it) } finally { it.unpin() } }
        else -> throw IllegalArgumentException("Unsupported storage type for pinning")
    }
}
