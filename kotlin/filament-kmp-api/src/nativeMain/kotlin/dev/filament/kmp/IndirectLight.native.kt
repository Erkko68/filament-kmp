package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class IndirectLight internal constructor(
    internal val nativeObject: CPointer<FilaIndirectLight>
) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaIndirectLightBuilder_create()!!

        actual fun reflections(cubemap: Texture): Builder {
            FilaIndirectLightBuilder_reflections(nativeBuilder, cubemap.nativeObject)
            return this
        }

        actual fun irradiance(bands: Int, sh: FloatArray): Builder {
            memScoped {
                FilaIndirectLightBuilder_irradiance(nativeBuilder, bands.toUByte(), sh.toCValues().ptr)
            }
            return this
        }

        actual fun radiance(bands: Int, sh: FloatArray): Builder {
            memScoped {
                FilaIndirectLightBuilder_radiance(nativeBuilder, bands.toUByte(), sh.toCValues().ptr)
            }
            return this
        }

        actual fun irradiance(cubemap: Texture): Builder {
            FilaIndirectLightBuilder_irradianceAsTexture(nativeBuilder, cubemap.nativeObject)
            return this
        }

        actual fun intensity(envIntensity: Float): Builder {
            FilaIndirectLightBuilder_intensity(nativeBuilder, envIntensity)
            return this
        }

        actual fun rotation(rotation: FloatArray): Builder {
            memScoped {
                FilaIndirectLightBuilder_rotation(nativeBuilder, rotation.toCValues().ptr)
            }
            return this
        }

        actual fun build(engine: Engine): IndirectLight {
            val nativeIndirectLight = FilaIndirectLightBuilder_build(nativeBuilder, engine.nativeObject)
                ?: throw IllegalStateException("Couldn't create IndirectLight")
            FilaIndirectLightBuilder_destroy(nativeBuilder)
            return IndirectLight(nativeIndirectLight)
        }
    }

    actual fun setIntensity(intensity: Float) {
        FilaIndirectLight_setIntensity(nativeObject, intensity)
    }

    actual fun getIntensity(): Float = FilaIndirectLight_getIntensity(nativeObject)

    actual fun setRotation(rotation: FloatArray) {
        memScoped {
            FilaIndirectLight_setRotation(nativeObject, rotation.toCValues().ptr)
        }
    }

    actual fun getRotation(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(9)
        memScoped {
            val nativeRotation = allocArray<FloatVar>(9)
            FilaIndirectLight_getRotation(nativeObject, nativeRotation)
            for (i in 0 until 9) result[i] = nativeRotation[i]
        }
        return result
    }

    actual fun getDirectionEstimate(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        memScoped {
            val nativeDirection = allocArray<FloatVar>(3)
            FilaIndirectLight_getDirectionEstimate(nativeObject, nativeDirection)
            for (i in 0 until 3) result[i] = nativeDirection[i]
        }
        return result
    }

    actual fun getColorEstimate(out: FloatArray?, x: Float, y: Float, z: Float): FloatArray {
        val result = out ?: FloatArray(3)
        memScoped {
            val nativeColor = allocArray<FloatVar>(3)
            FilaIndirectLight_getColorEstimate(nativeObject, x, y, z, nativeColor)
            for (i in 0 until 3) result[i] = nativeColor[i]
        }
        return result
    }

    actual val reflectionsTexture: Texture?
        get() {
            val nativeTexture = FilaIndirectLight_getReflectionsTexture(nativeObject) ?: return null
            return Texture(nativeTexture)
        }

    actual val irradianceTexture: Texture?
        get() {
            val nativeTexture = FilaIndirectLight_getIrradianceTexture(nativeObject) ?: return null
            return Texture(nativeTexture)
        }

    actual companion object {
        actual fun getDirectionEstimate(sh: FloatArray, out: FloatArray?): FloatArray {
            val result = out ?: FloatArray(3)
            memScoped {
                val nativeDirection = allocArray<FloatVar>(3)
                FilaIndirectLight_getDirectionEstimateStatic(sh.toCValues().ptr, nativeDirection)
                for (i in 0 until 3) result[i] = nativeDirection[i]
            }
            return result
        }

        actual fun getColorEstimate(sh: FloatArray, out: FloatArray?, x: Float, y: Float, z: Float): FloatArray {
            val result = out ?: FloatArray(3)
            memScoped {
                val nativeColor = allocArray<FloatVar>(3)
                FilaIndirectLight_getColorEstimateStatic(sh.toCValues().ptr, x, y, z, nativeColor)
                for (i in 0 until 3) result[i] = nativeColor[i]
            }
            return result
        }
    }
}
