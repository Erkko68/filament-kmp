package dev.filament.kmp

import com.google.android.filament.Skybox as AndroidSkybox

actual class Skybox internal constructor(
    internal var androidSkybox: AndroidSkybox?,
) {
    actual fun setColor(r: Float, g: Float, b: Float, a: Float) {
        val skybox = requireNotNull(androidSkybox) { "Calling method on destroyed Skybox" }
        skybox.setColor(r, g, b, a)
    }

    actual fun setColor(color: FloatArray) {
        val skybox = requireNotNull(androidSkybox) { "Calling method on destroyed Skybox" }
        skybox.setColor(color)
    }

    actual fun setLayerMask(select: Int, values: Int) {
        val skybox = requireNotNull(androidSkybox) { "Calling method on destroyed Skybox" }
        skybox.setLayerMask(select, values)
    }

    actual fun getLayerMask(): Int {
        val skybox = requireNotNull(androidSkybox) { "Calling method on destroyed Skybox" }
        return skybox.layerMask
    }

    actual fun getIntensity(): Float {
        val skybox = requireNotNull(androidSkybox) { "Calling method on destroyed Skybox" }
        return skybox.intensity
    }

    actual fun getTexture(): Texture? {
        val skybox = requireNotNull(androidSkybox) { "Calling method on destroyed Skybox" }
        val texture = skybox.texture ?: return null
        return Texture(texture)
    }

    actual fun getNativeObject(): Long {
        val skybox = requireNotNull(androidSkybox) { "Calling method on destroyed Skybox" }
        return skybox.nativeObject
    }

    actual internal fun invalidate() {
        androidSkybox = null
    }

    actual class Builder {
        private val androidBuilder = AndroidSkybox.Builder()

        actual fun environment(cubemap: Texture): Builder {
            val texture = requireNotNull(cubemap.androidTexture) { "Calling method on destroyed Texture" }
            androidBuilder.environment(texture)
            return this
        }

        actual fun showSun(show: Boolean): Builder {
            androidBuilder.showSun(show)
            return this
        }

        actual fun intensity(envIntensity: Float): Builder {
            androidBuilder.intensity(envIntensity)
            return this
        }

        actual fun color(r: Float, g: Float, b: Float, a: Float): Builder {
            androidBuilder.color(r, g, b, a)
            return this
        }

        actual fun color(color: FloatArray): Builder {
            androidBuilder.color(color)
            return this
        }

        actual fun priority(priority: Int): Builder {
            androidBuilder.priority(priority)
            return this
        }

        actual fun build(engine: Engine): Skybox {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return Skybox(androidBuilder.build(androidEngine))
        }
    }
}

