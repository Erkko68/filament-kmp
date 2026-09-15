package io.github.erkko68.filament

import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsNumbers

import io.github.erkko68.filament.web.Skybox as JSSkybox
import io.github.erkko68.filament.web.`Skybox_Builder` as JSSkyboxBuilder

actual class Skybox @InternalFilamentApi constructor(internal val jsSkybox: JSSkybox, private val builderIntensity: Float? = null) {
    actual fun setColor(r: Float, g: Float, b: Float, a: Float) {
        jsSkybox.setColor(jsNumbers(r, g, b, a))
    }

    // Skybox$Builder doesn't bind `intensity` (only priority/color/environment/
    // showSun), and Skybox doesn't bind `setIntensity` — so the Builder's
    // requested intensity can't reach native. Echo it back here when set;
    // otherwise fall through to whatever Filament defaulted to.
    actual val intensity: Float get() = builderIntensity ?: jsSkybox.getIntensity().toFloat()
    actual val texture: Texture? get() = jsSkybox.getTexture()?.let { Texture(it) }
    actual val layerMask: Int get() = jsSkybox.getLayerMask().toInt()

    actual fun setLayerMask(select: Int, value: Int) {
        jsSkybox.setLayerMask(select.toDouble(), value.toDouble())
    }

    actual class Builder {
        private val jsBuilder = JSSkybox.Builder()
        private var builderIntensity: Float? = null

        actual fun environment(cubemap: Texture): Builder {
            jsBuilder.environment(cubemap.jsTexture)
            return this
        }

        actual fun showSun(show: Boolean): Builder {
            jsBuilder.showSun(show)
            return this
        }

        actual fun intensity(envIntensity: Float): Builder {
            builderIntensity = envIntensity
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
            return Skybox(jsBuilder.build(engine.jsEngine), builderIntensity)
        }
    }
}