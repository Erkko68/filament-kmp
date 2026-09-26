package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class MaterialInstance @InternalFilamentApi constructor(
    internal val nativeHandle: Int
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

    actual val material: Material get() = Material(FilaMaterialInstance_getMaterial(nativeHandle))
    actual val name: String get() = FilaMaterialInstance_getName(nativeHandle) ?: ""

    actual fun setParameter(name: String, x: Boolean) { FilaMaterialInstance_setParameterBool(nativeHandle, name, x) }
    actual fun setParameter(name: String, x: Float) { FilaMaterialInstance_setParameterFloat(nativeHandle, name, x) }
    actual fun setParameter(name: String, x: Int) { FilaMaterialInstance_setParameterInt(nativeHandle, name, x) }
    actual fun getConstantBoolean(name: String): Boolean = FilaMaterialInstance_getConstantBool(nativeHandle, name)
    actual fun getConstantFloat(name: String): Float = FilaMaterialInstance_getConstantFloat(nativeHandle, name)
    actual fun getConstantInt(name: String): Int = FilaMaterialInstance_getConstantInt(nativeHandle, name)
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
    
    actual fun setParameter(name: String, type: BooleanElement, v: BooleanArray, offset: Int, count: Int) = fila.heapScoped {
        val nativeArray = BoolArray(alloc((count) * 1))
        for (i in 0 until count) {
            nativeArray[i] = v[offset + i]
        }
        FilaMaterialInstance_setBooleanParameterArray(nativeHandle, name, (type.ordinal + 1), nativeArray.ptr, count)
    }
    actual fun setParameter(name: String, type: IntElement, v: IntArray, offset: Int, count: Int) {
        v.usePinned { pinned ->
            FilaMaterialInstance_setIntParameterArray(nativeHandle, name, (type.ordinal + 1), pinned + offset * 4, count)
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
        v.usePinned { pinned ->
            FilaMaterialInstance_setFloatParameterArray(nativeHandle, name, elementSize, pinned + offset * 4, count)
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
        FilaMaterialInstance_setScissor(nativeHandle, left, bottom, width, height)
    }
    actual fun unsetScissor() { FilaMaterialInstance_unsetScissor(nativeHandle) }
    
    actual fun setPolygonOffset(scale: Float, constant: Float) { FilaMaterialInstance_setPolygonOffset(nativeHandle, scale, constant) }
    actual var maskThreshold: Float
        get() = FilaMaterialInstance_getMaskThreshold(nativeHandle)
        set(value) { FilaMaterialInstance_setMaskThreshold(nativeHandle, value) }
    actual var specularAntiAliasingVariance: Float
        get() = FilaMaterialInstance_getSpecularAntiAliasingVariance(nativeHandle)
        set(value) { FilaMaterialInstance_setSpecularAntiAliasingVariance(nativeHandle, value) }
    actual var specularAntiAliasingThreshold: Float
        get() = FilaMaterialInstance_getSpecularAntiAliasingThreshold(nativeHandle)
        set(value) { FilaMaterialInstance_setSpecularAntiAliasingThreshold(nativeHandle, value) }
    actual var isDoubleSided: Boolean
        get() = FilaMaterialInstance_isDoubleSided(nativeHandle)
        set(value) { FilaMaterialInstance_setDoubleSided(nativeHandle, value) }
    
    actual var transparencyMode: Material.TransparencyMode
        get() = Material.TransparencyMode.entries[FilaMaterialInstance_getTransparencyMode(nativeHandle).toInt()]
        set(value) { FilaMaterialInstance_setTransparencyMode(nativeHandle, value.ordinal) }
    
    actual var cullingMode: Material.CullingMode
        get() = Material.CullingMode.entries[FilaMaterialInstance_getCullingMode(nativeHandle).toInt()]
        set(value) { FilaMaterialInstance_setCullingMode(nativeHandle, value.ordinal) }

    actual fun setCullingMode(colorPassCullingMode: Material.CullingMode, shadowPassCullingMode: Material.CullingMode) {
        FilaMaterialInstance_setCullingModeSeparate(nativeHandle, colorPassCullingMode.ordinal, shadowPassCullingMode.ordinal)
    }

    actual val shadowCullingMode: Material.CullingMode get() = Material.CullingMode.entries[FilaMaterialInstance_getShadowCullingMode(nativeHandle).toInt()]
    
    actual var isColorWriteEnabled: Boolean
        get() = FilaMaterialInstance_isColorWriteEnabled(nativeHandle)
        set(value) { FilaMaterialInstance_setColorWrite(nativeHandle, value) }
    actual var isDepthWriteEnabled: Boolean
        get() = FilaMaterialInstance_isDepthWriteEnabled(nativeHandle)
        set(value) { FilaMaterialInstance_setDepthWrite(nativeHandle, value) }
    actual var isStencilWriteEnabled: Boolean
        get() = FilaMaterialInstance_isStencilWriteEnabled(nativeHandle)
        set(value) { FilaMaterialInstance_setStencilWrite(nativeHandle, value) }
    
    actual var isDepthCullingEnabled: Boolean
        get() = FilaMaterialInstance_isDepthCullingEnabled(nativeHandle)
        set(value) { FilaMaterialInstance_setDepthCulling(nativeHandle, value) }

    actual var depthFunc: TextureSampler.CompareFunction
        get() = TextureSampler.CompareFunction.entries[FilaMaterialInstance_getDepthFunc(nativeHandle).toInt()]
        set(value) { FilaMaterialInstance_setDepthFunc(nativeHandle, value.ordinal) }
    
    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction, face: StencilFace) {
        FilaMaterialInstance_setStencilCompareFunction(nativeHandle, func.ordinal, face.native)
    }
    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction) {
        FilaMaterialInstance_setStencilCompareFunction(nativeHandle, func.ordinal, StencilFace.FRONT_AND_BACK.native)
    }
    actual fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace) {
        FilaMaterialInstance_setStencilOpStencilFail(nativeHandle, op.ordinal, face.native)
    }
    actual fun setStencilOpStencilFail(op: StencilOperation) {
        FilaMaterialInstance_setStencilOpStencilFail(nativeHandle, op.ordinal, StencilFace.FRONT_AND_BACK.native)
    }
    actual fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace) {
        FilaMaterialInstance_setStencilOpDepthFail(nativeHandle, op.ordinal, face.native)
    }
    actual fun setStencilOpDepthFail(op: StencilOperation) {
        FilaMaterialInstance_setStencilOpDepthFail(nativeHandle, op.ordinal, StencilFace.FRONT_AND_BACK.native)
    }
    actual fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace) {
        FilaMaterialInstance_setStencilOpDepthStencilPass(nativeHandle, op.ordinal, face.native)
    }
    actual fun setStencilOpDepthStencilPass(op: StencilOperation) {
        FilaMaterialInstance_setStencilOpDepthStencilPass(nativeHandle, op.ordinal, StencilFace.FRONT_AND_BACK.native)
    }
    
    actual fun setStencilReferenceValue(value: Int, face: StencilFace) {
        FilaMaterialInstance_setStencilReferenceValue(nativeHandle, value, face.native)
    }
    actual fun setStencilReferenceValue(value: Int) {
        FilaMaterialInstance_setStencilReferenceValue(nativeHandle, value, StencilFace.FRONT_AND_BACK.native)
    }
    actual fun setStencilReadMask(readMask: Int, face: StencilFace) {
        FilaMaterialInstance_setStencilReadMask(nativeHandle, readMask, face.native)
    }
    actual fun setStencilReadMask(readMask: Int) {
        FilaMaterialInstance_setStencilReadMask(nativeHandle, readMask, StencilFace.FRONT_AND_BACK.native)
    }
    actual fun setStencilWriteMask(writeMask: Int, face: StencilFace) {
        FilaMaterialInstance_setStencilWriteMask(nativeHandle, writeMask, face.native)
    }
    actual fun setStencilWriteMask(writeMask: Int) {
        FilaMaterialInstance_setStencilWriteMask(nativeHandle, writeMask, StencilFace.FRONT_AND_BACK.native)
    }
}

private val MaterialInstance.StencilFace.native: Int
    get() = when (this) {
        MaterialInstance.StencilFace.FRONT -> 1
        MaterialInstance.StencilFace.BACK -> 2
        MaterialInstance.StencilFace.FRONT_AND_BACK -> 3
    }
