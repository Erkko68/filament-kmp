package io.github.erkko68.filament

/** The xy chromaticities (in xyY, Y = 1) of a color space's red, green and blue primaries; each a `float2`. */
class Primaries(val r: FloatArray, val g: FloatArray, val b: FloatArray) {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is Primaries && r.contentEquals(other.r) && g.contentEquals(other.g) && b.contentEquals(other.b))

    override fun hashCode(): Int = (r.contentHashCode() * 31 + g.contentHashCode()) * 31 + b.contentHashCode()

    override fun toString(): String = "Primaries(r=${r.contentToString()}, g=${g.contentToString()}, b=${b.contentToString()})"
}

/**
 * An ICC parametric curve type 4 (ICC.1:2004-10, 10.15): the EOTF is `Y = cX + f` for `X < d` and
 * `Y = (aX + b)^g + e` otherwise; the OETF is its inverse. The 5-parameter constructor (e = f = 0) is curve type 3.
 */
data class TransferFunction(
    val a: Double, val b: Double, val c: Double, val d: Double,
    val e: Double, val f: Double, val g: Double,
) {
    constructor(a: Double, b: Double, c: Double, d: Double, g: Double) : this(a, b, c, d, 0.0, 0.0, g)

    companion object {
        /** Linear transfer function. */
        val Linear = TransferFunction(1.0, 0.0, 0.0, 0.0, 1.0)

        /** sRGB transfer function. */
        val sRGB = TransferFunction(1.0 / 1.055, 0.055 / 1.055, 1.0 / 12.92, 0.04045, 2.4)
    }
}

/** Reference white points: xy chromaticities in xyY, each a new `float2`. */
object WhitePoint {
    /** Standard CIE 1931 2° illuminant D65 (6504K). */
    val D65: FloatArray get() = floatArrayOf(0.31271f, 0.32902f)
}

/**
 * An RGB color space: the [primaries] that define its gamut, a [whitePoint] (`float2`) its values are
 * normalized to, and the [transferFunction] between linear and encoded values. Also written
 * `gamut - transferFunction - whitePoint`, e.g. `Gamut.Rec709 - TransferFunction.sRGB - WhitePoint.D65`.
 */
class ColorSpace(val primaries: Primaries, val transferFunction: TransferFunction, val whitePoint: FloatArray) {
    override fun equals(other: Any?): Boolean = this === other || (other is ColorSpace && primaries == other.primaries &&
        transferFunction == other.transferFunction && whitePoint.contentEquals(other.whitePoint))

    override fun hashCode(): Int = (primaries.hashCode() * 31 + transferFunction.hashCode()) * 31 + whitePoint.contentHashCode()

    override fun toString(): String =
        "ColorSpace(primaries=$primaries, transferFunction=$transferFunction, whitePoint=${whitePoint.contentToString()})"
}

/** A color space missing its white point: `gamut - transferFunction`. */
class PartialColorSpace internal constructor(private val primaries: Primaries, private val transferFunction: TransferFunction) {
    operator fun minus(whitePoint: FloatArray): ColorSpace = ColorSpace(primaries, transferFunction, whitePoint)
}

/** A color gamut, defined by its [primaries]. */
class Gamut(val primaries: Primaries) {
    constructor(r: FloatArray, g: FloatArray, b: FloatArray) : this(Primaries(r, g, b))

    operator fun minus(transferFunction: TransferFunction): PartialColorSpace = PartialColorSpace(primaries, transferFunction)

    companion object {
        /** Rec.709 color gamut, used in the sRGB and DisplayP3 color spaces. */
        val Rec709: Gamut get() = Gamut(floatArrayOf(0.640f, 0.330f), floatArrayOf(0.300f, 0.600f), floatArrayOf(0.150f, 0.060f))
    }
}
