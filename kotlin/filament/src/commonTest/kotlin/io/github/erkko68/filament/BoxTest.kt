package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoxTest : FilamentTestFixture() {
    @Test
    fun defaultConstructor() {
        val box = Box()
        assertEquals(3, box.center.size)
        assertEquals(3, box.halfExtent.size)
    }

    @Test
    fun parameterizedConstructor() {
        val box = Box(1f, 2f, 3f, 4f, 5f, 6f)
        assertEquals(1f, box.center[0])
        assertEquals(2f, box.center[1])
        assertEquals(3f, box.center[2])
        assertEquals(4f, box.halfExtent[0])
        assertEquals(5f, box.halfExtent[1])
        assertEquals(6f, box.halfExtent[2])
    }

    @Test
    fun setCenterRoundTrip() {
        val box = Box()
        box.setCenter(10f, 20f, 30f)
        assertEquals(10f, box.center[0])
        assertEquals(20f, box.center[1])
        assertEquals(30f, box.center[2])
    }

    @Test
    fun setHalfExtentRoundTrip() {
        val box = Box()
        box.setHalfExtent(5f, 6f, 7f)
        assertEquals(5f, box.halfExtent[0])
        assertEquals(6f, box.halfExtent[1])
        assertEquals(7f, box.halfExtent[2])
    }

    @Test
    fun minMaxAreConsistent() {
        val box = Box(0f, 0f, 0f, 1f, 1f, 1f)
        val min = box.min
        val max = box.max
        assertTrue(min[0] <= max[0])
        assertTrue(min[1] <= max[1])
        assertTrue(min[2] <= max[2])
    }
}
