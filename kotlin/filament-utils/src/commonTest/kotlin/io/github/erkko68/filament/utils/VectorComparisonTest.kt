package io.github.erkko68.filament.utils

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Component-wise comparisons of every vector type, in function and infix form, against a vector and a scalar.
 *
 * Each component is driven to both outcomes, and the probes differ between every pair of components, so a
 * comparison that reads the wrong one fails.
 */
class VectorComparisonTest {
    @Test
    fun float2() {
        val lo = 1f; val mid = 5f; val hi = 9f
        val m = Float2(mid, mid); val s = Float2(lo, mid)
        val b1 = Float2(hi, lo); val b2 = Float2(lo, hi); val b3 = Float2(hi, hi)
        val odd = Bool2(true, false); val even = Bool2(false, true); val front = Bool2(true, true); val back = Bool2(false, false)
        val ends = Bool2(true, false); val middle = Bool2(false, true); val all = Bool2(true, true); val none = Bool2(false, false)

        assertEquals(listOf(odd, even, front, none), listOf(lessThan(m, b1), lessThan(m, b2), lessThan(m, b3), lessThan(m, m)))
        assertEquals(listOf(even, odd, back, none), listOf(lessThan(b1, mid), lessThan(b2, mid), lessThan(b3, mid), lessThan(m, mid)))
        assertEquals(listOf(odd, even, front, none), listOf(m lt b1, m lt b2, m lt b3, m lt m))
        assertEquals(listOf(even, odd, back, none), listOf(b1 lt mid, b2 lt mid, b3 lt mid, m lt mid))
        assertEquals(listOf(odd, even, front, all), listOf(lessThanEqual(m, b1), lessThanEqual(m, b2), lessThanEqual(m, b3), lessThanEqual(m, m)))
        assertEquals(listOf(even, odd, back, all), listOf(lessThanEqual(b1, mid), lessThanEqual(b2, mid), lessThanEqual(b3, mid), lessThanEqual(m, mid)))
        assertEquals(listOf(odd, even, front, all), listOf(m lte b1, m lte b2, m lte b3, m lte m))
        assertEquals(listOf(even, odd, back, all), listOf(b1 lte mid, b2 lte mid, b3 lte mid, m lte mid))
        assertEquals(listOf(even, odd, back, none), listOf(greaterThan(m, b1), greaterThan(m, b2), greaterThan(m, b3), greaterThan(m, m)))
        assertEquals(listOf(odd, even, front, none), listOf(greaterThan(b1, mid), greaterThan(b2, mid), greaterThan(b3, mid), greaterThan(m, mid)))
        assertEquals(listOf(even, odd, back, none), listOf(m gt b1, m gt b2, m gt b3, m gt m))
        assertEquals(listOf(odd, even, front, none), listOf(b1 gt mid, b2 gt mid, b3 gt mid, m gt mid))
        assertEquals(listOf(even, odd, back, all), listOf(greaterThanEqual(m, b1), greaterThanEqual(m, b2), greaterThanEqual(m, b3), greaterThanEqual(m, m)))
        assertEquals(listOf(odd, even, front, all), listOf(greaterThanEqual(b1, mid), greaterThanEqual(b2, mid), greaterThanEqual(b3, mid), greaterThanEqual(m, mid)))
        assertEquals(listOf(even, odd, back, all), listOf(m gte b1, m gte b2, m gte b3, m gte m))
        assertEquals(listOf(odd, even, front, all), listOf(b1 gte mid, b2 gte mid, b3 gte mid, m gte mid))

        assertEquals(listOf(all, none, ends), listOf(equal(s, s), equal(b1, b2), equal(b1, b3)))
        assertEquals(listOf(odd, even, front), listOf(equal(b1, hi), equal(b2, hi), equal(b3, hi)))
        assertEquals(listOf(all, none, ends), listOf(s eq s, b1 eq b2, b1 eq b3))
        assertEquals(listOf(odd, even, front), listOf(b1 eq hi, b2 eq hi, b3 eq hi))
        assertEquals(listOf(none, all, middle), listOf(notEqual(s, s), notEqual(b1, b2), notEqual(b1, b3)))
        assertEquals(listOf(even, odd, back), listOf(notEqual(b1, hi), notEqual(b2, hi), notEqual(b3, hi)))
        assertEquals(listOf(none, all, middle), listOf(s neq s, b1 neq b2, b1 neq b3))
        assertEquals(listOf(even, odd, back), listOf(b1 neq hi, b2 neq hi, b3 neq hi))

        // Within delta counts as equal.
        val near = Float2(mid + 0.05f, mid + 0.05f)
        assertEquals(listOf(all, none), listOf(equal(near, m, 0.1f), equal(near, m)))
        assertEquals(listOf(all, none), listOf(equal(near, mid, 0.1f), equal(near, mid)))
        assertEquals(listOf(none, all), listOf(notEqual(near, m, 0.1f), notEqual(near, m)))
        assertEquals(listOf(none, all), listOf(notEqual(near, mid, 0.1f), notEqual(near, mid)))

        // Every component below, within and above the range in turn.
        val c1 = Float2(lo, mid); val c2 = Float2(mid, hi); val c3 = Float2(hi, lo)
        val min = 3f; val max = 7f
        assertEquals(listOf(Float2(min, mid), Float2(mid, max), Float2(max, min)), listOf(clamp(c1, min, max), clamp(c2, min, max), clamp(c3, min, max)))
        val vmin = Float2(min, min); val vmax = Float2(max, max)
        assertEquals(listOf(Float2(min, mid), Float2(mid, max), Float2(max, min)), listOf(clamp(c1, vmin, vmax), clamp(c2, vmin, vmax), clamp(c3, vmin, vmax)))
    }

