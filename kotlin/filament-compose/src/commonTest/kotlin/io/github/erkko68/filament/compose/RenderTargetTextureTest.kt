package io.github.erkko68.filament.compose

import io.github.erkko68.filament.compose.testutils.TierBSceneFixture
import io.github.erkko68.filament.compose.testutils.requestsFrames
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** [rememberRenderTargetTexture] on a real backend (render targets need one); skips without a GPU. */
class RenderTargetTextureTest : TierBSceneFixture() {

    /** `renderingEnabled = false` keeps the frame it has, so it first renders one, and only then stops. */
    @Test
    fun pausedTextureRendersItsSceneThenStops() {
        val engine = engine ?: return
        val scene = scene ?: return
        val handle = FilamentScene(engine, scene)

        assertTrue(requestsFrames(engine, scene) { rememberRenderTargetTexture(handle, renderingEnabled = false) })
        assertFalse(requestsFrames(engine, scene, frames = 5) { rememberRenderTargetTexture(handle, renderingEnabled = false) })
        assertTrue(requestsFrames(engine, scene, frames = 5) { rememberRenderTargetTexture(handle) })
    }
}
