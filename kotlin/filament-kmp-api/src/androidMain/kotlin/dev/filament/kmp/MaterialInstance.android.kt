package dev.filament.kmp

import com.google.android.filament.Colors as AndroidColors
import com.google.android.filament.MaterialInstance as AndroidMaterialInstance
import com.google.android.filament.TextureSampler as AndroidTextureSampler

actual class MaterialInstance internal constructor(
    internal var androidMaterialInstance: AndroidMaterialInstance?,
) {
    private fun instance(): AndroidMaterialInstance =
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }

    actual enum class StencilOperation {
        KEEP,
        ZERO,
        REPLACE,
        INCR_CLAMP,
        INCR_WRAP,
        DECR_CLAMP,
        DECR_WRAP,
        INVERT,
    }

    actual enum class StencilFace {
        FRONT,
        BACK,
        FRONT_AND_BACK,
    }

    actual fun getMaterial(): Material {
        return Material(instance().material)
    }

    actual fun getName(): String {
        return instance().name
    }

    actual fun setParameter(name: String, x: Float) {
        instance().setParameter(name, x)
    }

    actual fun setParameter(name: String, x: Int) {
        instance().setParameter(name, x)
    }

    actual fun setParameter(name: String, x: Float, y: Float) {
        instance().setParameter(name, x, y)
    }

    actual fun setParameter(name: String, x: Int, y: Int) {
        instance().setParameter(name, x, y)
    }

    actual fun setParameter(name: String, x: Float, y: Float, z: Float) {
        instance().setParameter(name, x, y, z)
    }

    actual fun setParameter(name: String, x: Int, y: Int, z: Int) {
        instance().setParameter(name, x, y, z)
    }

    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) {
        instance().setParameter(name, x, y, z, w)
    }

    actual fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) {
        instance().setParameter(name, x, y, z, w)
    }

    actual fun setParameter(name: String, type: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float) {
        instance().setParameter(name, type.toAndroid(), r, g, b, a)
    }

    actual fun setParameter(name: String, texture: Texture, sampler: TextureSampler) {
        val androidTexture = requireNotNull(texture.androidTexture) { "Calling method on destroyed Texture" }
        instance().setParameter(name, androidTexture, sampler.androidSampler)
    }

    actual fun setScissor(left: Int, bottom: Int, width: Int, height: Int) {
        instance().setScissor(left, bottom, width, height)
    }

    actual fun unsetScissor() {
        instance().unsetScissor()
    }

    actual fun setPolygonOffset(scale: Float, constant: Float) {
        instance().setPolygonOffset(scale, constant)
    }

    actual fun setMaskThreshold(threshold: Float) {
        instance().setMaskThreshold(threshold)
    }

    actual fun getMaskThreshold(): Float {
        return instance().maskThreshold
    }

    actual fun setDoubleSided(doubleSided: Boolean) {
        instance().setDoubleSided(doubleSided)
    }

    actual fun isDoubleSided(): Boolean {
        return instance().isDoubleSided
    }

    actual fun setTransparencyMode(mode: Material.TransparencyMode) {
        instance().setTransparencyMode(mode.toAndroid())
    }

    actual fun getTransparencyMode(): Material.TransparencyMode {
        return instance().transparencyMode.toKmp()
    }

    actual fun setCullingMode(mode: Material.CullingMode) {
        instance().setCullingMode(mode.toAndroid())
    }

    actual fun setCullingMode(colorPassCullingMode: Material.CullingMode, shadowPassCullingMode: Material.CullingMode) {
        instance().setCullingMode(colorPassCullingMode.toAndroid(), shadowPassCullingMode.toAndroid())
    }

    actual fun getCullingMode(): Material.CullingMode {
        return instance().cullingMode.toKmp()
    }

    actual fun getShadowCullingMode(): Material.CullingMode {
        return instance().shadowCullingMode.toKmp()
    }

    actual fun setColorWrite(enable: Boolean) {
        instance().setColorWrite(enable)
    }

    actual fun isColorWriteEnabled(): Boolean {
        return instance().isColorWriteEnabled
    }

    actual fun setDepthWrite(enable: Boolean) {
        instance().setDepthWrite(enable)
    }

    actual fun isDepthWriteEnabled(): Boolean {
        return instance().isDepthWriteEnabled
    }

    actual fun setStencilWrite(enable: Boolean) {
        instance().setStencilWrite(enable)
    }

    actual fun isStencilWriteEnabled(): Boolean {
        return instance().isStencilWriteEnabled
    }

    actual fun setDepthCulling(enable: Boolean) {
        instance().setDepthCulling(enable)
    }

    actual fun isDepthCullingEnabled(): Boolean {
        return instance().isDepthCullingEnabled
    }

    actual fun setDepthFunc(func: TextureSampler.CompareFunction) {
        instance().setDepthFunc(func.toAndroid())
    }

    actual fun getDepthFunc(): TextureSampler.CompareFunction {
        return instance().depthFunc.toKmp()
    }

    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction, face: StencilFace) {
        instance().setStencilCompareFunction(func.toAndroid(), face.toAndroid())
    }

    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction) {
        instance().setStencilCompareFunction(func.toAndroid())
    }

    actual fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace) {
        instance().setStencilOpStencilFail(op.toAndroid(), face.toAndroid())
    }

    actual fun setStencilOpStencilFail(op: StencilOperation) {
        instance().setStencilOpStencilFail(op.toAndroid())
    }

    actual fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace) {
        instance().setStencilOpDepthFail(op.toAndroid(), face.toAndroid())
    }

    actual fun setStencilOpDepthFail(op: StencilOperation) {
        instance().setStencilOpDepthFail(op.toAndroid())
    }

    actual fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace) {
        instance().setStencilOpDepthStencilPass(op.toAndroid(), face.toAndroid())
    }

    actual fun setStencilOpDepthStencilPass(op: StencilOperation) {
        instance().setStencilOpDepthStencilPass(op.toAndroid())
    }

    actual fun setStencilReferenceValue(value: Int, face: StencilFace) {
        instance().setStencilReferenceValue(value, face.toAndroid())
    }

    actual fun setStencilReferenceValue(value: Int) {
        instance().setStencilReferenceValue(value)
    }

    actual fun setStencilReadMask(readMask: Int, face: StencilFace) {
        instance().setStencilReadMask(readMask, face.toAndroid())
    }

    actual fun setStencilReadMask(readMask: Int) {
        instance().setStencilReadMask(readMask)
    }

    actual fun setStencilWriteMask(writeMask: Int, face: StencilFace) {
        instance().setStencilWriteMask(writeMask, face.toAndroid())
    }

    actual fun setStencilWriteMask(writeMask: Int) {
        instance().setStencilWriteMask(writeMask)
    }

    actual fun getNativeObject(): Long {
        return instance().nativeObject
    }

    actual internal fun invalidate() {
        androidMaterialInstance = null
    }

    actual companion object {
        actual fun duplicate(other: MaterialInstance, name: String?): MaterialInstance {
            val instance = requireNotNull(other.androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
            return MaterialInstance(AndroidMaterialInstance.duplicate(instance, name))
        }
    }
}

