package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.utils.Float3

/**
 * A point in 3-D space.
 *
 * Distinct from [Direction], [Scale], and [Color] so the compiler stops you passing one where
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
 * An RGB color (linear or sRGB depending on the consuming API). Distinct from the spatial
 * vectors so a [Color] can't be passed as a [Position]. Components are [r]/[g]/[b].
 */
data class Color(val r: Float, val g: Float, val b: Float) {
    /** Uniform value on all channels (grey). */
    constructor(v: Float) : this(v, v, v)
    /** From a filament-utils [Float3] (x→r, y→g, z→b). */
    constructor(v: Float3) : this(v.x, v.y, v.z)

    operator fun times(s: Float) = Color(r * s, g * s, b * s)
    operator fun plus(o: Color) = Color(r + o.r, g + o.g, b + o.b)

    fun toFloat3() = Float3(r, g, b)
}

/** Reinterpret a [Float3] as a [Position]. */
fun Float3.toPosition() = Position(x, y, z)
/** Reinterpret a [Float3] as a [Direction]. */
fun Float3.toDirection() = Direction(x, y, z)
/** Reinterpret a [Float3] as a [Scale]. */
fun Float3.toScale() = Scale(x, y, z)
/** Reinterpret a [Float3] as a [Color] (x→r, y→g, z→b). */
fun Float3.toColor() = Color(x, y, z)