    @Test
    fun float3() {
        val lo = 1f; val mid = 5f; val hi = 9f
        val m = Float3(mid, mid, mid); val s = Float3(lo, mid, hi)
        val b1 = Float3(hi, lo, hi); val b2 = Float3(lo, hi, lo); val b3 = Float3(hi, hi, lo)
        val odd = Bool3(true, false, true); val even = Bool3(false, true, false); val front = Bool3(true, true, false); val back = Bool3(false, false, true)
        val ends = Bool3(true, false, false); val middle = Bool3(false, true, true); val all = Bool3(true, true, true); val none = Bool3(false, false, false)

        assertEquals(listOf(odd, even, front, none), listOf(lessThan(m, b1), lessThan(m, b2), lessThan(m, b3), lessThan(m, m)))
        assertEquals(listOf(even, odd, back, none), listOf(lessThan(b1, mid), lessThan(b2, mid), lessThan(b3, mid), lessThan(m, mid)))
        assertEquals(listOf(odd, even, front, none), listOf(m lt b1, m lt b2, m lt b3, m lt m))
        assertEquals(listOf(even, odd, back, none), listOf(b1 lt mid, b2 lt mid, b3 lt mid, m lt mid))
        assertEquals(listOf(odd, even, front, all), listOf(lessThanEqual(m, b1), lessThanEqual(m, b2), lessThanEqual(m, b3), lessThanEqual(m, m)))
        assertEquals(listOf(even, odd, back, all), listOf(lessThanEqual(b1, mid), lessThanEqual(b2, mid), lessThanEqual(b3, mid), lessThanEqual(m, mid)))
        assertEquals(listOf(odd, even, front, all), listOf(m lte b1, m lte b2, m lte b3, m lte m))
        assertEquals(listOf(even, odd, back, all), listOf(b1 lte mid, b2 lte mid, b3 lte mid, m lte mid))
        assertEquals(listOf(even, odd, back, none), listOf(greaterThan(m, b1), greaterThan(m, b2), greaterThan(m, b3), greaterThan(m, m)))
        assertEquals(listOf(odd, even, front, none), listOf(greaterThan(b1, mid), greaterThan(b2, mid), greaterThan(b3, mid), greaterThan(m, mid)))
        assertEquals(listOf(even, odd, back, none), listOf(m gt b1, m gt b2, m gt b3, m gt m))
        assertEquals(listOf(odd, even, front, none), listOf(b1 gt mid, b2 gt mid, b3 gt mid, m gt mid))
        assertEquals(listOf(even, odd, back, all), listOf(greaterThanEqual(m, b1), greaterThanEqual(m, b2), greaterThanEqual(m, b3), greaterThanEqual(m, m)))
        assertEquals(listOf(odd, even, front, all), listOf(greaterThanEqual(b1, mid), greaterThanEqual(b2, mid), greaterThanEqual(b3, mid), greaterThanEqual(m, mid)))
        assertEquals(listOf(even, odd, back, all), listOf(m gte b1, m gte b2, m gte b3, m gte m))
        assertEquals(listOf(odd, even, front, all), listOf(b1 gte mid, b2 gte mid, b3 gte mid, m gte mid))

        assertEquals(listOf(all, none, ends), listOf(equal(s, s), equal(b1, b2), equal(b1, b3)))
        assertEquals(listOf(odd, even, front), listOf(equal(b1, hi), equal(b2, hi), equal(b3, hi)))
        assertEquals(listOf(all, none, ends), listOf(s eq s, b1 eq b2, b1 eq b3))
        assertEquals(listOf(odd, even, front), listOf(b1 eq hi, b2 eq hi, b3 eq hi))
        assertEquals(listOf(none, all, middle), listOf(notEqual(s, s), notEqual(b1, b2), notEqual(b1, b3)))
        assertEquals(listOf(even, odd, back), listOf(notEqual(b1, hi), notEqual(b2, hi), notEqual(b3, hi)))
        assertEquals(listOf(none, all, middle), listOf(s neq s, b1 neq b2, b1 neq b3))
        assertEquals(listOf(even, odd, back), listOf(b1 neq hi, b2 neq hi, b3 neq hi))

        // Within delta counts as equal.
        val near = Float3(mid + 0.05f, mid + 0.05f, mid + 0.05f)
        assertEquals(listOf(all, none), listOf(equal(near, m, 0.1f), equal(near, m)))
        assertEquals(listOf(all, none), listOf(equal(near, mid, 0.1f), equal(near, mid)))
        assertEquals(listOf(none, all), listOf(notEqual(near, m, 0.1f), notEqual(near, m)))
        assertEquals(listOf(none, all), listOf(notEqual(near, mid, 0.1f), notEqual(near, mid)))

        // Every component below, within and above the range in turn.
        val c1 = Float3(lo, mid, hi); val c2 = Float3(mid, hi, lo); val c3 = Float3(hi, lo, mid)
        val min = 3f; val max = 7f
        assertEquals(listOf(Float3(min, mid, max), Float3(mid, max, min), Float3(max, min, mid)), listOf(clamp(c1, min, max), clamp(c2, min, max), clamp(c3, min, max)))
        val vmin = Float3(min, min, min); val vmax = Float3(max, max, max)
        assertEquals(listOf(Float3(min, mid, max), Float3(mid, max, min), Float3(max, min, mid)), listOf(clamp(c1, vmin, vmax), clamp(c2, vmin, vmax), clamp(c3, vmin, vmax)))
    }

