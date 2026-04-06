package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class Skybox internal constructor(
    internal val nativeObject: CPointer<FilaSkybox>
) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaSkyboxBuilder_create()!!

        actual fun environment(texture: Texture): Builder {
            FilaSkyboxBuilder_environment(nativeBuilder, texture.nativeObject)
            return this
        }

        actual fun showSun(show: Boolean): Builder {
            FilaSkyboxBuilder_showSun(nativeBuilder, show)
            return this
        }

        actual fun intensity(intensity: Float): Builder {
            FilaSkyboxBuilder_intensity(nativeBuilder, intensity)
            return this
        }

        actual fun color(r: Float, g: Float, b: Float, a: Float): Builder {
            FilaSkyboxBuilder_color(nativeBuilder, r, g, b, a)
            return this
        }

        actual fun color(color: FloatArray): Builder {
            FilaSkyboxBuilder_color(nativeBuilder, color[0], color[1], color[2], color[3])
            return this
        }

        actual fun priority(priority: Int): Builder {
            FilaSkyboxBuilder_priority(nativeBuilder, priority.toUByte())
            return this
        }

        actual fun build(engine: Engine): Skybox {
            val nativeSkybox = FilaSkyboxBuilder_build(nativeBuilder, engine.nativeObject)
                ?: throw IllegalStateException("Couldn't create Skybox")
            FilaSkyboxBuilder_destroy(nativeBuilder)
            return Skybox(nativeSkybox)
        }
    }

    actual fun setLayerMask(select: Int, value: Int) {
        FilaSkybox_setLayerMask(nativeObject, select.toUByte(), value.toUByte())
    }

    actual fun getLayerMask(): Int = FilaSkybox_getLayerMask(nativeObject).toInt()
    actual fun getIntensity(): Float = FilaSkybox_getIntensity(nativeObject)

    actual fun setColor(r: Float, g: Float, b: Float, a: Float) {
        FilaSkybox_setColor(nativeObject, r, g, b, a)
    }

    actual val texture: Texture?
        get() {
            val nativeTexture = FilaSkybox_getTexture(nativeObject) ?: return null
            return Texture(nativeTexture)
        }
}
