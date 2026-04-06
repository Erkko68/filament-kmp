package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*
import kotlin.math.pow
import kotlin.math.sqrt

actual class Colors {
    actual enum class RgbType {
        SRGB, LINEAR
    }

    actual enum class RgbaType {
        SRGB, LINEAR, PREMULTIPLIED_SRGB, PREMULTIPLIED_LINEAR
    }

    actual enum class Conversion {
        ACCURATE, FAST
    }

    actual companion object {
        actual fun toLinear(type: RgbType, r: Float, g: Float, b: Float): FloatArray =
            toLinear(type, floatArrayOf(r, g, b))

        actual fun toLinear(type: RgbType, rgb: FloatArray): FloatArray =
            if (type == RgbType.LINEAR) rgb else toLinear(Conversion.ACCURATE, rgb)

        actual fun toLinear(type: RgbaType, r: Float, g: Float, b: Float, a: Float): FloatArray =
            toLinear(type, floatArrayOf(r, g, b, a))

        actual fun toLinear(type: RgbaType, rgba: FloatArray): FloatArray {
            when (type) {
                RgbaType.SRGB -> {
                    toLinear(Conversion.ACCURATE, rgba)
                    val a = rgba[3]
                    rgba[0] *= a; rgba[1] *= a; rgba[2] *= a
                }
                RgbaType.LINEAR -> {
                    val a = rgba[3]
                    rgba[0] *= a; rgba[1] *= a; rgba[2] *= a
                }
                RgbaType.PREMULTIPLIED_SRGB -> toLinear(Conversion.ACCURATE, rgba)
                RgbaType.PREMULTIPLIED_LINEAR -> {}
            }
            return rgba
        }

        actual fun toLinear(conversion: Conversion, rgb: FloatArray): FloatArray {
            when (conversion) {
                Conversion.ACCURATE -> {
                    for (i in 0 until 3) {
                        rgb[i] = if (rgb[i] <= 0.04045f) rgb[i] / 12.92f else ((rgb[i] + 0.055f) / 1.055f).pow(2.4f)
                    }
                }
                Conversion.FAST -> {
                    for (i in 0 until 3) {
                        rgb[i] = sqrt(rgb[i]) // Matching Colors.java exactly despite it being weird
                    }
                }
            }
            return rgb
        }

        actual fun cct(temperature: Float): FloatArray {
            val color = FloatArray(3)
            memScoped {
                FilaColors_cct(temperature, color.toCValues().ptr)
            }
            return color
        }

        actual fun illuminantD(temperature: Float): FloatArray {
            val color = FloatArray(3)
            memScoped {
                FilaColors_illuminantD(temperature, color.toCValues().ptr)
            }
            return color
        }
    }
}
