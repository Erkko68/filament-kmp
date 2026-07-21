package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.assertEntitiesDestroyed
import io.github.erkko68.filament.compose.testutils.assertSceneEmpty
import io.github.erkko68.filament.compose.testutils.composeScene
import io.github.erkko68.filament.utils.Float2
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Coverage for [CameraNode]: it creates no entity of its own — it drives a hoisted [CameraState]'s
 * eye/target/up from the enclosing [Group]'s world transform every frame. Verifies the per-frame
 * drive runs (the camera follows the group) and that nothing leaks on disposal.
 */
class CameraNodeLifecycleTest : ComposeTestFixture() {

    private fun newCameraState() = CameraState(
        initialEye = Position(0f),
        initialTarget = Position(0f, 0f, -1f),
        initialUp = Direction(0f, 1f, 0f),
        initialProjection = Projection.Perspective(),
        initialExposure = Exposure(),
        initialFocusDistance = 10f,
        initialShift = Float2(0f, 0f),
        initialScaling = Float2(1f, 1f),
    )

    // Assertions live in the harness callbacks and the test returns the harness result so JS's
    // asynchronous `runComposeUiTest` is awaited — see composeScene's KDoc.
    @Test
    fun cameraFollowsGroupTransform() = run {
        val cam = newCameraState()
        var groupEntity = -1
        composeScene(
            engine, scene,
            frames = 2, // CameraNode drives the state in an OnFrame loop
            whileComposed = {
                // eyeOffset defaults to the group origin, so eye tracks the group's translation.
                assertEquals(5f, cam.eye.x, 1e-4f)
                assertEquals(2f, cam.eye.y, 1e-4f)
                assertEquals(-3f, cam.eye.z, 1e-4f)
            },
            afterDispose = {
                assertSceneEmpty(scene)
                assertEntitiesDestroyed(engine, intArrayOf(groupEntity))
            },
        ) {
            Group(position = Position(5f, 2f, -3f), onCreate = { groupEntity = entity }) {
                CameraNode(cam)
            }
        }
    }
}
