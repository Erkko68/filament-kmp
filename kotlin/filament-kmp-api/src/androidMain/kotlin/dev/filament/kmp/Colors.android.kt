package dev.filament.kmp

import com.google.android.filament.Colors as AndroidColors

actual object Colors {

    actual enum class RgbType {
        SRGB,
        LINEAR,
    }

    actual enum class RgbaType {
        SRGB,
        LINEAR,
        PREMULTIPLIED_SRGB,
        PREMULTIPLIED_LINEAR,
    }

    actual enum class Conversion {
        ACCURATE,
        FAST,
    }

    @LinearColor
    actual fun toLinear(type: RgbType, r: Float, g: Float, b: Float): FloatArray {
        return AndroidColors.toLinear(type.toAndroid(), r, g, b)
    }

    @LinearColor
    actual fun toLinear(type: RgbType, rgb: FloatArray): FloatArray {
        return AndroidColors.toLinear(type.toAndroid(), rgb)
    }

    @LinearColor
    actual fun toLinear(type: RgbaType, r: Float, g: Float, b: Float, a: Float): FloatArray {
        return AndroidColors.toLinear(type.toAndroid(), r, g, b, a)
    }

    @LinearColor
    actual fun toLinear(type: RgbaType, rgba: FloatArray): FloatArray {
        return AndroidColors.toLinear(type.toAndroid(), rgba)
    }

    @LinearColor
    actual fun toLinear(conversion: Conversion, rgb: FloatArray): FloatArray {
        return AndroidColors.toLinear(conversion.toAndroid(), rgb)
    }

    @LinearColor
    actual fun cct(temperature: Float): FloatArray {
        return AndroidColors.cct(temperature)
    }

    @LinearColor
    actual fun illuminantD(temperature: Float): FloatArray {
        return AndroidColors.illuminantD(temperature)
    }
}

private fun Colors.RgbType.toAndroid(): AndroidColors.RgbType = when (this) {
    Colors.RgbType.SRGB -> AndroidColors.RgbType.SRGB
    Colors.RgbType.LINEAR -> AndroidColors.RgbType.LINEAR
}

private fun Colors.RgbaType.toAndroid(): AndroidColors.RgbaType = when (this) {
    Colors.RgbaType.SRGB -> AndroidColors.RgbaType.SRGB
    Colors.RgbaType.LINEAR -> AndroidColors.RgbaType.LINEAR
    Colors.RgbaType.PREMULTIPLIED_SRGB -> AndroidColors.RgbaType.PREMULTIPLIED_SRGB
    Colors.RgbaType.PREMULTIPLIED_LINEAR -> AndroidColors.RgbaType.PREMULTIPLIED_LINEAR
}

private fun Colors.Conversion.toAndroid(): AndroidColors.Conversion = when (this) {
    Colors.Conversion.ACCURATE -> AndroidColors.Conversion.ACCURATE
    Colors.Conversion.FAST -> AndroidColors.Conversion.FAST
}

