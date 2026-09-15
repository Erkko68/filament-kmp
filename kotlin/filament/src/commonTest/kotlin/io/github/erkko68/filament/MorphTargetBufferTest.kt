package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MorphTargetBufferTest : FilamentTestFixture() {
    @Test
    fun testMorphTargetBufferLifecycle() {
        val buffer = MorphTargetBuffer.Builder()
            .vertexCount(100)
            .count(2)
            .withPositions(true)
            .withTangents(true)
            .enableCustomMorphing(true)
            .build(engine)

        assertNotNull(buffer)
        assertTrue(engine.isValidMorphTargetBuffer(buffer))

        assertEquals(100, buffer.vertexCount)
        assertEquals(2, buffer.count)
        assertTrue(buffer.hasPositions)
        assertTrue(buffer.hasTangents)
        assertTrue(buffer.isCustomMorphingEnabled)

        // 100 vertices * 4 floats (float4: x,y,z,w) = 400 floats
        val positions = FloatArray(400)
        buffer.setPositionsAt(engine, 0, positions, 100)

        // 100 vertices * 4 shorts = 400 shorts
        val tangents = ShortArray(400)
        buffer.setTangentsAt(engine, 0, tangents, 100)

        engine.destroyMorphTargetBuffer(buffer)
    }
}