private fun Colors.RgbaType.toAndroid(): AndroidColors.RgbaType = AndroidColors.RgbaType.valueOf(name)

private fun Material.TransparencyMode.toAndroid(): com.google.android.filament.Material.TransparencyMode =
    com.google.android.filament.Material.TransparencyMode.valueOf(name)

private fun com.google.android.filament.Material.TransparencyMode.toKmp(): Material.TransparencyMode =
    Material.TransparencyMode.valueOf(name)

private fun com.google.android.filament.Material.CullingMode.toKmp(): Material.CullingMode =
    Material.CullingMode.valueOf(name)

private fun TextureSampler.CompareFunction.toAndroid(): AndroidTextureSampler.CompareFunction =
    AndroidTextureSampler.CompareFunction.valueOf(name)

private fun AndroidTextureSampler.CompareFunction.toKmp(): TextureSampler.CompareFunction =
    TextureSampler.CompareFunction.valueOf(name)

private fun MaterialInstance.StencilOperation.toAndroid(): AndroidMaterialInstance.StencilOperation =
    AndroidMaterialInstance.StencilOperation.valueOf(name)

private fun MaterialInstance.StencilFace.toAndroid(): AndroidMaterialInstance.StencilFace =
    AndroidMaterialInstance.StencilFace.valueOf(name)

