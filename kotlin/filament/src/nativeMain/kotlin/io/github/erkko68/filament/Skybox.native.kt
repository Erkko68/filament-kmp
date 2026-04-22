@file:OptIn(ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaSkybox

actual class Skybox(internal var nativeHandle: CPointer<FilaSkybox>?) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaSkyboxBuilder_create()

        actual fun environment(cubemap: Texture): Builder {
            FilaSkyboxBuilder_environment(nativeBuilder, cubemap.nativeHandle)
            return this
        }
        
        actual fun showSun(show: Boolean): Builder {
            FilaSkyboxBuilder_showSun(nativeBuilder, show)
            return this
        }
        
        actual fun intensity(envIntensity: Float): Builder {
            FilaSkyboxBuilder_intensity(nativeBuilder, envIntensity)
            return this
        }
        
        actual fun color(r: Float, g: Float, b: Float, a: Float): Builder {
            FilaSkyboxBuilder_color(nativeBuilder, r, g, b, a)
            return this
        }

        actual fun priority(priority: Int): Builder {
            FilaSkyboxBuilder_priority(nativeBuilder, priority.toUByte())
            return this
        }

        actual fun build(engine: Engine): Skybox {
            val handle = FilaSkyboxBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaSkyboxBuilder_destroy(nativeBuilder)
            return Skybox(handle)
        }
    }

    actual fun setColor(r: Float, g: Float, b: Float, a: Float) {
        FilaSkybox_setColor(nativeHandle, r, g, b, a)
    }
    actual fun getIntensity(): Float = FilaSkybox_getIntensity(nativeHandle)
    actual fun setLayerMask(select: Int, value: Int) =
        FilaSkybox_setLayerMask(nativeHandle, select.toUByte(), value.toUByte())
    actual fun getLayerMask(): Int = FilaSkybox_getLayerMask(nativeHandle).toInt()
    actual fun getTexture(): Texture? = FilaSkybox_getTexture(nativeHandle)?.let { Texture(it) }
}