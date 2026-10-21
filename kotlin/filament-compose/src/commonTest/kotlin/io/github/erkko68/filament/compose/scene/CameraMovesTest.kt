package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.saveable.SaverScope
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class CameraMovesTest {
    private fun assertNear(expected: Position, actual: Position, what: String) {
        assertEquals(0f, (expected - actual).length, 1e-4f, "$what: expected $expected, got $actual")
    }

    // An eye at [radius] from the origin, [degrees] round from +Z towards +X on the horizon.
    private fun around(degrees: Double, radius: Float = 5f) = Viewpoint(
        Position((sin(degrees * PI / 180) * radius).toFloat(), 0f, (cos(degrees * PI / 180) * radius).toFloat()),
        Position(0f),
    )

    @Test
    fun orbitStartsAndEndsOnItsViewpoints() {
        val from = Viewpoint(Position(1f, 2f, 3f), Position(0f, 1f, 0f))
        val to = Viewpoint(Position(-4f, 0.5f, 2f), Position(1f, 0f, -1f))
        assertNear(from.eye, CameraPath.Orbit.at(from, to, 0f).eye, "t = 0")
        assertNear(to.eye, CameraPath.Orbit.at(from, to, 1f).eye, "t = 1")
        assertNear(Position(0.5f, 0.5f, -0.5f), CameraPath.Orbit.at(from, to, 0.5f).target, "the target moves in a straight line")
    }

    /** From -170° to 170° is 20° the short way, through 180° (behind the target), not 340° through the front. */
    @Test
    fun orbitTurnsTheShortWayRound() {
        assertNear(around(180.0).eye, CameraPath.Orbit.at(around(-170.0), around(170.0), 0.5f).eye, "halfway")
        assertNear(around(-90.0).eye, CameraPath.Orbit.at(around(-170.0), around(-10.0), 0.5f).eye, "a turn under 180° is unchanged")
    }

    @Test
    fun orbitZoomsEvenlyInScale() {
        val mid = CameraPath.Orbit.at(around(0.0, radius = 1f), around(0.0, radius = 4f), 0.5f)
        assertEquals(2f, (mid.eye - mid.target).length, 1e-4f)
    }

    /** Straight above the target any heading is the same view, so leaving it only rises to the other heading. */
    @Test
    fun orbitFromOverheadKeepsTheOtherHeading() {
        val overhead = Viewpoint(Position(0f, 5f, 0f), Position(0f), up = Direction(0f, 0f, -1f))
        val mid = CameraPath.Orbit.at(overhead, around(90.0), 0.5f)
        assertEquals(0f, mid.eye.z, 1e-4f, "the eye stays in the plane of the end heading: ${mid.eye}")
    }

    @Test
    fun straightAndArcRunInLines() {
        val from = Viewpoint(Position(0f, 1f, 4f), Position(0f))
        val to = Viewpoint(Position(4f, 1f, 0f), Position(2f, 0f, 0f))
        val straight = CameraPath.Straight.at(from, to, 0.5f)
        assertNear(Position(2f, 1f, 2f), straight.eye, "straight eye")
        assertNear(Position(1f, 0f, 0f), straight.target, "straight target")
        // 4t(1 - t) of the lift: all of it halfway, none at the ends.
        assertNear(Position(2f, 4f, 2f), CameraPath.Arc(3f).at(from, to, 0.5f).eye, "arc halfway")
        assertNear(to.eye, CameraPath.Arc(3f).at(from, to, 1f).eye, "arc end")
    }

    /** A 90° vertical lens fits a unit sphere at √2: its edge touches the frustum's 45° sides. */
    @Test
    fun fittingFramesTheSphere() {
        val cam = CameraState(
            initialEye = Position(0f, 0f, 10f), initialTarget = Position(0f), initialUp = Direction.Up,
            initialProjection = Projection.Perspective(fovDegrees = 90.0), initialExposure = Exposure(),
            initialFocusDistance = 10f, initialShift = LensShift.None, initialScaling = LensScaling.Identity,
        )
        val fit = cam.fitting(Position(1f, 0f, 0f), radius = 1f)
        assertNear(Position(1f, 0f, sqrt(2f)), fit.eye, "seen from the current direction")
        assertEquals(Position(1f, 0f, 0f), fit.target)

        val above = cam.fitting(Position(0f), radius = 1f, from = Direction(0f, 2f, 0f))
        assertNear(Position(0f, sqrt(2f), 0f), above.eye, "seen from the given direction, normalised")
    }

    @Test
    fun viewpointSurvivesSaving() {
        val viewpoint = Viewpoint(Position(1f, 2f, 3f), Position(4f, 5f, 6f), Direction(0f, 0f, -1f))
        val saved = with(Viewpoint.Saver) { SaverScope { true }.save(viewpoint) }!!
        assertEquals(viewpoint, Viewpoint.Saver.restore(saved))
    }
}
