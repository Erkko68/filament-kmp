package dev.filament.kmp

actual class Skybox {
    actual fun setColor(r: Float, g: Float, b: Float, a: Float) {
    }

    actual fun setColor(color: FloatArray) {
    }

    actual fun setLayerMask(select: Int, values: Int) {
    }

    actual fun getLayerMask(): Int = TODO("Not yet implemented")

    actual fun getIntensity(): Float = TODO("Not yet implemented")

    actual fun getTexture(): Texture? = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }

    actual class Builder {
        actual fun environment(cubemap: Texture): Builder = TODO("Not yet implemented")

        actual fun showSun(show: Boolean): Builder = TODO("Not yet implemented")

        actual fun intensity(envIntensity: Float): Builder = TODO("Not yet implemented")

        actual fun color(r: Float, g: Float, b: Float, a: Float): Builder = TODO("Not yet implemented")

        actual fun color(color: FloatArray): Builder = TODO("Not yet implemented")

        actual fun priority(priority: Int): Builder = TODO("Not yet implemented")

        actual fun build(engine: Engine): Skybox = TODO("Not yet implemented")
    }
}

