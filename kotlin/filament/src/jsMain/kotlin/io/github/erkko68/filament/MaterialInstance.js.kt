package io.github.erkko68.filament

actual class MaterialInstance {
    actual fun getMaterial(): Material {
        TODO("Not yet implemented")
    }

    actual fun getName(): String {
        TODO("Not yet implemented")
    }

    actual fun setParameter(name: String, x: Boolean) {
    }

    actual fun setParameter(name: String, x: Float) {
    }

    actual fun setParameter(name: String, x: Int) {
    }

    actual fun setParameter(name: String, x: Boolean, y: Boolean) {
    }

    actual fun setParameter(name: String, x: Float, y: Float) {
    }

    actual fun setParameter(name: String, x: Int, y: Int) {
    }

    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean) {
    }

    actual fun setParameter(name: String, x: Float, y: Float, z: Float) {
    }

    actual fun setParameter(name: String, x: Int, y: Int, z: Int) {
    }

    actual fun setParameter(
        name: String,
        x: Boolean,
        y: Boolean,
        z: Boolean,
        w: Boolean
    ) {
    }

    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) {
    }

    actual fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) {
    }

    actual fun setParameter(
        name: String,
        texture: Texture,
        sampler: TextureSampler
    ) {
    }

    actual fun setParameter(
        name: String,
        type: BooleanElement,
        v: BooleanArray,
        offset: Int,
        count: Int
    ) {
    }

    actual fun setParameter(
        name: String,
        type: IntElement,
        v: IntArray,
        offset: Int,
        count: Int
    ) {
    }

    actual fun setParameter(
        name: String,
        type: FloatElement,
        v: FloatArray,
        offset: Int,
        count: Int
    ) {
    }

    actual fun setParameter(
        name: String,
        type: Colors.RgbType,
        r: Float,
        g: Float,
        b: Float
    ) {
    }

    actual fun setParameter(
        name: String,
        type: Colors.RgbaType,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
    }

    actual fun setScissor(left: Int, bottom: Int, width: Int, height: Int) {
    }

    actual fun unsetScissor() {
    }

    actual fun setPolygonOffset(scale: Float, constant: Float) {
    }

    actual fun setMaskThreshold(threshold: Float) {
    }

    actual fun getMaskThreshold(): Float {
        TODO("Not yet implemented")
    }

    actual fun setSpecularAntiAliasingVariance(variance: Float) {
    }

    actual fun getSpecularAntiAliasingVariance(): Float {
        TODO("Not yet implemented")
    }

    actual fun setSpecularAntiAliasingThreshold(threshold: Float) {
    }

    actual fun getSpecularAntiAliasingThreshold(): Float {
        TODO("Not yet implemented")
    }

    actual fun setDoubleSided(doubleSided: Boolean) {
    }

    actual fun isDoubleSided(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setTransparencyMode(mode: Material.TransparencyMode) {
    }

    actual fun getTransparencyMode(): Material.TransparencyMode {
        TODO("Not yet implemented")
    }

    actual fun setCullingMode(mode: Material.CullingMode) {
    }

    actual fun setCullingMode(
        colorPassCullingMode: Material.CullingMode,
        shadowPassCullingMode: Material.CullingMode
    ) {
    }

    actual fun getCullingMode(): Material.CullingMode {
        TODO("Not yet implemented")
    }

    actual fun getShadowCullingMode(): Material.CullingMode {
        TODO("Not yet implemented")
    }

    actual fun setColorWrite(enable: Boolean) {
    }

    actual fun isColorWriteEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setDepthWrite(enable: Boolean) {
    }

    actual fun isDepthWriteEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setStencilWrite(enable: Boolean) {
    }

    actual fun isStencilWriteEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setDepthCulling(enable: Boolean) {
    }

    actual fun setDepthFunc(func: TextureSampler.CompareFunction) {
    }

    actual fun isDepthCullingEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getDepthFunc(): TextureSampler.CompareFunction {
        TODO("Not yet implemented")
    }

    actual fun setStencilCompareFunction(
        func: TextureSampler.CompareFunction,
        face: StencilFace
    ) {
    }

    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction) {
    }

    actual fun setStencilOpStencilFail(
        op: StencilOperation,
        face: StencilFace
    ) {
    }

    actual fun setStencilOpStencilFail(op: StencilOperation) {
    }

    actual fun setStencilOpDepthFail(
        op: StencilOperation,
        face: StencilFace
    ) {
    }

    actual fun setStencilOpDepthFail(op: StencilOperation) {
    }

    actual fun setStencilOpDepthStencilPass(
        op: StencilOperation,
        face: StencilFace
    ) {
    }

    actual fun setStencilOpDepthStencilPass(op: StencilOperation) {
    }

    actual fun setStencilReferenceValue(
        value: Int,
        face: StencilFace
    ) {
    }

    actual fun setStencilReferenceValue(value: Int) {
    }

    actual fun setStencilReadMask(readMask: Int, face: StencilFace) {
    }

    actual fun setStencilReadMask(readMask: Int) {
    }

    actual fun setStencilWriteMask(
        writeMask: Int,
        face: StencilFace
    ) {
    }

    actual fun setStencilWriteMask(writeMask: Int) {
    }

    actual enum class BooleanElement { BOOL, BOOL2, BOOL3, BOOL4 }
    actual enum class IntElement { INT, INT2, INT3, INT4 }
    actual enum class FloatElement { FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, MAT4 }
    actual enum class StencilOperation { KEEP, ZERO, REPLACE, INCR_CLAMP, INCR_WRAP, DECR_CLAMP, DECR_WRAP, INVERT }
    actual enum class StencilFace { FRONT, BACK, FRONT_AND_BACK }
    actual companion object {
        actual fun duplicate(
            other: MaterialInstance,
            name: String?
        ): MaterialInstance {
            TODO("Not yet implemented")
        }
    }
}