    @Test
    fun float4() {
        val lo = 1f; val mid = 5f; val hi = 9f; val top = 13f
        val m = Float4(mid, mid, mid, mid); val s = Float4(lo, mid, hi, top)
        val b1 = Float4(hi, lo, hi, lo); val b2 = Float4(lo, hi, lo, hi); val b3 = Float4(hi, hi, lo, lo)
        val odd = Bool4(true, false, true, false); val even = Bool4(false, true, false, true); val front = Bool4(true, true, false, false); val back = Bool4(false, false, true, true)
        val ends = Bool4(true, false, false, true); val middle = Bool4(false, true, true, false); val all = Bool4(true, true, true, true); val none = Bool4(false, false, false, false)

        assertEquals(listOf(odd, even, front, none), listOf(lessThan(m, b1), lessThan(m, b2), lessThan(m, b3), lessThan(m, m)))
        assertEquals(listOf(even, odd, back, none), listOf(lessThan(b1, mid), lessThan(b2, mid), lessThan(b3, mid), lessThan(m, mid)))
        assertEquals(listOf(odd, even, front, none), listOf(m lt b1, m lt b2, m lt b3, m lt m))
        assertEquals(listOf(even, odd, back, none), listOf(b1 lt mid, b2 lt mid, b3 lt mid, m lt mid))
        assertEquals(listOf(odd, even, front, all), listOf(lessThanEqual(m, b1), lessThanEqual(m, b2), lessThanEqual(m, b3), lessThanEqual(m, m)))
        assertEquals(listOf(even, odd, back, all), listOf(lessThanEqual(b1, mid), lessThanEqual(b2, mid), lessThanEqual(b3, mid), lessThanEqual(m, mid)))
        assertEquals(listOf(odd, even, front, all), listOf(m lte b1, m lte b2, m lte b3, m lte m))
        assertEquals(listOf(even, odd, back, all), listOf(b1 lte mid, b2 lte mid, b3 lte mid, m lte mid))
        assertEquals(listOf(even, odd, back, none), listOf(greaterThan(m, b1), greaterThan(m, b2), greaterThan(m, b3), greaterThan(m, m)))
        assertEquals(listOf(odd, even, front, none), listOf(greaterThan(b1, mid), greaterThan(b2, mid), greaterThan(b3, mid), greaterThan(m, mid)))
        assertEquals(listOf(even, odd, back, none), listOf(m gt b1, m gt b2, m gt b3, m gt m))
        assertEquals(listOf(odd, even, front, none), listOf(b1 gt mid, b2 gt mid, b3 gt mid, m gt mid))
        assertEquals(listOf(even, odd, back, all), listOf(greaterThanEqual(m, b1), greaterThanEqual(m, b2), greaterThanEqual(m, b3), greaterThanEqual(m, m)))
        assertEquals(listOf(odd, even, front, all), listOf(greaterThanEqual(b1, mid), greaterThanEqual(b2, mid), greaterThanEqual(b3, mid), greaterThanEqual(m, mid)))
        assertEquals(listOf(even, odd, back, all), listOf(m gte b1, m gte b2, m gte b3, m gte m))
        assertEquals(listOf(odd, even, front, all), listOf(b1 gte mid, b2 gte mid, b3 gte mid, m gte mid))

        assertEquals(listOf(all, none, ends), listOf(equal(s, s), equal(b1, b2), equal(b1, b3)))
        assertEquals(listOf(odd, even, front), listOf(equal(b1, hi), equal(b2, hi), equal(b3, hi)))
        assertEquals(listOf(all, none, ends), listOf(s eq s, b1 eq b2, b1 eq b3))
        assertEquals(listOf(odd, even, front), listOf(b1 eq hi, b2 eq hi, b3 eq hi))
        assertEquals(listOf(none, all, middle), listOf(notEqual(s, s), notEqual(b1, b2), notEqual(b1, b3)))
        assertEquals(listOf(even, odd, back), listOf(notEqual(b1, hi), notEqual(b2, hi), notEqual(b3, hi)))
        assertEquals(listOf(none, all, middle), listOf(s neq s, b1 neq b2, b1 neq b3))
        assertEquals(listOf(even, odd, back), listOf(b1 neq hi, b2 neq hi, b3 neq hi))

        // Within delta counts as equal.
        val near = Float4(mid + 0.05f, mid + 0.05f, mid + 0.05f, mid + 0.05f)
        assertEquals(listOf(all, none), listOf(equal(near, m, 0.1f), equal(near, m)))
        assertEquals(listOf(all, none), listOf(equal(near, mid, 0.1f), equal(near, mid)))
        assertEquals(listOf(none, all), listOf(notEqual(near, m, 0.1f), notEqual(near, m)))
        assertEquals(listOf(none, all), listOf(notEqual(near, mid, 0.1f), notEqual(near, mid)))

        // Every component below, within and above the range in turn.
        val c1 = Float4(lo, mid, hi, mid); val c2 = Float4(mid, hi, lo, hi); val c3 = Float4(hi, lo, mid, lo)
        val min = 3f; val max = 7f
        assertEquals(listOf(Float4(min, mid, max, mid), Float4(mid, max, min, max), Float4(max, min, mid, min)), listOf(clamp(c1, min, max), clamp(c2, min, max), clamp(c3, min, max)))
        val vmin = Float4(min, min, min, min); val vmax = Float4(max, max, max, max)
        assertEquals(listOf(Float4(min, mid, max, mid), Float4(mid, max, min, max), Float4(max, min, mid, min)), listOf(clamp(c1, vmin, vmax), clamp(c2, vmin, vmax), clamp(c3, vmin, vmax)))
    }

