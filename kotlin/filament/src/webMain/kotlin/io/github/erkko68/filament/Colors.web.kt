package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual object Colors {
    actual enum class RgbType {
        SRGB, LINEAR;
        internal fun toNative(): Int = ordinal
    }

    actual enum class RgbaType {
        SRGB, LINEAR, PREMULTIPLIED_SRGB, PREMULTIPLIED_LINEAR;
        internal fun toNative(): Int = ordinal
    }

    actual enum class Conversion {
        ACCURATE, FAST;
        internal fun toNative(): Int = ordinal
    }

    actual fun toLinear(type: RgbType, r: Float, g: Float, b: Float): FloatArray {
        return toLinear(type, floatArrayOf(r, g, b))
    }

    actual fun toLinear(type: RgbType, rgb: FloatArray): FloatArray {
        fila.heapScoped {
            val inRgb = F32Array(alloc((3) * 4))
            val outRgb = F32Array(alloc((3) * 4))
            for (i in 0 until 3) inRgb[i] = rgb[i]
            FilaColors_toLinearRgb(type.toNative(), inRgb.ptr, outRgb.ptr)
            for (i in 0 until 3) rgb[i] = outRgb[i]
        }
        return rgb
    }

    actual fun toLinear(type: RgbaType, r: Float, g: Float, b: Float, a: Float): FloatArray {
        return toLinear(type, floatArrayOf(r, g, b, a))
    }

    actual fun toLinear(type: RgbaType, rgba: FloatArray): FloatArray {
        fila.heapScoped {
            val inRgba = F32Array(alloc((4) * 4))
            val outRgba = F32Array(alloc((4) * 4))
            for (i in 0 until 4) inRgba[i] = rgba[i]
            FilaColors_toLinearRgba(type.toNative(), inRgba.ptr, outRgba.ptr)
            for (i in 0 until 4) rgba[i] = outRgba[i]
        }
        return rgba
    }

    actual fun toLinear(conversion: Conversion, rgb: FloatArray): FloatArray {
        fila.heapScoped {
            val inRgb = F32Array(alloc((3) * 4))
            val outRgb = F32Array(alloc((3) * 4))
            for (i in 0 until 3) inRgb[i] = rgb[i]
            FilaColors_toLinearConvert(conversion.toNative(), inRgb.ptr, outRgb.ptr)
            for (i in 0 until 3) rgb[i] = outRgb[i]
        }
        return rgb
    }

    actual fun cct(temperature: Float): FloatArray {
        val color = FloatArray(3)
        fila.heapScoped {
            val outColor = F32Array(alloc((3) * 4))
            FilaColors_cct(temperature, outColor.ptr)
            for (i in 0 until 3) color[i] = outColor[i]
        }
        return color
    }

    actual fun illuminantD(temperature: Float): FloatArray {
        val color = FloatArray(3)
        fila.heapScoped {
            val outColor = F32Array(alloc((3) * 4))
            FilaColors_illuminantD(temperature, outColor.ptr)
            for (i in 0 until 3) color[i] = outColor[i]
        }
        return color
    }
}
