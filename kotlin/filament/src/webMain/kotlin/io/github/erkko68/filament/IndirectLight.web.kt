package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class IndirectLight @InternalFilamentApi constructor(internal var nativeHandle: Int) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaIndirectLightBuilder_create()

        actual fun reflections(cubemap: Texture): Builder = apply { FilaIndirectLightBuilder_reflections(nativeBuilder, cubemap.nativeHandle) }
        actual fun irradiance(bands: Int, sh: FloatArray): Builder = apply {
            sh.usePinned { pinned ->
                FilaIndirectLightBuilder_irradiance(nativeBuilder, bands, pinned)
            }
        }
        actual fun radiance(bands: Int, sh: FloatArray): Builder = apply {
            sh.usePinned { pinned ->
                FilaIndirectLightBuilder_radiance(nativeBuilder, bands, pinned)
            }
        }
        actual fun irradiance(cubemap: Texture): Builder = apply { FilaIndirectLightBuilder_irradianceAsTexture(nativeBuilder, cubemap.nativeHandle) }
        actual fun intensity(envIntensity: Float): Builder = apply { FilaIndirectLightBuilder_intensity(nativeBuilder, envIntensity) }
        actual fun rotation(rotation: FloatArray): Builder = apply {
            rotation.usePinned { pinned ->
                FilaIndirectLightBuilder_rotation(nativeBuilder, pinned)
            }
        }
        actual fun build(engine: Engine): IndirectLight {
            val handle = FilaIndirectLightBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaIndirectLightBuilder_destroy(nativeBuilder)
            return IndirectLight(handle)
        }
    }

    actual var intensity: Float
        get() = FilaIndirectLight_getIntensity(nativeHandle)
        set(value) { FilaIndirectLight_setIntensity(nativeHandle, value) }

    actual var rotation: FloatArray
        get() = FloatArray(9).also { result -> result.usePinned { FilaIndirectLight_getRotation(nativeHandle, it) } }
        set(value) { value.usePinned { FilaIndirectLight_setRotation(nativeHandle, it) } }

    actual val reflectionsTexture: Texture? get() = FilaIndirectLight_getReflectionsTexture(nativeHandle).takeIf { it != 0 }?.let { Texture(it) }
    actual val irradianceTexture: Texture? get() = FilaIndirectLight_getIrradianceTexture(nativeHandle).takeIf { it != 0 }?.let { Texture(it) }

    actual companion object {
        actual fun getDirectionEstimate(sh: FloatArray, out: FloatArray?): FloatArray {
            val result = out ?: FloatArray(3)
            sh.usePinned { pSh ->
                result.usePinned { pOut ->
                    FilaIndirectLight_getDirectionEstimateStatic(pSh, pOut)
                }
            }
            return result
        }

        actual fun getColorEstimate(sh: FloatArray, x: Double, y: Double, z: Double, out: FloatArray?): FloatArray {
            val result = out ?: FloatArray(4)
            sh.usePinned { pSh ->
                result.usePinned { pOut ->
                    FilaIndirectLight_getColorEstimateStatic(pSh, x.toFloat(), y.toFloat(), z.toFloat(), pOut)
                }
            }
            return result
        }
    }
}
