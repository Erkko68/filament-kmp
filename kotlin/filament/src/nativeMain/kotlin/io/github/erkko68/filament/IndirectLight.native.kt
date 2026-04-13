@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaIndirectLight

actual class IndirectLight internal constructor(internal var nativeHandle: CPointer<FilaIndirectLight>?) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaIndirectLightBuilder_create()

        actual fun reflections(cubemap: Texture): Builder = apply { FilaIndirectLightBuilder_reflections(nativeBuilder, cubemap.nativeHandle) }
        actual fun irradiance(bands: Int, sh: FloatArray): Builder = apply {
            sh.usePinned { pinned ->
                FilaIndirectLightBuilder_irradiance(nativeBuilder, bands.toUByte(), pinned.addressOf(0))
            }
        }
        actual fun radiance(bands: Int, sh: FloatArray): Builder = apply {
            sh.usePinned { pinned ->
                FilaIndirectLightBuilder_radiance(nativeBuilder, bands.toUByte(), pinned.addressOf(0))
            }
        }
        actual fun irradiance(cubemap: Texture): Builder = apply { FilaIndirectLightBuilder_irradianceAsTexture(nativeBuilder, cubemap.nativeHandle) }
        actual fun intensity(envIntensity: Float): Builder = apply { FilaIndirectLightBuilder_intensity(nativeBuilder, envIntensity) }
        actual fun rotation(rotation: FloatArray): Builder = apply {
            rotation.usePinned { pinned ->
                FilaIndirectLightBuilder_rotation(nativeBuilder, pinned.addressOf(0))
            }
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
        rotation.usePinned { pinned ->
            FilaIndirectLight_setRotation(nativeHandle, pinned.addressOf(0))
        }
    }
    actual fun getRotation(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(9)
        result.usePinned { pinned ->
            FilaIndirectLight_getRotation(nativeHandle, pinned.addressOf(0))
        }
        return result
    }

    actual fun getReflectionsTexture(): Texture? {
        val tex = FilaIndirectLight_getReflectionsTexture(nativeHandle)
        return if (tex != null) Texture(tex) else null
    }
    actual fun getIrradianceTexture(): Texture? {
        val tex = FilaIndirectLight_getIrradianceTexture(nativeHandle)
        return if (tex != null) Texture(tex) else null
    }

    actual companion object {
        actual fun getDirectionEstimate(sh: FloatArray, out: FloatArray?): FloatArray {
            val result = out ?: FloatArray(3)
            sh.usePinned { pSh ->
                result.usePinned { pOut ->
                    FilaIndirectLight_getDirectionEstimateStatic(pSh.addressOf(0), pOut.addressOf(0))
                }
            }
            return result
        }

        actual fun getColorEstimate(sh: FloatArray, x: Double, y: Double, z: Double, out: FloatArray?): FloatArray {
            val result = out ?: FloatArray(4)
            sh.usePinned { pSh ->
                result.usePinned { pOut ->
                    FilaIndirectLight_getColorEstimateStatic(pSh.addressOf(0), x.toFloat(), y.toFloat(), z.toFloat(), pOut.addressOf(0))
                }
            }
            return result
        }
    }
}
