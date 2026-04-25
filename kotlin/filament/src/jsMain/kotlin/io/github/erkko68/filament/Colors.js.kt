package io.github.erkko68.filament

import kotlin.math.pow

actual object Colors {
    actual fun toLinear(
        type: RgbType,
        r: Float,
        g: Float,
        b: Float
    ): FloatArray {
        return if (type == RgbType.SRGB) {
            floatArrayOf(sRGBToLinear(r), sRGBToLinear(g), sRGBToLinear(b))
        } else {
            floatArrayOf(r, g, b)
        }
    }

    actual fun toLinear(type: RgbType, rgb: FloatArray): FloatArray {
        return toLinear(type, rgb[0], rgb[1], rgb[2])
    }

    actual fun toLinear(
        type: RgbaType,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ): FloatArray {
        return when (type) {
            RgbaType.SRGB -> floatArrayOf(sRGBToLinear(r), sRGBToLinear(g), sRGBToLinear(b), a)
            RgbaType.LINEAR -> floatArrayOf(r, g, b, a)
            RgbaType.PREMULTIPLIED_SRGB -> {
                val lr = sRGBToLinear(r)
                val lg = sRGBToLinear(g)
                val lb = sRGBToLinear(b)
                floatArrayOf(lr * a, lg * a, lb * a, a)
            }
            RgbaType.PREMULTIPLIED_LINEAR -> floatArrayOf(r * a, g * a, b * a, a)
        }
    }

    actual fun toLinear(type: RgbaType, rgba: FloatArray): FloatArray {
        return toLinear(type, rgba[0], rgba[1], rgba[2], rgba[3])
    }

    actual fun toLinear(
        conversion: Conversion,
        rgb: FloatArray
    ): FloatArray {
        return toLinear(RgbType.SRGB, rgb[0], rgb[1], rgb[2])
    }

    actual fun cct(temperature: Float): FloatArray {
        // Simple approximation for CCT to RGB (Kass-Barten) or stub
        // Standard Filament implementation is complex, we'll return white for now
        return floatArrayOf(1.0f, 1.0f, 1.0f)
    }

    actual fun illuminantD(temperature: Float): FloatArray {
        return floatArrayOf(1.0f, 1.0f, 1.0f)
    }

    private fun sRGBToLinear(x: Float): Float {
        return if (x <= 0.04045f) x / 12.92f else ((x + 0.055f) / 1.055f).pow(2.4f)
    }

    actual enum class RgbType { SRGB, LINEAR }
    actual enum class RgbaType { SRGB, LINEAR, PREMULTIPLIED_SRGB, PREMULTIPLIED_LINEAR }
    actual enum class Conversion { ACCURATE, FAST }
}