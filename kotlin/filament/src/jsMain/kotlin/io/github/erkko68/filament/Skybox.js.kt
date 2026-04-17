package io.github.erkko68.filament

actual class Skybox {
    actual fun setColor(r: Float, g: Float, b: Float, a: Float) {
    }

    actual fun getIntensity(): Float {
        TODO("Not yet implemented")
    }

    actual class Builder {
        actual fun environment(cubemap: Texture): Builder {
            TODO("Not yet implemented")
        }

        actual fun showSun(show: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun intensity(envIntensity: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun color(
            r: Float,
            g: Float,
            b: Float,
            a: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(engine: Engine): Skybox {
            TODO("Not yet implemented")
        }
    }
}