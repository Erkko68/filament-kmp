package io.github.erkko68.filament

import io.github.erkko68.filament.jni.Colors as JniColors

actual object Colors {
    actual enum class RgbType {
        SRGB, LINEAR;
        internal fun toJni() = JniColors.RgbType.values()[ordinal]
    }

    actual enum class RgbaType {
        SRGB, LINEAR, PREMULTIPLIED_SRGB, PREMULTIPLIED_LINEAR;
        internal fun toJni() = JniColors.RgbaType.values()[ordinal]
    }

    actual enum class Conversion {
        ACCURATE, FAST;
        internal fun toJni() = JniColors.Conversion.values()[ordinal]
    }

    actual fun toLinear(type: RgbType, r: Float, g: Float, b: Float): FloatArray =
        JniColors.toLinear(type.toJni(), r, g, b)

    actual fun toLinear(type: RgbType, rgb: FloatArray): FloatArray =
        JniColors.toLinear(type.toJni(), rgb)

    actual fun toLinear(type: RgbaType, r: Float, g: Float, b: Float, a: Float): FloatArray =
        JniColors.toLinear(type.toJni(), r, g, b, a)

    actual fun toLinear(type: RgbaType, rgba: FloatArray): FloatArray =
        JniColors.toLinear(type.toJni(), rgba)

    actual fun toLinear(conversion: Conversion, rgb: FloatArray): FloatArray =
        JniColors.toLinear(conversion.toJni(), rgb)

    actual fun cct(temperature: Float): FloatArray = JniColors.cct(temperature)

    actual fun illuminantD(temperature: Float): FloatArray = JniColors.illuminantD(temperature)
}
