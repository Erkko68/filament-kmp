package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.utils.Float3
import io.github.erkko68.filament.utils.Quaternion
import io.github.erkko68.filament.utils.RotationsOrder

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
 * A 3-D rotation, stored as a unit quaternion. See [Position] for the rationale behind the
 * distinct type — the same one applies doubly here, since filament-utils' [Quaternion] has
 * mutable components and so is an unstable Compose input.
 *
 * Build one with [axisAngle] or [euler] rather than the raw component constructor:
 *
 * ```kotlin
 * Cube(rotation = Rotation.axisAngle(Direction(0f, 1f, 0f), degrees = spin))
 * Cube(rotation = Rotation.euler(yaw = 45f) * Rotation.euler(pitch = 30f))
 * ```
 *
 * Interop with filament-utils works like the other types: construct from a [Quaternion]
 * (`Rotation(myQuat)`) or drop out with [toQuaternion]/[Quaternion.toRotation].
 */
data class Rotation(val x: Float, val y: Float, val z: Float, val w: Float) {
    /** From a filament-utils [Quaternion]. */
    constructor(q: Quaternion) : this(q.x, q.y, q.z, q.w)

    /** Applies [other] and then this rotation (quaternion product, not commutative). */
    operator fun times(other: Rotation) = Rotation(toQuaternion() * other.toQuaternion())

    /** This rotation applied to a direction vector. */
    operator fun times(d: Direction) = (toQuaternion() * d.toFloat3()).toDirection()

    /** The inverse rotation — undoes this one. */
    fun inverse() = Rotation(-x, -y, -z, w)

    fun toQuaternion() = Quaternion(x, y, z, w)

    companion object {
        /** No rotation. The default for every scene composable's `rotation` argument. */
        val Identity = Rotation(0f, 0f, 0f, 1f)

        /** [degrees] of rotation about [axis] (normalized for you), right-hand rule. */
        fun axisAngle(axis: Direction, degrees: Float) =
            Rotation(Quaternion.fromAxisAngle(axis.toFloat3(), degrees))

        /**
         * From intrinsic Tait-Bryan angles in degrees, applied Z (roll) then Y (yaw) then
         * X (pitch) — filament-utils' default [RotationsOrder.ZYX].
         */
        fun euler(pitch: Float = 0f, yaw: Float = 0f, roll: Float = 0f) =
            Rotation(Quaternion.fromEuler(Float3(pitch, yaw, roll)))
    }
}

/**
 * An RGB color (linear or sRGB depending on the consuming API). Distinct from the spatial
 * vectors so a [Color] can't be passed as a [Position]. Components are [r]/[g]/[b].
 *
 * Interop with Compose UI colors: construct from an [androidx.compose.ui.graphics.Color]
 * (`Color(MaterialTheme.colorScheme.primary)`) or convert back with [toComposeColor]. The
 * alpha channel is dropped — scene colors are RGB.
 */
data class Color(val r: Float, val g: Float, val b: Float) {
    /** Uniform value on all channels (grey). */
    constructor(v: Float) : this(v, v, v)
    /** From a filament-utils [Float3] (x→r, y→g, z→b). */
    constructor(v: Float3) : this(v.x, v.y, v.z)
    /** From a Compose UI color (alpha is dropped). */
    constructor(v: androidx.compose.ui.graphics.Color) : this(v.red, v.green, v.blue)

    operator fun times(s: Float) = Color(r * s, g * s, b * s)
    operator fun plus(o: Color) = Color(r + o.r, g + o.g, b + o.b)

    fun toFloat3() = Float3(r, g, b)

    /** As a Compose UI color (alpha 1). Channels are clamped to 0..1 by Compose. */
    fun toComposeColor() = androidx.compose.ui.graphics.Color(
        r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), b.coerceIn(0f, 1f),
    )
}

/** Reinterpret a [Float3] as a [Position]. */
fun Float3.toPosition() = Position(x, y, z)
/** Reinterpret a [Float3] as a [Direction]. */
fun Float3.toDirection() = Direction(x, y, z)
/** Reinterpret a [Float3] as a [Scale]. */
fun Float3.toScale() = Scale(x, y, z)
/** Reinterpret a [Float3] as a [Color] (x→r, y→g, z→b). */
fun Float3.toColor() = Color(x, y, z)
/** Reinterpret a [Quaternion] as a [Rotation]. */
fun Quaternion.toRotation() = Rotation(x, y, z, w)
