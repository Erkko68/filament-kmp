package io.github.erkko68.filament.utils

import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ManipulatorTest : UtilsTestFixture() {
    private fun buildOrbitManipulator(): Manipulator =
        Manipulator.Builder()
            .viewport(800, 600)
            .targetPosition(0f, 0f, 0f)
            .upVector(0f, 1f, 0f)
            .zoomSpeed(0.1f)
            .orbitHomePosition(0f, 0f, 10f)
            .orbitSpeed(0.01f, 0.01f)
            .fovDirection(Fov.VERTICAL)
            .fovDegrees(45f)
            .farPlane(100f)
            .panning(true)
            .groundPlane(0f, 0f, 1f, 0f)
            .build(Mode.ORBIT)

    @Test
    fun testOrbitModeIsReported() {
        val m = buildOrbitManipulator()
        assertEquals(Mode.ORBIT, m.mode)
        m.destroy()
    }

    @Test
    fun testMapMode() {
        val m = Manipulator.Builder()
            .viewport(800, 600)
            .mapExtent(100f, 100f)
            .mapMinDistance(1f)
            .build(Mode.MAP)
        assertEquals(Mode.MAP, m.mode)
        m.destroy()
    }

    @Test
    fun testFreeFlightMode() {
        val m = Manipulator.Builder()
            .viewport(800, 600)
            .flightStartPosition(0f, 0f, 0f)
            .flightStartOrientation(0f, 0f)
            .flightMaxMoveSpeed(5f)
            .flightSpeedSteps(10)
            .flightPanSpeed(0.01f, 0.01f)
            .flightMoveDamping(0.5f)
            .build(Mode.FREE_FLIGHT)
        assertEquals(Mode.FREE_FLIGHT, m.mode)
        m.destroy()
    }

    @Test
    fun testViewportResize() {
        val m = buildOrbitManipulator()
        m.setViewport(1024, 768)
        m.destroy()
    }

    @Test
    fun testLookAt() {
        val m = buildOrbitManipulator()
        val eye = FloatArray(3)
        val target = FloatArray(3)
        val up = FloatArray(3)
        m.getLookAt(eye, target, up)
        assertEquals(3, eye.size)
        assertEquals(3, target.size)
        assertEquals(3, up.size)
        m.destroy()
    }

    @Test
    fun testRaycast() {
        val m = buildOrbitManipulator()
        val result = FloatArray(3)
        // The orbit camera looks down -z from (0, 0, 10): the viewport centre hits the z=0 ground plane.
        assertTrue(m.raycast(400, 300, result))
        assertEquals(0f, result[2], 1e-3f)
        m.destroy()
    }

    @Test
    fun testGetRay() {
        val m = buildOrbitManipulator()
        val origin = FloatArray(3)
        val dir = FloatArray(3)
        m.getRay(400, 300, origin, dir)
        assertEquals(10f, origin[2], 1e-3f)
        assertTrue(dir[2] < 0f)
        m.destroy()
    }

    @Test
    fun testGrabInteraction() {
        val m = buildOrbitManipulator()
        m.grabBegin(100, 100, false)
        m.grabUpdate(150, 120)
        m.grabEnd()
        m.destroy()
    }

    @Test
    fun testStrafeGrab() {
        val m = buildOrbitManipulator()
        m.grabBegin(100, 100, strafe = true)
        m.grabUpdate(110, 110)
        m.grabEnd()
        m.destroy()
    }

    @Test
    fun testKeyEvents() {
        val m = Manipulator.Builder()
            .viewport(800, 600)
            .build(Mode.FREE_FLIGHT)
        m.keyDown(Manipulator.Key.FORWARD)
        m.keyDown(Manipulator.Key.LEFT)
        m.keyUp(Manipulator.Key.FORWARD)
        m.keyUp(Manipulator.Key.LEFT)
        m.keyDown(Manipulator.Key.BACKWARD)
        m.keyDown(Manipulator.Key.RIGHT)
        m.keyDown(Manipulator.Key.UP)
        m.keyDown(Manipulator.Key.DOWN)
        m.keyUp(Manipulator.Key.BACKWARD)
        m.keyUp(Manipulator.Key.RIGHT)
        m.keyUp(Manipulator.Key.UP)
        m.keyUp(Manipulator.Key.DOWN)
        m.destroy()
    }

    @Test
    fun testScroll() {
        val m = buildOrbitManipulator()
        m.scroll(400, 300, 1.0f)
        m.scroll(400, 300, -1.0f)
        m.destroy()
    }

    @Test
    fun testUpdate() {
        val m = buildOrbitManipulator()
        m.update(0.016f)
        m.update(0.033f)
        m.destroy()
    }

    @Test
    fun testBookmarks() {
        val m = buildOrbitManipulator()
        val home = m.homeBookmark
        m.grabBegin(100, 100, false)
        m.grabUpdate(300, 100)
        m.grabEnd()
        val moved = m.currentBookmark
        m.jumpToBookmark(home)
        val eye = FloatArray(3)
        m.getLookAt(eye, FloatArray(3), FloatArray(3))
        assertEquals(10f, eye[2], 1e-3f)

        val halfway = Bookmark.interpolate(home, moved, 0.5)
        m.jumpToBookmark(halfway)
        m.getLookAt(eye, FloatArray(3), FloatArray(3))
        assertTrue(eye[2] < 10f - 1e-3f)
        assertTrue(Bookmark.duration(home, moved) >= 0.0)
        listOf(home, moved, halfway).forEach { it.destroy() }
        m.destroy()
    }

    @Test
    fun testGroundPlane() {
        val m = Manipulator.Builder()
            .viewport(800, 600)
            .groundPlane(0f, 1f, 0f, 0f)
            .build(Mode.ORBIT)
        m.destroy()
    }

    @Test
    fun testAllBuilderOptions() {
        val m = Manipulator.Builder()
            .viewport(1280, 720)
            .targetPosition(1f, 2f, 3f)
            .upVector(0f, 1f, 0f)
            .zoomSpeed(0.5f)
            .orbitHomePosition(0f, 5f, 20f)
            .orbitSpeed(0.005f, 0.005f)
            .fovDirection(Fov.HORIZONTAL)
            .fovDegrees(60f)
            .farPlane(500f)
            .mapExtent(200f, 200f)
            .mapMinDistance(0.5f)
            .flightStartPosition(0f, 1f, 0f)
            .flightStartOrientation(0f, 0f)
            .flightMaxMoveSpeed(10f)
            .flightSpeedSteps(20)
            .flightPanSpeed(0.005f, 0.005f)
            .flightMoveDamping(0.8f)
            .groundPlane(0f, 1f, 0f, 0f)
            .panning(false)
            .build(Mode.ORBIT)
        assertEquals(Mode.ORBIT, m.mode)
        m.destroy()
    }
}