    @Test
    fun half2() {
        val lo = Half(1f); val mid = Half(5f); val hi = Half(9f)
        val m = Half2(mid, mid); val s = Half2(lo, mid)
        val b1 = Half2(hi, lo); val b2 = Half2(lo, hi); val b3 = Half2(hi, hi)
        val odd = Bool2(true, false); val even = Bool2(false, true); val front = Bool2(true, true); val back = Bool2(false, false)
        val ends = Bool2(true, false); val middle = Bool2(false, true); val all = Bool2(true, true); val none = Bool2(false, false)

        assertEquals(listOf(odd, even, front, none), listOf(lessThan(m, b1), lessThan(m, b2), lessThan(m, b3), lessThan(m, m)))
        assertEquals(listOf(even, odd, back, none), listOf(lessThan(b1, mid), lessThan(b2, mid), lessThan(b3, mid), lessThan(m, mid)))
        assertEquals(listOf(odd, even, front, none), listOf(m lt b1, m lt b2, m lt b3, m lt m))
        assertEquals(listOf(even, odd, back, none), listOf(b1 lt mid, b2 lt mid, b3 lt mid, m lt mid))
        assertEquals(listOf(odd, even, front, all), listOf(lessThanEqual(m, b1), lessThanEqual(m, b2), lessThanEqual(m, b3), lessThanEqual(m, m)))
        assertEquals(listOf(even, odd, back, all), listOf(lessThanEqual(b1, mid), lessThanEqual(b2, mid), lessThanEqual(b3, mid), lessThanEqual(m, mid)))
        assertEquals(listOf(odd, even, front, all), listOf(m lte b1, m lte b2, m lte b3, m lte m))
        assertEquals(listOf(even, odd, back, all), listOf(b1 lte mid, b2 lte mid, b3 lte mid, m lte mid))
        assertEquals(listOf(even, odd, back, none), listOf(greaterThan(m, b1), greaterThan(m, b2), greaterThan(m, b3), greaterThan(m, m)))
        assertEquals(listOf(odd, even, front, none), listOf(greaterThan(b1, mid), greaterThan(b2, mid), greaterThan(b3, mid), greaterThan(m, mid)))
        assertEquals(listOf(even, odd, back, none), listOf(m gt b1, m gt b2, m gt b3, m gt m))
        assertEquals(listOf(odd, even, front, none), listOf(b1 gt mid, b2 gt mid, b3 gt mid, m gt mid))
        assertEquals(listOf(even, odd, back, all), listOf(greaterThanEqual(m, b1), greaterThanEqual(m, b2), greaterThanEqual(m, b3), greaterThanEqual(m, m)))
        assertEquals(listOf(odd, even, front, all), listOf(greaterThanEqual(b1, mid), greaterThanEqual(b2, mid), greaterThanEqual(b3, mid), greaterThanEqual(m, mid)))
        assertEquals(listOf(even, odd, back, all), listOf(m gte b1, m gte b2, m gte b3, m gte m))
        assertEquals(listOf(odd, even, front, all), listOf(b1 gte mid, b2 gte mid, b3 gte mid, m gte mid))

        assertEquals(listOf(all, none, ends), listOf(equal(s, s), equal(b1, b2), equal(b1, b3)))
        assertEquals(listOf(odd, even, front), listOf(equal(b1, hi), equal(b2, hi), equal(b3, hi)))
        assertEquals(listOf(all, none, ends), listOf(s eq s, b1 eq b2, b1 eq b3))
        assertEquals(listOf(odd, even, front), listOf(b1 eq hi, b2 eq hi, b3 eq hi))
        assertEquals(listOf(none, all, middle), listOf(notEqual(s, s), notEqual(b1, b2), notEqual(b1, b3)))
        assertEquals(listOf(even, odd, back), listOf(notEqual(b1, hi), notEqual(b2, hi), notEqual(b3, hi)))
        assertEquals(listOf(none, all, middle), listOf(s neq s, b1 neq b2, b1 neq b3))
        assertEquals(listOf(even, odd, back), listOf(b1 neq hi, b2 neq hi, b3 neq hi))
    }

    @Test
    fun half3() {
        val lo = Half(1f); val mid = Half(5f); val hi = Half(9f)
        val m = Half3(mid, mid, mid); val s = Half3(lo, mid, hi)
        val b1 = Half3(hi, lo, hi); val b2 = Half3(lo, hi, lo); val b3 = Half3(hi, hi, lo)
        val odd = Bool3(true, false, true); val even = Bool3(false, true, false); val front = Bool3(true, true, false); val back = Bool3(false, false, true)
        val ends = Bool3(true, false, false); val middle = Bool3(false, true, true); val all = Bool3(true, true, true); val none = Bool3(false, false, false)

        assertEquals(listOf(odd, even, front, none), listOf(lessThan(m, b1), lessThan(m, b2), lessThan(m, b3), lessThan(m, m)))
        assertEquals(listOf(even, odd, back, none), listOf(lessThan(b1, mid), lessThan(b2, mid), lessThan(b3, mid), lessThan(m, mid)))
        assertEquals(listOf(odd, even, front, none), listOf(m lt b1, m lt b2, m lt b3, m lt m))
        assertEquals(listOf(even, odd, back, none), listOf(b1 lt mid, b2 lt mid, b3 lt mid, m lt mid))
        assertEquals(listOf(odd, even, front, all), listOf(lessThanEqual(m, b1), lessThanEqual(m, b2), lessThanEqual(m, b3), lessThanEqual(m, m)))
        assertEquals(listOf(even, odd, back, all), listOf(lessThanEqual(b1, mid), lessThanEqual(b2, mid), lessThanEqual(b3, mid), lessThanEqual(m, mid)))
        assertEquals(listOf(odd, even, front, all), listOf(m lte b1, m lte b2, m lte b3, m lte m))
        assertEquals(listOf(even, odd, back, all), listOf(b1 lte mid, b2 lte mid, b3 lte mid, m lte mid))
        assertEquals(listOf(even, odd, back, none), listOf(greaterThan(m, b1), greaterThan(m, b2), greaterThan(m, b3), greaterThan(m, m)))
        assertEquals(listOf(odd, even, front, none), listOf(greaterThan(b1, mid), greaterThan(b2, mid), greaterThan(b3, mid), greaterThan(m, mid)))
        assertEquals(listOf(even, odd, back, none), listOf(m gt b1, m gt b2, m gt b3, m gt m))
        assertEquals(listOf(odd, even, front, none), listOf(b1 gt mid, b2 gt mid, b3 gt mid, m gt mid))
        assertEquals(listOf(even, odd, back, all), listOf(greaterThanEqual(m, b1), greaterThanEqual(m, b2), greaterThanEqual(m, b3), greaterThanEqual(m, m)))
        assertEquals(listOf(odd, even, front, all), listOf(greaterThanEqual(b1, mid), greaterThanEqual(b2, mid), greaterThanEqual(b3, mid), greaterThanEqual(m, mid)))
        assertEquals(listOf(even, odd, back, all), listOf(m gte b1, m gte b2, m gte b3, m gte m))
        assertEquals(listOf(odd, even, front, all), listOf(b1 gte mid, b2 gte mid, b3 gte mid, m gte mid))

        assertEquals(listOf(all, none, ends), listOf(equal(s, s), equal(b1, b2), equal(b1, b3)))
        assertEquals(listOf(odd, even, front), listOf(equal(b1, hi), equal(b2, hi), equal(b3, hi)))
        assertEquals(listOf(all, none, ends), listOf(s eq s, b1 eq b2, b1 eq b3))
        assertEquals(listOf(odd, even, front), listOf(b1 eq hi, b2 eq hi, b3 eq hi))
        assertEquals(listOf(none, all, middle), listOf(notEqual(s, s), notEqual(b1, b2), notEqual(b1, b3)))
        assertEquals(listOf(even, odd, back), listOf(notEqual(b1, hi), notEqual(b2, hi), notEqual(b3, hi)))
        assertEquals(listOf(none, all, middle), listOf(s neq s, b1 neq b2, b1 neq b3))
        assertEquals(listOf(even, odd, back), listOf(b1 neq hi, b2 neq hi, b3 neq hi))

        // Every component below, within and above the range in turn.
        val c1 = Half3(lo, mid, hi); val c2 = Half3(mid, hi, lo); val c3 = Half3(hi, lo, mid)
        val min = Half(3f); val max = Half(7f)
        assertEquals(listOf(Half3(min, mid, max), Half3(mid, max, min), Half3(max, min, mid)), listOf(clamp(c1, min, max), clamp(c2, min, max), clamp(c3, min, max)))
        val vmin = Half3(min, min, min); val vmax = Half3(max, max, max)
        assertEquals(listOf(Half3(min, mid, max), Half3(mid, max, min), Half3(max, min, mid)), listOf(clamp(c1, vmin, vmax), clamp(c2, vmin, vmax), clamp(c3, vmin, vmax)))
    }

