package io.github.erkko68

import kotlin.math.pow
import kotlin.math.sqrt

expect object Colors {
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

    fun toLinear(type: RgbType, r: Float, g: Float, b: Float): FloatArray
    fun toLinear(type: RgbType, rgb: FloatArray): FloatArray
    fun toLinear(type: RgbaType, r: Float, g: Float, b: Float, a: Float): FloatArray
    fun toLinear(type: RgbaType, rgba: FloatArray): FloatArray
    fun toLinear(conversion: Conversion, rgb: FloatArray): FloatArray

    fun cct(temperature: Float): FloatArray
    fun illuminantD(temperature: Float): FloatArray
}
