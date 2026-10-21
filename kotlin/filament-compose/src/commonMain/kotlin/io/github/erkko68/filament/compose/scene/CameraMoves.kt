package io.github.erkko68.filament.compose.scene

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import io.github.erkko68.filament.Camera as FilamentCamera
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

/**
 * Where a camera is and what it looks at: the value [CameraState.viewpoint] reads and writes, and what
 * [CameraState.animateTo] flies to. With an orbit controller, [target] is the point it orbits; with a map one, the
 * ground point under the eye.
 *
 * A plain value: keep viewpoints in constants or state, compare them, and save them with [Saver].
 */
@Immutable
data class Viewpoint(val eye: Position, val target: Position, val up: Direction = Direction.Up) {
    companion object {
        /** For `rememberSaveable(stateSaver = Viewpoint.Saver)`. */
        val Saver: Saver<Viewpoint, Any> = listSaver(
            save = { listOf(it.eye.x, it.eye.y, it.eye.z, it.target.x, it.target.y, it.target.z, it.up.x, it.up.y, it.up.z) },
            restore = { Viewpoint(Position(it[0], it[1], it[2]), Position(it[3], it[4], it[5]), Direction(it[6], it[7], it[8])) },
        )
    }
}

/**
 * Ready-made timings for [CameraState.animateTo]. Any `AnimationSpec<Float>` works: a `tween` with an `Easing` of
 * your own (`Easing { t -> t * t }`), `keyframes`, or a `spring`, whose overshoot carries the camera past the
 * viewpoint and back.
 */
object CameraTransitions {
    /** A second, easing in and out. The default. */
    val Smooth: AnimationSpec<Float> = tween(1000, easing = FastOutSlowInEasing)

    /** A short hop, for small moves. */
    val Quick: AnimationSpec<Float> = tween(400, easing = FastOutSlowInEasing)

    /** Two seconds of a slow start and a slow landing, for a guided tour. */
    val Cinematic: AnimationSpec<Float> = tween(2000, easing = CubicBezierEasing(0.65f, 0f, 0.35f, 1f))

    /** Constant speed, no easing. */
    val Linear: AnimationSpec<Float> = tween(1000, easing = LinearEasing)

    /** Overshoots and settles back, like a camera on a spring arm. */
    val Bouncy: AnimationSpec<Float> = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessVeryLow)
}

/**
 * The route [CameraState.animateTo] takes: the viewpoint a fraction [t] of the way from [from] to [to]. [t] runs from
 * 0 to 1 as the timing plays and leaves that range while a spring overshoots. Write your own as a lambda,
 * `CameraPath { from, to, t -> … }`.
 */
fun interface CameraPath {
    fun at(from: Viewpoint, to: Viewpoint, t: Float): Viewpoint

    companion object {
        /**
         * Swings around the targets: the target moves in a straight line while the eye circles it the short way
         * round, rising or dipping to the new height, and the distance changes evenly in scale (a 1→4 zoom is
         * halfway at 2). The default.
         */
        val Orbit = CameraPath { from, to, t ->
            val a = from.eye - from.target
            val b = to.eye - to.target
            val ra = a.length
            val rb = b.length
            if (ra < 1e-6f || rb < 1e-6f) return@CameraPath Viewpoint(lerp(from.eye, to.eye, t), lerp(from.target, to.target, t), lerpUp(from.up, to.up, t))
            val pa = asin((a.y / ra).coerceIn(-1f, 1f))
            val pb = asin((b.y / rb).coerceIn(-1f, 1f))
            // Straight above or below the target any heading is the same view: take the other end's.
            var ta = atan2(a.x, a.z)
            var tb = atan2(b.x, b.z)
            if (abs(cos(pa)) < 1e-4f) ta = tb
            if (abs(cos(pb)) < 1e-4f) tb = ta
            var turn = tb - ta
            if (turn > PI) turn -= (2 * PI).toFloat() else if (turn < -PI) turn += (2 * PI).toFloat()
            val phi = pa + (pb - pa) * t
            val theta = ta + turn * t
            val r = ra * (rb / ra).pow(t)
            val target = lerp(from.target, to.target, t)
            Viewpoint(target + Direction(sin(theta) * cos(phi), sin(phi), cos(theta) * cos(phi)) * r, target, lerpUp(from.up, to.up, t))
        }
    }
}

private fun lerp(a: Position, b: Position, t: Float) = Position(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t)

// Opposite ups cancel halfway; the far end's is as good as any there.
private fun lerpUp(a: Direction, b: Direction, t: Float) = (a + (b - a) * t).normalized().takeIf { it.length > 0f } ?: b

/** tan of the view's vertical half-angle; null for an orthographic projection. */
internal fun Projection.tanHalfVertical(aspect: Double): Double? = when (this) {
    is Projection.Perspective -> tan(fovDegrees * PI / 360.0).let { if (fovDirection == FilamentCamera.Fov.VERTICAL) it else it / aspect }
    // A lens's is 12 mm (half Filament's 24 mm sensor) over its focal length.
    is Projection.Lens -> 12.0 / focalLength
    is Projection.Orthographic -> null
}

/** How far from a sphere of [radius] the camera fits it in both directions of the view; null for an orthographic one. */
internal fun fittingDistance(projection: Projection, aspect: Double, radius: Float): Float? {
    val vertical = projection.tanHalfVertical(aspect) ?: return null
    return (radius / sin(atan(min(vertical, vertical * aspect)))).toFloat()
}
