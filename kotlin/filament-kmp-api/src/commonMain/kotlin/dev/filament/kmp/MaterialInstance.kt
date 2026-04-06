package dev.filament.kmp

expect class MaterialInstance {
    fun getName(): String
    fun getMaterial(): Material

    fun setParameter(name: String, x: Float)
    fun setParameter(name: String, x: Float, y: Float)
    fun setParameter(name: String, x: Float, y: Float, z: Float)
    fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float)

    fun setParameter(name: String, x: Int)
    fun setParameter(name: String, x: Int, y: Int)
    fun setParameter(name: String, x: Int, y: Int, z: Int)
    fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int)

    fun setParameter(name: String, x: Boolean)
    fun setParameter(name: String, x: Boolean, y: Boolean)
    fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean)
    fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean, w: Boolean)

    fun setParameter(name: String, texture: Texture, sampler: TextureSampler)

    fun setParameter(name: String, colorType: Colors.RgbType, r: Float, g: Float, b: Float)
    fun setParameter(name: String, colorType: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float)

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
    fun getCullingMode(): Material.CullingMode

    fun setColorWrite(enable: Boolean)
    fun isColorWriteEnabled(): Boolean

    fun setDepthWrite(enable: Boolean)
    fun isDepthWriteEnabled(): Boolean

    fun setStencilWrite(enable: Boolean)
    fun isStencilWriteEnabled(): Boolean

    fun setDepthCulling(enable: Boolean)
    fun isDepthCullingEnabled(): Boolean

    fun setDepthFunc(func: TextureSampler.CompareFunction)
    fun getDepthFunc(): TextureSampler.CompareFunction
}
