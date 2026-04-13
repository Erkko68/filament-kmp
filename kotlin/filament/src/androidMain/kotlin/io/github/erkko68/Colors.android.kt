package io.github.erkko68

import com.google.android.filament.Colors as AndroidColors

actual object Colors {
    actual enum class RgbType {
        SRGB, LINEAR;
        internal fun toAndroid() = AndroidColors.RgbType.values()[ordinal]
    }

    actual enum class RgbaType {
        SRGB, LINEAR, PREMULTIPLIED_SRGB, PREMULTIPLIED_LINEAR;
        internal fun toAndroid() = AndroidColors.RgbaType.values()[ordinal]
    }

    actual enum class Conversion {
        ACCURATE, FAST;
        internal fun toAndroid() = AndroidColors.Conversion.values()[ordinal]
    }

    actual fun toLinear(type: RgbType, r: Float, g: Float, b: Float): FloatArray =
        AndroidColors.toLinear(type.toAndroid(), r, g, b)

    actual fun toLinear(type: RgbType, rgb: FloatArray): FloatArray =
        AndroidColors.toLinear(type.toAndroid(), rgb)

    actual fun toLinear(type: RgbaType, r: Float, g: Float, b: Float, a: Float): FloatArray =
        AndroidColors.toLinear(type.toAndroid(), r, g, b, a)

    actual fun toLinear(type: RgbaType, rgba: FloatArray): FloatArray =
        AndroidColors.toLinear(type.toAndroid(), rgba)

    actual fun toLinear(conversion: Conversion, rgb: FloatArray): FloatArray =
        AndroidColors.toLinear(conversion.toAndroid(), rgb)

    actual fun cct(temperature: Float): FloatArray = AndroidColors.cct(temperature)

    actual fun illuminantD(temperature: Float): FloatArray = AndroidColors.illuminantD(temperature)
}
