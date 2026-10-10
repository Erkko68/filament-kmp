package io.github.erkko68.filament.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Component access by alias and index, and the checks that short-circuit across components, for every vector type. */
class VectorComponentsTest {
    // Reads and writes through every valid index reach exactly their field; the others throw.
    private fun <V, I, T> checkIndexing(
        make: () -> V, fields: (V) -> List<T>, fresh: Pair<T, T>,
        valid: List<Pair<I, Int>>, invalid: List<I>,
        get: (V, I) -> T, set: (V, I, T) -> Unit,
    ) {
        for ((index, field) in valid) {
            val v = make()
            val before = fields(v)
            assertEquals(before[field], get(v, index), "get $index")
            val value = if (before[field] == fresh.first) fresh.second else fresh.first
            set(v, index, value)
            assertEquals(before.toMutableList().also { it[field] = value }, fields(v), "set $index")
        }
        for (index in invalid) {
            val v = make()
            assertFailsWith<IllegalArgumentException>("get $index") { get(v, index) }
            assertFailsWith<IllegalArgumentException>("set $index") { set(v, index, fresh.first) }
            assertEquals(fields(make()), fields(v), "a rejected set $index changed the vector")
        }
    }

    // X=R=S, Y=G=T, Z=B=P, W=A=Q: the aliases a vector of [size] components has, and the ones it lacks.
    private fun <V, T> check(
        size: Int, make: () -> V, fields: (V) -> List<T>, fresh: Pair<T, T>,
        getAlias: (V, VectorComponent) -> T, setAlias: (V, VectorComponent, T) -> Unit,
        getIndex: (V, Int) -> T, setIndex: (V, Int, T) -> Unit,
    ) {
        val (has, lacks) = VectorComponent.entries.partition { it.ordinal % 4 < size }
        assertEquals(fields(make()).size, size)
        checkIndexing(make, fields, fresh, has.map { it to it.ordinal % 4 }, lacks, getAlias, setAlias)
        checkIndexing(make, fields, fresh, (0 until size).map { it to it }, listOf(-1, size), getIndex, setIndex)
    }

    @Test
    fun floatComponents() {
        check(2, { Float2(1f, 2f) }, { listOf(it.x, it.y) }, 7f to 8f, { v, c -> v[c] }, { v, c, x -> v[c] = x }, { v, i -> v[i] }, { v, i, x -> v[i] = x })
        check(3, { Float3(1f, 2f, 3f) }, { listOf(it.x, it.y, it.z) }, 7f to 8f, { v, c -> v[c] }, { v, c, x -> v[c] = x }, { v, i -> v[i] }, { v, i, x -> v[i] = x })
        check(4, { Float4(1f, 2f, 3f, 4f) }, { listOf(it.x, it.y, it.z, it.w) }, 7f to 8f, { v, c -> v[c] }, { v, c, x -> v[c] = x }, { v, i -> v[i] }, { v, i, x -> v[i] = x })
    }

    @Test
    fun halfComponents() {
        val h = { f: Float -> Half(f) }
        val fresh = h(7f) to h(8f)
        check(2, { Half2(h(1f), h(2f)) }, { listOf(it.x, it.y) }, fresh, { v, c -> v[c] }, { v, c, x -> v[c] = x }, { v, i -> v[i] }, { v, i, x -> v[i] = x })
        check(3, { Half3(h(1f), h(2f), h(3f)) }, { listOf(it.x, it.y, it.z) }, fresh, { v, c -> v[c] }, { v, c, x -> v[c] = x }, { v, i -> v[i] }, { v, i, x -> v[i] = x })
        check(4, { Half4(h(1f), h(2f), h(3f), h(4f)) }, { listOf(it.x, it.y, it.z, it.w) }, fresh, { v, c -> v[c] }, { v, c, x -> v[c] = x }, { v, i -> v[i] }, { v, i, x -> v[i] = x })
    }

    @Test
    fun intComponents() {
        check(2, { Int2(1, 2) }, { listOf(it.x, it.y) }, 7 to 8, { v, c -> v[c] }, { v, c, x -> v[c] = x }, { v, i -> v[i] }, { v, i, x -> v[i] = x })
        check(3, { Int3(1, 2, 3) }, { listOf(it.x, it.y, it.z) }, 7 to 8, { v, c -> v[c] }, { v, c, x -> v[c] = x }, { v, i -> v[i] }, { v, i, x -> v[i] = x })
        check(4, { Int4(1, 2, 3, 4) }, { listOf(it.x, it.y, it.z, it.w) }, 7 to 8, { v, c -> v[c] }, { v, c, x -> v[c] = x }, { v, i -> v[i] }, { v, i, x -> v[i] = x })
    }

    @Test
    fun boolComponents() {
        check(2, { Bool2(true, false) }, { listOf(it.x, it.y) }, true to false, { v, c -> v[c] }, { v, c, x -> v[c] = x }, { v, i -> v[i] }, { v, i, x -> v[i] = x })
        check(3, { Bool3(true, false, true) }, { listOf(it.x, it.y, it.z) }, true to false, { v, c -> v[c] }, { v, c, x -> v[c] = x }, { v, i -> v[i] }, { v, i, x -> v[i] = x })
        check(4, { Bool4(true, false, true, false) }, { listOf(it.x, it.y, it.z, it.w) }, true to false, { v, c -> v[c] }, { v, c, x -> v[c] = x }, { v, i -> v[i] }, { v, i, x -> v[i] = x })
    }

