package io.github.erkko68.filament.utils

import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ManipulatorTest : UtilsTestFixture() {
    private fun buildOrbitManipulator(): Manipulator =
        Manipulator.Builder()
            .viewport(800, 600)
            .targetPosition(0f, 0f, 0f)
            .upVector(0f, 1f, 0f)
            .zoomSpeed(0.1f)
            .orbitHomePosition(0f, 0f, 10f)
            .orbitSpeed(0.01f, 0.01f)
            .fovDirection(Manipulator.Fov.VERTICAL)
            .fovDegrees(45f)
            .farPlane(100f)
            .panning(true)
            .build(Manipulator.Mode.ORBIT)

    @Test
    fun testOrbitModeIsReported() {
        val m = buildOrbitManipulator()
        assertEquals(Manipulator.Mode.ORBIT, m.getMode())
        m.destroy()
    }

    @Test
    fun testMapMode() {
        val m = Manipulator.Builder()
            .viewport(800, 600)
            .mapExtent(100f, 100f)
            .mapMinDistance(1f)
            .build(Manipulator.Mode.MAP)
        assertEquals(Manipulator.Mode.MAP, m.getMode())
        m.destroy()
    }

    @Test
    fun testFlightMode() {
        val m = Manipulator.Builder()
            .viewport(800, 600)
            .flightStartPosition(0f, 0f, 0f)
            .flightStartOrientation(0f, 0f)
            .flightMaxMoveSpeed(5f)
            .flightSpeedSteps(10)
            .flightPanSpeed(0.01f, 0.01f)
            .flightMoveDamping(0.5f)
            .build(Manipulator.Mode.FLIGHT)
        assertEquals(Manipulator.Mode.FLIGHT, m.getMode())
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
        m.raycast(400, 300, result)
        assertEquals(3, result.size)
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
            .build(Manipulator.Mode.FLIGHT)
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
        val home = m.getHomeBookmark()
        assertNotNull(home)
        val current = m.getCurrentBookmark()
        assertNotNull(current)
        m.jumpToBookmark(home)
        m.jumpToBookmark(current)
        m.destroy()
    }

    @Test
    fun testGroundPlane() {
        val m = Manipulator.Builder()
            .viewport(800, 600)
            .groundPlane(0f, 1f, 0f, 0f)
            .build(Manipulator.Mode.ORBIT)
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
            .fovDirection(Manipulator.Fov.HORIZONTAL)
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
            .build(Manipulator.Mode.ORBIT)
        assertEquals(Manipulator.Mode.ORBIT, m.getMode())
        m.destroy()
    }
}
