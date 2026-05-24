package io.github.erkko68.filament

import io.github.erkko68.filament.js.Skybox as JSSkybox
import io.github.erkko68.filament.js.`Skybox_Builder` as JSSkyboxBuilder

actual class Skybox(val jsSkybox: JSSkybox) {
    private var _layerMask = 0xFF

    actual fun setColor(r: Float, g: Float, b: Float, a: Float) {
        jsSkybox.setColor(arrayOf(r, g, b, a) as Array<Number>)
    }

    // jsbindings.cpp binds Skybox::getIntensity; the d.ts misses it. The Builder still
    // can't set intensity (only `priority`, `color`, `environment`, `showSun` are bound),
    // so the value is whatever Filament defaulted it to at construction time.
    actual val intensity: Float get() = (jsSkybox.asDynamic().getIntensity() as Number).toFloat()
    actual val texture: Texture? get() = jsSkybox.getTexture()?.let { Texture(it) }
    actual val layerMask: Int get() = _layerMask

    actual fun setLayerMask(select: Int, value: Int) {
        _layerMask = value
        val jsInst = jsSkybox.asDynamic()
        if (jsInst.setLayerMask != null) jsInst.setLayerMask(select, value)
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
            // intensity not in JS Skybox_Builder bindings
            return this
        }

        actual fun priority(priority: Int): Builder {
            // jsbindings.cpp binds `priority` on Skybox$Builder; the d.ts misses it.
            jsBuilder.asDynamic().priority(priority)
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