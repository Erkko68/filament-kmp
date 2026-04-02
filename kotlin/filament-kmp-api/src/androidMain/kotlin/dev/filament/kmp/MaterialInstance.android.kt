package dev.filament.kmp

import com.google.android.filament.MaterialInstance as AndroidMaterialInstance

actual class MaterialInstance internal constructor(
    internal var androidMaterialInstance: AndroidMaterialInstance?,
) {
    actual fun getMaterial(): Material {
        val instance = requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
        return Material(instance.material)
    }

    actual fun getName(): String {
        val instance = requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
        return instance.name
    }

    actual fun setParameter(name: String, x: Float) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }.setParameter(name, x)
    }

    actual fun setParameter(name: String, x: Int) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }.setParameter(name, x)
    }

    actual fun setParameter(name: String, x: Float, y: Float) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }.setParameter(name, x, y)
    }

    actual fun setParameter(name: String, x: Int, y: Int) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }.setParameter(name, x, y)
    }

    actual fun setParameter(name: String, x: Float, y: Float, z: Float) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }.setParameter(name, x, y, z)
    }

    actual fun setParameter(name: String, x: Int, y: Int, z: Int) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }.setParameter(name, x, y, z)
    }

    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }.setParameter(name, x, y, z, w)
    }

    actual fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }.setParameter(name, x, y, z, w)
    }

    actual fun setParameter(name: String, texture: Texture, sampler: TextureSampler) {
        val instance = requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
        val androidTexture = requireNotNull(texture.androidTexture) { "Calling method on destroyed Texture" }
        instance.setParameter(name, androidTexture, sampler.androidSampler)
    }

    actual fun setScissor(left: Int, bottom: Int, width: Int, height: Int) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
            .setScissor(left, bottom, width, height)
    }

    actual fun unsetScissor() {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }.unsetScissor()
    }

    actual fun setPolygonOffset(scale: Float, constant: Float) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
            .setPolygonOffset(scale, constant)
    }

    actual fun setMaskThreshold(threshold: Float) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
            .setMaskThreshold(threshold)
    }

    actual fun getMaskThreshold(): Float {
        return requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
            .maskThreshold
    }

    actual fun setDoubleSided(doubleSided: Boolean) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
            .setDoubleSided(doubleSided)
    }

    actual fun setTransparencyMode(mode: Material.TransparencyMode) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
            .setTransparencyMode(mode.toAndroid())
    }

    actual fun setCullingMode(mode: Material.CullingMode) {
        requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
            .setCullingMode(mode.toAndroid())
    }

    actual fun getNativeObject(): Long {
        return requireNotNull(androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
            .nativeObject
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

