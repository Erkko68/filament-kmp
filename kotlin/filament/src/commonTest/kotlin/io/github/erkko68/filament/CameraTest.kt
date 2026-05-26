package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CameraTest : FilamentTestFixture() {
    @Test
    fun testCameraLifecycleAndMethods() {
        val entity = EntityManager.get().create()
        val cam = engine.createCamera(entity)
        assertNotNull(cam)
        assertEquals(entity, cam.entity)

        // Projection
        cam.setProjection(Camera.Projection.PERSPECTIVE, -1.0, 1.0, -1.0, 1.0, 1.0, 100.0)
        cam.setProjection(45.0, 1.0, 1.0, 100.0, Camera.Fov.VERTICAL)
        cam.setLensProjection(50.0, 1.0, 1.0, 100.0)
        
        val projMatrix = DoubleArray(16)
        cam.setCustomProjection(projMatrix, 1.0, 100.0)
        cam.setCustomProjection(projMatrix, projMatrix, 1.0, 100.0)

        // Custom eye projections
        val eyeProjs = DoubleArray(32)
        cam.setCustomEyeProjection(eyeProjs, 2, projMatrix, 1.0, 100.0)
        cam.setEyeModelMatrix(0, DoubleArray(16))

        // Scaling & Shift
        cam.setScaling(1.5, 1.5)
        val scale = cam.getScaling()
        assertEquals(1.5, scale[0])
        assertEquals(1.5, scale[1])

        cam.setShift(0.1, 0.2)
        val shift = cam.getShift()
        assertEquals(0.1, shift[0])
        assertEquals(0.2, shift[1])

        // Transforms
        cam.lookAt(0.0, 0.0, 10.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
        cam.setModelMatrix(FloatArray(16))
        cam.setModelMatrix(DoubleArray(16))

        // Getters
        val proj = cam.getProjectionMatrix()
        assertEquals(16, proj.size)
        val cullProj = cam.getCullingProjectionMatrix()
        assertEquals(16, cullProj.size)

        val modelF = cam.getModelMatrix(null as FloatArray?)
        assertEquals(16, modelF.size)
        val modelD = cam.getModelMatrix(null as DoubleArray?)
        assertEquals(16, modelD.size)

        val viewF = cam.getViewMatrix(null as FloatArray?)
        assertEquals(16, viewF.size)
        val viewD = cam.getViewMatrix(null as DoubleArray?)
        assertEquals(16, viewD.size)

        // Directions & Vectors
        val pos = cam.getPosition()
        assertEquals(3, pos.size)
        val left = cam.getLeftVector()
        assertEquals(3, left.size)
        val up = cam.getUpVector()
        assertEquals(3, up.size)
        val fwd = cam.getForwardVector()
        assertEquals(3, fwd.size)

        // Clip planes
        assertTrue(cam.near >= 0f)
        assertTrue(cam.cullingFar >= 0f)

        // Exposure & Lens Properties
        cam.setExposure(1.8f, 0.01f, 100f)
        cam.setExposure(12.0f)
        assertTrue(cam.aperture >= 0f)
        assertTrue(cam.shutterSpeed >= 0f)
        assertTrue(cam.sensitivity >= 0f)
        assertTrue(cam.focalLength >= 0.0)

        // Focus Distance
        cam.focusDistance = 7.5f
        assertEquals(7.5f, cam.focusDistance)

        // FOV check
        assertTrue(cam.getFieldOfViewInDegrees(Camera.Fov.VERTICAL) >= 0.0)

        // Cleanup
        engine.destroyCameraComponent(entity)
        EntityManager.get().destroy(entity)
    }
}
