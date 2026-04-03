package dev.filament.kmp

actual class MaterialInstance {
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

    actual fun setParameter(name: String, type: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float) {
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

    actual fun isDoubleSided(): Boolean = TODO("Not yet implemented")

    actual fun setTransparencyMode(mode: Material.TransparencyMode) {
        TODO("Not yet implemented")
    }

    actual fun getTransparencyMode(): Material.TransparencyMode = TODO("Not yet implemented")

    actual fun setCullingMode(mode: Material.CullingMode) {
        TODO("Not yet implemented")
    }

    actual fun setCullingMode(colorPassCullingMode: Material.CullingMode, shadowPassCullingMode: Material.CullingMode) {
        TODO("Not yet implemented")
    }

    actual fun getCullingMode(): Material.CullingMode = TODO("Not yet implemented")

    actual fun getShadowCullingMode(): Material.CullingMode = TODO("Not yet implemented")

    actual fun setColorWrite(enable: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun isColorWriteEnabled(): Boolean = TODO("Not yet implemented")

    actual fun setDepthWrite(enable: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun isDepthWriteEnabled(): Boolean = TODO("Not yet implemented")

    actual fun setStencilWrite(enable: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun isStencilWriteEnabled(): Boolean = TODO("Not yet implemented")

    actual fun setDepthCulling(enable: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun isDepthCullingEnabled(): Boolean = TODO("Not yet implemented")

    actual fun setDepthFunc(func: TextureSampler.CompareFunction) {
        TODO("Not yet implemented")
    }

    actual fun getDepthFunc(): TextureSampler.CompareFunction = TODO("Not yet implemented")

    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction, face: StencilFace) {
        TODO("Not yet implemented")
    }

    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction) {
        TODO("Not yet implemented")
    }

    actual fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace) {
        TODO("Not yet implemented")
    }

    actual fun setStencilOpStencilFail(op: StencilOperation) {
        TODO("Not yet implemented")
    }

    actual fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace) {
        TODO("Not yet implemented")
    }

    actual fun setStencilOpDepthFail(op: StencilOperation) {
        TODO("Not yet implemented")
    }

    actual fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace) {
        TODO("Not yet implemented")
    }

    actual fun setStencilOpDepthStencilPass(op: StencilOperation) {
        TODO("Not yet implemented")
    }

    actual fun setStencilReferenceValue(value: Int, face: StencilFace) {
        TODO("Not yet implemented")
    }

    actual fun setStencilReferenceValue(value: Int) {
        TODO("Not yet implemented")
    }

    actual fun setStencilReadMask(readMask: Int, face: StencilFace) {
        TODO("Not yet implemented")
    }

    actual fun setStencilReadMask(readMask: Int) {
        TODO("Not yet implemented")
    }

    actual fun setStencilWriteMask(writeMask: Int, face: StencilFace) {
        TODO("Not yet implemented")
    }

    actual fun setStencilWriteMask(writeMask: Int) {
        TODO("Not yet implemented")
    }

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }

    actual companion object {
        actual fun duplicate(other: MaterialInstance, name: String?): MaterialInstance = TODO("Not yet implemented")
    }
}