    @Test
    fun half4() {
        val lo = Half(1f); val mid = Half(5f); val hi = Half(9f); val top = Half(13f)
        val m = Half4(mid, mid, mid, mid); val s = Half4(lo, mid, hi, top)
        val b1 = Half4(hi, lo, hi, lo); val b2 = Half4(lo, hi, lo, hi); val b3 = Half4(hi, hi, lo, lo)
        val odd = Bool4(true, false, true, false); val even = Bool4(false, true, false, true); val front = Bool4(true, true, false, false); val back = Bool4(false, false, true, true)
        val ends = Bool4(true, false, false, true); val middle = Bool4(false, true, true, false); val all = Bool4(true, true, true, true); val none = Bool4(false, false, false, false)

        assertEquals(listOf(odd, even, front, none), listOf(lessThan(m, b1), lessThan(m, b2), lessThan(m, b3), lessThan(m, m)))
        assertEquals(listOf(even, odd, back, none), listOf(lessThan(b1, mid), lessThan(b2, mid), lessThan(b3, mid), lessThan(m, mid)))
        assertEquals(listOf(odd, even, front, none), listOf(m lt b1, m lt b2, m lt b3, m lt m))
        assertEquals(listOf(even, odd, back, none), listOf(b1 lt mid, b2 lt mid, b3 lt mid, m lt mid))
        assertEquals(listOf(odd, even, front, all), listOf(lessThanEqual(m, b1), lessThanEqual(m, b2), lessThanEqual(m, b3), lessThanEqual(m, m)))
        assertEquals(listOf(even, odd, back, all), listOf(lessThanEqual(b1, mid), lessThanEqual(b2, mid), lessThanEqual(b3, mid), lessThanEqual(m, mid)))
        assertEquals(listOf(odd, even, front, all), listOf(m lte b1, m lte b2, m lte b3, m lte m))
        assertEquals(listOf(even, odd, back, all), listOf(b1 lte mid, b2 lte mid, b3 lte mid, m lte mid))
        assertEquals(listOf(even, odd, back, none), listOf(greaterThan(m, b1), greaterThan(m, b2), greaterThan(m, b3), greaterThan(m, m)))
        assertEquals(listOf(odd, even, front, none), listOf(greaterThan(b1, mid), greaterThan(b2, mid), greaterThan(b3, mid), greaterThan(m, mid)))
        assertEquals(listOf(even, odd, back, none), listOf(m gt b1, m gt b2, m gt b3, m gt m))
        assertEquals(listOf(odd, even, front, none), listOf(b1 gt mid, b2 gt mid, b3 gt mid, m gt mid))
        assertEquals(listOf(even, odd, back, all), listOf(greaterThanEqual(m, b1), greaterThanEqual(m, b2), greaterThanEqual(m, b3), greaterThanEqual(m, m)))
        assertEquals(listOf(odd, even, front, all), listOf(greaterThanEqual(b1, mid), greaterThanEqual(b2, mid), greaterThanEqual(b3, mid), greaterThanEqual(m, mid)))
        assertEquals(listOf(even, odd, back, all), listOf(m gte b1, m gte b2, m gte b3, m gte m))
        assertEquals(listOf(odd, even, front, all), listOf(b1 gte mid, b2 gte mid, b3 gte mid, m gte mid))

        assertEquals(listOf(all, none, ends), listOf(equal(s, s), equal(b1, b2), equal(b1, b3)))
        assertEquals(listOf(odd, even, front), listOf(equal(b1, hi), equal(b2, hi), equal(b3, hi)))
        assertEquals(listOf(all, none, ends), listOf(s eq s, b1 eq b2, b1 eq b3))
        assertEquals(listOf(odd, even, front), listOf(b1 eq hi, b2 eq hi, b3 eq hi))
        assertEquals(listOf(none, all, middle), listOf(notEqual(s, s), notEqual(b1, b2), notEqual(b1, b3)))
        assertEquals(listOf(even, odd, back), listOf(notEqual(b1, hi), notEqual(b2, hi), notEqual(b3, hi)))
        assertEquals(listOf(none, all, middle), listOf(s neq s, b1 neq b2, b1 neq b3))
        assertEquals(listOf(even, odd, back), listOf(b1 neq hi, b2 neq hi, b3 neq hi))

        // Every component below, within and above the range in turn.
        val c1 = Half4(lo, mid, hi, mid); val c2 = Half4(mid, hi, lo, hi); val c3 = Half4(hi, lo, mid, lo)
        val min = Half(3f); val max = Half(7f)
        assertEquals(listOf(Half4(min, mid, max, mid), Half4(mid, max, min, max), Half4(max, min, mid, min)), listOf(clamp(c1, min, max), clamp(c2, min, max), clamp(c3, min, max)))
        val vmin = Half4(min, min, min, min); val vmax = Half4(max, max, max, max)
        assertEquals(listOf(Half4(min, mid, max, mid), Half4(mid, max, min, max), Half4(max, min, mid, min)), listOf(clamp(c1, vmin, vmax), clamp(c2, vmin, vmax), clamp(c3, vmin, vmax)))
    }

