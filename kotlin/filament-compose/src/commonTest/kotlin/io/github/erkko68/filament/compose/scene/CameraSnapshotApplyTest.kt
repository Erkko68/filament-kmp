package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.Camera
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.utils.Float2
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verifies [CameraSnapshot.applyTo] pushes eye/target/up, each [Projection] variant, exposure,
 * shift and scaling onto a (NOOP) [Camera], read back through the camera's own getters. Cameras are
 * freed by the engine on fixture teardown.
 */
class CameraSnapshotApplyTest : ComposeTestFixture() {

    private fun newCamera(): Camera = engine.createCamera()

    private fun snapshot(
        eye: Position = Position(0f, 1f, 10f),
        target: Position = Position(0f, 0f, 0f),
        up: Direction = Direction(0f, 1f, 0f),
        projection: Projection = Projection.Perspective(),
        exposure: Exposure = Exposure(),
        shift: Float2 = Float2(0f, 0f),
        scaling: Float2 = Float2(1f, 1f),
    ) = CameraSnapshot(eye, target, up, projection, exposure, shift, scaling)

    @Test
    fun lookAtPositionsTheCamera() {
        val camera = newCamera()
        snapshot(eye = Position(3f, 4f, 5f)).applyTo(camera, aspect = 1.0)
        val pos = camera.getPosition()
        assertEquals(3f, pos[0], 1e-4f)
        assertEquals(4f, pos[1], 1e-4f)
        assertEquals(5f, pos[2], 1e-4f)
    }

    @Test
    fun perspectiveProjectionApplies() {
        val camera = newCamera()
        snapshot(projection = Projection.Perspective(fovDegrees = 60.0, near = 0.5, far = 200.0))
            .applyTo(camera, aspect = 2.0)
        assertEquals(0.5f, camera.near, 1e-4f)
        assertEquals(60.0, camera.getFieldOfViewInDegrees(Camera.Fov.VERTICAL), 1e-3)
    }

    @Test
    fun orthographicProjectionApplies() {
        val camera = newCamera()
        snapshot(projection = Projection.Orthographic(near = 0.0, far = 10.0))
            .applyTo(camera, aspect = 1.0)
        assertEquals(0f, camera.near, 1e-4f)
    }

    @Test
    fun lensProjectionApplies() {
        val camera = newCamera()
        snapshot(projection = Projection.Lens(focalLength = 50.0, near = 0.2, far = 80.0))
            .applyTo(camera, aspect = 1.5)
        assertEquals(0.2f, camera.near, 1e-4f)
    }

    @Test
    fun exposureShiftScalingRoundTrip() {
        val camera = newCamera()
        snapshot(
            exposure = Exposure(aperture = 8f, shutterSpeed = 1f / 60f, sensitivity = 200f),
            shift = Float2(0.1f, 0.2f),
            scaling = Float2(0.5f, 0.5f),
        ).applyTo(camera, aspect = 1.0)

        assertEquals(8f, camera.aperture, 1e-4f)
        assertEquals(1f / 60f, camera.shutterSpeed, 1e-4f)
        assertEquals(200f, camera.sensitivity, 1e-4f)
        val shift = camera.getShift()
        assertEquals(0.1, shift[0], 1e-4)
        assertEquals(0.2, shift[1], 1e-4)
        val scaling = camera.getScaling()
        assertEquals(0.5, scaling[0], 1e-4)
        assertEquals(0.5, scaling[1], 1e-4)
    }
}
