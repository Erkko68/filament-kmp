package dev.filament.kmp

import com.google.android.filament.IndirectLight as AndroidIndirectLight

actual class IndirectLight internal constructor(
    internal var androidIndirectLight: AndroidIndirectLight?,
) {
    actual fun setIntensity(intensity: Float) {
        val indirectLight = requireNotNull(androidIndirectLight) { "Calling method on destroyed IndirectLight" }
        indirectLight.setIntensity(intensity)
    }

    actual fun getIntensity(): Float {
        val indirectLight = requireNotNull(androidIndirectLight) { "Calling method on destroyed IndirectLight" }
        return indirectLight.intensity
    }

    actual fun setRotation(rotation: FloatArray) {
        val indirectLight = requireNotNull(androidIndirectLight) { "Calling method on destroyed IndirectLight" }
        indirectLight.setRotation(rotation)
    }

    actual fun getRotation(rotation: FloatArray?): FloatArray {
        val indirectLight = requireNotNull(androidIndirectLight) { "Calling method on destroyed IndirectLight" }
        return indirectLight.getRotation(rotation)
    }

    @Deprecated("Use IndirectLight.getDirectionEstimate(sh, direction)")
    actual fun getDirectionEstimate(direction: FloatArray?): FloatArray {
        val indirectLight = requireNotNull(androidIndirectLight) { "Calling method on destroyed IndirectLight" }
        return indirectLight.getDirectionEstimate(direction)
    }

    @Deprecated("Use IndirectLight.getColorEstimate(colorIntensity, sh, x, y, z)")
    actual fun getColorEstimate(colorIntensity: FloatArray?, x: Float, y: Float, z: Float): FloatArray {
        val indirectLight = requireNotNull(androidIndirectLight) { "Calling method on destroyed IndirectLight" }
        return indirectLight.getColorEstimate(colorIntensity, x, y, z)
    }

    actual fun getReflectionsTexture(): Texture? {
        val indirectLight = requireNotNull(androidIndirectLight) { "Calling method on destroyed IndirectLight" }
        val texture = indirectLight.reflectionsTexture ?: return null
        return Texture(texture)
    }

    actual fun getIrradianceTexture(): Texture? {
        val indirectLight = requireNotNull(androidIndirectLight) { "Calling method on destroyed IndirectLight" }
        val texture = indirectLight.irradianceTexture ?: return null
        return Texture(texture)
    }

    actual fun getNativeObject(): Long {
        val indirectLight = requireNotNull(androidIndirectLight) { "Calling method on destroyed IndirectLight" }
        return indirectLight.nativeObject
    }

    actual internal fun invalidate() {
        androidIndirectLight = null
    }

    actual class Builder {
        private val androidBuilder = AndroidIndirectLight.Builder()

        actual fun reflections(cubemap: Texture): Builder {
            val texture = requireNotNull(cubemap.androidTexture) { "Calling method on destroyed Texture" }
            androidBuilder.reflections(texture)
            return this
        }

        actual fun irradiance(bands: Int, sh: FloatArray): Builder {
            androidBuilder.irradiance(bands, sh)
            return this
        }

        actual fun radiance(bands: Int, sh: FloatArray): Builder {
            androidBuilder.radiance(bands, sh)
            return this
        }

        actual fun irradiance(cubemap: Texture): Builder {
            val texture = requireNotNull(cubemap.androidTexture) { "Calling method on destroyed Texture" }
            androidBuilder.irradiance(texture)
            return this
        }

        actual fun intensity(envIntensity: Float): Builder {
            androidBuilder.intensity(envIntensity)
            return this
        }

        actual fun rotation(rotation: FloatArray): Builder {
            androidBuilder.rotation(rotation)
            return this
        }

        actual fun build(engine: Engine): IndirectLight {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return IndirectLight(androidBuilder.build(androidEngine))
        }
    }

    actual companion object {
        actual fun getDirectionEstimate(sh: FloatArray, direction: FloatArray?): FloatArray {
            return AndroidIndirectLight.getDirectionEstimate(sh, direction)
        }

        actual fun getColorEstimate(colorIntensity: FloatArray?, sh: FloatArray, x: Float, y: Float, z: Float): FloatArray {
            return AndroidIndirectLight.getColorEstimate(colorIntensity, sh, x, y, z)
        }
    }
}

