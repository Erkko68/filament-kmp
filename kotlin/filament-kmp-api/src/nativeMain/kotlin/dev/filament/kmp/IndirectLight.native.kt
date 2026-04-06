@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaIndirectLight

actual class IndirectLight internal constructor(internal var nativeHandle: CPointer<FilaIndirectLight>?) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaIndirectLightBuilder_create()

        actual fun reflections(cubemap: Texture): Builder {
            FilaIndirectLightBuilder_reflections(nativeBuilder, cubemap.nativeHandle)
            return this
        }
        
        actual fun irradiance(bands: Int, sh: FloatArray): Builder {
            sh.usePinned { 
                FilaIndirectLightBuilder_irradiance(nativeBuilder, bands.toUByte(), it.addressOf(0))
            }
            return this
        }
        
        actual fun irradiance(cubemap: Texture): Builder {
            FilaIndirectLightBuilder_irradianceAsTexture(nativeBuilder, cubemap.nativeHandle)
            return this
        }
        
        actual fun intensity(envIntensity: Float): Builder {
            FilaIndirectLightBuilder_intensity(nativeBuilder, envIntensity)
            return this
        }
        
        actual fun rotation(rotation: FloatArray): Builder {
            rotation.usePinned {
                FilaIndirectLightBuilder_rotation(nativeBuilder, it.addressOf(0))
            }
            return this
        }
        
        actual fun build(engine: Engine): IndirectLight {
            val handle = FilaIndirectLightBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaIndirectLightBuilder_destroy(nativeBuilder)
            return IndirectLight(handle)
        }
    }

    actual fun setIntensity(intensity: Float) = FilaIndirectLight_setIntensity(nativeHandle, intensity)
    actual fun getIntensity(): Float = FilaIndirectLight_getIntensity(nativeHandle)
    
    actual fun setRotation(rotation: FloatArray) {
        rotation.usePinned {
            FilaIndirectLight_setRotation(nativeHandle, it.addressOf(0))
        }
    }
    
    actual fun getRotation(outRotation: FloatArray?): FloatArray {
        val result = outRotation ?: FloatArray(9)
        result.usePinned { 
            FilaIndirectLight_getRotation(nativeHandle, it.addressOf(0))
        }
        return result
    }
}
