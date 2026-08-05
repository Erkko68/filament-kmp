package io.github.erkko68.filament.compose.scene

import androidx.compose.ui.graphics.colorspace.ColorSpaces
import io.github.erkko68.filament.utils.Float3
import kotlin.math.pow

/**
 * A point in 3-D space.
 *
 * Distinct from [Direction], [Scale], and [LinearColor] so the compiler stops you passing one where
 * another is expected (they are all float-triples and trivially confused). All four are
 * **immutable**, which also makes them stable Compose inputs — passing them to scene composables
 * doesn't force needless recompositions the way the mutable [Float3] did.
 *
 * Interop with filament-utils vector math is a hop away: construct from a [Float3]
 * (`Position(myFloat3)`), or drop out with [toFloat3]/[Float3.toPosition]. Common in-domain math
 * (translate, scale) is available as operators so you rarely need to.
 */
data class Position(val x: Float, val y: Float, val z: Float) {
    /** Uniform value on all axes. */
    constructor(v: Float) : this(v, v, v)
    /** From a filament-utils [Float3]. */
    constructor(v: Float3) : this(v.x, v.y, v.z)

    operator fun plus(d: Direction) = Position(x + d.x, y + d.y, z + d.z)
    operator fun minus(d: Direction) = Position(x - d.x, y - d.y, z - d.z)
    /** Displacement from [other] to this point. */
    operator fun minus(other: Position) = Direction(x - other.x, y - other.y, z - other.z)

    fun toFloat3() = Float3(x, y, z)
}

/**
 * A direction / displacement vector in 3-D space. See [Position] for the rationale behind the
 * distinct type and the [Float3] interop story.
 */
data class Direction(val x: Float, val y: Float, val z: Float) {
    constructor(v: Float) : this(v, v, v)
    constructor(v: Float3) : this(v.x, v.y, v.z)

    operator fun plus(o: Direction) = Direction(x + o.x, y + o.y, z + o.z)
    operator fun minus(o: Direction) = Direction(x - o.x, y - o.y, z - o.z)
    operator fun times(s: Float) = Direction(x * s, y * s, z * s)
    operator fun div(s: Float) = Direction(x / s, y / s, z / s)
    operator fun unaryMinus() = Direction(-x, -y, -z)

    /** Euclidean length. */
    val length: Float get() = kotlin.math.sqrt(x * x + y * y + z * z)
    /** Unit-length copy, or this vector unchanged if it is zero-length. */
    fun normalized(): Direction = length.let { if (it > 0f) this / it else this }

    fun toFloat3() = Float3(x, y, z)
}

/**
 * A per-axis scale factor. See [Position] for the rationale behind the distinct type.
 */
data class Scale(val x: Float, val y: Float, val z: Float) {
    constructor(v: Float) : this(v, v, v)
    constructor(v: Float3) : this(v.x, v.y, v.z)

    operator fun times(s: Float) = Scale(x * s, y * s, z * s)
    operator fun times(o: Scale) = Scale(x * o.x, y * o.y, z * o.z)

    fun toFloat3() = Float3(x, y, z)
}

/**
 * An RGB colour in **linear** space — the space Filament works in throughout (light colours,
 * `baseColor` material parameters, skybox and fog colours). Distinct from the spatial vectors so
 * a [LinearColor] can't be passed as a [Position]. Components are [r]/[g]/[b] and may exceed 1
 * for over-bright tints.
 *
 * Deliberately **not** called `Color`: `androidx.compose.ui.graphics.Color` is in scope in
 * practically every file that uses this library, and two same-named colour types with different
 * transfer functions is a trap.
 *
 * Interop with Compose UI colours goes through [fromComposeColor] / [toComposeColor], which apply
 * the sRGB transfer function in each direction — Compose colours are gamma-encoded, so copying
 * their components across raw would wash the result out. Alpha is dropped; scene colours are RGB.
 *
 * ```kotlin
 * val tint = LinearColor.fromComposeColor(MaterialTheme.colorScheme.primary)
 * ```
 */
data class LinearColor(val r: Float, val g: Float, val b: Float) {
    /** Uniform value on all channels (grey). */
    constructor(v: Float) : this(v, v, v)
    /** From a filament-utils [Float3] (x→r, y→g, z→b), assumed already linear. */
    constructor(v: Float3) : this(v.x, v.y, v.z)

    operator fun times(s: Float) = LinearColor(r * s, g * s, b * s)
    operator fun plus(o: LinearColor) = LinearColor(r + o.r, g + o.g, b + o.b)

    fun toFloat3() = Float3(r, g, b)

    /**
     * As a gamma-encoded Compose UI colour (alpha 1), for feeding a scene colour back into
     * Compose UI. Channels are clamped to 0..1 — over-bright values lose their headroom.
     */
    fun toComposeColor(): androidx.compose.ui.graphics.Color =
        androidx.compose.ui.graphics.Color(
            linearToSrgb(r), linearToSrgb(g), linearToSrgb(b),
        )

    companion object {
        /**
         * Converts a Compose UI colour to linear space, dropping alpha. Handles any source colour
         * space (sRGB, Display P3, …) by converting through Compose's own colour management.
         */
        fun fromComposeColor(v: androidx.compose.ui.graphics.Color): LinearColor {
            val linear = v.convert(ColorSpaces.LinearSrgb)
            return LinearColor(linear.red, linear.green, linear.blue)
        }
    }
}

/** sRGB OETF — linear component to gamma-encoded. */
private fun linearToSrgb(c: Float): Float {
    val v = c.coerceIn(0f, 1f)
    return if (v <= 0.0031308f) v * 12.92f else 1.055f * v.pow(1f / 2.4f) - 0.055f
}

/** Reinterpret a [Float3] as a [Position]. */
fun Float3.toPosition() = Position(x, y, z)
/** Reinterpret a [Float3] as a [Direction]. */
fun Float3.toDirection() = Direction(x, y, z)
/** Reinterpret a [Float3] as a [Scale]. */
fun Float3.toScale() = Scale(x, y, z)
/** Reinterpret a [Float3] as a [LinearColor] (x→r, y→g, z→b). */
fun Float3.toLinearColor() = LinearColor(x, y, z)
