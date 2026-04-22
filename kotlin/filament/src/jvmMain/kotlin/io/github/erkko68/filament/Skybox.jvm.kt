package io.github.erkko68.filament

import io.github.erkko68.filament.jni.Skybox as JniSkybox

actual class Skybox(val nativeSkybox: JniSkybox) {
    actual class Builder actual constructor() {
        private val jni = JniSkybox.Builder()

        actual fun environment(cubemap: Texture): Builder {
            jni.environment(cubemap.nativeTexture)
            return this
        }

        actual fun showSun(show: Boolean): Builder {
            jni.showSun(show)
            return this
        }

        actual fun intensity(envIntensity: Float): Builder {
            jni.intensity(envIntensity)
            return this
        }

        actual fun color(r: Float, g: Float, b: Float, a: Float): Builder {
            jni.color(r, g, b, a)
            return this
        }

        actual fun priority(priority: Int): Builder {
            jni.priority(priority)
            return this
        }

        actual fun build(engine: Engine): Skybox =
            Skybox(jni.build(engine.nativeEngine))
    }

    actual fun setColor(r: Float, g: Float, b: Float, a: Float) { nativeSkybox.setColor(r, g, b, a) }
    actual fun getIntensity(): Float = nativeSkybox.intensity
    actual fun setLayerMask(select: Int, value: Int) = nativeSkybox.setLayerMask(select, value)
    actual fun getLayerMask(): Int = nativeSkybox.getLayerMask()
    actual fun getTexture(): Texture? = nativeSkybox.getTexture()?.let { Texture(it) }
}
