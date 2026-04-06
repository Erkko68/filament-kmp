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

    actual fun getName(): String = FilaMaterial_getName(nativeHandle)?.toKString() ?: ""
    
    actual fun getShading(): Shading {
        val shading = FilaMaterial_getShading(nativeHandle).toInt()
        return when (shading) {
            0 -> Shading.UNLIT
            1 -> Shading.LIT
            2 -> Shading.CLOTH // Swapped in C API
            3 -> Shading.SUBSURFACE
            4 -> Shading.SPECULAR_GLOSSINESS
            else -> Shading.LIT
        }
    }

    actual fun getBlendingMode(): BlendingMode = BlendingMode.values()[FilaMaterial_getBlendingMode(nativeHandle).toInt()]
    
    actual fun createInstance(): MaterialInstance = MaterialInstance(FilaMaterial_createInstance(nativeHandle))
    actual fun createInstance(name: String): MaterialInstance = MaterialInstance(FilaMaterial_createInstanceWithName(nativeHandle, name))
    actual fun getDefaultInstance(): MaterialInstance = MaterialInstance(FilaMaterial_getDefaultInstance(nativeHandle))
}
