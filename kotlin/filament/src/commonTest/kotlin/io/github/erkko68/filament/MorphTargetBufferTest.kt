package io.github.erkko68.filament

import io.github.erkko68.filament.testsupport.IgnoreJs
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
        assertTrue(engine.isValid(buffer))

        assertEquals(100, buffer.vertexCount)
        assertEquals(2, buffer.count)
        assertTrue(buffer.hasPositions)
        assertTrue(buffer.hasTangents)
        assertTrue(buffer.isCustomMorphingEnabled)

        // 100 vertices * 3 floats (float3: x,y,z)
        val positions = FloatArray(300)
        buffer.setPositionsAt(engine, 0, positions, 100)

        // 100 vertices * 4 shorts = 400 shorts
        val tangents = ShortArray(400)
        buffer.setTangentsAt(engine, 0, tangents, 100)

        engine.destroy(buffer)
    }
}
