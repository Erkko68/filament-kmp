@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*

actual object Colors {
    actual enum class RgbType {
        SRGB, LINEAR;
        internal fun toNative(): UInt = ordinal.toUInt()
    }

    actual enum class RgbaType {
        SRGB, LINEAR, PREMULTIPLIED_SRGB, PREMULTIPLIED_LINEAR;
        internal fun toNative(): UInt = ordinal.toUInt()
    }

    actual enum class Conversion {
        ACCURATE, FAST;
        internal fun toNative(): UInt = ordinal.toUInt()
    }

    actual fun toLinear(type: RgbType, r: Float, g: Float, b: Float): FloatArray {
        return toLinear(type, floatArrayOf(r, g, b))
    }

    actual fun toLinear(type: RgbType, rgb: FloatArray): FloatArray {
        memScoped {
            val inRgb = allocArray<FloatVar>(3)
            val outRgb = allocArray<FloatVar>(3)
            for (i in 0 until 3) inRgb[i] = rgb[i]
            FilaColors_toLinearRgb(type.toNative(), inRgb, outRgb)
            for (i in 0 until 3) rgb[i] = outRgb[i]
        }
        return rgb
    }

    actual fun toLinear(type: RgbaType, r: Float, g: Float, b: Float, a: Float): FloatArray {
        return toLinear(type, floatArrayOf(r, g, b, a))
    }

    actual fun toLinear(type: RgbaType, rgba: FloatArray): FloatArray {
        memScoped {
            val inRgba = allocArray<FloatVar>(4)
            val outRgba = allocArray<FloatVar>(4)
            for (i in 0 until 4) inRgba[i] = rgba[i]
            FilaColors_toLinearRgba(type.toNative(), inRgba, outRgba)
            for (i in 0 until 4) rgba[i] = outRgba[i]
        }
        return rgba
    }

    actual fun toLinear(conversion: Conversion, rgb: FloatArray): FloatArray {
        memScoped {
            val inRgb = allocArray<FloatVar>(3)
            val outRgb = allocArray<FloatVar>(3)
            for (i in 0 until 3) inRgb[i] = rgb[i]
            FilaColors_toLinearConvert(conversion.toNative(), inRgb, outRgb)
            for (i in 0 until 3) rgb[i] = outRgb[i]
        }
        return rgb
    }

    actual fun cct(temperature: Float): FloatArray {
        val color = FloatArray(3)
        memScoped {
            val outColor = allocArray<FloatVar>(3)
            FilaColors_cct(temperature, outColor)
            for (i in 0 until 3) color[i] = outColor[i]
        }
        return color
    }

    actual fun illuminantD(temperature: Float): FloatArray {
        val color = FloatArray(3)
        memScoped {
            val outColor = allocArray<FloatVar>(3)
            FilaColors_illuminantD(temperature, outColor)
            for (i in 0 until 3) color[i] = outColor[i]
        }
        return color
    }
}
