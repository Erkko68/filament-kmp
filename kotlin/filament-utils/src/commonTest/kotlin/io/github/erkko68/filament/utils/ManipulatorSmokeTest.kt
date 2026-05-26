package io.github.erkko68.filament.utils

import kotlin.test.Test
import kotlin.test.assertTrue

class ManipulatorSmokeTest {
    @Test
    fun verifyManipulatorApi() {
        val manipulatorVerify: () -> Unit = {
            val builder = Manipulator.Builder()
                .viewport(800, 600)
                .targetPosition(0f, 0f, 0f)
                .upVector(0f, 1f, 0f)
                .zoomSpeed(0.1f)
                .orbitHomePosition(0f, 0f, 10f)
                .orbitSpeed(0.01f, 0.01f)
                .fovDirection(Manipulator.Fov.VERTICAL)
                .fovDegrees(45f)
                .farPlane(100f)
                .mapExtent(100f, 100f)
                .mapMinDistance(1f)
                .flightStartPosition(0f, 0f, 0f)
                .flightStartOrientation(0f, 0f)
                .flightMaxMoveSpeed(5f)
                .flightSpeedSteps(10)
                .flightPanSpeed(0.01f, 0.01f)
                .flightMoveDamping(0.5f)
                .groundPlane(0f, 1f, 0f, 0f)
                .panning(true)

            val orbitManip = builder.build(Manipulator.Mode.ORBIT)
            val mode: Manipulator.Mode = orbitManip.getMode()
            orbitManip.setViewport(1024, 768)
            
            val eye = FloatArray(3)
            val target = FloatArray(3)
            val up = FloatArray(3)
            orbitManip.getLookAt(eye, target, up)
            
            val raycastRes = FloatArray(3)
            orbitManip.raycast(100, 100, raycastRes)
            
            orbitManip.grabBegin(100, 100, false)
            orbitManip.grabUpdate(110, 110)
            orbitManip.grabEnd()
            
            orbitManip.keyDown(Manipulator.Key.FORWARD)
            orbitManip.keyUp(Manipulator.Key.FORWARD)
            orbitManip.scroll(100, 100, 1.0f)
            orbitManip.update(0.016f)
            
            val current: Manipulator.Bookmark = orbitManip.getCurrentBookmark()
            val home: Manipulator.Bookmark = orbitManip.getHomeBookmark()
            orbitManip.jumpToBookmark(home)
            orbitManip.destroy()
        }
        manipulatorVerify()
        assertTrue(true, "Manipulator API signatures resolved successfully.")
    }
}
