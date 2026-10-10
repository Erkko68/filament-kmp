package io.github.erkko68.filament.utils

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Component-wise comparisons of every vector type, in function and infix form, against a vector and a scalar.
 *
 * The functions are inline, so each call below runs over a table rather than once: every component reaches both
 * outcomes, and the operands differ between every pair of components, so a comparison reading the wrong one fails.
 */
class VectorComparisonTest {
    private class Row<A, B, M>(val a: A, val b: B, val lt: M, val lte: M, val gt: M, val gte: M, val eq: M, val neq: M)

    // m is mid everywhere and s is ascending; b1, b2 and b3 are hi or lo where odd, even and front say hi.
    private fun <V, M> vectorRows(m: V, s: V, b1: V, b2: V, b3: V, odd: M, even: M, front: M, back: M, all: M, none: M) = listOf(
        Row(m, b1, lt = odd, lte = odd, gt = even, gte = even, eq = none, neq = all),
        Row(m, b2, lt = even, lte = even, gt = odd, gte = odd, eq = none, neq = all),
        Row(m, b3, lt = front, lte = front, gt = back, gte = back, eq = none, neq = all),
        Row(b1, m, lt = even, lte = even, gt = odd, gte = odd, eq = none, neq = all),
        Row(b2, m, lt = odd, lte = odd, gt = even, gte = even, eq = none, neq = all),
        Row(b3, m, lt = back, lte = back, gt = front, gte = front, eq = none, neq = all),
        Row(s, s, lt = none, lte = all, gt = none, gte = all, eq = all, neq = none),
    )

    private fun <V, S, M> scalarRows(m: V, mid: S, b1: V, b2: V, b3: V, odd: M, even: M, front: M, back: M, all: M, none: M) = listOf(
        Row(b1, mid, lt = even, lte = even, gt = odd, gte = odd, eq = none, neq = all),
        Row(b2, mid, lt = odd, lte = odd, gt = even, gte = even, eq = none, neq = all),
        Row(b3, mid, lt = back, lte = back, gt = front, gte = front, eq = none, neq = all),
        Row(m, mid, lt = none, lte = all, gt = none, gte = all, eq = all, neq = none),
    )

