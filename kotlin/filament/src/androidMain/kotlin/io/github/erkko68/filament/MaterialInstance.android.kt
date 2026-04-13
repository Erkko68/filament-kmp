package io.github.erkko68.filament

import com.google.android.filament.MaterialInstance as AndroidMaterialInstance

actual class MaterialInstance internal constructor(
    private var mMaterial: Material?, 
    internal val nativeMaterialInstance: AndroidMaterialInstance
) {
    internal constructor(nativeMaterialInstance: AndroidMaterialInstance) : this(null, nativeMaterialInstance)
    actual enum class BooleanElement { BOOL, BOOL2, BOOL3, BOOL4 }
    actual enum class IntElement { INT, INT2, INT3, INT4 }
    actual enum class FloatElement { FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, MAT4 }
    
    actual companion object {
        actual fun duplicate(other: MaterialInstance, name: String?): MaterialInstance {
            return MaterialInstance(other.getMaterial(), AndroidMaterialInstance.duplicate(other.nativeMaterialInstance, name))
        }
    }
    
    actual enum class StencilOperation { KEEP, ZERO, REPLACE, INCR_CLAMP, INCR_WRAP, DECR_CLAMP, DECR_WRAP, INVERT }
    actual enum class StencilFace { FRONT, BACK, FRONT_AND_BACK }

    actual fun getMaterial(): Material {
        if (mMaterial == null) {
            mMaterial = Material(nativeMaterialInstance.material)
        }
        return mMaterial!!
    }
    actual fun getName(): String = nativeMaterialInstance.name

    actual fun setParameter(name: String, x: Boolean) { nativeMaterialInstance.setParameter(name, x) }
    actual fun setParameter(name: String, x: Float) { nativeMaterialInstance.setParameter(name, x) }
    actual fun setParameter(name: String, x: Int) { nativeMaterialInstance.setParameter(name, x) }
    actual fun setParameter(name: String, x: Boolean, y: Boolean) { nativeMaterialInstance.setParameter(name, x, y) }
    actual fun setParameter(name: String, x: Float, y: Float) { nativeMaterialInstance.setParameter(name, x, y) }
    actual fun setParameter(name: String, x: Int, y: Int) { nativeMaterialInstance.setParameter(name, x, y) }
    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean) { nativeMaterialInstance.setParameter(name, x, y, z) }
    actual fun setParameter(name: String, x: Float, y: Float, z: Float) { nativeMaterialInstance.setParameter(name, x, y, z) }
    actual fun setParameter(name: String, x: Int, y: Int, z: Int) { nativeMaterialInstance.setParameter(name, x, y, z) }
    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean, w: Boolean) { nativeMaterialInstance.setParameter(name, x, y, z, w) }
    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) { nativeMaterialInstance.setParameter(name, x, y, z, w) }
    actual fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) { nativeMaterialInstance.setParameter(name, x, y, z, w) }
    
    actual fun setParameter(name: String, texture: Texture, sampler: TextureSampler) {
        nativeMaterialInstance.setParameter(name, texture.nativeTexture, sampler.nativeSampler)
    }
 
    actual fun setParameter(name: String, type: Colors.RgbType, r: Float, g: Float, b: Float) {
        val linear = Colors.toLinear(type, r, g, b)
        nativeMaterialInstance.setParameter(name, com.google.android.filament.Colors.RgbType.values()[type.ordinal], linear[0], linear[1], linear[2])
    }
 
    actual fun setParameter(name: String, type: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float) {
        val linear = Colors.toLinear(type, r, g, b, a)
        nativeMaterialInstance.setParameter(name, com.google.android.filament.Colors.RgbaType.values()[type.ordinal], linear[0], linear[1], linear[2], linear[3])
    }
    
    actual fun setParameter(name: String, type: BooleanElement, v: BooleanArray, offset: Int, count: Int) {
        nativeMaterialInstance.setParameter(name, AndroidMaterialInstance.BooleanElement.values()[type.ordinal], v, offset, count)
    }
    actual fun setParameter(name: String, type: IntElement, v: IntArray, offset: Int, count: Int) {
        nativeMaterialInstance.setParameter(name, AndroidMaterialInstance.IntElement.values()[type.ordinal], v, offset, count)
    }
    actual fun setParameter(name: String, type: FloatElement, v: FloatArray, offset: Int, count: Int) {
        nativeMaterialInstance.setParameter(name, AndroidMaterialInstance.FloatElement.values()[type.ordinal], v, offset, count)
    }

    actual fun setScissor(left: Int, bottom: Int, width: Int, height: Int) { nativeMaterialInstance.setScissor(left, bottom, width, height) }
    actual fun unsetScissor() { nativeMaterialInstance.unsetScissor() }
    
    actual fun setPolygonOffset(scale: Float, constant: Float) { nativeMaterialInstance.setPolygonOffset(scale, constant) }
    actual fun setMaskThreshold(threshold: Float) { nativeMaterialInstance.setMaskThreshold(threshold) }
    actual fun getMaskThreshold(): Float = nativeMaterialInstance.maskThreshold
    actual fun setSpecularAntiAliasingVariance(variance: Float) { nativeMaterialInstance.setSpecularAntiAliasingVariance(variance) }
    actual fun getSpecularAntiAliasingVariance(): Float = nativeMaterialInstance.specularAntiAliasingVariance
    actual fun setSpecularAntiAliasingThreshold(threshold: Float) { nativeMaterialInstance.setSpecularAntiAliasingThreshold(threshold) }
    actual fun getSpecularAntiAliasingThreshold(): Float = nativeMaterialInstance.specularAntiAliasingThreshold
    actual fun setDoubleSided(doubleSided: Boolean) { nativeMaterialInstance.setDoubleSided(doubleSided) }
    actual fun isDoubleSided(): Boolean = nativeMaterialInstance.isDoubleSided
    
    actual fun setTransparencyMode(mode: Material.TransparencyMode) {
        nativeMaterialInstance.setTransparencyMode(com.google.android.filament.Material.TransparencyMode.values()[mode.ordinal])
    }
    actual fun getTransparencyMode(): Material.TransparencyMode = Material.TransparencyMode.values()[nativeMaterialInstance.transparencyMode.ordinal]
    
    actual fun setCullingMode(mode: Material.CullingMode) {
        nativeMaterialInstance.setCullingMode(com.google.android.filament.Material.CullingMode.values()[mode.ordinal])
    }
    actual fun setCullingMode(colorPassCullingMode: Material.CullingMode, shadowPassCullingMode: Material.CullingMode) {
        nativeMaterialInstance.setCullingMode(
            com.google.android.filament.Material.CullingMode.values()[colorPassCullingMode.ordinal],
            com.google.android.filament.Material.CullingMode.values()[shadowPassCullingMode.ordinal]
        )
    }
    actual fun getCullingMode(): Material.CullingMode = Material.CullingMode.values()[nativeMaterialInstance.cullingMode.ordinal]
    actual fun getShadowCullingMode(): Material.CullingMode = Material.CullingMode.values()[nativeMaterialInstance.shadowCullingMode.ordinal]
    
    actual fun setColorWrite(enable: Boolean) { nativeMaterialInstance.setColorWrite(enable) }
    actual fun isColorWriteEnabled(): Boolean = nativeMaterialInstance.isColorWriteEnabled
    actual fun setDepthWrite(enable: Boolean) { nativeMaterialInstance.setDepthWrite(enable) }
    actual fun isDepthWriteEnabled(): Boolean = nativeMaterialInstance.isDepthWriteEnabled
    actual fun setStencilWrite(enable: Boolean) { nativeMaterialInstance.setStencilWrite(enable) }
    actual fun isStencilWriteEnabled(): Boolean = nativeMaterialInstance.isStencilWriteEnabled
    
    actual fun setDepthCulling(enable: Boolean) { nativeMaterialInstance.setDepthCulling(enable) }
    actual fun setDepthFunc(func: TextureSampler.CompareFunction) {
        nativeMaterialInstance.setDepthFunc(com.google.android.filament.TextureSampler.CompareFunction.values()[func.ordinal])
    }
    actual fun isDepthCullingEnabled(): Boolean = nativeMaterialInstance.isDepthCullingEnabled
    actual fun getDepthFunc(): TextureSampler.CompareFunction = TextureSampler.CompareFunction.values()[nativeMaterialInstance.depthFunc.ordinal]
    
    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction, face: StencilFace) {
        nativeMaterialInstance.setStencilCompareFunction(
            com.google.android.filament.TextureSampler.CompareFunction.values()[func.ordinal],
            com.google.android.filament.MaterialInstance.StencilFace.values()[face.ordinal]
        )
    }
    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction) {
        nativeMaterialInstance.setStencilCompareFunction(com.google.android.filament.TextureSampler.CompareFunction.values()[func.ordinal])
    }
    actual fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace) {
        nativeMaterialInstance.setStencilOpStencilFail(
            com.google.android.filament.MaterialInstance.StencilOperation.values()[op.ordinal],
            com.google.android.filament.MaterialInstance.StencilFace.values()[face.ordinal]
        )
    }
    actual fun setStencilOpStencilFail(op: StencilOperation) {
        nativeMaterialInstance.setStencilOpStencilFail(com.google.android.filament.MaterialInstance.StencilOperation.values()[op.ordinal])
    }
    actual fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace) {
        nativeMaterialInstance.setStencilOpDepthFail(
            com.google.android.filament.MaterialInstance.StencilOperation.values()[op.ordinal],
            com.google.android.filament.MaterialInstance.StencilFace.values()[face.ordinal]
        )
    }
    actual fun setStencilOpDepthFail(op: StencilOperation) {
        nativeMaterialInstance.setStencilOpDepthFail(com.google.android.filament.MaterialInstance.StencilOperation.values()[op.ordinal])
    }
    actual fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace) {
        nativeMaterialInstance.setStencilOpDepthStencilPass(
            com.google.android.filament.MaterialInstance.StencilOperation.values()[op.ordinal],
            com.google.android.filament.MaterialInstance.StencilFace.values()[face.ordinal]
        )
    }
    actual fun setStencilOpDepthStencilPass(op: StencilOperation) {
        nativeMaterialInstance.setStencilOpDepthStencilPass(com.google.android.filament.MaterialInstance.StencilOperation.values()[op.ordinal])
    }

    actual fun setStencilReferenceValue(value: Int, face: StencilFace) {
        nativeMaterialInstance.setStencilReferenceValue(value, com.google.android.filament.MaterialInstance.StencilFace.values()[face.ordinal])
    }
    actual fun setStencilReferenceValue(value: Int) {
        nativeMaterialInstance.setStencilReferenceValue(value)
    }
    actual fun setStencilReadMask(readMask: Int, face: StencilFace) {
        nativeMaterialInstance.setStencilReadMask(readMask, com.google.android.filament.MaterialInstance.StencilFace.values()[face.ordinal])
    }
    actual fun setStencilReadMask(readMask: Int) {
        nativeMaterialInstance.setStencilReadMask(readMask)
    }
    actual fun setStencilWriteMask(writeMask: Int, face: StencilFace) {
        nativeMaterialInstance.setStencilWriteMask(writeMask, com.google.android.filament.MaterialInstance.StencilFace.values()[face.ordinal])
    }
    actual fun setStencilWriteMask(writeMask: Int) {
        nativeMaterialInstance.setStencilWriteMask(writeMask)
    }
}
