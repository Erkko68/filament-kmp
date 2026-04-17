package io.github.erkko68.filament

actual class IndirectLight {
    actual fun setIntensity(intensity: Float) {
    }

    actual fun getIntensity(): Float {
        TODO("Not yet implemented")
    }

    actual fun setRotation(rotation: FloatArray) {
    }

    actual fun getRotation(out: FloatArray?): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getReflectionsTexture(): Texture? {
        TODO("Not yet implemented")
    }

    actual fun getIrradianceTexture(): Texture? {
        TODO("Not yet implemented")
    }

    actual class Builder {
        actual fun reflections(cubemap: Texture): Builder {
            TODO("Not yet implemented")
        }

        actual fun irradiance(
            bands: Int,
            sh: FloatArray
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun irradiance(cubemap: Texture): Builder {
            TODO("Not yet implemented")
        }

        actual fun radiance(
            bands: Int,
            sh: FloatArray
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun intensity(envIntensity: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun rotation(rotation: FloatArray): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(engine: Engine): IndirectLight {
            TODO("Not yet implemented")
        }
    }

    actual companion object {
        actual fun getDirectionEstimate(sh: FloatArray, out: FloatArray?): FloatArray {
            TODO("Not yet implemented")
        }

        actual fun getColorEstimate(
            sh: FloatArray,
            x: Double,
            y: Double,
            z: Double,
            out: FloatArray?
        ): FloatArray {
            TODO("Not yet implemented")
        }
    }
}