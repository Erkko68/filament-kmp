package dev.filament.kmp

import com.google.android.filament.MaterialInstance as AndroidMaterialInstance

actual class MaterialInstance internal constructor(val nativeMaterialInstance: AndroidMaterialInstance) {
    actual fun getName(): String = nativeMaterialInstance.name
    actual fun getMaterial(): Material = Material(nativeMaterialInstance.material)

    actual fun setParameter(name: String, x: Float) { nativeMaterialInstance.setParameter(name, x) }
    actual fun setParameter(name: String, x: Float, y: Float) { nativeMaterialInstance.setParameter(name, x, y) }
    actual fun setParameter(name: String, x: Float, y: Float, z: Float) { nativeMaterialInstance.setParameter(name, x, y, z) }
    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) { nativeMaterialInstance.setParameter(name, x, y, z, w) }

    actual fun setParameter(name: String, x: Int) { nativeMaterialInstance.setParameter(name, x) }
    actual fun setParameter(name: String, x: Int, y: Int) { nativeMaterialInstance.setParameter(name, x, y) }
    actual fun setParameter(name: String, x: Int, y: Int, z: Int) { nativeMaterialInstance.setParameter(name, x, y, z) }
    actual fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) { nativeMaterialInstance.setParameter(name, x, y, z, w) }

    actual fun setParameter(name: String, x: Boolean) { nativeMaterialInstance.setParameter(name, x) }
    actual fun setParameter(name: String, x: Boolean, y: Boolean) { nativeMaterialInstance.setParameter(name, x, y) }
    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean) { nativeMaterialInstance.setParameter(name, x, y, z) }
    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean, w: Boolean) { nativeMaterialInstance.setParameter(name, x, y, z, w) }

    actual fun setParameter(name: String, texture: Texture, sampler: TextureSampler) {
        nativeMaterialInstance.setParameter(name, texture.nativeTexture, sampler.mSampler)
    }

    actual fun setParameter(name: String, colorType: Colors.RgbType, r: Float, g: Float, b: Float) {
        val linear = Colors.toLinear(colorType, r, g, b)
        nativeMaterialInstance.setParameter(name, linear[0], linear[1], linear[2])
    }

    actual fun setParameter(name: String, colorType: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float) {
        val linear = Colors.toLinear(colorType, r, g, b, a)
        nativeMaterialInstance.setParameter(name, linear[0], linear[1], linear[2], linear[3])
    }

    actual fun setScissor(left: Int, bottom: Int, width: Int, height: Int) {
        nativeMaterialInstance.setScissor(left, bottom, width, height)
    }
    actual fun unsetScissor() { nativeMaterialInstance.unsetScissor() }

    actual fun setPolygonOffset(scale: Float, constant: Float) {
        nativeMaterialInstance.setPolygonOffset(scale, constant)
    }

    actual fun setMaskThreshold(threshold: Float) { nativeMaterialInstance.maskThreshold = threshold }
    actual fun getMaskThreshold(): Float = nativeMaterialInstance.maskThreshold

    actual fun setSpecularAntiAliasingVariance(variance: Float) {
        nativeMaterialInstance.specularAntiAliasingVariance = variance
    }
    actual fun getSpecularAntiAliasingVariance(): Float = nativeMaterialInstance.specularAntiAliasingVariance

    actual fun setSpecularAntiAliasingThreshold(threshold: Float) {
        nativeMaterialInstance.specularAntiAliasingThreshold = threshold
    }
    actual fun getSpecularAntiAliasingThreshold(): Float = nativeMaterialInstance.specularAntiAliasingThreshold

    actual fun setDoubleSided(doubleSided: Boolean) { nativeMaterialInstance.setDoubleSided(doubleSided) }
    actual fun isDoubleSided(): Boolean = nativeMaterialInstance.isDoubleSided

    actual fun setTransparencyMode(mode: Material.TransparencyMode) {
        nativeMaterialInstance.transparencyMode = com.google.android.filament.Material.TransparencyMode.values()[mode.ordinal]
    }
    actual fun getTransparencyMode(): Material.TransparencyMode =
        Material.TransparencyMode.values()[nativeMaterialInstance.transparencyMode.ordinal]

    actual fun setCullingMode(mode: Material.CullingMode) {
        nativeMaterialInstance.cullingMode = com.google.android.filament.Material.CullingMode.values()[mode.ordinal]
    }
    actual fun getCullingMode(): Material.CullingMode =
        Material.CullingMode.values()[nativeMaterialInstance.cullingMode.ordinal]

    actual fun setColorWrite(enable: Boolean) { nativeMaterialInstance.setColorWrite(enable) }
    actual fun isColorWriteEnabled(): Boolean = nativeMaterialInstance.isColorWriteEnabled

    actual fun setDepthWrite(enable: Boolean) { nativeMaterialInstance.setDepthWrite(enable) }
    actual fun isDepthWriteEnabled(): Boolean = nativeMaterialInstance.isDepthWriteEnabled

    actual fun setStencilWrite(enable: Boolean) { nativeMaterialInstance.setStencilWrite(enable) }
    actual fun isStencilWriteEnabled(): Boolean = nativeMaterialInstance.isStencilWriteEnabled

    actual fun setDepthCulling(enable: Boolean) { nativeMaterialInstance.setDepthCulling(enable) }
    actual fun isDepthCullingEnabled(): Boolean = nativeMaterialInstance.isDepthCullingEnabled

    actual fun setDepthFunc(func: TextureSampler.CompareFunction) {
        nativeMaterialInstance.setDepthFunc(com.google.android.filament.TextureSampler.CompareFunction.values()[func.ordinal])
    }
    actual fun getDepthFunc(): TextureSampler.CompareFunction =
        TextureSampler.CompareFunction.values()[nativeMaterialInstance.depthFunc.ordinal]
}
