package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FenceTest : FilamentTestFixture() {
    @Test
    fun fencesSignalOnceTheQueueIsFlushed() {
        val fence = engine.createFence()
        assertTrue(engine.isValid(fence))
        assertEquals(Fence.FenceStatus.CONDITION_SATISFIED, fence.wait(Fence.Mode.FLUSH, Fence.WAIT_FOR_EVER))
        assertEquals(Fence.FenceStatus.CONDITION_SATISFIED, Fence.waitAndDestroy(engine.createFence(), Fence.Mode.FLUSH))
        engine.destroy(fence)
    }
}
