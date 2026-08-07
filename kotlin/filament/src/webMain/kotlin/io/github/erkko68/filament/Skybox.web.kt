package io.github.erkko68.filament

import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsNumbers

import io.github.erkko68.filament.web.Skybox as JSSkybox
import io.github.erkko68.filament.web.`Skybox_Builder` as JSSkyboxBuilder

actual class Skybox(val jsSkybox: JSSkybox) {
    actual fun setColor(r: Float, g: Float, b: Float, a: Float) {
        jsSkybox.setColor(jsNumbers(r, g, b, a))
    }

    actual val intensity: Float get() = jsSkybox.getIntensity().toFloat()
    actual val texture: Texture? get() = jsSkybox.getTexture()?.let { Texture(it) }
    actual val layerMask: Int get() = jsSkybox.getLayerMask().toInt()

    actual fun setLayerMask(select: Int, value: Int) {
        jsSkybox.setLayerMask(select.toDouble(), value.toDouble())
    }

    actual class Builder {
        private val jsBuilder = JSSkybox.Builder()

        actual fun environment(cubemap: Texture): Builder {
            jsBuilder.environment(cubemap.jsTexture)
            return this
        }

        actual fun showSun(show: Boolean): Builder {
            jsBuilder.showSun(show)
            return this
        }

        actual fun intensity(envIntensity: Float): Builder {
            jsBuilder.intensity(envIntensity.toDouble())
            return this
        }

        actual fun priority(priority: Int): Builder {
            jsBuilder.priority(priority.toDouble())
            return this
        }

        actual fun color(
            r: Float,
            g: Float,
            b: Float,
            a: Float
        ): Builder {
            jsBuilder.color(jsNumbers(r, g, b, a))
            return this
        }

        actual fun build(engine: Engine): Skybox {
            return Skybox(jsBuilder.build(engine.jsEngine))
        }
    }
}