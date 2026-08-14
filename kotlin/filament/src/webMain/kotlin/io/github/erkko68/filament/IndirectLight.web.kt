package io.github.erkko68.filament

import io.github.erkko68.filament.web.interop.toFloatArray

import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsNumbers

import io.github.erkko68.filament.web.IndirectLight as JSIndirectLight
import io.github.erkko68.filament.web.`IndirectLight_Builder` as JSIndirectLightBuilder

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class IndirectLight(val jsIndirectLight: JSIndirectLight) {
    actual var intensity: Float
        get() = jsIndirectLight.getIntensity().toFloat()
        set(value) { jsIndirectLight.setIntensity(value.toDouble()) }

    actual var rotation: FloatArray
        get() {
            val result = FloatArray(9)
            val jsVec = jsIndirectLight.getRotation()
            if (jsVec != null) {
                val arr = jsVec.toFloatArray(9)
                for (i in 0 until 9) result[i] = arr[i]
            }
            return result
        }
        set(value) { jsIndirectLight.setRotation(value.toJsNumbers()) }

    actual val reflectionsTexture: Texture? get() = jsIndirectLight.getReflectionsTexture()?.let { Texture(it) }
    actual val irradianceTexture: Texture? get() = jsIndirectLight.getIrradianceTexture()?.let { Texture(it) }

    actual class Builder {
        private val jsBuilder = JSIndirectLight.Builder()

        actual fun reflections(cubemap: Texture): Builder {
            jsBuilder.reflections(cubemap.jsTexture)
            return this
        }

        actual fun irradiance(
            bands: Int,
            sh: FloatArray
        ): Builder {
            jsBuilder.irradiance(bands.toDouble(), sh.toJsNumbers())
            return this
        }

        actual fun irradiance(cubemap: Texture): Builder {
            jsBuilder.irradiance(cubemap.jsTexture)
            return this
        }

        actual fun radiance(
            bands: Int,
            sh: FloatArray
        ): Builder {
            jsBuilder.radiance(bands.toDouble(), sh.toJsNumbers())
            return this
        }

        actual fun intensity(envIntensity: Float): Builder {
            jsBuilder.intensity(envIntensity.toDouble())
            return this
        }

        actual fun rotation(rotation: FloatArray): Builder {
            jsBuilder.rotation(rotation.toJsNumbers())
            return this
        }

        actual fun build(engine: Engine): IndirectLight {
            return IndirectLight(jsBuilder.build(engine.jsEngine))
        }
    }

    actual companion object {
        actual fun getDirectionEstimate(sh: FloatArray, out: FloatArray?): FloatArray {
            val result = out ?: FloatArray(3)
            val res = JSIndirectLight.getDirectionEstimate(sh.toJsNumbers())
            if (res != null) {
                val arr = res.toFloatArray(3)
                for (i in 0 until 3) result[i] = arr[i]
            }
            return result
        }

        actual fun getColorEstimate(
            sh: FloatArray,
            x: Double,
            y: Double,
            z: Double,
            out: FloatArray?
        ): FloatArray {
            val result = out ?: FloatArray(4)
            val res = JSIndirectLight.getColorEstimate(sh.toJsNumbers(), jsNumbers(x, y, z))
            if (res != null) {
                val arr = res.toFloatArray(4)
                for (i in 0 until 4) result[i] = arr[i]
            }
            return result
        }
    }
}