    @Test
    fun int2() {
        val lo = 1; val mid = 5; val hi = 9
        val m = Int2(mid, mid); val s = Int2(lo, mid)
        val b1 = Int2(hi, lo); val b2 = Int2(lo, hi); val b3 = Int2(hi, hi)
        val odd = Bool2(true, false); val even = Bool2(false, true); val front = Bool2(true, true); val back = Bool2(false, false)
        val ends = Bool2(true, false); val middle = Bool2(false, true); val all = Bool2(true, true); val none = Bool2(false, false)

        assertEquals(listOf(odd, even, front, none), listOf(lessThan(m, b1), lessThan(m, b2), lessThan(m, b3), lessThan(m, m)))
        assertEquals(listOf(even, odd, back, none), listOf(lessThan(b1, mid), lessThan(b2, mid), lessThan(b3, mid), lessThan(m, mid)))
        assertEquals(listOf(odd, even, front, none), listOf(m lt b1, m lt b2, m lt b3, m lt m))
        assertEquals(listOf(even, odd, back, none), listOf(b1 lt mid, b2 lt mid, b3 lt mid, m lt mid))
        assertEquals(listOf(odd, even, front, all), listOf(lessThanEqual(m, b1), lessThanEqual(m, b2), lessThanEqual(m, b3), lessThanEqual(m, m)))
        assertEquals(listOf(even, odd, back, all), listOf(lessThanEqual(b1, mid), lessThanEqual(b2, mid), lessThanEqual(b3, mid), lessThanEqual(m, mid)))
        assertEquals(listOf(odd, even, front, all), listOf(m lte b1, m lte b2, m lte b3, m lte m))
        assertEquals(listOf(even, odd, back, all), listOf(b1 lte mid, b2 lte mid, b3 lte mid, m lte mid))
        assertEquals(listOf(even, odd, back, none), listOf(greaterThan(m, b1), greaterThan(m, b2), greaterThan(m, b3), greaterThan(m, m)))
        assertEquals(listOf(odd, even, front, none), listOf(greaterThan(b1, mid), greaterThan(b2, mid), greaterThan(b3, mid), greaterThan(m, mid)))
        assertEquals(listOf(even, odd, back, none), listOf(m gt b1, m gt b2, m gt b3, m gt m))
        assertEquals(listOf(odd, even, front, none), listOf(b1 gt mid, b2 gt mid, b3 gt mid, m gt mid))
        assertEquals(listOf(even, odd, back, all), listOf(greaterThanEqual(m, b1), greaterThanEqual(m, b2), greaterThanEqual(m, b3), greaterThanEqual(m, m)))
        assertEquals(listOf(odd, even, front, all), listOf(greaterThanEqual(b1, mid), greaterThanEqual(b2, mid), greaterThanEqual(b3, mid), greaterThanEqual(m, mid)))
        assertEquals(listOf(even, odd, back, all), listOf(m gte b1, m gte b2, m gte b3, m gte m))
        assertEquals(listOf(odd, even, front, all), listOf(b1 gte mid, b2 gte mid, b3 gte mid, m gte mid))

        assertEquals(listOf(all, none, ends), listOf(equal(s, s), equal(b1, b2), equal(b1, b3)))
        assertEquals(listOf(odd, even, front), listOf(equal(b1, hi), equal(b2, hi), equal(b3, hi)))
        assertEquals(listOf(all, none, ends), listOf(s eq s, b1 eq b2, b1 eq b3))
        assertEquals(listOf(odd, even, front), listOf(b1 eq hi, b2 eq hi, b3 eq hi))
        assertEquals(listOf(none, all, middle), listOf(notEqual(s, s), notEqual(b1, b2), notEqual(b1, b3)))
        assertEquals(listOf(even, odd, back), listOf(notEqual(b1, hi), notEqual(b2, hi), notEqual(b3, hi)))
        assertEquals(listOf(none, all, middle), listOf(s neq s, b1 neq b2, b1 neq b3))
        assertEquals(listOf(even, odd, back), listOf(b1 neq hi, b2 neq hi, b3 neq hi))
    }

    @Test
    fun int3() {
        val lo = 1; val mid = 5; val hi = 9
        val m = Int3(mid, mid, mid); val s = Int3(lo, mid, hi)
        val b1 = Int3(hi, lo, hi); val b2 = Int3(lo, hi, lo); val b3 = Int3(hi, hi, lo)
        val odd = Bool3(true, false, true); val even = Bool3(false, true, false); val front = Bool3(true, true, false); val back = Bool3(false, false, true)
        val ends = Bool3(true, false, false); val middle = Bool3(false, true, true); val all = Bool3(true, true, true); val none = Bool3(false, false, false)

        assertEquals(listOf(odd, even, front, none), listOf(lessThan(m, b1), lessThan(m, b2), lessThan(m, b3), lessThan(m, m)))
        assertEquals(listOf(even, odd, back, none), listOf(lessThan(b1, mid), lessThan(b2, mid), lessThan(b3, mid), lessThan(m, mid)))
        assertEquals(listOf(odd, even, front, none), listOf(m lt b1, m lt b2, m lt b3, m lt m))
        assertEquals(listOf(even, odd, back, none), listOf(b1 lt mid, b2 lt mid, b3 lt mid, m lt mid))
        assertEquals(listOf(odd, even, front, all), listOf(lessThanEqual(m, b1), lessThanEqual(m, b2), lessThanEqual(m, b3), lessThanEqual(m, m)))
        assertEquals(listOf(even, odd, back, all), listOf(lessThanEqual(b1, mid), lessThanEqual(b2, mid), lessThanEqual(b3, mid), lessThanEqual(m, mid)))
        assertEquals(listOf(odd, even, front, all), listOf(m lte b1, m lte b2, m lte b3, m lte m))
        assertEquals(listOf(even, odd, back, all), listOf(b1 lte mid, b2 lte mid, b3 lte mid, m lte mid))
        assertEquals(listOf(even, odd, back, none), listOf(greaterThan(m, b1), greaterThan(m, b2), greaterThan(m, b3), greaterThan(m, m)))
        assertEquals(listOf(odd, even, front, none), listOf(greaterThan(b1, mid), greaterThan(b2, mid), greaterThan(b3, mid), greaterThan(m, mid)))
        assertEquals(listOf(even, odd, back, none), listOf(m gt b1, m gt b2, m gt b3, m gt m))
        assertEquals(listOf(odd, even, front, none), listOf(b1 gt mid, b2 gt mid, b3 gt mid, m gt mid))
        assertEquals(listOf(even, odd, back, all), listOf(greaterThanEqual(m, b1), greaterThanEqual(m, b2), greaterThanEqual(m, b3), greaterThanEqual(m, m)))
        assertEquals(listOf(odd, even, front, all), listOf(greaterThanEqual(b1, mid), greaterThanEqual(b2, mid), greaterThanEqual(b3, mid), greaterThanEqual(m, mid)))
        assertEquals(listOf(even, odd, back, all), listOf(m gte b1, m gte b2, m gte b3, m gte m))
        assertEquals(listOf(odd, even, front, all), listOf(b1 gte mid, b2 gte mid, b3 gte mid, m gte mid))

        assertEquals(listOf(all, none, ends), listOf(equal(s, s), equal(b1, b2), equal(b1, b3)))
        assertEquals(listOf(odd, even, front), listOf(equal(b1, hi), equal(b2, hi), equal(b3, hi)))
        assertEquals(listOf(all, none, ends), listOf(s eq s, b1 eq b2, b1 eq b3))
        assertEquals(listOf(odd, even, front), listOf(b1 eq hi, b2 eq hi, b3 eq hi))
        assertEquals(listOf(none, all, middle), listOf(notEqual(s, s), notEqual(b1, b2), notEqual(b1, b3)))
        assertEquals(listOf(even, odd, back), listOf(notEqual(b1, hi), notEqual(b2, hi), notEqual(b3, hi)))
        assertEquals(listOf(none, all, middle), listOf(s neq s, b1 neq b2, b1 neq b3))
        assertEquals(listOf(even, odd, back), listOf(b1 neq hi, b2 neq hi, b3 neq hi))

        // Every component below, within and above the range in turn.
        val c1 = Int3(lo, mid, hi); val c2 = Int3(mid, hi, lo); val c3 = Int3(hi, lo, mid)
        val min = 3; val max = 7
        assertEquals(listOf(Int3(min, mid, max), Int3(mid, max, min), Int3(max, min, mid)), listOf(clamp(c1, min, max), clamp(c2, min, max), clamp(c3, min, max)))
        val vmin = Int3(min, min, min); val vmax = Int3(max, max, max)
        assertEquals(listOf(Int3(min, mid, max), Int3(mid, max, min), Int3(max, min, mid)), listOf(clamp(c1, vmin, vmax), clamp(c2, vmin, vmax), clamp(c3, vmin, vmax)))
    }

