@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaMaterialInstance

actual class MaterialInstance internal constructor(internal var nativeHandle: CPointer<FilaMaterialInstance>?) {
    actual fun getName(): String = FilaMaterialInstance_getName(nativeHandle)?.toKString() ?: ""
    actual fun getMaterial(): Material = Material(FilaMaterialInstance_getMaterial(nativeHandle))

    actual fun setParameter(name: String, x: Float) { FilaMaterialInstance_setParameterFloat(nativeHandle, name, x) }
    actual fun setParameter(name: String, x: Float, y: Float) { FilaMaterialInstance_setParameterFloat2(nativeHandle, name, x, y) }
    actual fun setParameter(name: String, x: Float, y: Float, z: Float) { FilaMaterialInstance_setParameterFloat3(nativeHandle, name, x, y, z) }
    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) { FilaMaterialInstance_setParameterFloat4(nativeHandle, name, x, y, z, w) }

    actual fun setParameter(name: String, x: Int) { FilaMaterialInstance_setParameterInt(nativeHandle, name, x) }
    actual fun setParameter(name: String, x: Int, y: Int) { FilaMaterialInstance_setParameterInt2(nativeHandle, name, x, y) }
    actual fun setParameter(name: String, x: Int, y: Int, z: Int) { FilaMaterialInstance_setParameterInt3(nativeHandle, name, x, y, z) }
    actual fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) { FilaMaterialInstance_setParameterInt4(nativeHandle, name, x, y, z, w) }

    actual fun setParameter(name: String, x: Boolean) { FilaMaterialInstance_setParameterBool(nativeHandle, name, x) }
    actual fun setParameter(name: String, x: Boolean, y: Boolean) { FilaMaterialInstance_setParameterBool2(nativeHandle, name, x, y) }
    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean) { FilaMaterialInstance_setParameterBool3(nativeHandle, name, x, y, z) }
    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean, w: Boolean) { FilaMaterialInstance_setParameterBool4(nativeHandle, name, x, y, z, w) }

    actual fun setParameter(name: String, texture: Texture, sampler: TextureSampler) {
        FilaMaterialInstance_setParameterTexture(nativeHandle, name, texture.nativeHandle, sampler.mSampler)
    }

    actual fun setParameter(name: String, colorType: Colors.RgbType, r: Float, g: Float, b: Float) {
        val linear = Colors.toLinear(colorType, r, g, b)
        FilaMaterialInstance_setParameterFloat3(nativeHandle, name, linear[0], linear[1], linear[2])
    }

    actual fun setParameter(name: String, colorType: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float) {
        val linear = Colors.toLinear(colorType, r, g, b, a)
        FilaMaterialInstance_setParameterFloat4(nativeHandle, name, linear[0], linear[1], linear[2], linear[3])
    }

    actual fun setScissor(left: Int, bottom: Int, width: Int, height: Int) {
        FilaMaterialInstance_setScissor(nativeHandle, left, bottom, width.toUInt(), height.toUInt())
    }
    actual fun unsetScissor() { FilaMaterialInstance_unsetScissor(nativeHandle) }

    actual fun setPolygonOffset(scale: Float, constant: Float) {
        FilaMaterialInstance_setPolygonOffset(nativeHandle, scale, constant)
    }

    actual fun setMaskThreshold(threshold: Float) { FilaMaterialInstance_setMaskThreshold(nativeHandle, threshold) }
    actual fun getMaskThreshold(): Float = FilaMaterialInstance_getMaskThreshold(nativeHandle)

    actual fun setSpecularAntiAliasingVariance(variance: Float) {
        FilaMaterialInstance_setSpecularAntiAliasingVariance(nativeHandle, variance)
    }
    actual fun getSpecularAntiAliasingVariance(): Float = FilaMaterialInstance_getSpecularAntiAliasingVariance(nativeHandle)

    actual fun setSpecularAntiAliasingThreshold(threshold: Float) {
        FilaMaterialInstance_setSpecularAntiAliasingThreshold(nativeHandle, threshold)
    }
    actual fun getSpecularAntiAliasingThreshold(): Float = FilaMaterialInstance_getSpecularAntiAliasingThreshold(nativeHandle)

    actual fun setDoubleSided(doubleSided: Boolean) { FilaMaterialInstance_setDoubleSided(nativeHandle, doubleSided) }
    actual fun isDoubleSided(): Boolean = FilaMaterialInstance_isDoubleSided(nativeHandle)

    actual fun setTransparencyMode(mode: Material.TransparencyMode) {
        FilaMaterialInstance_setTransparencyMode(nativeHandle, mode.ordinal.toUInt().convert())
    }
    actual fun getTransparencyMode(): Material.TransparencyMode =
        Material.TransparencyMode.values()[FilaMaterialInstance_getTransparencyMode(nativeHandle).toInt()]

    actual fun setCullingMode(mode: Material.CullingMode) {
        FilaMaterialInstance_setCullingMode(nativeHandle, mode.ordinal.toUInt().convert())
    }
    actual fun getCullingMode(): Material.CullingMode =
        Material.CullingMode.values()[FilaMaterialInstance_getCullingMode(nativeHandle).toInt()]

    actual fun setColorWrite(enable: Boolean) { FilaMaterialInstance_setColorWrite(nativeHandle, enable) }
    actual fun isColorWriteEnabled(): Boolean = FilaMaterialInstance_isColorWriteEnabled(nativeHandle)

    actual fun setDepthWrite(enable: Boolean) { FilaMaterialInstance_setDepthWrite(nativeHandle, enable) }
    actual fun isDepthWriteEnabled(): Boolean = FilaMaterialInstance_isDepthWriteEnabled(nativeHandle)

    actual fun setStencilWrite(enable: Boolean) { FilaMaterialInstance_setStencilWrite(nativeHandle, enable) }
    actual fun isStencilWriteEnabled(): Boolean = FilaMaterialInstance_isStencilWriteEnabled(nativeHandle)

    actual fun setDepthCulling(enable: Boolean) { FilaMaterialInstance_setDepthCulling(nativeHandle, enable) }
    actual fun isDepthCullingEnabled(): Boolean = FilaMaterialInstance_isDepthCullingEnabled(nativeHandle)

    actual fun setDepthFunc(func: TextureSampler.CompareFunction) {
        FilaMaterialInstance_setDepthFunc(nativeHandle, func.ordinal.toUInt().convert())
    }
    actual fun getDepthFunc(): TextureSampler.CompareFunction =
        TextureSampler.CompareFunction.values()[FilaMaterialInstance_getDepthFunc(nativeHandle).toInt()]
}
