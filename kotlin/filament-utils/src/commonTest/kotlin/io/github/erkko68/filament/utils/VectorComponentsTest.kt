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

    // Exhaustive: inline short-circuits only count as covered when one call sees every combination.
    @Test
    fun anyAndAll() {
        for (bits in 0 until 4) {
            val v = Bool2(bits and 1 != 0, bits and 2 != 0)
            assertEquals(bits != 0, any(v), "$v"); assertEquals(bits == 3, all(v), "$v")
        }
        for (bits in 0 until 8) {
            val v = Bool3(bits and 1 != 0, bits and 2 != 0, bits and 4 != 0)
            assertEquals(bits != 0, any(v), "$v"); assertEquals(bits == 7, all(v), "$v")
        }
        for (bits in 0 until 16) {
            val v = Bool4(bits and 1 != 0, bits and 2 != 0, bits and 4 != 0, bits and 8 != 0)
            assertEquals(bits != 0, any(v), "$v"); assertEquals(bits == 15, all(v), "$v")
        }
    }

    // Equal within delta as a whole, unequal as soon as any one component is off.
    @Test
    fun equalsWithinDelta() {
        val a2 = Float2(1f, 2f)
        val near2 = listOf(Float2(1.05f, 2.05f) to true) + (0 until 2).map { i -> Float2(a2).also { it[i] += 1f } to false }
        for ((other, expected) in near2) assertEquals(expected, a2.equals(other, 0.1f), "$other")
        val uniform2 = listOf(Float2(2.05f) to true) + (0 until 2).map { i -> Float2(2f).also { it[i] = 3f } to false }
        for ((v, expected) in uniform2) assertEquals(expected, v.equals(2f, 0.1f), "$v")

        val a3 = Float3(1f, 2f, 3f)
        val near3 = listOf(Float3(1.05f, 2.05f, 3.05f) to true) + (0 until 3).map { i -> Float3(a3).also { it[i] += 1f } to false }
        for ((other, expected) in near3) assertEquals(expected, a3.equals(other, 0.1f), "$other")
        val uniform3 = listOf(Float3(2.05f) to true) + (0 until 3).map { i -> Float3(2f).also { it[i] = 3f } to false }
        for ((v, expected) in uniform3) assertEquals(expected, v.equals(2f, 0.1f), "$v")

        val a4 = Float4(1f, 2f, 3f, 4f)
        val near4 = listOf(Float4(1.05f, 2.05f, 3.05f, 4.05f) to true) + (0 until 4).map { i -> Float4(a4).also { it[i] += 1f } to false }
        for ((other, expected) in near4) assertEquals(expected, a4.equals(other, 0.1f), "$other")
        val uniform4 = listOf(Float4(2.05f) to true) + (0 until 4).map { i -> Float4(2f).also { it[i] = 3f } to false }
        for ((v, expected) in uniform4) assertEquals(expected, v.equals(2f, 0.1f), "$v")

        // Without a delta the match has to be exact.
        assertFalse(a2.equals(near2[0].first)); assertFalse(a3.equals(near3[0].first)); assertFalse(a4.equals(near4[0].first))
        assertTrue(a2.equals(Float2(a2))); assertTrue(a3.equals(Float3(a3))); assertTrue(a4.equals(Float4(a4)))
    }

    // -1, 0 or 1 per component, with anything within delta counting as equal.
    @Test
    fun compareToWithinDelta() {
        val pairs2 = listOf(Float2(1.05f, 5f) to Float2(0f, -1f), Float2(0f, 2.05f) to Float2(1f, 0f))
        for ((other, expected) in pairs2) assertEquals(expected, Float2(1f, 2f).compareTo(other, 0.1f))
        for ((v, expected) in listOf(1.05f to Float2(0f, 1f), 2.05f to Float2(-1f, 0f))) assertEquals(expected, Float2(1f, 2f).compareTo(v, 0.1f))

        val pairs3 = listOf(Float3(1.05f, 5f, 0f) to Float3(0f, -1f, 1f), Float3(0f, 2.05f, 3.05f) to Float3(1f, 0f, 0f))
        for ((other, expected) in pairs3) assertEquals(expected, Float3(1f, 2f, 3f).compareTo(other, 0.1f))
        for ((v, expected) in listOf(1.05f to Float3(0f, 1f, 1f), 3.05f to Float3(-1f, -1f, 0f), 2.05f to Float3(-1f, 0f, 1f))) {
            assertEquals(expected, Float3(1f, 2f, 3f).compareTo(v, 0.1f))
        }

        val pairs4 = listOf(Float4(1.05f, 5f, 0f, 4.05f) to Float4(0f, -1f, 1f, 0f), Float4(0f, 2.05f, 3.05f, 9f) to Float4(1f, 0f, 0f, -1f))
        for ((other, expected) in pairs4) assertEquals(expected, Float4(1f, 2f, 3f, 4f).compareTo(other, 0.1f))
        val scalars4 = listOf(1.05f to Float4(0f, 1f, 1f, 1f), 2.05f to Float4(-1f, 0f, 1f, 1f), 3.05f to Float4(-1f, -1f, 0f, 1f), 4.05f to Float4(-1f, -1f, -1f, 0f))
        for ((v, expected) in scalars4) assertEquals(expected, Float4(1f, 2f, 3f, 4f).compareTo(v, 0.1f))
    }
}
