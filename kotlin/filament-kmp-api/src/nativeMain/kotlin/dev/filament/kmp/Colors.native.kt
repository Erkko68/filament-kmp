package dev.filament.kmp

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
        TODO("Not yet implemented")
    }

    @LinearColor
    actual fun toLinear(type: RgbType, rgb: FloatArray): FloatArray {
        TODO("Not yet implemented")
    }

    @LinearColor
    actual fun toLinear(type: RgbaType, r: Float, g: Float, b: Float, a: Float): FloatArray {
        TODO("Not yet implemented")
    }

    @LinearColor
    actual fun toLinear(type: RgbaType, rgba: FloatArray): FloatArray {
        TODO("Not yet implemented")
    }

    @LinearColor
    actual fun toLinear(conversion: Conversion, rgb: FloatArray): FloatArray {
        TODO("Not yet implemented")
    }

    @LinearColor
    actual fun cct(temperature: Float): FloatArray {
        TODO("Not yet implemented")
    }

    @LinearColor
    actual fun illuminantD(temperature: Float): FloatArray {
        TODO("Not yet implemented")
    }
}

