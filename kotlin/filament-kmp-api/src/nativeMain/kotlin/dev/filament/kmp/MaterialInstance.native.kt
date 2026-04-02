package dev.filament.kmp

actual class MaterialInstance {
    actual fun getMaterial(): Material = TODO("Not yet implemented")

    actual fun getName(): String = TODO("Not yet implemented")

    actual fun setParameter(name: String, x: Float) {
        TODO("Not yet implemented")
    }

    actual fun setParameter(name: String, x: Int) {
        TODO("Not yet implemented")
    }

    actual fun setParameter(name: String, x: Float, y: Float) {
        TODO("Not yet implemented")
    }

    actual fun setParameter(name: String, x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    actual fun setParameter(name: String, x: Float, y: Float, z: Float) {
        TODO("Not yet implemented")
    }

    actual fun setParameter(name: String, x: Int, y: Int, z: Int) {
        TODO("Not yet implemented")
    }

    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) {
        TODO("Not yet implemented")
    }

    actual fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) {
        TODO("Not yet implemented")
    }

    actual fun setParameter(name: String, texture: Texture, sampler: TextureSampler) {
        TODO("Not yet implemented")
    }

    actual fun setScissor(left: Int, bottom: Int, width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    actual fun unsetScissor() {
        TODO("Not yet implemented")
    }

    actual fun setPolygonOffset(scale: Float, constant: Float) {
        TODO("Not yet implemented")
    }

    actual fun setMaskThreshold(threshold: Float) {
        TODO("Not yet implemented")
    }

    actual fun getMaskThreshold(): Float = TODO("Not yet implemented")

    actual fun setDoubleSided(doubleSided: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun setTransparencyMode(mode: Material.TransparencyMode) {
        TODO("Not yet implemented")
    }

    actual fun setCullingMode(mode: Material.CullingMode) {
        TODO("Not yet implemented")
    }

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }

    actual companion object {
        actual fun duplicate(other: MaterialInstance, name: String?): MaterialInstance = TODO("Not yet implemented")
    }
}

