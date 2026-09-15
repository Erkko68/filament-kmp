package io.github.erkko68.filament

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
        assertTrue(engine.isValidSkinningBuffer(buffer))

        assertEquals(10, buffer.boneCount)

        // 10 matrices * 16 floats per matrix = 160 floats
        val matrices = FloatArray(160)
        buffer.setBonesAsMatrices(engine, matrices, 10, 0)

        // 10 quaternions * 8 floats per bone = 80 floats
        val quaternions = FloatArray(80)
        buffer.setBonesAsQuaternions(engine, quaternions, 10, 0)

        engine.destroySkinningBuffer(buffer)
    }
}
