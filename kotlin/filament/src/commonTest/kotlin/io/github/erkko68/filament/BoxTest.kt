package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoxTest : FilamentTestFixture() {
    // Column-major: rotates 90° about z (x → y), then translates by (10, 20, 30).
    private val rotateZ = floatArrayOf(0f, 1f, 0f, -1f, 0f, 0f, 0f, 0f, 1f)
    private val mat4 = floatArrayOf(0f, 1f, 0f, 0f, -1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 10f, 20f, 30f, 1f)

    @Test
    fun boxQueries() {
        val box = Box(floatArrayOf(1f, 2f, 3f), floatArrayOf(4f, 5f, 6f))
        assertFalse(box.isEmpty())
        assertTrue(Box().isEmpty())
        assertContentEquals(floatArrayOf(-3f, -3f, -3f), box.getMin())
        assertContentEquals(floatArrayOf(5f, 7f, 9f), box.getMax())
        assertContentEquals(floatArrayOf(1f, 2f, 3f, 0f), Box(floatArrayOf(1f, 2f, 3f), FloatArray(3)).getBoundingSphere())
        assertEquals(Box(floatArrayOf(7f, 8f, 9f), floatArrayOf(4f, 5f, 6f)), box.translateTo(floatArrayOf(7f, 8f, 9f)))
    }

    @Test
    fun boxSetAndUnion() {
        val box = Box().set(floatArrayOf(0f, 0f, 0f), floatArrayOf(2f, 4f, 6f))
        assertEquals(Box(floatArrayOf(1f, 2f, 3f), floatArrayOf(1f, 2f, 3f)), box)
        box.unionSelf(Box().set(floatArrayOf(-2f, 0f, 0f), floatArrayOf(0f, 0f, 0f)))
        assertContentEquals(floatArrayOf(-2f, 0f, 0f), box.getMin())
        assertContentEquals(floatArrayOf(2f, 4f, 6f), box.getMax())
    }

    @Test
    fun boxTransform() {
        val box = Box(floatArrayOf(1f, 0f, 0f), floatArrayOf(1f, 2f, 3f))
        val expected = Box(floatArrayOf(10f, 21f, 30f), floatArrayOf(2f, 1f, 3f))
        assertEquals(expected, Box.transform(rotateZ, floatArrayOf(10f, 20f, 30f), box))
        assertEquals(expected, rigidTransform(box, mat4))
    }

    @Test
    fun aabbQueries() {
        val aabb = Aabb(floatArrayOf(0f, 0f, 0f), floatArrayOf(2f, 4f, 6f))
        assertTrue(Aabb().isEmpty())
        assertFalse(aabb.isEmpty())
        assertContentEquals(floatArrayOf(1f, 2f, 3f), aabb.center())
        assertContentEquals(floatArrayOf(1f, 2f, 3f), aabb.extent())
        assertEquals(-1f, aabb.contains(floatArrayOf(1f, 1f, 1f)))
        assertEquals(1f, aabb.contains(floatArrayOf(3f, 1f, 1f)))
        val corners = aabb.getCorners()
        assertEquals(8, corners.size)
        assertContentEquals(floatArrayOf(0f, 0f, 0f), corners[0])
        assertContentEquals(floatArrayOf(2f, 0f, 0f), corners[1])
        assertContentEquals(floatArrayOf(2f, 4f, 6f), corners[7])
    }

    @Test
    fun aabbTransform() {
        val aabb = Aabb(floatArrayOf(0f, -1f, -3f), floatArrayOf(2f, 1f, 3f))
        val expected = Aabb(floatArrayOf(9f, 20f, 27f), floatArrayOf(11f, 22f, 33f))
        assertEquals(expected, Aabb.transform(rotateZ, floatArrayOf(10f, 20f, 30f), aabb))
        assertEquals(expected, aabb.transform(mat4))
    }
}
