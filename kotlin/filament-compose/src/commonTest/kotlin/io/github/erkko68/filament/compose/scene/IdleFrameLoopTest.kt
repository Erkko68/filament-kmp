package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.requestsFrames
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * A scene node runs a frame loop only while it has per-frame work: an idle one would keep the window redrawing
 * every vsync, also with `renderingEnabled = false`. Each idle case is paired with the one that does need the loop.
 */
class IdleFrameLoopTest : ComposeTestFixture() {

    @Test
    fun lightTurningWithItsGroupRunsNoFrameLoop() {
        assertFalse(requestsFrames(engine, scene) { DirectionalLight(followGroupRotation = false) })
        // Filament turns a parented light itself; a point light has no direction to pin.
        assertFalse(requestsFrames(engine, scene) { Group { DirectionalLight() } })
        assertFalse(requestsFrames(engine, scene) { Group { PointLight() } })
    }

    @Test
    fun lightPinnedInWorldSpaceRunsAFrameLoop() {
        assertTrue(requestsFrames(engine, scene) { Group { DirectionalLight(followGroupRotation = false) } })
    }

    @Test
    fun cameraNodeOutsideAGroupRunsNoFrameLoop() {
        assertFalse(requestsFrames(engine, scene) { CameraNode(rememberCameraState()) })
    }

    @Test
    fun cameraNodeFollowingItsGroupRunsAFrameLoop() {
        assertTrue(requestsFrames(engine, scene) { Group { CameraNode(rememberCameraState()) } })
    }
}
