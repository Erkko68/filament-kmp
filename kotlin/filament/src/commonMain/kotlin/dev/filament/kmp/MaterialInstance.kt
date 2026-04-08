package dev.filament.kmp

/**
 * A Filament MaterialInstance is used to set the parameters of a [Material].
 */
expect class MaterialInstance {
    enum class BooleanElement { BOOL, BOOL2, BOOL3, BOOL4 }
    enum class IntElement { INT, INT2, INT3, INT4 }
    enum class FloatElement { FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, MAT4 }
    
    enum class StencilOperation { KEEP, ZERO, REPLACE, INCR_CLAMP, INCR_WRAP, DECR_CLAMP, DECR_WRAP, INVERT }
    enum class StencilFace { FRONT, BACK, FRONT_AND_BACK }
 
    companion object {
        fun duplicate(other: MaterialInstance, name: String? = null): MaterialInstance
    }

    fun getMaterial(): Material
    fun getName(): String

    fun setParameter(name: String, x: Boolean)
    fun setParameter(name: String, x: Float)
    fun setParameter(name: String, x: Int)
    fun setParameter(name: String, x: Boolean, y: Boolean)
    fun setParameter(name: String, x: Float, y: Float)
    fun setParameter(name: String, x: Int, y: Int)
    fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean)
    fun setParameter(name: String, x: Float, y: Float, z: Float)
    fun setParameter(name: String, x: Int, y: Int, z: Int)
    fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean, w: Boolean)
    fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float)
    fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int)
    fun setParameter(name: String, texture: Texture, sampler: TextureSampler)
    
    // Arrays
    fun setParameter(name: String, type: BooleanElement, v: BooleanArray, offset: Int, count: Int)
    fun setParameter(name: String, type: IntElement, v: IntArray, offset: Int, count: Int)
    fun setParameter(name: String, type: FloatElement, v: FloatArray, offset: Int, count: Int)
    
    // Colors
    fun setParameter(name: String, type: Colors.RgbType, r: Float, g: Float, b: Float)
    fun setParameter(name: String, type: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float)

    fun setScissor(left: Int, bottom: Int, width: Int, height: Int)
    fun unsetScissor()
    
    fun setPolygonOffset(scale: Float, constant: Float)
    fun setMaskThreshold(threshold: Float)
    fun getMaskThreshold(): Float
    fun setSpecularAntiAliasingVariance(variance: Float)
    fun getSpecularAntiAliasingVariance(): Float
    fun setSpecularAntiAliasingThreshold(threshold: Float)
    fun getSpecularAntiAliasingThreshold(): Float
    fun setDoubleSided(doubleSided: Boolean)
    fun isDoubleSided(): Boolean
    
    fun setTransparencyMode(mode: Material.TransparencyMode)
    fun getTransparencyMode(): Material.TransparencyMode
    
    fun setCullingMode(mode: Material.CullingMode)
    fun setCullingMode(colorPassCullingMode: Material.CullingMode, shadowPassCullingMode: Material.CullingMode)
    fun getCullingMode(): Material.CullingMode
    fun getShadowCullingMode(): Material.CullingMode
    
    fun setColorWrite(enable: Boolean)
    fun isColorWriteEnabled(): Boolean
    fun setDepthWrite(enable: Boolean)
    fun isDepthWriteEnabled(): Boolean
    fun setStencilWrite(enable: Boolean)
    fun isStencilWriteEnabled(): Boolean
    
    fun setDepthCulling(enable: Boolean)
    fun setDepthFunc(func: TextureSampler.CompareFunction)
    fun isDepthCullingEnabled(): Boolean
    fun getDepthFunc(): TextureSampler.CompareFunction
    
    fun setStencilCompareFunction(func: TextureSampler.CompareFunction, face: StencilFace)
    fun setStencilCompareFunction(func: TextureSampler.CompareFunction)
    fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace)
    fun setStencilOpStencilFail(op: StencilOperation)
    fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace)
    fun setStencilOpDepthFail(op: StencilOperation)
    fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace)
    fun setStencilOpDepthStencilPass(op: StencilOperation)
    
    fun setStencilReferenceValue(value: Int, face: StencilFace)
    fun setStencilReferenceValue(value: Int)
    fun setStencilReadMask(readMask: Int, face: StencilFace)
    fun setStencilReadMask(readMask: Int)
    fun setStencilWriteMask(writeMask: Int, face: StencilFace)
    fun setStencilWriteMask(writeMask: Int)
}
