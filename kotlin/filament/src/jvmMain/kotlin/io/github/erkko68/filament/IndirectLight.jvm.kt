package io.github.erkko68.filament

import io.github.erkko68.filament.jni.IndirectLight as JniIndirectLight

actual class IndirectLight(val nativeIndirectLight: JniIndirectLight) {
    actual class Builder actual constructor() {
        private val jni = JniIndirectLight.Builder()

        actual fun reflections(cubemap: Texture): Builder {
            jni.reflections(cubemap.nativeTexture)
            return this
        }

        actual fun irradiance(bands: Int, sh: FloatArray): Builder {
            jni.irradiance(bands, sh)
            return this
        }

        actual fun radiance(bands: Int, sh: FloatArray): Builder {
            jni.radiance(bands, sh)
            return this
        }

        actual fun irradiance(cubemap: Texture): Builder {
            jni.irradiance(cubemap.nativeTexture)
            return this
        }

        actual fun intensity(envIntensity: Float): Builder {
            jni.intensity(envIntensity)
            return this
        }

        actual fun rotation(rotation: FloatArray): Builder {
            jni.rotation(rotation)
            return this
        }

        actual fun build(engine: Engine): IndirectLight =
            IndirectLight(jni.build(engine.nativeEngine))
    }

    actual var intensity: Float
        get() = nativeIndirectLight.intensity
        set(value) { nativeIndirectLight.setIntensity(value) }

    actual var rotation: FloatArray
        get() = nativeIndirectLight.getRotation(FloatArray(9))
        set(value) { nativeIndirectLight.setRotation(value) }

    actual val reflectionsTexture: Texture? get() = nativeIndirectLight.reflectionsTexture?.let { Texture(it) }
    actual val irradianceTexture: Texture? get() = nativeIndirectLight.irradianceTexture?.let { Texture(it) }

    actual companion object {
        actual fun getDirectionEstimate(sh: FloatArray, out: FloatArray?): FloatArray {
            val result = out ?: FloatArray(3)
            // sh is interleaved RGB: [L00_r,g,b, L1-1_r,g,b, L10_r,g,b, L11_r,g,b, ...]
            // Dominant direction ~ -normalize(float3(L11.r, L1-1.r, L10.r))
            val x = -sh[9]; val y = -sh[3]; val z = -sh[6]
            val len = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            if (len > 1e-6f) { result[0] = x / len; result[1] = y / len; result[2] = z / len }
            return result
        }

        actual fun getColorEstimate(sh: FloatArray, x: Double, y: Double, z: Double, out: FloatArray?): FloatArray {
            val result = out ?: FloatArray(4)
            val xf = x.toFloat(); val yf = y.toFloat(); val zf = z.toFloat()
            val PI = kotlin.math.PI.toFloat()
            // SH basis functions at (xf, yf, zf)
            val b = floatArrayOf(
                0.282095f,
                0.488603f * yf,
                0.488603f * zf,
                0.488603f * xf,
                1.092548f * xf * yf,
                1.092548f * yf * zf,
                0.315392f * (3f * zf * zf - 1f),
                1.092548f * xf * zf,
                0.546274f * (xf * xf - yf * yf)
            )
            // Lambertian irradiance convolution factors per SH band
            val c = floatArrayOf(PI, 2f*PI/3f, 2f*PI/3f, 2f*PI/3f,
                PI/4f, PI/4f, PI/4f, PI/4f, PI/4f)
            for (ch in 0..2) {
                var sum = 0f
                for (i in 0..8) sum += sh[i * 3 + ch] * b[i] * c[i]
                result[ch] = maxOf(0f, sum)
            }
            result[3] = 1f
            return result
        }
    }
}
