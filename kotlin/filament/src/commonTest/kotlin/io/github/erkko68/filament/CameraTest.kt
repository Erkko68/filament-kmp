package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.math.atan
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CameraTest : FilamentTestFixture() {
    // A helper, not @BeforeTest: web runs a subclass's @BeforeTest before the fixture's.
    private fun withCamera(block: (Camera) -> Unit) {
        val entity = EntityManager.get().create()
        try {
            block(engine.createCamera(entity))
        } finally {
            engine.destroyCameraComponent(entity)
            EntityManager.get().destroy(entity)
        }
    }

    private fun assertClose(expected: FloatArray, actual: FloatArray, what: String) =
        expected.indices.forEach { assertEquals(expected[it], actual[it], 1e-5f, "$what[$it]: ${actual.toList()}") }

    private fun dot(a: FloatArray, b: FloatArray) = a.indices.sumOf { (a[it] * b[it]).toDouble() }

    @Test
    fun lookAtPlacesAndOrientsTheCamera() = withCamera { camera ->
        camera.lookAt(1.0, 2.0, 10.0, 1.0, 2.0, 0.0, 0.0, 1.0, 0.0)
        assertContentEquals(doubleArrayOf(1.0, 2.0, 10.0), camera.getPosition())
        assertClose(floatArrayOf(0f, 0f, -1f), camera.getForwardVector(), "forward")
        assertClose(floatArrayOf(0f, 1f, 0f), camera.getUpVector(), "up")
        val left = camera.getLeftVector()
        assertEquals(1.0, sqrt(dot(left, left)), 1e-5)
        assertEquals(0.0, dot(left, camera.getUpVector()), 1e-5)
        assertEquals(0.0, dot(left, camera.getForwardVector()), 1e-5)

        // The model matrix holds the eye; the view matrix takes it back to the origin.
        val model = camera.getModelMatrix()
        assertContentEquals(doubleArrayOf(1.0, 2.0, 10.0), model.copyOfRange(12, 15))
        val view = camera.getViewMatrix()
        for (row in 0 until 3) {
            val eyeInView = view[row] * 1.0 + view[4 + row] * 2.0 + view[8 + row] * 10.0 + view[12 + row]
            assertEquals(0.0, eyeInView, 1e-9, "view * eye, row $row")
        }
    }

    @Test
    fun perspectiveProjectionKeepsItsParameters() = withCamera { camera ->
        camera.setProjection(90.0, 1.0, 0.5, 50.0, Camera.Fov.VERTICAL)
        assertEquals(0.5, camera.near)
        assertEquals(50.0, camera.cullingFar)
        assertEquals(90f, camera.getFieldOfViewInDegrees(Camera.Fov.VERTICAL), 1e-3f)
        assertEquals(90f, camera.getFieldOfViewInDegrees(Camera.Fov.HORIZONTAL), 1e-3f)
        // 1 / tan(45°) on both axes.
        val projection = camera.getProjectionMatrix()
        assertEquals(1.0, projection[0], 1e-9)
        assertEquals(1.0, projection[5], 1e-9)
    }

    @Test
    fun lensProjectionUsesAFullFrameSensor() = withCamera { camera ->
        camera.setLensProjection(50.0, 1.0, 0.1, 100.0)
        assertEquals(0.05, camera.focalLength, 1e-9)
        // 24 mm sensor height: 2·atan(12 / 50).
        val expected = (2 * atan(12.0 / 50.0) * 180 / kotlin.math.PI).toFloat()
        assertEquals(expected, camera.getFieldOfViewInDegrees(Camera.Fov.VERTICAL), 1e-3f)
    }

    @Test
    fun customProjectionIsReturnedAsGiven() = withCamera { camera ->
        val projection = Camera.projection(Camera.Fov.HORIZONTAL, 60.0, 1.5, 0.1, 100.0)
        camera.setCustomProjection(projection, 0.1, 100.0)
        val read = camera.getProjectionMatrix()
        projection.indices.forEach { assertEquals(projection[it], read[it], 1e-9, "[$it]") }

        camera.setScaling(1.5, 2.5)
        assertContentEquals(doubleArrayOf(1.5, 2.5), camera.getScaling().copyOf(2))
        camera.setShift(0.1, 0.2)
        assertContentEquals(doubleArrayOf(0.1, 0.2), camera.getShift())
    }

    @Test
    fun exposureSettingsRoundTrip() = withCamera { camera ->
        camera.setExposure(16f, 1f / 125f, 100f)
        assertEquals(16f, camera.aperture)
        assertEquals(1f / 125f, camera.shutterSpeed)
        assertEquals(100f, camera.sensitivity)
        // A bare exposure is f/1, 1.2 s at the ISO that gives it (Filament clamps ISO to 10 and up).
        camera.setExposure(0.5f)
        assertEquals(1f, camera.aperture)
        assertEquals(1.2f, camera.shutterSpeed, 1e-6f)
        assertEquals(200f, camera.sensitivity, 1e-3f)
        camera.focusDistance = 7.5f
        assertEquals(7.5f, camera.focusDistance)
    }

    @Test
    fun staticProjectionHelpers() {
        val lens = Camera.projection(50.0, 1.0, 0.1, 100.0)
        val inverse = Camera.inverseProjection(lens)
        val identity = DoubleArray(16) { i -> (0..3).sumOf { k -> lens[k * 4 + i % 4] * inverse[(i / 4) * 4 + k] } }
        for (i in 0 until 16) assertEquals(if (i % 5 == 0) 1.0 else 0.0, identity[i], 1e-9)
        val inverseF = Camera.inverseProjection(FloatArray(16) { lens[it].toFloat() })
        for (i in 0 until 16) assertEquals(inverse[i].toFloat(), inverseF[i], 1e-4f)
        assertTrue(Camera.computeEffectiveFocalLength(0.05, 10.0) > 0.05)
        // Focusing closer narrows the effective field of view.
        assertTrue(Camera.computeEffectiveFov(60.0, 1.0) < 60.0)
    }
}
