package io.github.erkko68.filament

import com.google.android.filament.IndirectLight as AndroidIndirectLight

actual class IndirectLight constructor(val nativeIndirectLight: AndroidIndirectLight) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidIndirectLight.Builder()

        actual fun reflections(cubemap: Texture): Builder = apply { nativeBuilder.reflections(cubemap.nativeTexture) }
        actual fun irradiance(bands: Int, sh: FloatArray): Builder = apply { nativeBuilder.irradiance(bands, sh) }
        actual fun radiance(bands: Int, sh: FloatArray): Builder = apply { nativeBuilder.radiance(bands, sh) }
        actual fun irradiance(cubemap: Texture): Builder = apply { nativeBuilder.irradiance(cubemap.nativeTexture) }
        actual fun intensity(envIntensity: Float): Builder = apply { nativeBuilder.intensity(envIntensity) }
        actual fun rotation(rotation: FloatArray): Builder = apply { nativeBuilder.rotation(rotation) }
        actual fun build(engine: Engine): IndirectLight = IndirectLight(nativeBuilder.build(engine.nativeEngine))
    }

    actual var intensity: Float
        get() = nativeIndirectLight.intensity
        set(value) { nativeIndirectLight.intensity = value }

    actual var rotation: FloatArray
        get() = nativeIndirectLight.getRotation(FloatArray(9))
        set(value) { nativeIndirectLight.setRotation(value) }

    actual val reflectionsTexture: Texture? get() = nativeIndirectLight.reflectionsTexture?.let { Texture(it) }
    actual val irradianceTexture: Texture? get() = nativeIndirectLight.irradianceTexture?.let { Texture(it) }

    actual companion object {
        actual fun getDirectionEstimate(sh: FloatArray, out: FloatArray?): FloatArray =
            AndroidIndirectLight.getDirectionEstimate(sh, out)

        actual fun getColorEstimate(sh: FloatArray, x: Double, y: Double, z: Double, out: FloatArray?): FloatArray =
            AndroidIndirectLight.getColorEstimate(out, sh, x.toFloat(), y.toFloat(), z.toFloat())
    }
}
