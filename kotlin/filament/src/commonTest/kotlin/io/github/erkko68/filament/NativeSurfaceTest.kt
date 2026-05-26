package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.createTestSurface
import kotlin.test.Test
import kotlin.test.assertNotNull

class NativeSurfaceTest {
    @Test
    fun testCreateNativeSurface() {
        val surface = createTestSurface()
        assertNotNull(surface)
    }
}
