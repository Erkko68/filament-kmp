package io.github.erkko68.filament

import io.github.erkko68.filament.js.Skybox as JSSkybox
import io.github.erkko68.filament.js.`Skybox_Builder` as JSSkyboxBuilder

actual class Skybox(internal val jsSkybox: JSSkybox) {
    actual fun setColor(r: Float, g: Float, b: Float, a: Float) {
        jsSkybox.setColor(arrayOf(r, g, b, a) as Array<Number>)
    }

    actual fun getIntensity(): Float {
        // Skybox intensity is typically handled in the builder, no direct getter in JS
        return 1.0f
    }

    actual fun getTexture(): Texture? {
        val jsTex = jsSkybox.getTexture()
        return if (jsTex != null) Texture(jsTex) else null
    }

    actual class Builder {
        private val jsBuilder = JSSkyboxBuilder()

        actual fun environment(cubemap: Texture): Builder {
            jsBuilder.environment(cubemap.jsTexture)
            return this
        }

        actual fun showSun(show: Boolean): Builder {
            jsBuilder.showSun(show)
            return this
        }

        actual fun intensity(envIntensity: Float): Builder {
            jsBuilder.intensity(envIntensity)
            return this
        }

        actual fun color(
            r: Float,
            g: Float,
            b: Float,
            a: Float
        ): Builder {
            jsBuilder.color(arrayOf(r, g, b, a) as Array<Number>)
            return this
        }

        actual fun build(engine: Engine): Skybox {
            return Skybox(jsBuilder.build(engine.jsEngine))
        }
    }
}