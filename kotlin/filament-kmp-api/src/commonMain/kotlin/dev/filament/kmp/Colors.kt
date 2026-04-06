package dev.filament.kmp

import kotlin.math.pow
import kotlin.math.sqrt

object Colors {
    enum class RgbType {
        SRGB,
        LINEAR
    }

    enum class RgbaType {
        SRGB,
        LINEAR,
        PREMULTIPLIED_SRGB,
        PREMULTIPLIED_LINEAR
    }

    enum class Conversion {
        ACCURATE,
        FAST
    }

    fun toLinear(type: RgbType, r: Float, g: Float, b: Float): FloatArray {
        return if (type == RgbType.LINEAR) floatArrayOf(r, g, b) else toLinear(Conversion.ACCURATE, floatArrayOf(r, g, b))
    }

    fun toLinear(type: RgbaType, r: Float, g: Float, b: Float, a: Float): FloatArray {
        val rgba = floatArrayOf(r, g, b, a)
        return when (type) {
            RgbaType.SRGB -> {
                toLinear(Conversion.ACCURATE, rgba)
                rgba[0] *= a; rgba[1] *= a; rgba[2] *= a
                rgba
            }
            RgbaType.LINEAR -> {
                rgba[0] *= a; rgba[1] *= a; rgba[2] *= a
                rgba
            }
            RgbaType.PREMULTIPLIED_SRGB -> toLinear(Conversion.ACCURATE, rgba)
            RgbaType.PREMULTIPLIED_LINEAR -> rgba
        }
    }

    private fun toLinear(conversion: Conversion, rgb: FloatArray): FloatArray {
        when (conversion) {
            Conversion.ACCURATE -> {
                for (i in 0 until 3) {
                    rgb[i] = if (rgb[i] <= 0.04045f) {
                        rgb[i] / 12.92f
                    } else {
                        ((rgb[i] + 0.055f) / 1.055f).pow(2.4f)
                    }
                }
            }
            Conversion.FAST -> {
                for (i in 0 until 3) {
                    rgb[i] = sqrt(rgb[i])
                }
            }
        }
        return rgb
    }
}
