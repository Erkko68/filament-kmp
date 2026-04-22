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

    actual fun getMaterial(): Material = Material(nativeMaterialInstance.material)
    actual fun getName(): String = nativeMaterialInstance.name ?: ""

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
    actual fun setMaskThreshold(threshold: Float) = nativeMaterialInstance.setMaskThreshold(threshold)
    actual fun getMaskThreshold(): Float = nativeMaterialInstance.maskThreshold
    actual fun setSpecularAntiAliasingVariance(variance: Float) = nativeMaterialInstance.setSpecularAntiAliasingVariance(variance)
    actual fun getSpecularAntiAliasingVariance(): Float = nativeMaterialInstance.specularAntiAliasingVariance
    actual fun setSpecularAntiAliasingThreshold(threshold: Float) = nativeMaterialInstance.setSpecularAntiAliasingThreshold(threshold)
    actual fun getSpecularAntiAliasingThreshold(): Float = nativeMaterialInstance.specularAntiAliasingThreshold
    actual fun setDoubleSided(doubleSided: Boolean) = nativeMaterialInstance.setDoubleSided(doubleSided)
    actual fun isDoubleSided(): Boolean = nativeMaterialInstance.isDoubleSided
    
    actual fun setTransparencyMode(mode: Material.TransparencyMode) = nativeMaterialInstance.setTransparencyMode(io.github.erkko68.filament.jni.Material.TransparencyMode.values()[mode.ordinal])
    actual fun getTransparencyMode(): Material.TransparencyMode = Material.TransparencyMode.values()[nativeMaterialInstance.transparencyMode.ordinal]
    
    actual fun setCullingMode(mode: Material.CullingMode) = nativeMaterialInstance.setCullingMode(io.github.erkko68.filament.jni.Material.CullingMode.values()[mode.ordinal])
    actual fun setCullingMode(colorPassCullingMode: Material.CullingMode, shadowPassCullingMode: Material.CullingMode) =
        nativeMaterialInstance.setCullingMode(io.github.erkko68.filament.jni.Material.CullingMode.values()[colorPassCullingMode.ordinal], io.github.erkko68.filament.jni.Material.CullingMode.values()[shadowPassCullingMode.ordinal])
    actual fun getCullingMode(): Material.CullingMode = Material.CullingMode.values()[nativeMaterialInstance.cullingMode.ordinal]
    actual fun getShadowCullingMode(): Material.CullingMode = Material.CullingMode.values()[nativeMaterialInstance.shadowCullingMode.ordinal]
    
    actual fun setColorWrite(enable: Boolean) = nativeMaterialInstance.setColorWrite(enable)
    actual fun isColorWriteEnabled(): Boolean = nativeMaterialInstance.isColorWriteEnabled
    actual fun setDepthWrite(enable: Boolean) = nativeMaterialInstance.setDepthWrite(enable)
    actual fun isDepthWriteEnabled(): Boolean = nativeMaterialInstance.isDepthWriteEnabled
    actual fun setStencilWrite(enable: Boolean) = nativeMaterialInstance.setStencilWrite(enable)
    actual fun isStencilWriteEnabled(): Boolean = nativeMaterialInstance.isStencilWriteEnabled
    
    actual fun setDepthCulling(enable: Boolean) = nativeMaterialInstance.setDepthCulling(enable)
    actual fun setDepthFunc(func: TextureSampler.CompareFunction) = nativeMaterialInstance.setDepthFunc(io.github.erkko68.filament.jni.TextureSampler.CompareFunction.values()[func.ordinal])
    actual fun isDepthCullingEnabled(): Boolean = nativeMaterialInstance.isDepthCullingEnabled
    actual fun getDepthFunc(): TextureSampler.CompareFunction = TextureSampler.CompareFunction.values()[nativeMaterialInstance.depthFunc.ordinal]
    
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
