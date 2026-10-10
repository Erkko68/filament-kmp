package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.Camera
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.testsupport.TestEnv
import io.github.erkko68.filament.testsupport.TestTarget
import io.github.erkko68.filament.utils.Float4
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Verifies [CameraSnapshot.applyTo] pushes eye/target/up, each [Projection] variant, exposure,
 * shift and scaling onto a (NOOP) [Camera], read back through the camera's own getters. Cameras are
 * freed by the engine on fixture teardown.
 */
class CameraSnapshotApplyTest : ComposeTestFixture() {

    private fun newCamera(): Camera = engine.createCamera(engine.entityManager.create())

    private fun snapshot(
        eye: Position = Position(0f, 1f, 10f),
        target: Position = Position(0f, 0f, 0f),
        up: Direction = Direction(0f, 1f, 0f),
        projection: Projection = Projection.Perspective(),
        exposure: Exposure = Exposure(),
        focusDistance: Float = 10f,
        shift: LensShift = LensShift.None,
        scaling: LensScaling = LensScaling.Identity,
    ) = CameraSnapshot(eye, target, up, projection, exposure, focusDistance, shift, scaling)

    @Test
    fun lookAtPositionsTheCamera() {
        val camera = newCamera()
        snapshot(eye = Position(3f, 4f, 5f)).applyTo(camera, aspect = 1.0)
        val pos = camera.getPosition()
        assertEquals(3.0, pos[0], 1e-4)
        assertEquals(4.0, pos[1], 1e-4)
        assertEquals(5.0, pos[2], 1e-4)
    }

    @Test
    fun perspectiveProjectionApplies() {
        val camera = newCamera()
        snapshot(projection = Projection.Perspective(fovDegrees = 60.0, near = 0.5, far = 200.0))
            .applyTo(camera, aspect = 2.0)
        assertEquals(0.5, camera.near, 1e-4)
        // getFieldOfViewInDegrees is unbound in the web wrapper (stubbed to 0), so only check it
        // where Filament actually recovers the FOV from the projection matrix.
        if (TestEnv.target != TestTarget.JS) {
            assertEquals(60f, camera.getFieldOfViewInDegrees(Camera.Fov.VERTICAL), 1e-3f)
        }
    }

    @Test
    fun orthographicProjectionApplies() {
        val camera = newCamera()
        snapshot(projection = Projection.Orthographic(near = 0.0, far = 10.0))
            .applyTo(camera, aspect = 1.0)
        assertEquals(0.0, camera.near, 1e-4)
    }

    @Test
    fun lensProjectionApplies() {
        val camera = newCamera()
        snapshot(projection = Projection.Lens(focalLength = 50.0, near = 0.2, far = 80.0))
            .applyTo(camera, aspect = 1.5)
        assertEquals(0.2, camera.near, 1e-4)
    }

    @Test
    fun exposureShiftScalingRoundTrip() {
        val camera = newCamera()
        snapshot(
            exposure = Exposure(aperture = 8f, shutterSpeed = 1f / 60f, sensitivity = 200f),
            shift = LensShift(0.1f, 0.2f),
            scaling = LensScaling(0.5f, 0.5f),
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

    @Test
    fun matricesAreReadableOnlyWhileAttached() {
        val state = CameraState(
            Position(3f, 4f, 5f), Position(0f), Direction(0f, 1f, 0f), Projection.Perspective(), Exposure(),
            10f, LensShift.None, LensScaling.Identity,
        )
        assertNull(state.viewMatrix)
        assertNull(state.projectionMatrix)

        val camera = newCamera()
        state.attach(camera)
        state.snapshot().applyTo(camera, aspect = 1.0)
        // The view matrix takes the eye to the origin; a perspective projection divides by -z.
        val eyeInView = state.viewMatrix!! * Float4(3f, 4f, 5f, 1f)
        assertEquals(0f, eyeInView.x, 1e-4f)
        assertEquals(0f, eyeInView.y, 1e-4f)
        assertEquals(0f, eyeInView.z, 1e-4f)
        assertEquals(-1f, state.projectionMatrix!![2, 3])

        state.detach(camera)
        assertNull(state.viewMatrix)
    }

    /** One state per view: a second camera is refused, and only the camera it holds can detach it. */
    @Test
    fun aStateAttachesToOneCameraAtATime() {
        val state = CameraState(
            Position(3f, 4f, 5f), Position(0f), Direction(0f, 1f, 0f), Projection.Perspective(), Exposure(),
            10f, LensShift.None, LensScaling.Identity,
        )
        val first = newCamera()
        val second = newCamera()
        state.attach(first)
        state.attach(first) // a view re-attaching its own camera is fine
        val failure = assertFailsWith<IllegalStateException> { state.attach(second) }
        assertTrue("its own CameraState" in failure.message.orEmpty(), "was: ${failure.message}")

        // The refused view going away must not blank the state for the one that holds it.
        state.detach(second)
        assertNotNull(state.viewMatrix)

        state.detach(first)
        assertNull(state.viewMatrix)
        state.attach(second)
        assertNotNull(state.viewMatrix)
    }
}
