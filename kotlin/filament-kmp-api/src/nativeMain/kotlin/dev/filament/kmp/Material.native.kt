@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaMaterial

actual class Material internal constructor(internal var nativeHandle: CPointer<FilaMaterial>?) {
    actual class Builder actual constructor() {
        private var payload: CPointer<*>? = null
        private var size: Int = 0

        actual fun payload(buffer: Any, size: Int): Builder {
            this.payload = buffer as? CPointer<*>
            this.size = size
            return this
        }
        
        actual fun build(engine: Engine): Material {
            val handle = FilaMaterial_Builder_build(engine.nativeHandle, payload, size.toULong())
                ?: throw IllegalStateException("Couldn't create Material")
            return Material(handle)
        }
    }

    actual enum class Shading { UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS }
    actual enum class BlendingMode { OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN }
    actual enum class CullingMode { NONE, FRONT, BACK, FRONT_AND_BACK }
    actual enum class TransparencyMode { DEFAULT, TWO_PASSES_ONE_SIDE, TWO_PASSES_TWO_SIDES }

    actual fun getName(): String = FilaMaterial_getName(nativeHandle)?.toKString() ?: ""
    
    actual fun getShading(): Shading {
        val s = FilaMaterial_getShading(nativeHandle).toInt()
        return Shading.values().getOrElse(s) { Shading.LIT }
    }

    actual fun getBlendingMode(): BlendingMode {
        val b = FilaMaterial_getBlendingMode(nativeHandle).toInt()
        return BlendingMode.values().getOrElse(b) { BlendingMode.OPAQUE }
    }

    actual fun getTransparencyMode(): TransparencyMode {
        val t = FilaMaterial_getTransparencyMode(nativeHandle).toInt()
        return TransparencyMode.values().getOrElse(t) { TransparencyMode.DEFAULT }
    }

    actual fun getCullingMode(): CullingMode {
        val c = FilaMaterial_getCullingMode(nativeHandle).toInt()
        return CullingMode.values().getOrElse(c) { CullingMode.NONE }
    }

    actual fun isColorWriteEnabled(): Boolean = FilaMaterial_isColorWriteEnabled(nativeHandle)
    actual fun isDepthWriteEnabled(): Boolean = FilaMaterial_isDepthWriteEnabled(nativeHandle)
    actual fun isDepthCullingEnabled(): Boolean = FilaMaterial_isDepthCullingEnabled(nativeHandle)
    actual fun isDoubleSided(): Boolean = FilaMaterial_isDoubleSided(nativeHandle)
    actual fun isAlphaToCoverageEnabled(): Boolean = FilaMaterial_isAlphaToCoverageEnabled(nativeHandle)

    actual fun getMaskThreshold(): Float = FilaMaterial_getMaskThreshold(nativeHandle)
    actual fun getSpecularAntiAliasingVariance(): Float = FilaMaterial_getSpecularAntiAliasingVariance(nativeHandle)
    actual fun getSpecularAntiAliasingThreshold(): Float = FilaMaterial_getSpecularAntiAliasingThreshold(nativeHandle)

    actual fun createInstance(): MaterialInstance = MaterialInstance(FilaMaterial_createInstance(nativeHandle))
    actual fun createInstance(name: String): MaterialInstance = MaterialInstance(FilaMaterial_createInstanceWithName(nativeHandle, name))
    actual fun getDefaultInstance(): MaterialInstance = MaterialInstance(FilaMaterial_getDefaultInstance(nativeHandle))
}
