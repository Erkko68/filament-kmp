package io.github.erkko68.filament

import io.github.erkko68.filament.js.IndirectLight as JSIndirectLight
import io.github.erkko68.filament.js.`IndirectLight_Builder` as JSIndirectLightBuilder

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class IndirectLight(val jsIndirectLight: JSIndirectLight) {
    actual fun setIntensity(intensity: Float) {
        jsIndirectLight.setIntensity(intensity)
    }

    actual fun getIntensity(): Float {
        return jsIndirectLight.getIntensity().toFloat()
    }

    actual fun setRotation(rotation: FloatArray) {
        jsIndirectLight.setRotation(rotation.toTypedArray() as Array<Number>)
    }

    actual fun getRotation(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(9)
        val jsVec = jsIndirectLight.getRotation()
        if (jsVec != null) {
            val arr = jsVec.unsafeCast<Array<Double>>()
            for (i in 0 until 9.coerceAtMost(arr.size)) result[i] = arr[i].toFloat()
        }
        return result
    }

    actual fun getReflectionsTexture(): Texture? {
        val jsTex = jsIndirectLight.getReflectionsTexture()
        return if (jsTex != null) Texture(jsTex) else null
    }

    actual fun getIrradianceTexture(): Texture? {
        val jsTex = jsIndirectLight.getIrradianceTexture()
        return if (jsTex != null) Texture(jsTex) else null
    }

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
            jsBuilder.irradianceSh(bands, sh.toTypedArray() as Array<Number>)
            return this
        }

        actual fun irradiance(cubemap: Texture): Builder {
            jsBuilder.irradianceTex(cubemap.jsTexture)
            return this
        }

        actual fun radiance(
            bands: Int,
            sh: FloatArray
        ): Builder {
            // JS bindings don't seem to have radiance, mapping to irradiance
            jsBuilder.irradianceSh(bands, sh.toTypedArray() as Array<Number>)
            return this
        }

        actual fun intensity(envIntensity: Float): Builder {
            jsBuilder.intensity(envIntensity)
            return this
        }

        actual fun rotation(rotation: FloatArray): Builder {
            jsBuilder.rotation(rotation.toTypedArray() as Array<Number>)
            return this
        }

        actual fun build(engine: Engine): IndirectLight {
            return IndirectLight(jsBuilder.build(engine.jsEngine))
        }
    }

    actual companion object {
        actual fun getDirectionEstimate(sh: FloatArray, out: FloatArray?): FloatArray {
            val result = out ?: FloatArray(3)
            val res = JSIndirectLight.getDirectionEstimate(sh.toTypedArray() as Array<Number>)
            if (res != null) {
                val arr = res.unsafeCast<Array<Double>>()
                for (i in 0 until 3.coerceAtMost(arr.size)) result[i] = arr[i].toFloat()
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
            val res = JSIndirectLight.getColorEstimate(sh.toTypedArray() as Array<Number>, arrayOf(x, y, z) as Array<Number>)
            if (res != null) {
                val arr = res.unsafeCast<Array<Double>>()
                for (i in 0 until 4.coerceAtMost(arr.size)) result[i] = arr[i].toFloat()
            }
            return result
        }
    }
}