    @Test
    fun int4() {
        val lo = 1; val mid = 5; val hi = 9; val top = 13
        val m = Int4(mid, mid, mid, mid); val s = Int4(lo, mid, hi, top)
        val b1 = Int4(hi, lo, hi, lo); val b2 = Int4(lo, hi, lo, hi); val b3 = Int4(hi, hi, lo, lo)
        val odd = Bool4(true, false, true, false); val even = Bool4(false, true, false, true); val front = Bool4(true, true, false, false); val back = Bool4(false, false, true, true)
        val ends = Bool4(true, false, false, true); val middle = Bool4(false, true, true, false); val all = Bool4(true, true, true, true); val none = Bool4(false, false, false, false)

        assertEquals(listOf(odd, even, front, none), listOf(lessThan(m, b1), lessThan(m, b2), lessThan(m, b3), lessThan(m, m)))
        assertEquals(listOf(even, odd, back, none), listOf(lessThan(b1, mid), lessThan(b2, mid), lessThan(b3, mid), lessThan(m, mid)))
        assertEquals(listOf(odd, even, front, none), listOf(m lt b1, m lt b2, m lt b3, m lt m))
        assertEquals(listOf(even, odd, back, none), listOf(b1 lt mid, b2 lt mid, b3 lt mid, m lt mid))
        assertEquals(listOf(odd, even, front, all), listOf(lessThanEqual(m, b1), lessThanEqual(m, b2), lessThanEqual(m, b3), lessThanEqual(m, m)))
        assertEquals(listOf(even, odd, back, all), listOf(lessThanEqual(b1, mid), lessThanEqual(b2, mid), lessThanEqual(b3, mid), lessThanEqual(m, mid)))
        assertEquals(listOf(odd, even, front, all), listOf(m lte b1, m lte b2, m lte b3, m lte m))
        assertEquals(listOf(even, odd, back, all), listOf(b1 lte mid, b2 lte mid, b3 lte mid, m lte mid))
        assertEquals(listOf(even, odd, back, none), listOf(greaterThan(m, b1), greaterThan(m, b2), greaterThan(m, b3), greaterThan(m, m)))
        assertEquals(listOf(odd, even, front, none), listOf(greaterThan(b1, mid), greaterThan(b2, mid), greaterThan(b3, mid), greaterThan(m, mid)))
        assertEquals(listOf(even, odd, back, none), listOf(m gt b1, m gt b2, m gt b3, m gt m))
        assertEquals(listOf(odd, even, front, none), listOf(b1 gt mid, b2 gt mid, b3 gt mid, m gt mid))
        assertEquals(listOf(even, odd, back, all), listOf(greaterThanEqual(m, b1), greaterThanEqual(m, b2), greaterThanEqual(m, b3), greaterThanEqual(m, m)))
        assertEquals(listOf(odd, even, front, all), listOf(greaterThanEqual(b1, mid), greaterThanEqual(b2, mid), greaterThanEqual(b3, mid), greaterThanEqual(m, mid)))
        assertEquals(listOf(even, odd, back, all), listOf(m gte b1, m gte b2, m gte b3, m gte m))
        assertEquals(listOf(odd, even, front, all), listOf(b1 gte mid, b2 gte mid, b3 gte mid, m gte mid))

        assertEquals(listOf(all, none, ends), listOf(equal(s, s), equal(b1, b2), equal(b1, b3)))
        assertEquals(listOf(odd, even, front), listOf(equal(b1, hi), equal(b2, hi), equal(b3, hi)))
        assertEquals(listOf(all, none, ends), listOf(s eq s, b1 eq b2, b1 eq b3))
        assertEquals(listOf(odd, even, front), listOf(b1 eq hi, b2 eq hi, b3 eq hi))
        assertEquals(listOf(none, all, middle), listOf(notEqual(s, s), notEqual(b1, b2), notEqual(b1, b3)))
        assertEquals(listOf(even, odd, back), listOf(notEqual(b1, hi), notEqual(b2, hi), notEqual(b3, hi)))
        assertEquals(listOf(none, all, middle), listOf(s neq s, b1 neq b2, b1 neq b3))
        assertEquals(listOf(even, odd, back), listOf(b1 neq hi, b2 neq hi, b3 neq hi))

        // Every component below, within and above the range in turn.
        val c1 = Int4(lo, mid, hi, mid); val c2 = Int4(mid, hi, lo, hi); val c3 = Int4(hi, lo, mid, lo)
        val min = 3; val max = 7
        assertEquals(listOf(Int4(min, mid, max, mid), Int4(mid, max, min, max), Int4(max, min, mid, min)), listOf(clamp(c1, min, max), clamp(c2, min, max), clamp(c3, min, max)))
        val vmin = Int4(min, min, min, min); val vmax = Int4(max, max, max, max)
        assertEquals(listOf(Int4(min, mid, max, mid), Int4(mid, max, min, max), Int4(max, min, mid, min)), listOf(clamp(c1, vmin, vmax), clamp(c2, vmin, vmax), clamp(c3, vmin, vmax)))
    }

