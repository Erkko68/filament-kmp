package io.github.erkko68.filament.utils

import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Gestures in, camera pose out, against what upstream camutils (1.78) computes. */
class ManipulatorTest : UtilsTestFixture() {
    private class Pose(val eye: FloatArray, val target: FloatArray)

    private fun Manipulator.pose() = Pose(FloatArray(3), FloatArray(3)).also { getLookAt(it.eye, it.target, FloatArray(3)) }

    private fun Manipulator.drag(fromX: Int, toX: Int, strafe: Boolean = false) {
        grabBegin(fromX, 300, strafe)
        grabUpdate(toX, 300)
        grabEnd()
    }

    /** An orbit camera 10 units in front of the origin, on an 800×600 viewport, with default speeds (0.01). */
    private fun orbit() = Manipulator.Builder()
        .viewport(800, 600)
        .orbitHomePosition(0f, 0f, 10f)
        .groundPlane(0f, 0f, 1f, 0f)
        .build(Mode.ORBIT)

    /** Where an orbit of [theta] radians around the origin puts the eye. */
    private fun orbitEye(theta: Float) = floatArrayOf(10 * sin(theta), 0f, 10 * cos(theta))

    private fun assertNear(expected: FloatArray, actual: FloatArray, tolerance: Float = 1e-4f) =
        assertTrue(expected.indices.all { abs(expected[it] - actual[it]) < tolerance }, "${expected.toList()} vs ${actual.toList()}")

    @Test
    fun theHomePoseComesFromTheBuilder() = Manipulator.Builder()
        .viewport(800, 600)
        .targetPosition(1f, 2f, 3f)
        .orbitHomePosition(0f, 5f, 20f)
        .build(Mode.ORBIT)
        .use { m ->
            val pose = m.pose()
            assertContentEquals(floatArrayOf(0f, 5f, 20f), pose.eye)
            assertContentEquals(floatArrayOf(1f, 2f, 3f), pose.target)
        }

    @Test
    fun eachModeIsReported() {
        for (mode in Mode.entries) Manipulator.Builder().viewport(800, 600).build(mode).use { assertEquals(mode, it.mode) }
    }

    /**
     * Orbiting turns by `(grabX - x) * orbitSpeed` radians around the pivot; the target becomes the point one unit
     * along the gaze (OrbitManipulator::jumpToBookmark).
     */
    @Test
    fun orbitingTurnsAroundThePivot() = orbit().use { m ->
        m.drag(400, 500)
        val eye = orbitEye(-1f)
        val pose = m.pose()
        assertNear(eye, pose.eye)
        assertNear(FloatArray(3) { eye[it] * 0.9f }, pose.target)
    }

    /** Panning slides eye and target together across a plane facing the camera. */
    @Test
    fun strafingPansTheView() = orbit().use { m ->
        m.drag(400, 500, strafe = true)
        val pose = m.pose()
        assertTrue(pose.eye[0] < 0f, "dragging right moves the camera left: ${pose.eye.toList()}")
        assertEquals(10f, pose.eye[2], 1e-4f)
        assertNear(floatArrayOf(0f, 0f, -10f), FloatArray(3) { pose.target[it] - pose.eye[it] })
    }

    /** Scrolling moves eye and target by `gaze * zoomSpeed * -delta`: negative zooms in, positive out. */
    @Test
    fun scrollingDolliesAlongTheGaze() = orbit().use { m ->
        m.scroll(400, 300, -10f)
        val zoomedIn = m.pose()
        m.scroll(400, 300, 20f)
        val zoomedOut = m.pose()
        assertNear(floatArrayOf(0f, 0f, 9.9f), zoomedIn.eye)
        assertNear(floatArrayOf(0f, 0f, -0.1f), zoomedIn.target)
        assertNear(floatArrayOf(0f, 0f, 10.1f), zoomedOut.eye)
    }

    /** With no damping (the default) the eye moves at the starting speed of 1 unit/s along the gaze, -Z at pitch = yaw = 0. */
    @Test
    fun flyingForwardMovesAlongTheGaze() = Manipulator.Builder()
        .viewport(800, 600)
        .flightStartPosition(0f, 0f, 0f)
        .build(Mode.FREE_FLIGHT)
        .use { m ->
            assertNear(floatArrayOf(0f, 0f, -1f), m.pose().target)
            m.keyDown(Manipulator.Key.FORWARD)
            repeat(10) { m.update(0.1f) }
            m.keyUp(Manipulator.Key.FORWARD)
            m.update(0.1f)
            assertNear(floatArrayOf(0f, 0f, -1f), m.pose().eye)
        }

    /** Rays go through pixel centres (`0.5 + x`), so the centre pixel lands within half a pixel of the target. */
    @Test
    fun raysFollowTheViewport() = orbit().use { m ->
        val hit = FloatArray(3)
        assertTrue(m.raycast(400, 300, hit))
        assertNear(floatArrayOf(0f, 0f, 0f), hit, 1e-2f)
        val origin = FloatArray(3)
        val dir = FloatArray(3)
        m.getRay(400, 300, origin, dir)
        assertNear(floatArrayOf(0f, 0f, 10f), origin)
        assertTrue(dir[2] < -0.99f)

        m.setViewport(1600, 600)
        assertTrue(m.raycast(800, 300, hit))
        assertNear(floatArrayOf(0f, 0f, 0f), hit, 1e-2f)
    }

    /** Orbit bookmarks interpolate theta linearly; their duration only measures zoom and pan (Van Wijk), not turning. */
    @Test
    fun bookmarksRestoreAndInterpolatePoses() = orbit().use { m ->
        val home = m.homeBookmark
        m.drag(100, 300)
        val turned = m.currentBookmark
        m.jumpToBookmark(home)
        val atHome = m.pose().eye
        m.scroll(400, 300, -100f)
        val zoomed = m.currentBookmark
        val halfway = Bookmark.interpolate(home, turned, 0.5)
        m.jumpToBookmark(halfway)
        val between = m.pose().eye
        val turnDuration = Bookmark.duration(home, turned)
        val zoomDuration = Bookmark.duration(home, zoomed)
        listOf(home, turned, zoomed, halfway).forEach { it.close() }

        assertNear(floatArrayOf(0f, 0f, 10f), atHome)
        assertNear(orbitEye(-1f), between)
        assertEquals(0.0, turnDuration)
        assertTrue(zoomDuration > 0.0)
    }

    /** The pose drives a Filament camera the way an app's frame loop does. */
    @Test
    fun thePoseDrivesACamera() = orbit().use { m ->
        m.drag(400, 600)
        val pose = m.pose()
        val entity = engine.entityManager.create()
        val camera = engine.createCamera(entity)
        camera.lookAt(
            pose.eye[0].toDouble(), pose.eye[1].toDouble(), pose.eye[2].toDouble(),
            pose.target[0].toDouble(), pose.target[1].toDouble(), pose.target[2].toDouble(),
        )
        val position = camera.getPosition()
        engine.destroyCameraComponent(entity)
        engine.entityManager.destroy(entity)
        assertNear(pose.eye, FloatArray(3) { position[it].toFloat() })
    }
}
