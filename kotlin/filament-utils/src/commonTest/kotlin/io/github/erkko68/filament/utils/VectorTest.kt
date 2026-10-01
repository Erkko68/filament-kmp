package io.github.erkko68.filament.utils

import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VectorTest {
    private fun assertNear(expected: Float, actual: Float) = assertEquals(expected, actual, 1e-5f)

    @Test
    fun float2Accessors() {
        val v = Float2(1f, 2f)
        assertEquals(listOf(1f, 2f), listOf(v.r, v.g))
        assertEquals(listOf(1f, 2f), listOf(v.s, v.t))
        assertEquals(v, v.xy); assertEquals(v, v.rg); assertEquals(v, v.st)
        assertEquals(Float2(3f), Float2(3f, 3f))
        assertEquals(v, Float2(v))

        v.r = 3f; v.g = 4f; assertEquals(Float2(3f, 4f), v)
        v.s = 5f; v.t = 6f; assertEquals(Float2(5f, 6f), v)
        v.xy = Float2(1f, 2f); assertEquals(Float2(1f, 2f), v)
        v.rg = Float2(3f, 4f); assertEquals(Float2(3f, 4f), v)
        v.st = Float2(5f, 6f); assertEquals(Float2(5f, 6f), v)

        assertEquals(5f, v[0]); assertEquals(6f, v[1])
        assertEquals(5f, v(1)); assertEquals(6f, v(2))
        assertEquals(Float2(6f, 5f), v[1, 0])
        assertEquals(6f, v[VectorComponent.T])
        assertEquals(Float2(6f, 5f), v[VectorComponent.G, VectorComponent.X])
        v[0] = 1f; v[VectorComponent.Y] = 2f; assertEquals(Float2(1f, 2f), v)
        v[0, 1] = 7f; assertEquals(Float2(7f), v)
        v[VectorComponent.R, VectorComponent.T] = 8f; assertEquals(Float2(8f), v)
        assertFailsWith<IllegalArgumentException> { v[2] }
        assertFailsWith<IllegalArgumentException> { v[VectorComponent.Z] }
        assertFailsWith<IllegalArgumentException> { v[2] = 0f }
        assertFailsWith<IllegalArgumentException> { v[VectorComponent.W] = 0f }
        assertContentEquals(floatArrayOf(8f, 8f), v.toFloatArray())
    }

    @Test
    fun float3Accessors() {
        val v = Float3(1f, 2f, 3f)
        assertEquals(listOf(1f, 2f, 3f), listOf(v.r, v.g, v.b))
        assertEquals(listOf(1f, 2f, 3f), listOf(v.s, v.t, v.p))
        assertEquals(Float2(1f, 2f), v.xy); assertEquals(Float2(1f, 2f), v.rg); assertEquals(Float2(1f, 2f), v.st)
        assertEquals(v, v.xyz); assertEquals(v, v.rgb); assertEquals(v, v.stp)
        assertEquals(Float3(1f, 2f, 0f), Float3(Float2(1f, 2f)))
        assertEquals(Float3(1f, 2f, 9f), Float3(Float2(1f, 2f), 9f))
        assertEquals(v, Float3(v))

        v.r = 4f; v.g = 5f; v.b = 6f; assertEquals(Float3(4f, 5f, 6f), v)
        v.s = 1f; v.t = 2f; v.p = 3f; assertEquals(Float3(1f, 2f, 3f), v)
        v.xy = Float2(7f, 8f); assertEquals(Float3(7f, 8f, 3f), v)
        v.rg = Float2(1f, 2f); v.st = Float2(1f, 2f); assertEquals(Float3(1f, 2f, 3f), v)
        v.xyz = Float3(4f, 5f, 6f); assertEquals(Float3(4f, 5f, 6f), v)
        v.rgb = Float3(1f, 2f, 3f); v.stp = Float3(1f, 2f, 3f); assertEquals(Float3(1f, 2f, 3f), v)

        assertEquals(3f, v[2]); assertEquals(3f, v(3))
        assertEquals(Float2(3f, 1f), v[2, 0]); assertEquals(Float3(3f, 2f, 1f), v[2, 1, 0])
        assertEquals(3f, v[VectorComponent.P])
        assertEquals(Float2(3f, 1f), v[VectorComponent.B, VectorComponent.S])
        assertEquals(Float3(3f, 2f, 1f), v[VectorComponent.Z, VectorComponent.Y, VectorComponent.X])
        v[2] = 9f; v[VectorComponent.R] = 8f; assertEquals(Float3(8f, 2f, 9f), v)
        v[0, 1] = 0f; v[1, 2, 0] = 1f; assertEquals(Float3(1f), v)
        v[VectorComponent.X, VectorComponent.Z] = 2f; assertEquals(Float3(2f, 1f, 2f), v)
        v[VectorComponent.X, VectorComponent.Y, VectorComponent.Z] = 3f; assertEquals(Float3(3f), v)
        assertFailsWith<IllegalArgumentException> { v[3] }
        assertFailsWith<IllegalArgumentException> { v[VectorComponent.W] }
        assertContentEquals(floatArrayOf(3f, 3f, 3f), v.toFloatArray())
    }

    @Test
    fun float4Accessors() {
        val v = Float4(1f, 2f, 3f, 4f)
        assertEquals(listOf(1f, 2f, 3f, 4f), listOf(v.r, v.g, v.b, v.a))
        assertEquals(listOf(1f, 2f, 3f, 4f), listOf(v.s, v.t, v.p, v.q))
        assertEquals(Float2(1f, 2f), v.xy); assertEquals(Float2(1f, 2f), v.rg); assertEquals(Float2(1f, 2f), v.st)
        assertEquals(Float3(1f, 2f, 3f), v.xyz); assertEquals(Float3(1f, 2f, 3f), v.rgb); assertEquals(Float3(1f, 2f, 3f), v.stp)
        assertEquals(v, v.xyzw); assertEquals(v, v.rgba); assertEquals(v, v.stpq)
        assertEquals(Float4(1f, 2f, 0f, 0f), Float4(Float2(1f, 2f)))
        assertEquals(Float4(1f, 2f, 3f, 0f), Float4(Float3(1f, 2f, 3f)))
        assertEquals(v, Float4(Float3(1f, 2f, 3f), 4f))
        assertEquals(v, Float4(v))

        v.r = 5f; v.g = 6f; v.b = 7f; v.a = 8f; assertEquals(Float4(5f, 6f, 7f, 8f), v)
        v.s = 1f; v.t = 2f; v.p = 3f; v.q = 4f; assertEquals(Float4(1f, 2f, 3f, 4f), v)
        v.xy = Float2(9f, 9f); v.rg = Float2(1f, 2f); v.st = Float2(1f, 2f); assertEquals(Float4(1f, 2f, 3f, 4f), v)
        v.xyz = Float3(9f); v.rgb = Float3(9f); v.stp = Float3(1f, 2f, 3f); assertEquals(Float4(1f, 2f, 3f, 4f), v)
        v.xyzw = Float4(0f); v.rgba = Float4(0f); v.stpq = Float4(1f, 2f, 3f, 4f); assertEquals(Float4(1f, 2f, 3f, 4f), v)

        assertEquals(4f, v[3]); assertEquals(4f, v(4))
        assertEquals(Float2(4f, 1f), v[3, 0]); assertEquals(Float3(4f, 3f, 2f), v[3, 2, 1])
        assertEquals(Float4(4f, 3f, 2f, 1f), v[3, 2, 1, 0])
        assertEquals(4f, v[VectorComponent.Q])
        assertEquals(Float2(4f, 1f), v[VectorComponent.A, VectorComponent.X])
        assertEquals(Float3(4f, 3f, 2f), v[VectorComponent.W, VectorComponent.Z, VectorComponent.Y])
        assertEquals(Float4(4f, 3f, 2f, 1f), v[VectorComponent.W, VectorComponent.Z, VectorComponent.Y, VectorComponent.X])
        v[3] = 0f; v[VectorComponent.A] = 5f; assertEquals(5f, v.w)
        v[0, 1] = 0f; v[0, 1, 2] = 1f; v[0, 1, 2, 3] = 2f; assertEquals(Float4(2f), v)
        v[VectorComponent.X, VectorComponent.W] = 3f
        v[VectorComponent.X, VectorComponent.Y, VectorComponent.Z] = 3f
        assertEquals(Float4(3f), v)
        v[VectorComponent.X, VectorComponent.Y, VectorComponent.Z, VectorComponent.W] = 4f
        assertEquals(Float4(4f), v)
        assertFailsWith<IllegalArgumentException> { v[4] }
        assertContentEquals(floatArrayOf(4f, 4f, 4f, 4f), v.toFloatArray())
    }

    @Test
    fun incrementAndDecrementReturnNewValuesWithoutMutating() {
        var a = Float2(1f, 2f); val a0 = a
        a++; assertEquals(Float2(2f, 3f), a); assertEquals(Float2(1f, 2f), a0)
        a--; assertEquals(Float2(1f, 2f), a)
        var b = Float3(1f); val b0 = b
        ++b; assertEquals(Float3(2f), b); assertEquals(Float3(1f), b0)
        --b; assertEquals(Float3(1f), b)
        var c = Float4(1f); val c0 = c
        c++; assertEquals(Float4(2f), c); assertEquals(Float4(1f), c0)
        c--; assertEquals(Float4(1f), c)
        var i = Int2(1); i++; assertEquals(Int2(2), i); i--; assertEquals(Int2(1), i)
        var j = Int3(1); j++; assertEquals(Int3(2), j); j--; assertEquals(Int3(1), j)
        var k = Int4(1); val k0 = k; k++; assertEquals(Int4(2), k); assertEquals(Int4(1), k0); k--; assertEquals(Int4(1), k)
    }

    @Test
    fun floatArithmetic() {
        val a2 = Float2(2f, 4f)
        assertEquals(Float2(-2f, -4f), -a2)
        assertEquals(Float2(3f, 5f), a2 + 1f); assertEquals(Float2(1f, 3f), a2 - 1f)
        assertEquals(Float2(4f, 8f), a2 * 2f); assertEquals(Float2(1f, 2f), a2 / 2f)
        assertEquals(Float2(3f, 5f), 1f + a2); assertEquals(Float2(-1f, -3f), 1f - a2)
        assertEquals(Float2(4f, 8f), 2f * a2); assertEquals(Float2(4f, 2f), 8f / a2)
        assertEquals(Float2(3f, 6f), a2 + Float2(1f, 2f)); assertEquals(Float2(1f, 2f), a2 - Float2(1f, 2f))
        assertEquals(Float2(2f, 8f), a2 * Float2(1f, 2f)); assertEquals(Float2(2f, 2f), a2 / Float2(1f, 2f))

        val a3 = Float3(2f, 4f, 6f)
        assertEquals(Float3(-2f, -4f, -6f), -a3)
        assertEquals(Float3(3f, 5f, 7f), a3 + 1f); assertEquals(Float3(1f, 3f, 5f), a3 - 1f)
        assertEquals(Float3(4f, 8f, 12f), a3 * 2f); assertEquals(Float3(1f, 2f, 3f), a3 / 2f)
        assertEquals(Float3(3f, 5f, 7f), 1f + a3); assertEquals(Float3(-1f, -3f, -5f), 1f - a3)
        assertEquals(Float3(4f, 8f, 12f), 2f * a3); assertEquals(Float3(6f, 3f, 2f), 12f / a3)
        assertEquals(Float3(3f, 6f, 6f), a3 + Float2(1f, 2f)); assertEquals(Float3(1f, 2f, 6f), a3 - Float2(1f, 2f))
        assertEquals(Float3(2f, 8f, 6f), a3 * Float2(1f, 2f)); assertEquals(Float3(2f, 2f, 6f), a3 / Float2(1f, 2f))
        assertEquals(Float3(3f, 6f, 9f), a3 + Float3(1f, 2f, 3f)); assertEquals(Float3(1f, 2f, 3f), a3 - Float3(1f, 2f, 3f))
        assertEquals(Float3(2f, 8f, 18f), a3 * Float3(1f, 2f, 3f)); assertEquals(Float3(2f), a3 / Float3(1f, 2f, 3f))

        val a4 = Float4(2f, 4f, 6f, 8f)
        assertEquals(Float4(-2f, -4f, -6f, -8f), -a4)
        assertEquals(Float4(3f, 5f, 7f, 9f), a4 + 1f); assertEquals(Float4(1f, 3f, 5f, 7f), a4 - 1f)
        assertEquals(Float4(4f, 8f, 12f, 16f), a4 * 2f); assertEquals(Float4(1f, 2f, 3f, 4f), a4 / 2f)
        assertEquals(Float4(3f, 5f, 7f, 9f), 1f + a4); assertEquals(Float4(-1f, -3f, -5f, -7f), 1f - a4)
        assertEquals(Float4(4f, 8f, 12f, 16f), 2f * a4); assertEquals(Float4(12f, 6f, 4f, 3f), 24f / a4)
        assertEquals(Float4(3f, 6f, 6f, 8f), a4 + Float2(1f, 2f)); assertEquals(Float4(1f, 2f, 6f, 8f), a4 - Float2(1f, 2f))
        assertEquals(Float4(2f, 8f, 6f, 8f), a4 * Float2(1f, 2f)); assertEquals(Float4(2f, 2f, 6f, 8f), a4 / Float2(1f, 2f))
        assertEquals(Float4(3f, 6f, 9f, 8f), a4 + Float3(1f, 2f, 3f)); assertEquals(Float4(1f, 2f, 3f, 8f), a4 - Float3(1f, 2f, 3f))
        assertEquals(Float4(2f, 8f, 18f, 8f), a4 * Float3(1f, 2f, 3f)); assertEquals(Float4(2f, 2f, 2f, 8f), a4 / Float3(1f, 2f, 3f))
        assertEquals(Float4(3f, 6f, 9f, 12f), a4 + Float4(1f, 2f, 3f, 4f)); assertEquals(Float4(1f, 2f, 3f, 4f), a4 - Float4(1f, 2f, 3f, 4f))
        assertEquals(Float4(2f, 8f, 18f, 32f), a4 * Float4(1f, 2f, 3f, 4f)); assertEquals(Float4(2f), a4 / Float4(1f, 2f, 3f, 4f))
    }

    @Test
    fun floatGeometry() {
        assertEquals(Float2(1f, 2f), abs(Float2(-1f, 2f)))
        assertEquals(Float3(1f, 2f, 3f), abs(Float3(-1f, 2f, -3f)))
        assertEquals(Float4(1f, 2f, 3f, 4f), abs(Float4(-1f, 2f, -3f, 4f)))
        assertNear(5f, length(Float2(3f, 4f))); assertNear(25f, length2(Float2(3f, 4f)))
        assertNear(3f, length(Float3(1f, 2f, 2f))); assertNear(9f, length2(Float3(1f, 2f, 2f)))
        assertNear(2f, length(Float4(1f))); assertNear(4f, length2(Float4(1f)))
        assertNear(5f, distance(Float2(0f), Float2(3f, 4f)))
        assertNear(3f, distance(Float3(0f), Float3(1f, 2f, 2f)))
        assertNear(2f, distance(Float4(0f), Float4(1f)))
        assertNear(11f, dot(Float2(1f, 2f), Float2(3f, 4f)))
        assertNear(32f, dot(Float3(1f, 2f, 3f), Float3(4f, 5f, 6f)))
        assertNear(70f, dot(Float4(1f, 2f, 3f, 4f), Float4(5f, 6f, 7f, 8f)))
        assertEquals(Float3(0f, 0f, 1f), cross(Float3(1f, 0f, 0f), Float3(0f, 1f, 0f)))
        assertEquals(Float3(0f, 0f, 1f), Float3(1f, 0f, 0f) x Float3(0f, 1f, 0f))
        normalize(Float2(3f, 4f)).let { assertNear(0.6f, it.x); assertNear(0.8f, it.y) }
        assertNear(1f, length(normalize(Float3(1f, 2f, 3f))))
        assertEquals(Float4(0.5f), normalize(Float4(1f)))

        // reflect/refract against a +Y surface; at eta = 1 refraction passes straight through.
        assertEquals(Float2(1f, 1f), reflect(Float2(1f, -1f), Float2(0f, 1f)))
        assertEquals(Float3(1f, 1f, 0f), reflect(Float3(1f, -1f, 0f), Float3(0f, 1f, 0f)))
        val incident2 = normalize(Float2(1f, -1f))
        assertEquals(incident2, refract(incident2, Float2(0f, 1f), 1f))
        assertEquals(Float2(0f), refract(incident2, Float2(0f, 1f), 2f), "total internal reflection")
        val incident3 = normalize(Float3(1f, -1f, 0f))
        assertEquals(Float3(0f), refract(incident3, Float3(0f, 1f, 0f), 2f))
        assertNear(1f, length(refract(incident3, Float3(0f, 1f, 0f), 0.5f)))
        assertNear(90f, degrees(angle(Float2(1f, 0f), Float2(0f, 1f))))
        assertNear(90f, degrees(angle(Float3(1f, 0f, 0f), Float3(0f, 0f, 1f))))
    }

    @Test
    fun floatRangeFunctions() {
        assertEquals(Float2(0f, 1f), clamp(Float2(-1f, 2f), 0f, 1f))
        assertEquals(Float2(0f, 3f), clamp(Float2(-1f, 4f), Float2(0f, 1f), Float2(2f, 3f)))
        assertEquals(Float3(0f, 0.5f, 1f), clamp(Float3(-1f, 0.5f, 2f), 0f, 1f))
        assertEquals(Float3(0f, 1f, 5f), clamp(Float3(-1f, 0f, 9f), Float3(0f, 1f, 2f), Float3(3f, 4f, 5f)))
        assertEquals(Float4(0f, 0.5f, 1f, 1f), clamp(Float4(-1f, 0.5f, 2f, 3f), 0f, 1f))
        assertEquals(Float4(0f, 1f, 2f, 7f), clamp(Float4(-1f, 0f, 1f, 9f), Float4(0f, 1f, 2f, 3f), Float4(4f, 5f, 6f, 7f)))
        assertEquals(Float2(1f, 2f), mix(Float2(0f), Float2(2f, 4f), 0.5f))
        assertEquals(Float2(0f, 4f), mix(Float2(0f), Float2(2f, 4f), Float2(0f, 1f)))
        assertEquals(Float3(1f, 2f, 3f), mix(Float3(0f), Float3(2f, 4f, 6f), 0.5f))
        assertEquals(Float3(0f, 4f, 3f), mix(Float3(0f), Float3(2f, 4f, 6f), Float3(0f, 1f, 0.5f)))
        assertEquals(Float4(1f), mix(Float4(0f), Float4(2f), 0.5f))
        assertEquals(Float4(0f, 2f, 1f, 2f), mix(Float4(0f), Float4(2f), Float4(0f, 1f, 0.5f, 1f)))
        assertEquals(1f, min(Float2(1f, 2f))); assertEquals(2f, max(Float2(1f, 2f)))
        assertEquals(1f, min(Float3(3f, 1f, 2f))); assertEquals(3f, max(Float3(3f, 1f, 2f)))
        assertEquals(1f, min(Float4(3f, 1f, 2f, 4f))); assertEquals(4f, max(Float4(3f, 1f, 2f, 4f)))
        assertEquals(Float2(1f, 1f), min(Float2(1f, 2f), Float2(2f, 1f))); assertEquals(Float2(2f), max(Float2(1f, 2f), Float2(2f, 1f)))
        assertEquals(Float3(1f), min(Float3(1f, 2f, 1f), Float3(2f, 1f, 2f))); assertEquals(Float3(2f), max(Float3(1f, 2f, 1f), Float3(2f, 1f, 2f)))
        assertEquals(Float4(1f), min(Float4(1f, 2f, 1f, 2f), Float4(2f, 1f, 2f, 1f))); assertEquals(Float4(2f), max(Float4(1f, 2f, 1f, 2f), Float4(2f, 1f, 2f, 1f)))
        assertEquals(Float2(2f, 4f), transform(Float2(1f, 2f)) { it * 2f })
        assertEquals(Float3(2f, 4f, 6f), transform(Float3(1f, 2f, 3f)) { it * 2f })
        assertEquals(Float4(2f), transform(Float4(1f)) { it * 2f })
        val mutable = Float4(1f); mutable.transform { it + 1f }; assertEquals(Float4(2f), mutable)
    }

    @Test
    fun floatComparisons() {
        val a2 = Float2(1f, 3f); val b2 = Float2(2f, 2f)
        assertEquals(Bool2(true, false), lessThan(a2, 2f)); assertEquals(Bool2(true, false), lessThan(a2, b2))
        assertEquals(Bool2(true, false), lessThanEqual(a2, 1f)); assertEquals(Bool2(true, false), lessThanEqual(a2, b2))
        assertEquals(Bool2(false, true), greaterThan(a2, 2f)); assertEquals(Bool2(false, true), greaterThan(a2, b2))
        assertEquals(Bool2(false, true), greaterThanEqual(a2, 3f)); assertEquals(Bool2(false, true), greaterThanEqual(a2, b2))
        assertEquals(Bool2(true, false), equal(a2, 1f)); assertEquals(Bool2(false, false), equal(a2, b2))
        assertEquals(Bool2(true, true), equal(a2, b2, delta = 1f))
        assertEquals(Bool2(false, true), notEqual(a2, 1f)); assertEquals(Bool2(true, true), notEqual(a2, b2))
        assertEquals(Bool2(false, false), notEqual(a2, b2, delta = 1f))
        assertEquals(Bool2(true, false), a2 lt 2f); assertEquals(Bool2(true, false), a2 lt b2)
        assertEquals(Bool2(true, false), a2 lte 1f); assertEquals(Bool2(true, false), a2 lte b2)
        assertEquals(Bool2(false, true), a2 gt 2f); assertEquals(Bool2(false, true), a2 gt b2)
        assertEquals(Bool2(false, true), a2 gte 3f); assertEquals(Bool2(false, true), a2 gte b2)
        assertEquals(Bool2(true, false), a2 eq 1f); assertEquals(Bool2(false, false), a2 eq b2)
        assertEquals(Bool2(false, true), a2 neq 1f); assertEquals(Bool2(true, true), a2 neq b2)
        assertEquals(Float2(-1f, 1f), a2.compareTo(2f)); assertEquals(Float2(-1f, 1f), a2.compareTo(b2))
        assertEquals(Float2(0f, 0f), a2.compareTo(2f, delta = 1f))
        assertTrue(a2.equals(Float2(1.1f, 3.1f), delta = 0.2f)); assertFalse(a2.equals(2f))

        val a3 = Float3(1f, 2f, 3f); val b3 = Float3(2f)
        assertEquals(Bool3(true, false, false), lessThan(a3, 2f)); assertEquals(Bool3(true, false, false), lessThan(a3, b3))
        assertEquals(Bool3(true, true, false), lessThanEqual(a3, 2f)); assertEquals(Bool3(true, true, false), lessThanEqual(a3, b3))
        assertEquals(Bool3(false, false, true), greaterThan(a3, 2f)); assertEquals(Bool3(false, false, true), greaterThan(a3, b3))
        assertEquals(Bool3(false, true, true), greaterThanEqual(a3, 2f)); assertEquals(Bool3(false, true, true), greaterThanEqual(a3, b3))
        assertEquals(Bool3(false, true, false), equal(a3, 2f)); assertEquals(Bool3(false, true, false), equal(a3, b3))
        assertEquals(Bool3(true, false, true), notEqual(a3, 2f)); assertEquals(Bool3(true, false, true), notEqual(a3, b3))
        assertEquals(Bool3(true, false, false), a3 lt 2f); assertEquals(Bool3(true, false, false), a3 lt b3)
        assertEquals(Bool3(true, true, false), a3 lte 2f); assertEquals(Bool3(true, true, false), a3 lte b3)
        assertEquals(Bool3(false, false, true), a3 gt 2f); assertEquals(Bool3(false, false, true), a3 gt b3)
        assertEquals(Bool3(false, true, true), a3 gte 2f); assertEquals(Bool3(false, true, true), a3 gte b3)
        assertEquals(Bool3(false, true, false), a3 eq 2f); assertEquals(Bool3(false, true, false), a3 eq b3)
        assertEquals(Bool3(true, false, true), a3 neq 2f); assertEquals(Bool3(true, false, true), a3 neq b3)
        assertEquals(Float3(-1f, 0f, 1f), a3.compareTo(2f)); assertEquals(Float3(-1f, 0f, 1f), a3.compareTo(b3))
        assertTrue(a3.equals(Float3(1f, 2f, 3f))); assertFalse(a3.equals(2f))

        val a4 = Float4(1f, 2f, 3f, 2f); val b4 = Float4(2f)
        assertEquals(Bool4(true, false, false, false), lessThan(a4, 2f)); assertEquals(Bool4(true, false, false, false), lessThan(a4, b4))
        assertEquals(Bool4(true, true, false, true), lessThanEqual(a4, 2f)); assertEquals(Bool4(true, true, false, true), lessThanEqual(a4, b4))
        assertEquals(Bool4(false, false, true, false), greaterThan(a4, 2f)); assertEquals(Bool4(false, false, true, false), greaterThan(a4, b4))
        assertEquals(Bool4(false, true, true, true), greaterThanEqual(a4, 2f)); assertEquals(Bool4(false, true, true, true), greaterThanEqual(a4, b4))
        assertEquals(Bool4(false, true, false, true), equal(a4, 2f)); assertEquals(Bool4(false, true, false, true), equal(a4, b4))
        assertEquals(Bool4(true, false, true, false), notEqual(a4, 2f)); assertEquals(Bool4(true, false, true, false), notEqual(a4, b4))
        assertEquals(Bool4(true, false, false, false), a4 lt 2f); assertEquals(Bool4(true, false, false, false), a4 lt b4)
        assertEquals(Bool4(true, true, false, true), a4 lte 2f); assertEquals(Bool4(true, true, false, true), a4 lte b4)
        assertEquals(Bool4(false, false, true, false), a4 gt 2f); assertEquals(Bool4(false, false, true, false), a4 gt b4)
        assertEquals(Bool4(false, true, true, true), a4 gte 2f); assertEquals(Bool4(false, true, true, true), a4 gte b4)
        assertEquals(Bool4(false, true, false, true), a4 eq 2f); assertEquals(Bool4(false, true, false, true), a4 eq b4)
        assertEquals(Bool4(true, false, true, false), a4 neq 2f); assertEquals(Bool4(true, false, true, false), a4 neq b4)
        assertEquals(Float4(-1f, 0f, 1f, 0f), a4.compareTo(2f)); assertEquals(Float4(-1f, 0f, 1f, 0f), a4.compareTo(b4))
        assertTrue(a4.equals(Float4(1f, 2f, 3f, 2f))); assertFalse(a4.equals(2f))

        assertTrue(any(Bool2(false, true))); assertFalse(all(Bool2(false, true)))
        assertTrue(any(Bool3(false, false, true))); assertTrue(all(Bool3(true, true, true)))
        assertFalse(any(Bool4())); assertTrue(all(Bool4(true, true, true, true)))
    }

    @Test
    fun rayPointsAlongItsDirection() {
        val ray = Ray(Float3(1f, 0f, 0f), Float3(0f, 2f, 0f))
        assertEquals(Float3(1f, 4f, 0f), pointAt(ray, 2f))
        assertEquals(Float3(0f, 0f, 0f), Ray(direction = Float3(1f)).origin)
        assertNear(sqrt(3f), length(Ray(direction = Float3(1f)).direction))
    }

    @Test
    fun int4AndBool4AndHalf4Swizzles() {
        val i = Int4(1, 2, 3, 4)
        assertEquals(listOf(1, 2, 3, 4), listOf(i.r, i.g, i.b, i.a)); assertEquals(listOf(1, 2, 3, 4), listOf(i.s, i.t, i.p, i.q))
        assertEquals(Int2(1, 2), i.xy); assertEquals(Int2(1, 2), i.rg); assertEquals(Int2(1, 2), i.st)
        assertEquals(Int3(1, 2, 3), i.xyz); assertEquals(Int3(1, 2, 3), i.rgb); assertEquals(Int3(1, 2, 3), i.stp)
        assertEquals(i, i.xyzw); assertEquals(i, i.rgba); assertEquals(i, i.stpq)
        i.a = 9; i.q = 8; assertEquals(8, i.w)
        i.rgb = Int3(7); i.stp = Int3(6); i.rg = Int2(5); i.st = Int2(4); assertEquals(Int4(4, 4, 6, 8), i)
        i.rgba = Int4(1); i.stpq = Int4(2); i.xyzw = Int4(3); i.xyz = Int3(4); i.xy = Int2(5); assertEquals(Int4(5, 5, 4, 3), i)
        i.r = 1; i.g = 2; i.b = 3; i.s = 1; i.t = 2; i.p = 3; assertEquals(Int4(1, 2, 3, 3), i)
        assertEquals(3, i[2]); assertEquals(3, i(3)); assertEquals(Int4(3, 3, 2, 1), i[3, 2, 1, 0])
        assertEquals(Int3(3, 2, 1), i[VectorComponent.W, VectorComponent.Y, VectorComponent.X])
        i[0, 1, 2, 3] = 0; i[VectorComponent.B] = 5; assertEquals(Int4(0, 0, 5, 0), i)
        assertContentEquals(intArrayOf(0, 0, 5, 0), i.toIntArray())
        assertEquals(Int4(1, 2, 3, 0), Int4(Int3(1, 2, 3))); assertEquals(Int4(1, 2, 0, 0), Int4(Int2(1, 2)))

        val b = Bool4(true, false, true, false)
        assertEquals(listOf(true, false, true, false), listOf(b.r, b.g, b.b, b.a))
        assertEquals(listOf(true, false, true, false), listOf(b.s, b.t, b.p, b.q))
        assertEquals(Bool2(true, false), b.xy); assertEquals(Bool2(true, false), b.rg); assertEquals(Bool2(true, false), b.st)
        assertEquals(Bool3(true, false, true), b.xyz); assertEquals(Bool3(true, false, true), b.rgb); assertEquals(Bool3(true, false, true), b.stp)
        assertEquals(b, b.xyzw); assertEquals(b, b.rgba); assertEquals(b, b.stpq)
        b.a = true; b.q = true; assertTrue(b.w)
        b.rgb = Bool3(false, false, false); assertEquals(Bool4(false, false, false, true), b)
        b.stp = Bool3(true, true, true); b.rg = Bool2(false, false); b.st = Bool2(false, true); assertEquals(Bool4(false, true, true, true), b)
        b.rgba = Bool4(); b.stpq = Bool4(); b.xyzw = Bool4(true, true, true, true); b.xyz = Bool3(); b.xy = Bool2(true, true)
        assertEquals(Bool4(true, true, false, true), b)
        b.r = false; b.g = false; b.b = true; b.s = true; b.t = true; b.p = false; assertEquals(Bool4(true, true, false, true), b)
        assertTrue(b[VectorComponent.X]); assertFalse(b[2]); assertEquals(Bool2(false, true), b[VectorComponent.Z, VectorComponent.W])
        assertEquals(Bool4(true, true, false, true), Bool4(Bool3(true, true, false), true))
        assertEquals(Bool4(true, false, false, false), Bool4(Bool2(true, false)))
        val b3 = Bool3(true, false, true)
        assertEquals(listOf(true, false, true), listOf(b3.r, b3.g, b3.b)); assertEquals(Bool2(false, true), b3[VectorComponent.G, VectorComponent.B])
        assertEquals(Bool3(true, false, false), Bool3(Bool2(true, false)))

        val h = Half4(1f.h, 2f.h, 3f.h, 4f.h)
        assertEquals(listOf(1f, 2f, 3f, 4f), listOf(h.r, h.g, h.b, h.a).map { it.toFloat() })
        assertEquals(listOf(1f, 2f, 3f, 4f), listOf(h.s, h.t, h.p, h.q).map { it.toFloat() })
        assertEquals(Half2(1f.h, 2f.h), h.xy); assertEquals(Half2(1f.h, 2f.h), h.rg); assertEquals(Half2(1f.h, 2f.h), h.st)
        assertEquals(Half3(1f.h, 2f.h, 3f.h), h.xyz); assertEquals(Half3(1f.h, 2f.h, 3f.h), h.rgb); assertEquals(Half3(1f.h, 2f.h, 3f.h), h.stp)
        assertEquals(h, h.xyzw); assertEquals(h, h.rgba); assertEquals(h, h.stpq)
        h.a = 5f.h; h.q = 6f.h; assertEquals(6f, h.w.toFloat())
        h.rgb = Half3(1f.h); h.stp = Half3(2f.h); h.rg = Half2(3f.h); h.st = Half2(4f.h)
        h.rgba = Half4(0f.h); h.stpq = Half4(1f.h); h.xyzw = Half4(2f.h); h.xyz = Half3(3f.h); h.xy = Half2(4f.h)
        h.r = 1f.h; h.g = 2f.h; h.b = 3f.h; h.s = 1f.h; h.t = 2f.h; h.p = 3f.h
        assertContentEquals(floatArrayOf(1f, 2f, 3f, 2f), h.toFloatArray())
        assertEquals(3f, h[2].toFloat()); assertEquals(3f, h(3).toFloat())
        assertEquals(Half4(2f.h, 3f.h, 2f.h, 1f.h), h[3, 2, 1, 0])
        h[0, 1, 2, 3] = 0f.h; h[VectorComponent.B] = 5f.h; assertContentEquals(floatArrayOf(0f, 0f, 5f, 0f), h.toFloatArray())
    }

    @Test
    fun intArithmeticAndFunctions() {
        val a3 = Int3(2, 4, 6); val a4 = Int4(2, 4, 6, 8)
        assertEquals(Int2(-1, -2), -Int2(1, 2)); assertEquals(Int3(-2, -4, -6), -a3); assertEquals(Int4(-2, -4, -6, -8), -a4)
        assertEquals(Int2(3, 4), Int2(1, 2) + 2); assertEquals(Int2(2, 4), Int2(1, 2) * 2); assertEquals(Int2(0, 1), Int2(1, 2) / 2)
        assertEquals(Int2(-1, 0), Int2(1, 2) - 2); assertEquals(Int2(2, 4), Int2(1, 2) + Int2(1, 2)); assertEquals(Int2(1, 1), Int2(1, 2) / Int2(1, 2))
        assertEquals(Int2(0, 0), Int2(1, 2) - Int2(1, 2)); assertEquals(Int2(1, 4), Int2(1, 2) * Int2(1, 2))
        assertEquals(Int3(3, 5, 7), a3 + 1); assertEquals(Int3(3, 5, 7), 1 + a3); assertEquals(Int3(-1, -3, -5), 1 - a3)
        assertEquals(Int3(4, 8, 12), 2 * a3); assertEquals(Int3(6, 3, 2), 12 / a3)
        assertEquals(Int3(3, 6, 6), a3 + Int2(1, 2)); assertEquals(Int3(1, 2, 6), a3 - Int2(1, 2))
        assertEquals(Int3(2, 8, 6), a3 * Int2(1, 2)); assertEquals(Int3(2, 2, 6), a3 / Int2(1, 2))
        assertEquals(Int3(1, 2, 3), a3 - Int3(1, 2, 3)); assertEquals(Int3(2), a3 / Int3(1, 2, 3)); assertEquals(Int3(2, 8, 18), a3 * Int3(1, 2, 3))
        assertEquals(Int4(3, 5, 7, 9), 1 + a4); assertEquals(Int4(-1, -3, -5, -7), 1 - a4); assertEquals(Int4(4, 8, 12, 16), 2 * a4)
        assertEquals(Int4(12, 6, 4, 3), 24 / a4); assertEquals(Int4(1, 2, 3, 4), a4 / 2); assertEquals(Int4(1, 3, 5, 7), a4 - 1)
        assertEquals(Int4(4, 8, 12, 16), a4 * 2)
        assertEquals(Int4(3, 6, 6, 8), a4 + Int2(1, 2)); assertEquals(Int4(1, 2, 6, 8), a4 - Int2(1, 2))
        assertEquals(Int4(2, 8, 6, 8), a4 * Int2(1, 2)); assertEquals(Int4(2, 2, 6, 8), a4 / Int2(1, 2))
        assertEquals(Int4(3, 6, 9, 8), a4 + Int3(1, 2, 3)); assertEquals(Int4(1, 2, 3, 8), a4 - Int3(1, 2, 3))
        assertEquals(Int4(2, 8, 18, 8), a4 * Int3(1, 2, 3)); assertEquals(Int4(2, 2, 2, 8), a4 / Int3(1, 2, 3))
        assertEquals(Int4(3, 6, 9, 12), a4 + Int4(1, 2, 3, 4)); assertEquals(Int4(1, 2, 3, 4), a4 - Int4(1, 2, 3, 4))
        assertEquals(Int4(2, 8, 18, 32), a4 * Int4(1, 2, 3, 4)); assertEquals(Int4(2), a4 / Int4(1, 2, 3, 4))

        assertEquals(Int3(1, 2, 3), abs(Int3(-1, 2, -3))); assertEquals(Int4(1, 2, 3, 4), abs(Int4(-1, 2, -3, 4)))
        assertEquals(32, dot(Int3(1, 2, 3), Int3(4, 5, 6))); assertEquals(70, dot(Int4(1, 2, 3, 4), Int4(5, 6, 7, 8)))
        assertEquals(Int3(0, 0, 1), cross(Int3(1, 0, 0), Int3(0, 1, 0))); assertEquals(Int3(0, 0, 1), Int3(1, 0, 0) x Int3(0, 1, 0))
        assertEquals(Int3(0, 1, 1), clamp(Int3(-1, 1, 5), 0, 1)); assertEquals(Int3(0, 2, 4), clamp(Int3(-1, 1, 5), Int3(0, 2, 3), Int3(1, 3, 4)))
        assertEquals(Int4(0, 1, 1, 1), clamp(Int4(-1, 1, 5, 9), 0, 1)); assertEquals(Int4(0, 2, 4, 5), clamp(Int4(-1, 1, 5, 9), Int4(0, 2, 3, 4), Int4(1, 3, 4, 5)))
        assertEquals(1, min(Int2(1, 2))); assertEquals(2, max(Int2(1, 2))); assertEquals(Int2(1), min(Int2(1, 2), Int2(2, 1))); assertEquals(Int2(2), max(Int2(1, 2), Int2(2, 1)))
        assertEquals(1, min(Int3(3, 1, 2))); assertEquals(3, max(Int3(3, 1, 2))); assertEquals(Int3(1), min(Int3(1, 2, 1), Int3(2, 1, 2))); assertEquals(Int3(2), max(Int3(1, 2, 1), Int3(2, 1, 2)))
        assertEquals(1, min(Int4(3, 1, 2, 4))); assertEquals(4, max(Int4(3, 1, 2, 4))); assertEquals(Int4(1), min(Int4(1, 2, 1, 2), Int4(2, 1, 2, 1))); assertEquals(Int4(2), max(Int4(1, 2, 1, 2), Int4(2, 1, 2, 1)))
        assertEquals(Int2(2, 4), transform(Int2(1, 2)) { it * 2 }); assertEquals(Int3(2), transform(Int3(1)) { it * 2 }); assertEquals(Int4(2), transform(Int4(1)) { it * 2 })
    }

    @Test
    fun intComparisons() {
        val a2 = Int2(1, 3); val b2 = Int2(2, 2)
        assertEquals(Bool2(true, false), lessThan(a2, 2)); assertEquals(Bool2(true, false), lessThan(a2, b2)); assertEquals(Bool2(true, false), a2 lt 2); assertEquals(Bool2(true, false), a2 lt b2)
        assertEquals(Bool2(true, false), lessThanEqual(a2, 1)); assertEquals(Bool2(true, false), lessThanEqual(a2, b2)); assertEquals(Bool2(true, false), a2 lte 1); assertEquals(Bool2(true, false), a2 lte b2)
        assertEquals(Bool2(false, true), greaterThan(a2, 2)); assertEquals(Bool2(false, true), greaterThan(a2, b2)); assertEquals(Bool2(false, true), a2 gt 2); assertEquals(Bool2(false, true), a2 gt b2)
        assertEquals(Bool2(false, true), greaterThanEqual(a2, 3)); assertEquals(Bool2(false, true), greaterThanEqual(a2, b2)); assertEquals(Bool2(false, true), a2 gte 3); assertEquals(Bool2(false, true), a2 gte b2)
        assertEquals(Bool2(true, false), equal(a2, 1)); assertEquals(Bool2(false, false), equal(a2, b2)); assertEquals(Bool2(true, false), a2 eq 1); assertEquals(Bool2(false, false), a2 eq b2)
        assertEquals(Bool2(false, true), notEqual(a2, 1)); assertEquals(Bool2(true, true), notEqual(a2, b2)); assertEquals(Bool2(false, true), a2 neq 1); assertEquals(Bool2(true, true), a2 neq b2)

        val a3 = Int3(1, 2, 3); val b3 = Int3(2)
        val lt3 = Bool3(true, false, false); val lte3 = Bool3(true, true, false); val gt3 = Bool3(false, false, true); val gte3 = Bool3(false, true, true)
        val eq3 = Bool3(false, true, false); val neq3 = Bool3(true, false, true)
        assertEquals(listOf(lt3, lt3, lt3, lt3), listOf(lessThan(a3, 2), lessThan(a3, b3), a3 lt 2, a3 lt b3))
        assertEquals(listOf(lte3, lte3, lte3, lte3), listOf(lessThanEqual(a3, 2), lessThanEqual(a3, b3), a3 lte 2, a3 lte b3))
        assertEquals(listOf(gt3, gt3, gt3, gt3), listOf(greaterThan(a3, 2), greaterThan(a3, b3), a3 gt 2, a3 gt b3))
        assertEquals(listOf(gte3, gte3, gte3, gte3), listOf(greaterThanEqual(a3, 2), greaterThanEqual(a3, b3), a3 gte 2, a3 gte b3))
        assertEquals(listOf(eq3, eq3, eq3, eq3), listOf(equal(a3, 2), equal(a3, b3), a3 eq 2, a3 eq b3))
        assertEquals(listOf(neq3, neq3, neq3, neq3), listOf(notEqual(a3, 2), notEqual(a3, b3), a3 neq 2, a3 neq b3))

        val a4 = Int4(1, 2, 3, 2); val b4 = Int4(2)
        val lt4 = Bool4(true, false, false, false); val lte4 = Bool4(true, true, false, true); val gt4 = Bool4(false, false, true, false)
        val gte4 = Bool4(false, true, true, true); val eq4 = Bool4(false, true, false, true); val neq4 = Bool4(true, false, true, false)
        assertEquals(listOf(lt4, lt4, lt4, lt4), listOf(lessThan(a4, 2), lessThan(a4, b4), a4 lt 2, a4 lt b4))
        assertEquals(listOf(lte4, lte4, lte4, lte4), listOf(lessThanEqual(a4, 2), lessThanEqual(a4, b4), a4 lte 2, a4 lte b4))
        assertEquals(listOf(gt4, gt4, gt4, gt4), listOf(greaterThan(a4, 2), greaterThan(a4, b4), a4 gt 2, a4 gt b4))
        assertEquals(listOf(gte4, gte4, gte4, gte4), listOf(greaterThanEqual(a4, 2), greaterThanEqual(a4, b4), a4 gte 2, a4 gte b4))
        assertEquals(listOf(eq4, eq4, eq4, eq4), listOf(equal(a4, 2), equal(a4, b4), a4 eq 2, a4 eq b4))
        assertEquals(listOf(neq4, neq4, neq4, neq4), listOf(notEqual(a4, 2), notEqual(a4, b4), a4 neq 2, a4 neq b4))
    }

    @Test
    fun halfArithmeticFunctionsAndComparisons() {
        fun Half2.f() = toFloatArray().toList()
        fun Half3.f() = toFloatArray().toList()
        fun Half4.f() = toFloatArray().toList()
        val two = 2f.h
        val a2 = Half2(2f.h, 4f.h)
        assertEquals(listOf(-2f, -4f), (-a2).f())
        assertEquals(listOf(4f, 6f), (a2 + two).f()); assertEquals(listOf(0f, 2f), (a2 - two).f())
        assertEquals(listOf(4f, 8f), (a2 * two).f()); assertEquals(listOf(1f, 2f), (a2 / two).f())
        assertEquals(listOf(4f, 6f), (two + a2).f()); assertEquals(listOf(0f, -2f), (two - a2).f())
        assertEquals(listOf(4f, 8f), (two * a2).f()); assertEquals(listOf(1f, 0.5f), (two / a2).f())
        assertEquals(listOf(4f, 8f), (a2 + a2).f()); assertEquals(listOf(0f, 0f), (a2 - a2).f())
        assertEquals(listOf(4f, 16f), (a2 * a2).f()); assertEquals(listOf(1f, 1f), (a2 / a2).f())
        var inc2 = a2; inc2++; assertEquals(listOf(3f, 5f), inc2.f()); assertEquals(listOf(2f, 4f), a2.f()); inc2--; assertEquals(a2, inc2)
        assertEquals(2f, min(a2).toFloat()); assertEquals(4f, max(a2).toFloat())
        assertEquals(listOf(2f, 2f), min(a2, Half2(two)).f()); assertEquals(listOf(2f, 4f), max(a2, Half2(two)).f())
        assertEquals(listOf(4f, 8f), transform(a2) { it * two }.f())
        assertEquals(Bool2(false, true), a2 gt two); assertEquals(Bool2(true, false), a2 eq Half2(two))

        val a3 = Half3(1f.h, 2f.h, 3f.h)
        assertEquals(listOf(-1f, -2f, -3f), (-a3).f())
        assertEquals(listOf(3f, 4f, 5f), (two + a3).f()); assertEquals(listOf(1f, 0f, -1f), (two - a3).f())
        assertEquals(listOf(2f, 4f, 6f), (two * a3).f()); assertEquals(listOf(2f, 1f), (two / a3).f().take(2))
        assertEquals(listOf(2f, 4f, 3f), (a3 + Half2(1f.h, 2f.h)).f()); assertEquals(listOf(2f, 4f, 6f), (a3 + a3).f())
        assertEquals(listOf(1f, 2f, 3f), abs(-a3).f())
        assertEquals(14f, length2(a3).toFloat()); assertEquals(14f, dot(a3, a3).toFloat())
        assertEquals(1f, distance(Half3(0f.h), Half3(0f.h, 0f.h, 1f.h)).toFloat())
        assertEquals(listOf(0f, 0f, 1f), cross(Half3(1f.h, 0f.h, 0f.h), Half3(0f.h, 1f.h, 0f.h)).f())
        assertEquals(listOf(0f, 0f, 1f), (Half3(1f.h, 0f.h, 0f.h) x Half3(0f.h, 1f.h, 0f.h)).f())
        assertEquals(listOf(0f, 0f, 1f), normalize(Half3(0f.h, 0f.h, 4f.h)).f())
        assertEquals(listOf(1f, 1f, 0f), reflect(Half3(1f.h, (-1f).h, 0f.h), Half3(0f.h, 1f.h, 0f.h)).f())
        assertEquals(listOf(0f, -1f, 0f), refract(Half3(0f.h, (-1f).h, 0f.h), Half3(0f.h, 1f.h, 0f.h), 1f.h).f())
        assertEquals(listOf(1f, 2f, 2f), clamp(a3, 1f.h, two).f()); assertEquals(listOf(1f, 2f, 2f), clamp(a3, Half3(0f.h), Half3(1f.h, 2f.h, 2f.h)).f())
        assertEquals(listOf(1f, 2f, 3f), mix(Half3(0f.h), Half3(2f.h, 4f.h, 6f.h), 0.5f.h).f())
        assertEquals(listOf(0f, 4f, 3f), mix(Half3(0f.h), Half3(2f.h, 4f.h, 6f.h), Half3(0f.h, 1f.h, 0.5f.h)).f())
        assertEquals(1f, min(a3).toFloat()); assertEquals(3f, max(a3).toFloat())
        assertEquals(listOf(1f, 2f, 2f), min(a3, Half3(two)).f()); assertEquals(listOf(2f, 2f, 3f), max(a3, Half3(two)).f())
        assertEquals(listOf(2f, 4f, 6f), transform(a3) { it * two }.f())
        assertEquals(Bool3(true, false, false), a3 lt two); assertEquals(Bool3(true, true, false), a3 lte two)
        assertEquals(Bool3(false, false, true), a3 gt Half3(two)); assertEquals(Bool3(false, true, true), a3 gte Half3(two))
        assertEquals(Bool3(false, true, false), a3 eq two); assertEquals(Bool3(true, false, true), a3 neq Half3(two))
        assertEquals(Bool3(true, false, false), lessThan(a3, two)); assertEquals(Bool3(false, false, true), greaterThan(a3, Half3(two)))
        assertEquals(Bool3(false, true, false), equal(a3, two)); assertEquals(Bool3(true, false, true), notEqual(a3, Half3(two)))

        val a4 = Half4(1f.h, 2f.h, 3f.h, 2f.h)
        assertEquals(listOf(3f, 4f, 5f, 4f), (two + a4).f()); assertEquals(listOf(1f, 0f, -1f, 0f), (two - a4).f())
        assertEquals(listOf(2f, 4f, 6f, 4f), (two * a4).f()); assertEquals(listOf(2f, 1f), (two / a4).f().take(2))
        assertEquals(listOf(2f, 4f, 6f, 2f), (a4 + Half3(1f.h, 2f.h, 3f.h)).f()); assertEquals(listOf(2f, 4f, 3f, 2f), (a4 + Half2(1f.h, 2f.h)).f())
        assertEquals(listOf(1f, 2f, 3f, 2f), abs(-a4).f())
        assertEquals(18f, length2(a4).toFloat()); assertEquals(18f, dot(a4, a4).toFloat())
        assertEquals(2f, length(Half4(1f.h)).toFloat()); assertEquals(2f, distance(Half4(0f.h), Half4(1f.h)).toFloat())
        assertEquals(listOf(0.5f, 0.5f, 0.5f, 0.5f), normalize(Half4(1f.h)).f())
        assertEquals(listOf(1f, 2f, 2f, 2f), clamp(a4, 1f.h, two).f()); assertEquals(listOf(1f, 2f, 2f, 2f), clamp(a4, Half4(0f.h), Half4(two)).f())
        assertEquals(listOf(1f, 1f, 1f, 1f), mix(Half4(0f.h), Half4(two), 0.5f.h).f())
        assertEquals(listOf(0f, 2f, 1f, 2f), mix(Half4(0f.h), Half4(two), Half4(0f.h, 1f.h, 0.5f.h, 1f.h)).f())
        assertEquals(1f, min(a4).toFloat()); assertEquals(3f, max(a4).toFloat())
        assertEquals(listOf(1f, 2f, 2f, 2f), min(a4, Half4(two)).f()); assertEquals(listOf(2f, 2f, 3f, 2f), max(a4, Half4(two)).f())
        assertEquals(listOf(2f, 4f, 6f, 4f), transform(a4) { it * two }.f())
        assertEquals(Bool4(true, false, false, false), a4 lt two); assertEquals(Bool4(true, true, false, true), a4 lte Half4(two))
        assertEquals(Bool4(false, false, true, false), a4 gt two); assertEquals(Bool4(false, true, true, true), a4 gte Half4(two))
        assertEquals(Bool4(false, true, false, true), a4 eq two); assertEquals(Bool4(true, false, true, false), a4 neq Half4(two))
        assertEquals(Bool4(true, true, false, true), lessThanEqual(a4, two)); assertEquals(Bool4(false, true, true, true), greaterThanEqual(a4, Half4(two)))
        assertEquals(Bool4(false, true, false, true), equal(a4, Half4(two))); assertEquals(Bool4(true, false, true, false), notEqual(a4, two))
    }

    @Test
    fun twoAndThreeComponentIntBoolHalfAccessors() {
        val i2 = Int2(1, 2)
        assertEquals(listOf(1, 2, 1, 2), listOf(i2.r, i2.g, i2.s, i2.t)); assertEquals(i2, i2.xy); assertEquals(i2, i2.rg); assertEquals(i2, i2.st)
        i2.r = 3; i2.g = 4; i2.s = 5; i2.t = 6; i2.xy = Int2(7); i2.rg = Int2(8); i2.st = Int2(1, 2); assertEquals(Int2(1, 2), i2)
        assertEquals(2, i2[1]); assertEquals(2, i2(2)); assertEquals(Int2(2, 1), i2[1, 0]); assertEquals(2, i2[VectorComponent.G])
        assertEquals(Int2(2, 1), i2[VectorComponent.Y, VectorComponent.X]); i2[0] = 3; i2[VectorComponent.T] = 4; i2[0, 1] = 5
        i2[VectorComponent.X, VectorComponent.Y] = 6; assertEquals(Int2(6), i2); assertEquals(Int2(6), Int2(i2)); assertContentEquals(intArrayOf(6, 6), i2.toIntArray())
        assertFailsWith<IllegalArgumentException> { i2[2] }; assertFailsWith<IllegalArgumentException> { i2[VectorComponent.Z] }

        val i3 = Int3(1, 2, 3)
        assertEquals(listOf(1, 2, 3, 1, 2, 3), listOf(i3.r, i3.g, i3.b, i3.s, i3.t, i3.p))
        assertEquals(Int2(1, 2), i3.xy); assertEquals(Int2(1, 2), i3.rg); assertEquals(Int2(1, 2), i3.st)
        assertEquals(i3, i3.xyz); assertEquals(i3, i3.rgb); assertEquals(i3, i3.stp)
        i3.r = 0; i3.g = 0; i3.b = 0; i3.s = 1; i3.t = 2; i3.p = 3; i3.xy = Int2(0); i3.rg = Int2(1); i3.st = Int2(1, 2)
        i3.xyz = Int3(0); i3.rgb = Int3(0); i3.stp = Int3(1, 2, 3); assertEquals(Int3(1, 2, 3), i3)
        assertEquals(3, i3[2]); assertEquals(3, i3(3)); assertEquals(Int2(3, 1), i3[2, 0]); assertEquals(Int3(3, 2, 1), i3[2, 1, 0])
        assertEquals(3, i3[VectorComponent.P]); assertEquals(Int2(3, 1), i3[VectorComponent.Z, VectorComponent.X])
        assertEquals(Int3(3, 2, 1), i3[VectorComponent.B, VectorComponent.G, VectorComponent.R])
        i3[0] = 0; i3[VectorComponent.Y] = 0; i3[0, 1] = 4; i3[0, 1, 2] = 5; i3[VectorComponent.X, VectorComponent.Z] = 6
        i3[VectorComponent.X, VectorComponent.Y, VectorComponent.Z] = 7; assertEquals(Int3(7), Int3(i3))
        assertEquals(Int3(1, 2, 0), Int3(Int2(1, 2))); assertEquals(Int3(7), Int3(7, 7, 7)); assertContentEquals(intArrayOf(7, 7, 7), i3.toIntArray())
        assertFailsWith<IllegalArgumentException> { i3[3] }; assertFailsWith<IllegalArgumentException> { i3[VectorComponent.W] }

        val b2 = Bool2(true, false)
        assertEquals(listOf(true, false, true, false), listOf(b2.r, b2.g, b2.s, b2.t)); assertEquals(b2, b2.xy); assertEquals(b2, b2.rg); assertEquals(b2, b2.st)
        b2.r = false; b2.g = true; b2.s = true; b2.t = false; b2.xy = Bool2(); b2.rg = Bool2(true, true); b2.st = Bool2(false, true); assertEquals(Bool2(false, true), b2)
        assertTrue(b2[1]); assertTrue(b2(2)); assertEquals(Bool2(true, false), b2[1, 0]); assertTrue(b2[VectorComponent.Y])
        assertEquals(Bool2(true, false), b2[VectorComponent.T, VectorComponent.S])
        b2[0] = true; b2[VectorComponent.Y] = false; assertEquals(Bool2(true, false), Bool2(b2))
        b2[0, 1] = true; b2[VectorComponent.X, VectorComponent.Y] = false; assertEquals(Bool2(), b2)
        assertFailsWith<IllegalArgumentException> { b2[2] }

        val b3 = Bool3(true, false, true)
        assertEquals(listOf(true, false, true, true, false, true), listOf(b3.r, b3.g, b3.b, b3.s, b3.t, b3.p))
        assertEquals(Bool2(true, false), b3.xy); assertEquals(Bool2(true, false), b3.rg); assertEquals(Bool2(true, false), b3.st)
        assertEquals(b3, b3.xyz); assertEquals(b3, b3.rgb); assertEquals(b3, b3.stp)
        b3.r = false; b3.g = false; b3.b = false; b3.s = true; b3.t = true; b3.p = true; b3.xy = Bool2(); b3.rg = Bool2(true, true)
        b3.st = Bool2(); b3.xyz = Bool3(true, true, true); b3.rgb = Bool3(); b3.stp = Bool3(false, true, false); assertEquals(Bool3(false, true, false), b3)
        assertTrue(b3[1]); assertTrue(b3(2)); assertEquals(Bool2(true, false), b3[1, 2]); assertEquals(Bool3(false, true, false), b3[2, 1, 0])
        assertTrue(b3[VectorComponent.G]); assertEquals(Bool3(false, true, false), b3[VectorComponent.Z, VectorComponent.Y, VectorComponent.X])
        b3[0] = true; b3[VectorComponent.Z] = true; b3[0, 1] = false; b3[0, 1, 2] = true; b3[VectorComponent.X, VectorComponent.Y] = false
        b3[VectorComponent.X, VectorComponent.Y, VectorComponent.Z] = true; assertEquals(Bool3(true, true, true), Bool3(b3))
        assertFailsWith<IllegalArgumentException> { b3[3] }

        val h2 = Half2(1f.h, 2f.h)
        assertEquals(listOf(1f, 2f, 1f, 2f), listOf(h2.r, h2.g, h2.s, h2.t).map { it.toFloat() }); assertEquals(h2, h2.xy); assertEquals(h2, h2.rg); assertEquals(h2, h2.st)
        h2.r = 3f.h; h2.g = 4f.h; h2.s = 5f.h; h2.t = 6f.h; h2.xy = Half2(7f.h); h2.rg = Half2(8f.h); h2.st = Half2(1f.h, 2f.h); assertEquals(Half2(1f.h, 2f.h), h2)
        assertEquals(2f, h2[1].toFloat()); assertEquals(2f, h2(2).toFloat()); assertEquals(Half2(2f.h, 1f.h), h2[1, 0])
        assertEquals(Half2(2f.h, 1f.h), h2[VectorComponent.G, VectorComponent.R]); assertEquals(2f, h2[VectorComponent.T].toFloat())
        h2[0] = 3f.h; h2[VectorComponent.Y] = 4f.h; h2[0, 1] = 5f.h; h2[VectorComponent.X, VectorComponent.Y] = 6f.h
        assertEquals(Half2(6f.h), Half2(h2)); assertContentEquals(floatArrayOf(6f, 6f), h2.toFloatArray())

        val h3 = Half3(1f.h, 2f.h, 3f.h)
        assertEquals(listOf(1f, 2f, 3f, 1f, 2f, 3f), listOf(h3.r, h3.g, h3.b, h3.s, h3.t, h3.p).map { it.toFloat() })
        assertEquals(Half2(1f.h, 2f.h), h3.xy); assertEquals(Half2(1f.h, 2f.h), h3.rg); assertEquals(Half2(1f.h, 2f.h), h3.st)
        assertEquals(h3, h3.xyz); assertEquals(h3, h3.rgb); assertEquals(h3, h3.stp)
        h3.r = 0f.h; h3.g = 0f.h; h3.b = 0f.h; h3.s = 1f.h; h3.t = 2f.h; h3.p = 3f.h; h3.xy = Half2(0f.h); h3.rg = Half2(1f.h)
        h3.st = Half2(1f.h, 2f.h); h3.xyz = Half3(0f.h); h3.rgb = Half3(0f.h); h3.stp = Half3(1f.h, 2f.h, 3f.h); assertEquals(Half3(1f.h, 2f.h, 3f.h), h3)
        assertEquals(3f, h3[2].toFloat()); assertEquals(3f, h3(3).toFloat()); assertEquals(Half2(3f.h, 1f.h), h3[2, 0])
        assertEquals(Half3(3f.h, 2f.h, 1f.h), h3[2, 1, 0]); assertEquals(Half3(3f.h, 2f.h, 1f.h), h3[VectorComponent.P, VectorComponent.T, VectorComponent.S])
        assertEquals(Half2(3f.h, 1f.h), h3[VectorComponent.B, VectorComponent.R])
        h3[0] = 0f.h; h3[VectorComponent.Z] = 0f.h; h3[0, 1] = 4f.h; h3[0, 1, 2] = 5f.h; h3[VectorComponent.X, VectorComponent.Y] = 6f.h
        h3[VectorComponent.X, VectorComponent.Y, VectorComponent.Z] = 7f.h; assertEquals(Half3(7f.h), Half3(h3))
        assertEquals(Half3(1f.h, 2f.h, 0f.h), Half3(Half2(1f.h, 2f.h))); assertContentEquals(floatArrayOf(7f, 7f, 7f), h3.toFloatArray())
        var hi = Half3(1f.h); hi++; assertEquals(Half3(2f.h), hi); hi--; assertEquals(Half3(1f.h), hi)
        var hj = Half4(1f.h); hj++; assertEquals(Half4(2f.h), hj); hj--; assertEquals(Half4(1f.h), hj)
    }

    @Test
    fun halfComparisonsAgreeWithTheirAliases() {
        val two = 2f.h
        val a2 = Half2(1f.h, 3f.h); val b2 = Half2(two)
        val lt2 = Bool2(true, false); val gt2 = Bool2(false, true); val eq2 = Bool2(false, false)
        assertEquals(listOf(lt2, lt2, lt2, lt2), listOf(lessThan(a2, two), lessThan(a2, b2), a2 lt two, a2 lt b2))
        assertEquals(listOf(lt2, lt2, lt2, lt2), listOf(lessThanEqual(a2, two), lessThanEqual(a2, b2), a2 lte two, a2 lte b2))
        assertEquals(listOf(gt2, gt2, gt2, gt2), listOf(greaterThan(a2, two), greaterThan(a2, b2), a2 gt two, a2 gt b2))
        assertEquals(listOf(gt2, gt2, gt2, gt2), listOf(greaterThanEqual(a2, two), greaterThanEqual(a2, b2), a2 gte two, a2 gte b2))
        assertEquals(listOf(eq2, eq2, eq2, eq2), listOf(equal(a2, two), equal(a2, b2), a2 eq two, a2 eq b2))
        assertEquals(listOf(Bool2(true, true), Bool2(true, true)), listOf(notEqual(a2, two), notEqual(a2, b2)))
        assertEquals(listOf(Bool2(true, true), Bool2(true, true)), listOf(a2 neq two, a2 neq b2))

        val a3 = Half3(1f.h, 2f.h, 3f.h); val b3 = Half3(two)
        val lt3 = Bool3(true, false, false); val lte3 = Bool3(true, true, false); val gt3 = Bool3(false, false, true)
        val gte3 = Bool3(false, true, true); val eq3 = Bool3(false, true, false); val neq3 = Bool3(true, false, true)
        assertEquals(listOf(lt3, lt3, lt3, lt3), listOf(lessThan(a3, two), lessThan(a3, b3), a3 lt two, a3 lt b3))
        assertEquals(listOf(lte3, lte3, lte3, lte3), listOf(lessThanEqual(a3, two), lessThanEqual(a3, b3), a3 lte two, a3 lte b3))
        assertEquals(listOf(gt3, gt3, gt3, gt3), listOf(greaterThan(a3, two), greaterThan(a3, b3), a3 gt two, a3 gt b3))
        assertEquals(listOf(gte3, gte3, gte3, gte3), listOf(greaterThanEqual(a3, two), greaterThanEqual(a3, b3), a3 gte two, a3 gte b3))
        assertEquals(listOf(eq3, eq3, eq3, eq3), listOf(equal(a3, two), equal(a3, b3), a3 eq two, a3 eq b3))
        assertEquals(listOf(neq3, neq3, neq3, neq3), listOf(notEqual(a3, two), notEqual(a3, b3), a3 neq two, a3 neq b3))

        val a4 = Half4(1f.h, 2f.h, 3f.h, 2f.h); val b4 = Half4(two)
        val lt4 = Bool4(true, false, false, false); val lte4 = Bool4(true, true, false, true); val gt4 = Bool4(false, false, true, false)
        val gte4 = Bool4(false, true, true, true); val eq4 = Bool4(false, true, false, true); val neq4 = Bool4(true, false, true, false)
        assertEquals(listOf(lt4, lt4, lt4, lt4), listOf(lessThan(a4, two), lessThan(a4, b4), a4 lt two, a4 lt b4))
        assertEquals(listOf(lte4, lte4, lte4, lte4), listOf(lessThanEqual(a4, two), lessThanEqual(a4, b4), a4 lte two, a4 lte b4))
        assertEquals(listOf(gt4, gt4, gt4, gt4), listOf(greaterThan(a4, two), greaterThan(a4, b4), a4 gt two, a4 gt b4))
        assertEquals(listOf(gte4, gte4, gte4, gte4), listOf(greaterThanEqual(a4, two), greaterThanEqual(a4, b4), a4 gte two, a4 gte b4))
        assertEquals(listOf(eq4, eq4, eq4, eq4), listOf(equal(a4, two), equal(a4, b4), a4 eq two, a4 eq b4))
        assertEquals(listOf(neq4, neq4, neq4, neq4), listOf(notEqual(a4, two), notEqual(a4, b4), a4 neq two, a4 neq b4))
    }
}
