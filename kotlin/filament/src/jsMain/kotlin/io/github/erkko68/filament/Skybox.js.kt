package io.github.erkko68.filament

import io.github.erkko68.filament.js.Skybox as JSSkybox
import io.github.erkko68.filament.js.`Skybox_Builder` as JSSkyboxBuilder

actual class Skybox(val jsSkybox: JSSkybox) {
    private var _layerMask = 0xFF

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

    actual fun setLayerMask(select: Int, value: Int) {
        _layerMask = value
        val jsInst = jsSkybox.asDynamic()
        if (jsInst.setLayerMask != null) {
            jsInst.setLayerMask(select, value)
        }
    }

    actual fun getLayerMask(): Int {
        return _layerMask
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
            // intensity not in JS Skybox_Builder bindings
            return this
        }

        actual fun priority(priority: Int): Builder {
            // priority not in JS Skybox_Builder bindings
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