    // One component deciding the result, at each position.
    @Test
    fun anyAndAll() {
        assertFalse(any(Bool2())); assertTrue(all(Bool2(true, true)))
        for (i in 0 until 2) {
            val one = Bool2().also { it[i] = true }
            val allButOne = Bool2(true, true).also { it[i] = false }
            assertTrue(any(one)); assertFalse(all(one)); assertTrue(any(allButOne)); assertFalse(all(allButOne))
        }
        assertFalse(any(Bool3())); assertTrue(all(Bool3(true, true, true)))
        for (i in 0 until 3) {
            val one = Bool3().also { it[i] = true }
            val allButOne = Bool3(true, true, true).also { it[i] = false }
            assertTrue(any(one)); assertFalse(all(one)); assertTrue(any(allButOne)); assertFalse(all(allButOne))
        }
        assertFalse(any(Bool4())); assertTrue(all(Bool4(true, true, true, true)))
        for (i in 0 until 4) {
            val one = Bool4().also { it[i] = true }
            val allButOne = Bool4(true, true, true, true).also { it[i] = false }
            assertTrue(any(one)); assertFalse(all(one)); assertTrue(any(allButOne)); assertFalse(all(allButOne))
        }
    }

    // Equal within delta as a whole, unequal as soon as any one component is off.
    @Test
    fun equalsWithinDelta() {
        val a2 = Float2(1f, 2f)
        assertTrue(a2.equals(Float2(1.05f, 2.05f), 0.1f)); assertFalse(a2.equals(Float2(1.05f, 2.05f)))
        assertTrue(Float2(2f).equals(2.05f, 0.1f)); assertFalse(Float2(2f).equals(2.05f))
        for (i in 0 until 2) {
            assertFalse(a2.equals(Float2(a2).also { it[i] += 1f }, 0.1f), "component $i")
            assertFalse(Float2(2f).also { it[i] = 3f }.equals(2f, 0.1f), "component $i")
        }
        val a3 = Float3(1f, 2f, 3f)
        assertTrue(a3.equals(Float3(1.05f, 2.05f, 3.05f), 0.1f)); assertFalse(a3.equals(Float3(1.05f, 2.05f, 3.05f)))
        assertTrue(Float3(2f).equals(2.05f, 0.1f)); assertFalse(Float3(2f).equals(2.05f))
        for (i in 0 until 3) {
            assertFalse(a3.equals(Float3(a3).also { it[i] += 1f }, 0.1f), "component $i")
            assertFalse(Float3(2f).also { it[i] = 3f }.equals(2f, 0.1f), "component $i")
        }
        val a4 = Float4(1f, 2f, 3f, 4f)
        assertTrue(a4.equals(Float4(1.05f, 2.05f, 3.05f, 4.05f), 0.1f)); assertFalse(a4.equals(Float4(1.05f, 2.05f, 3.05f, 4.05f)))
        assertTrue(Float4(2f).equals(2.05f, 0.1f)); assertFalse(Float4(2f).equals(2.05f))
        for (i in 0 until 4) {
            assertFalse(a4.equals(Float4(a4).also { it[i] += 1f }, 0.1f), "component $i")
            assertFalse(Float4(2f).also { it[i] = 3f }.equals(2f, 0.1f), "component $i")
        }
    }

    // -1, 0 or 1 per component, with anything within delta counting as equal.
    @Test
    fun compareToWithinDelta() {
        assertEquals(Float2(0f, -1f), Float2(1f, 2f).compareTo(Float2(1.05f, 5f), 0.1f))
        assertEquals(Float2(1f, 0f), Float2(1f, 2f).compareTo(Float2(0f, 2.05f), 0.1f))
        assertEquals(Float2(0f, 1f), Float2(1f, 2f).compareTo(1.05f, 0.1f))
        assertEquals(Float2(-1f, 0f), Float2(1f, 2f).compareTo(2.05f, 0.1f))
        assertEquals(Float3(0f, -1f, 1f), Float3(1f, 2f, 3f).compareTo(Float3(1.05f, 5f, 0f), 0.1f))
        assertEquals(Float3(1f, 0f, 0f), Float3(1f, 2f, 3f).compareTo(Float3(0f, 2.05f, 3.05f), 0.1f))
        assertEquals(Float3(0f, 1f, 1f), Float3(1f, 2f, 3f).compareTo(1.05f, 0.1f))
        assertEquals(Float3(-1f, 0f, 0f), Float3(1f, 2f, 2f).compareTo(2.05f, 0.1f))
        assertEquals(Float4(0f, -1f, 1f, 0f), Float4(1f, 2f, 3f, 4f).compareTo(Float4(1.05f, 5f, 0f, 4.05f), 0.1f))
        assertEquals(Float4(1f, 0f, 0f, -1f), Float4(1f, 2f, 3f, 4f).compareTo(Float4(0f, 2.05f, 3.05f, 9f), 0.1f))
        assertEquals(Float4(0f, 1f, 1f, 1f), Float4(1f, 2f, 3f, 4f).compareTo(1.05f, 0.1f))
        assertEquals(Float4(-1f, 0f, 0f, 0f), Float4(1f, 2f, 2f, 2f).compareTo(2.05f, 0.1f))
    }
}
