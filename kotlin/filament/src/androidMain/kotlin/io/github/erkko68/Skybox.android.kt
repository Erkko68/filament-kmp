package io.github.erkko68

import com.google.android.filament.Skybox as AndroidSkybox

actual class Skybox internal constructor(val nativeSkybox: AndroidSkybox) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidSkybox.Builder()

        actual fun environment(cubemap: Texture): Builder {
            nativeBuilder.environment(cubemap.nativeTexture)
            return this
        }
        actual fun showSun(show: Boolean): Builder {
            nativeBuilder.showSun(show)
            return this
        }
        actual fun intensity(envIntensity: Float): Builder {
            nativeBuilder.intensity(envIntensity)
            return this
        }
        actual fun color(r: Float, g: Float, b: Float, a: Float): Builder {
            nativeBuilder.color(r, g, b, a)
            return this
        }
        actual fun build(engine: Engine): Skybox = Skybox(nativeBuilder.build(engine.nativeEngine))
    }

    actual fun setColor(r: Float, g: Float, b: Float, a: Float) {
        nativeSkybox.setColor(r, g, b, a)
    }
    actual fun getIntensity(): Float = nativeSkybox.intensity
}
