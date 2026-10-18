package io.github.erkko68.filament.compose

import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.requestsFrames
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** [rememberRenderTargetTexture]'s frame requests. */
class RenderTargetTextureTest : ComposeTestFixture() {

    /** `renderingEnabled = false` keeps the frame it has, so it first renders one, and only then stops. */
    @Test
    fun pausedTextureRendersItsSceneThenStops() {
        val handle = FilamentScene(engine, scene)

        assertTrue(requestsFrames(engine, scene) { rememberRenderTargetTexture(handle, renderingEnabled = false) })
        assertFalse(requestsFrames(engine, scene, frames = 5) { rememberRenderTargetTexture(handle, renderingEnabled = false) })
        assertTrue(requestsFrames(engine, scene, frames = 5) { rememberRenderTargetTexture(handle) })
    }
}
