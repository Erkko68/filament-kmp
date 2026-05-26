package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FenceTest : FilamentTestFixture() {
    @Test
    fun testFenceLifecycle() {
        val fence = engine.createFence()
        assertNotNull(fence)
        assertTrue(engine.isValidFence(fence))

        assertTrue(fence.nativeObject != 0L)

        // Wait on the fence
        val status = fence.wait(Fence.Mode.FLUSH, 1000000L)
        assertNotNull(status)

        // Wait and destroy
        val status2 = Fence.waitAndDestroy(fence, Fence.Mode.FLUSH)
        assertNotNull(status2)
    }
}
