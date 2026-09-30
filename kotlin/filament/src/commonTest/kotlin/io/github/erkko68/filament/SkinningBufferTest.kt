package io.github.erkko68.filament

import io.github.erkko68.filament.testsupport.IgnoreJs
import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SkinningBufferTest : FilamentTestFixture() {
    @Test
    fun testSkinningBufferLifecycle() {
        val buffer = SkinningBuffer.Builder()
            .boneCount(10)
            .initialize(true)
            .build(engine)

        assertNotNull(buffer)
        assertTrue(engine.isValid(buffer))

        assertEquals(10, buffer.boneCount)

        // 10 matrices * 16 floats per matrix = 160 floats
        val matrices = FloatArray(160)
        buffer.setBones(engine, matrices, 10, 0)

        buffer.setBones(engine, Array(10) { RenderableManager.Bone() })

        engine.destroy(buffer)
    }
}