    @Test
    fun quaternion() {
        val lo = 1f; val mid = 5f; val hi = 9f; val top = 13f
        val m = Quaternion(mid, mid, mid, mid); val s = Quaternion(lo, mid, hi, top)
        val b1 = Quaternion(hi, lo, hi, lo); val b2 = Quaternion(lo, hi, lo, hi); val b3 = Quaternion(hi, hi, lo, lo)
        fun Quaternion.vec() = Float4(x, y, z, w)
        val rm = m.vec(); val rs = s.vec(); val r1 = b1.vec(); val r2 = b2.vec(); val r3 = b3.vec()
        val odd = Bool4(true, false, true, false); val even = Bool4(false, true, false, true); val front = Bool4(true, true, false, false); val back = Bool4(false, false, true, true)
        val ends = Bool4(true, false, false, true); val middle = Bool4(false, true, true, false); val all = Bool4(true, true, true, true); val none = Bool4(false, false, false, false)

        assertEquals(listOf(odd, even, front, none), listOf(lessThan(m, b1), lessThan(m, b2), lessThan(m, b3), lessThan(m, m)))
        assertEquals(listOf(even, odd, back, none), listOf(lessThan(b1, mid), lessThan(b2, mid), lessThan(b3, mid), lessThan(m, mid)))
        assertEquals(listOf(odd, even, front, none), listOf(m lt r1, m lt r2, m lt r3, m lt rm))
        assertEquals(listOf(even, odd, back, none), listOf(b1 lt mid, b2 lt mid, b3 lt mid, m lt mid))
        assertEquals(listOf(odd, even, front, all), listOf(lessThanEqual(m, b1), lessThanEqual(m, b2), lessThanEqual(m, b3), lessThanEqual(m, m)))
        assertEquals(listOf(even, odd, back, all), listOf(lessThanEqual(b1, mid), lessThanEqual(b2, mid), lessThanEqual(b3, mid), lessThanEqual(m, mid)))
        assertEquals(listOf(odd, even, front, all), listOf(m lte r1, m lte r2, m lte r3, m lte rm))
        assertEquals(listOf(even, odd, back, all), listOf(b1 lte mid, b2 lte mid, b3 lte mid, m lte mid))
        assertEquals(listOf(even, odd, back, none), listOf(greaterThan(m, b1), greaterThan(m, b2), greaterThan(m, b3), greaterThan(m, m)))
        assertEquals(listOf(odd, even, front, none), listOf(greaterThan(b1, mid), greaterThan(b2, mid), greaterThan(b3, mid), greaterThan(m, mid)))
        assertEquals(listOf(even, odd, back, none), listOf(m gt r1, m gt r2, m gt r3, m gt rm))
        assertEquals(listOf(odd, even, front, none), listOf(b1 gt mid, b2 gt mid, b3 gt mid, m gt mid))
        assertEquals(listOf(even, odd, back, all), listOf(greaterThanEqual(m, b1), greaterThanEqual(m, b2), greaterThanEqual(m, b3), greaterThanEqual(m, m)))
        assertEquals(listOf(odd, even, front, all), listOf(greaterThanEqual(b1, mid), greaterThanEqual(b2, mid), greaterThanEqual(b3, mid), greaterThanEqual(m, mid)))
        assertEquals(listOf(even, odd, back, all), listOf(m gte r1, m gte r2, m gte r3, m gte rm))
        assertEquals(listOf(odd, even, front, all), listOf(b1 gte mid, b2 gte mid, b3 gte mid, m gte mid))

        assertEquals(listOf(all, none, ends), listOf(equal(s, s), equal(b1, b2), equal(b1, b3)))
        assertEquals(listOf(odd, even, front), listOf(equal(b1, hi), equal(b2, hi), equal(b3, hi)))
        assertEquals(listOf(all, none, ends), listOf(s eq rs, b1 eq r2, b1 eq r3))
        assertEquals(listOf(odd, even, front), listOf(b1 eq hi, b2 eq hi, b3 eq hi))
        assertEquals(listOf(none, all, middle), listOf(notEqual(s, s), notEqual(b1, b2), notEqual(b1, b3)))
        assertEquals(listOf(even, odd, back), listOf(notEqual(b1, hi), notEqual(b2, hi), notEqual(b3, hi)))
        assertEquals(listOf(none, all, middle), listOf(s neq rs, b1 neq r2, b1 neq r3))
        assertEquals(listOf(even, odd, back), listOf(b1 neq hi, b2 neq hi, b3 neq hi))

        // Within delta counts as equal.
        val near = Quaternion(mid + 0.05f, mid + 0.05f, mid + 0.05f, mid + 0.05f)
        assertEquals(listOf(all, none), listOf(equal(near, m, 0.1f), equal(near, m)))
        assertEquals(listOf(all, none), listOf(equal(near, mid, 0.1f), equal(near, mid)))
        assertEquals(listOf(none, all), listOf(notEqual(near, m, 0.1f), notEqual(near, m)))
        assertEquals(listOf(none, all), listOf(notEqual(near, mid, 0.1f), notEqual(near, mid)))
    }
}