    @Test
    fun float2() {
        val lo = 1f; val mid = 5f; val hi = 9f
        val m = Float2(mid, mid); val s = Float2(lo, mid)
        val b1 = Float2(hi, lo); val b2 = Float2(lo, hi); val b3 = Float2(hi, hi)
        val odd = Bool2(true, false); val even = Bool2(false, true); val front = Bool2(true, true); val back = Bool2(false, false)
        val all = Bool2(true, true); val none = Bool2(false, false)

        for (r in vectorRows(m, s, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }
        for (r in scalarRows(m, mid, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }

        // Within delta counts as equal.
        val near = Float2(mid + 0.05f, mid + 0.05f)
        for ((delta, same, different) in listOf(Triple(0.1f, all, none), Triple(0f, none, all))) {
            assertEquals(same, equal(near, m, delta)); assertEquals(same, equal(near, mid, delta))
            assertEquals(different, notEqual(near, m, delta)); assertEquals(different, notEqual(near, mid, delta))
        }

        // Every component below, within and above the range in turn.
        val min = 3f; val max = 7f
        val vmin = Float2(min, min); val vmax = Float2(max, max)
        val clamped = listOf(
            Float2(lo, mid) to Float2(min, mid),
            Float2(mid, hi) to Float2(mid, max),
            Float2(hi, lo) to Float2(max, min),
        )
        for ((v, expected) in clamped) {
            assertEquals(expected, clamp(v, min, max)); assertEquals(expected, clamp(v, vmin, vmax))
        }
    }

    @Test
    fun float3() {
        val lo = 1f; val mid = 5f; val hi = 9f
        val m = Float3(mid, mid, mid); val s = Float3(lo, mid, hi)
        val b1 = Float3(hi, lo, hi); val b2 = Float3(lo, hi, lo); val b3 = Float3(hi, hi, lo)
        val odd = Bool3(true, false, true); val even = Bool3(false, true, false); val front = Bool3(true, true, false); val back = Bool3(false, false, true)
        val all = Bool3(true, true, true); val none = Bool3(false, false, false)

        for (r in vectorRows(m, s, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }
        for (r in scalarRows(m, mid, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }

        // Within delta counts as equal.
        val near = Float3(mid + 0.05f, mid + 0.05f, mid + 0.05f)
        for ((delta, same, different) in listOf(Triple(0.1f, all, none), Triple(0f, none, all))) {
            assertEquals(same, equal(near, m, delta)); assertEquals(same, equal(near, mid, delta))
            assertEquals(different, notEqual(near, m, delta)); assertEquals(different, notEqual(near, mid, delta))
        }

        // Every component below, within and above the range in turn.
        val min = 3f; val max = 7f
        val vmin = Float3(min, min, min); val vmax = Float3(max, max, max)
        val clamped = listOf(
            Float3(lo, mid, hi) to Float3(min, mid, max),
            Float3(mid, hi, lo) to Float3(mid, max, min),
            Float3(hi, lo, mid) to Float3(max, min, mid),
        )
        for ((v, expected) in clamped) {
            assertEquals(expected, clamp(v, min, max)); assertEquals(expected, clamp(v, vmin, vmax))
        }
    }

    @Test
    fun float4() {
        val lo = 1f; val mid = 5f; val hi = 9f; val top = 13f
        val m = Float4(mid, mid, mid, mid); val s = Float4(lo, mid, hi, top)
        val b1 = Float4(hi, lo, hi, lo); val b2 = Float4(lo, hi, lo, hi); val b3 = Float4(hi, hi, lo, lo)
        val odd = Bool4(true, false, true, false); val even = Bool4(false, true, false, true); val front = Bool4(true, true, false, false); val back = Bool4(false, false, true, true)
        val all = Bool4(true, true, true, true); val none = Bool4(false, false, false, false)

        for (r in vectorRows(m, s, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }
        for (r in scalarRows(m, mid, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }

        // Within delta counts as equal.
        val near = Float4(mid + 0.05f, mid + 0.05f, mid + 0.05f, mid + 0.05f)
        for ((delta, same, different) in listOf(Triple(0.1f, all, none), Triple(0f, none, all))) {
            assertEquals(same, equal(near, m, delta)); assertEquals(same, equal(near, mid, delta))
            assertEquals(different, notEqual(near, m, delta)); assertEquals(different, notEqual(near, mid, delta))
        }

        // Every component below, within and above the range in turn.
        val min = 3f; val max = 7f
        val vmin = Float4(min, min, min, min); val vmax = Float4(max, max, max, max)
        val clamped = listOf(
            Float4(lo, mid, hi, mid) to Float4(min, mid, max, mid),
            Float4(mid, hi, lo, hi) to Float4(mid, max, min, max),
            Float4(hi, lo, mid, lo) to Float4(max, min, mid, min),
        )
        for ((v, expected) in clamped) {
            assertEquals(expected, clamp(v, min, max)); assertEquals(expected, clamp(v, vmin, vmax))
        }
    }

    @Test
    fun half2() {
        val lo = Half(1f); val mid = Half(5f); val hi = Half(9f)
        val m = Half2(mid, mid); val s = Half2(lo, mid)
        val b1 = Half2(hi, lo); val b2 = Half2(lo, hi); val b3 = Half2(hi, hi)
        val odd = Bool2(true, false); val even = Bool2(false, true); val front = Bool2(true, true); val back = Bool2(false, false)
        val all = Bool2(true, true); val none = Bool2(false, false)

        for (r in vectorRows(m, s, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }
        for (r in scalarRows(m, mid, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }
    }

    @Test
    fun half3() {
        val lo = Half(1f); val mid = Half(5f); val hi = Half(9f)
        val m = Half3(mid, mid, mid); val s = Half3(lo, mid, hi)
        val b1 = Half3(hi, lo, hi); val b2 = Half3(lo, hi, lo); val b3 = Half3(hi, hi, lo)
        val odd = Bool3(true, false, true); val even = Bool3(false, true, false); val front = Bool3(true, true, false); val back = Bool3(false, false, true)
        val all = Bool3(true, true, true); val none = Bool3(false, false, false)

        for (r in vectorRows(m, s, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }
        for (r in scalarRows(m, mid, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }

        // Every component below, within and above the range in turn.
        val min = Half(3f); val max = Half(7f)
        val vmin = Half3(min, min, min); val vmax = Half3(max, max, max)
        val clamped = listOf(
            Half3(lo, mid, hi) to Half3(min, mid, max),
            Half3(mid, hi, lo) to Half3(mid, max, min),
            Half3(hi, lo, mid) to Half3(max, min, mid),
        )
        for ((v, expected) in clamped) {
            assertEquals(expected, clamp(v, min, max)); assertEquals(expected, clamp(v, vmin, vmax))
        }
    }

    @Test
    fun half4() {
        val lo = Half(1f); val mid = Half(5f); val hi = Half(9f); val top = Half(13f)
        val m = Half4(mid, mid, mid, mid); val s = Half4(lo, mid, hi, top)
        val b1 = Half4(hi, lo, hi, lo); val b2 = Half4(lo, hi, lo, hi); val b3 = Half4(hi, hi, lo, lo)
        val odd = Bool4(true, false, true, false); val even = Bool4(false, true, false, true); val front = Bool4(true, true, false, false); val back = Bool4(false, false, true, true)
        val all = Bool4(true, true, true, true); val none = Bool4(false, false, false, false)

        for (r in vectorRows(m, s, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }
        for (r in scalarRows(m, mid, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }

        // Every component below, within and above the range in turn.
        val min = Half(3f); val max = Half(7f)
        val vmin = Half4(min, min, min, min); val vmax = Half4(max, max, max, max)
        val clamped = listOf(
            Half4(lo, mid, hi, mid) to Half4(min, mid, max, mid),
            Half4(mid, hi, lo, hi) to Half4(mid, max, min, max),
            Half4(hi, lo, mid, lo) to Half4(max, min, mid, min),
        )
        for ((v, expected) in clamped) {
            assertEquals(expected, clamp(v, min, max)); assertEquals(expected, clamp(v, vmin, vmax))
        }
    }

    @Test
    fun int2() {
        val lo = 1; val mid = 5; val hi = 9
        val m = Int2(mid, mid); val s = Int2(lo, mid)
        val b1 = Int2(hi, lo); val b2 = Int2(lo, hi); val b3 = Int2(hi, hi)
        val odd = Bool2(true, false); val even = Bool2(false, true); val front = Bool2(true, true); val back = Bool2(false, false)
        val all = Bool2(true, true); val none = Bool2(false, false)

        for (r in vectorRows(m, s, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }
        for (r in scalarRows(m, mid, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }
    }

    @Test
    fun int3() {
        val lo = 1; val mid = 5; val hi = 9
        val m = Int3(mid, mid, mid); val s = Int3(lo, mid, hi)
        val b1 = Int3(hi, lo, hi); val b2 = Int3(lo, hi, lo); val b3 = Int3(hi, hi, lo)
        val odd = Bool3(true, false, true); val even = Bool3(false, true, false); val front = Bool3(true, true, false); val back = Bool3(false, false, true)
        val all = Bool3(true, true, true); val none = Bool3(false, false, false)

        for (r in vectorRows(m, s, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }
        for (r in scalarRows(m, mid, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }

        // Every component below, within and above the range in turn.
        val min = 3; val max = 7
        val vmin = Int3(min, min, min); val vmax = Int3(max, max, max)
        val clamped = listOf(
            Int3(lo, mid, hi) to Int3(min, mid, max),
            Int3(mid, hi, lo) to Int3(mid, max, min),
            Int3(hi, lo, mid) to Int3(max, min, mid),
        )
        for ((v, expected) in clamped) {
            assertEquals(expected, clamp(v, min, max)); assertEquals(expected, clamp(v, vmin, vmax))
        }
    }

    @Test
    fun int4() {
        val lo = 1; val mid = 5; val hi = 9; val top = 13
        val m = Int4(mid, mid, mid, mid); val s = Int4(lo, mid, hi, top)
        val b1 = Int4(hi, lo, hi, lo); val b2 = Int4(lo, hi, lo, hi); val b3 = Int4(hi, hi, lo, lo)
        val odd = Bool4(true, false, true, false); val even = Bool4(false, true, false, true); val front = Bool4(true, true, false, false); val back = Bool4(false, false, true, true)
        val all = Bool4(true, true, true, true); val none = Bool4(false, false, false, false)

        for (r in vectorRows(m, s, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }
        for (r in scalarRows(m, mid, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }

        // Every component below, within and above the range in turn.
        val min = 3; val max = 7
        val vmin = Int4(min, min, min, min); val vmax = Int4(max, max, max, max)
        val clamped = listOf(
            Int4(lo, mid, hi, mid) to Int4(min, mid, max, mid),
            Int4(mid, hi, lo, hi) to Int4(mid, max, min, max),
            Int4(hi, lo, mid, lo) to Int4(max, min, mid, min),
        )
        for ((v, expected) in clamped) {
            assertEquals(expected, clamp(v, min, max)); assertEquals(expected, clamp(v, vmin, vmax))
        }
    }

    @Test
    fun quaternion() {
        val lo = 1f; val mid = 5f; val hi = 9f; val top = 13f
        val m = Quaternion(mid, mid, mid, mid); val s = Quaternion(lo, mid, hi, top)
        val b1 = Quaternion(hi, lo, hi, lo); val b2 = Quaternion(lo, hi, lo, hi); val b3 = Quaternion(hi, hi, lo, lo)
        val odd = Bool4(true, false, true, false); val even = Bool4(false, true, false, true); val front = Bool4(true, true, false, false); val back = Bool4(false, false, true, true)
        val all = Bool4(true, true, true, true); val none = Bool4(false, false, false, false)

        for (r in vectorRows(m, s, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt Float4(r.b.x, r.b.y, r.b.z, r.b.w))
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte Float4(r.b.x, r.b.y, r.b.z, r.b.w))
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt Float4(r.b.x, r.b.y, r.b.z, r.b.w))
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte Float4(r.b.x, r.b.y, r.b.z, r.b.w))
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq Float4(r.b.x, r.b.y, r.b.z, r.b.w))
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq Float4(r.b.x, r.b.y, r.b.z, r.b.w))
        }
        for (r in scalarRows(m, mid, b1, b2, b3, odd, even, front, back, all, none)) {
            assertEquals(r.lt, lessThan(r.a, r.b)); assertEquals(r.lt, r.a lt r.b)
            assertEquals(r.lte, lessThanEqual(r.a, r.b)); assertEquals(r.lte, r.a lte r.b)
            assertEquals(r.gt, greaterThan(r.a, r.b)); assertEquals(r.gt, r.a gt r.b)
            assertEquals(r.gte, greaterThanEqual(r.a, r.b)); assertEquals(r.gte, r.a gte r.b)
            assertEquals(r.eq, equal(r.a, r.b)); assertEquals(r.eq, r.a eq r.b)
            assertEquals(r.neq, notEqual(r.a, r.b)); assertEquals(r.neq, r.a neq r.b)
        }

        // Within delta counts as equal.
        val near = Quaternion(mid + 0.05f, mid + 0.05f, mid + 0.05f, mid + 0.05f)
        for ((delta, same, different) in listOf(Triple(0.1f, all, none), Triple(0f, none, all))) {
            assertEquals(same, equal(near, m, delta)); assertEquals(same, equal(near, mid, delta))
            assertEquals(different, notEqual(near, m, delta)); assertEquals(different, notEqual(near, mid, delta))
        }
    }
}
