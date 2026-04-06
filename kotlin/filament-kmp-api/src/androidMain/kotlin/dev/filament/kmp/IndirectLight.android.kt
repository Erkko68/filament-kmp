package dev.filament.kmp

import com.google.android.filament.IndirectLight as AndroidIndirectLight

actual class IndirectLight internal constructor(val nativeIndirectLight: AndroidIndirectLight) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidIndirectLight.Builder()

        actual fun reflections(cubemap: Texture): Builder {
            nativeBuilder.reflections(cubemap.nativeTexture)
            return this
        }
        actual fun irradiance(bands: Int, sh: FloatArray): Builder {
            nativeBuilder.irradiance(bands, sh)
            return this
        }
        actual fun irradiance(cubemap: Texture): Builder {
            nativeBuilder.irradiance(cubemap.nativeTexture)
            return this
        }
        actual fun intensity(envIntensity: Float): Builder {
            nativeBuilder.intensity(envIntensity)
            return this
        }
        actual fun rotation(rotation: FloatArray): Builder {
            nativeBuilder.rotation(rotation)
            return this
        }
        actual fun build(engine: Engine): IndirectLight = IndirectLight(nativeBuilder.build(engine.nativeEngine))
    }

    actual fun setIntensity(intensity: Float) {
        nativeIndirectLight.intensity = intensity
    }
    actual fun getIntensity(): Float = nativeIndirectLight.intensity
    
    actual fun setRotation(rotation: FloatArray) {
        nativeIndirectLight.setRotation(rotation)
    }
    actual fun getRotation(outRotation: FloatArray?): FloatArray = nativeIndirectLight.getRotation(outRotation)
}
