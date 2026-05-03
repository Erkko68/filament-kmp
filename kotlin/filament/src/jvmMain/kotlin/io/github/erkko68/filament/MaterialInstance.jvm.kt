package io.github.erkko68.filament

import io.github.erkko68.filament.jni.MaterialInstance as JniMaterialInstance

actual class MaterialInstance(val nativeMaterialInstance: JniMaterialInstance) {
    actual enum class BooleanElement { 
        BOOL, BOOL2, BOOL3, BOOL4;
        internal fun toJni() = JniMaterialInstance.BooleanElement.values()[ordinal]
    }
    actual enum class IntElement { 
        INT, INT2, INT3, INT4;
        internal fun toJni() = JniMaterialInstance.IntElement.values()[ordinal]
    }
    actual enum class FloatElement { 
        FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, MAT4;
        internal fun toJni() = JniMaterialInstance.FloatElement.values()[ordinal]
    }
    
    actual enum class StencilOperation { 
        KEEP, ZERO, REPLACE, INCR_CLAMP, INCR_WRAP, DECR_CLAMP, DECR_WRAP, INVERT;
        internal fun toJni() = JniMaterialInstance.StencilOperation.values()[ordinal]
    }
    actual enum class StencilFace { 
        FRONT, BACK, FRONT_AND_BACK;
        internal fun toJni() = JniMaterialInstance.StencilFace.values()[ordinal]
    }

    actual companion object {
        actual fun duplicate(other: MaterialInstance, name: String?): MaterialInstance =
            MaterialInstance(JniMaterialInstance.duplicate(other.nativeMaterialInstance, name))
    }

    actual val material: Material get() = Material(nativeMaterialInstance.material)
    actual val name: String get() = nativeMaterialInstance.name ?: ""

    actual fun setParameter(name: String, x: Boolean) = nativeMaterialInstance.setParameter(name, x)
    actual fun setParameter(name: String, x: Float) = nativeMaterialInstance.setParameter(name, x)
    actual fun setParameter(name: String, x: Int) = nativeMaterialInstance.setParameter(name, x)
    actual fun setParameter(name: String, x: Boolean, y: Boolean) = nativeMaterialInstance.setParameter(name, x, y)
    actual fun setParameter(name: String, x: Float, y: Float) = nativeMaterialInstance.setParameter(name, x, y)
    actual fun setParameter(name: String, x: Int, y: Int) = nativeMaterialInstance.setParameter(name, x, y)
    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean) = nativeMaterialInstance.setParameter(name, x, y, z)
    actual fun setParameter(name: String, x: Float, y: Float, z: Float) = nativeMaterialInstance.setParameter(name, x, y, z)
    actual fun setParameter(name: String, x: Int, y: Int, z: Int) = nativeMaterialInstance.setParameter(name, x, y, z)
    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean, w: Boolean) = nativeMaterialInstance.setParameter(name, x, y, z, w)
    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) = nativeMaterialInstance.setParameter(name, x, y, z, w)
    actual fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) = nativeMaterialInstance.setParameter(name, x, y, z, w)
    
    actual fun setParameter(name: String, texture: Texture, sampler: TextureSampler) = 
        nativeMaterialInstance.setParameter(name, texture.nativeTexture, io.github.erkko68.filament.jni.TextureSampler(sampler.nativeSampler))
    
    actual fun setParameter(name: String, type: BooleanElement, v: BooleanArray, offset: Int, count: Int) =
        nativeMaterialInstance.setParameter(name, type.toJni(), v, offset, count)
    actual fun setParameter(name: String, type: IntElement, v: IntArray, offset: Int, count: Int) =
        nativeMaterialInstance.setParameter(name, type.toJni(), v, offset, count)
    actual fun setParameter(name: String, type: FloatElement, v: FloatArray, offset: Int, count: Int) =
        nativeMaterialInstance.setParameter(name, type.toJni(), v, offset, count)
    
    actual fun setParameter(name: String, type: Colors.RgbType, r: Float, g: Float, b: Float) = 
        nativeMaterialInstance.setParameter(name, type.toJni(), r, g, b)
    actual fun setParameter(name: String, type: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float) = 
        nativeMaterialInstance.setParameter(name, type.toJni(), r, g, b, a)

    actual fun setScissor(left: Int, bottom: Int, width: Int, height: Int) = nativeMaterialInstance.setScissor(left, bottom, width, height)
    actual fun unsetScissor() = nativeMaterialInstance.unsetScissor()
    
    actual fun setPolygonOffset(scale: Float, constant: Float) = nativeMaterialInstance.setPolygonOffset(scale, constant)
    actual var maskThreshold: Float
        get() = nativeMaterialInstance.maskThreshold
        set(value) { nativeMaterialInstance.setMaskThreshold(value) }
    actual var specularAntiAliasingVariance: Float
        get() = nativeMaterialInstance.specularAntiAliasingVariance
        set(value) { nativeMaterialInstance.setSpecularAntiAliasingVariance(value) }
    actual var specularAntiAliasingThreshold: Float
        get() = nativeMaterialInstance.specularAntiAliasingThreshold
        set(value) { nativeMaterialInstance.setSpecularAntiAliasingThreshold(value) }
    actual var isDoubleSided: Boolean
        get() = nativeMaterialInstance.isDoubleSided
        set(value) { nativeMaterialInstance.setDoubleSided(value) }
    
    actual var transparencyMode: Material.TransparencyMode
        get() = Material.TransparencyMode.values()[nativeMaterialInstance.transparencyMode.ordinal]
        set(value) { nativeMaterialInstance.setTransparencyMode(io.github.erkko68.filament.jni.Material.TransparencyMode.values()[value.ordinal]) }
    
    actual var cullingMode: Material.CullingMode
        get() = Material.CullingMode.values()[nativeMaterialInstance.cullingMode.ordinal]
        set(value) { nativeMaterialInstance.setCullingMode(io.github.erkko68.filament.jni.Material.CullingMode.values()[value.ordinal]) }

    actual fun setCullingMode(colorPassCullingMode: Material.CullingMode, shadowPassCullingMode: Material.CullingMode) =
        nativeMaterialInstance.setCullingMode(io.github.erkko68.filament.jni.Material.CullingMode.values()[colorPassCullingMode.ordinal], io.github.erkko68.filament.jni.Material.CullingMode.values()[shadowPassCullingMode.ordinal])

    actual val shadowCullingMode: Material.CullingMode get() = Material.CullingMode.values()[nativeMaterialInstance.shadowCullingMode.ordinal]
    
    actual var isColorWriteEnabled: Boolean
        get() = nativeMaterialInstance.isColorWriteEnabled
        set(value) { nativeMaterialInstance.setColorWrite(value) }
    actual var isDepthWriteEnabled: Boolean
        get() = nativeMaterialInstance.isDepthWriteEnabled
        set(value) { nativeMaterialInstance.setDepthWrite(value) }
    actual var isStencilWriteEnabled: Boolean
        get() = nativeMaterialInstance.isStencilWriteEnabled
        set(value) { nativeMaterialInstance.setStencilWrite(value) }
    
    actual var isDepthCullingEnabled: Boolean
        get() = nativeMaterialInstance.isDepthCullingEnabled
        set(value) { nativeMaterialInstance.setDepthCulling(value) }

    actual var depthFunc: TextureSampler.CompareFunction
        get() = TextureSampler.CompareFunction.values()[nativeMaterialInstance.depthFunc.ordinal]
        set(value) { nativeMaterialInstance.setDepthFunc(io.github.erkko68.filament.jni.TextureSampler.CompareFunction.values()[value.ordinal]) }
    
    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction, face: StencilFace) = nativeMaterialInstance.setStencilCompareFunction(io.github.erkko68.filament.jni.TextureSampler.CompareFunction.values()[func.ordinal], face.toJni())
    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction) = nativeMaterialInstance.setStencilCompareFunction(io.github.erkko68.filament.jni.TextureSampler.CompareFunction.values()[func.ordinal])
    actual fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace) = nativeMaterialInstance.setStencilOpStencilFail(op.toJni(), face.toJni())
    actual fun setStencilOpStencilFail(op: StencilOperation) = nativeMaterialInstance.setStencilOpStencilFail(op.toJni())
    actual fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace) = nativeMaterialInstance.setStencilOpDepthFail(op.toJni(), face.toJni())
    actual fun setStencilOpDepthFail(op: StencilOperation) = nativeMaterialInstance.setStencilOpDepthFail(op.toJni())
    actual fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace) = nativeMaterialInstance.setStencilOpDepthStencilPass(op.toJni(), face.toJni())
    actual fun setStencilOpDepthStencilPass(op: StencilOperation) = nativeMaterialInstance.setStencilOpDepthStencilPass(op.toJni())
    
    actual fun setStencilReferenceValue(value: Int, face: StencilFace) = nativeMaterialInstance.setStencilReferenceValue(value, face.toJni())
    actual fun setStencilReferenceValue(value: Int) = nativeMaterialInstance.setStencilReferenceValue(value)
    actual fun setStencilReadMask(readMask: Int, face: StencilFace) = nativeMaterialInstance.setStencilReadMask(readMask, face.toJni())
    actual fun setStencilReadMask(readMask: Int) = nativeMaterialInstance.setStencilReadMask(readMask)
    actual fun setStencilWriteMask(writeMask: Int, face: StencilFace) = nativeMaterialInstance.setStencilWriteMask(writeMask, face.toJni())
    actual fun setStencilWriteMask(writeMask: Int) = nativeMaterialInstance.setStencilWriteMask(writeMask)
}
