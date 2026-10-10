package io.github.erkko68.filament.utils

import kotlin.test.Test
import kotlin.test.assertEquals

/** Half and Int vector arithmetic, multi-component indexing and the conversions [VectorTest] leaves out. */
class VectorArithmeticTest {
    private fun half3(x: Float, y: Float, z: Float) = Half3(Half(x), Half(y), Half(z))
    private fun half4(x: Float, y: Float, z: Float, w: Float) = Half4(Half(x), Half(y), Half(z), Half(w))

    // A narrower operand applies to the leading components and leaves the rest alone.
    @Test
    fun half3Arithmetic() {
        val a = half3(2f, 4f, 8f)
        val two = Half(2f)
        val xy = Half2(Half(1f), Half(2f))
        val v = half3(1f, 2f, 4f)
        assertEquals(half3(4f, 6f, 10f), a + two); assertEquals(half3(0f, 2f, 6f), a - two)
        assertEquals(half3(4f, 8f, 16f), a * two); assertEquals(half3(1f, 2f, 4f), a / two)
        assertEquals(half3(1f, 2f, 8f), a - xy); assertEquals(half3(2f, 8f, 8f), a * xy); assertEquals(half3(2f, 2f, 8f), a / xy)
        assertEquals(half3(2f, 8f, 32f), a * v); assertEquals(half3(2f, 2f, 2f), a / v)
    }

    @Test
    fun half4Arithmetic() {
        val a = half4(2f, 4f, 8f, 16f)
        val two = Half(2f)
        val xy = Half2(Half(1f), Half(2f))
        val xyz = half3(1f, 2f, 4f)
        val v = half4(1f, 2f, 4f, 8f)
        assertEquals(half4(4f, 6f, 10f, 18f), a + two); assertEquals(half4(0f, 2f, 6f, 14f), a - two)
        assertEquals(half4(4f, 8f, 16f, 32f), a * two); assertEquals(half4(1f, 2f, 4f, 8f), a / two)
        assertEquals(half4(1f, 2f, 8f, 16f), a - xy); assertEquals(half4(2f, 8f, 8f, 16f), a * xy); assertEquals(half4(2f, 2f, 8f, 16f), a / xy)
        assertEquals(half4(1f, 2f, 4f, 16f), a - xyz); assertEquals(half4(2f, 8f, 32f, 16f), a * xyz); assertEquals(half4(2f, 2f, 2f, 16f), a / xyz)
        assertEquals(half4(3f, 6f, 12f, 24f), a + v); assertEquals(half4(2f, 8f, 32f, 128f), a * v); assertEquals(half4(2f, 2f, 2f, 2f), a / v)

        assertEquals(a, Half4(a))
        assertEquals(half4(1f, 2f, 0f, 0f), Half4(xy)); assertEquals(half4(1f, 2f, 3f, 4f), Half4(xy, Half(3f), Half(4f)))
        assertEquals(half4(1f, 2f, 4f, 0f), Half4(xyz)); assertEquals(half4(1f, 2f, 4f, 5f), Half4(xyz, Half(5f)))
    }

    @Test
    fun intArithmetic() {
        val a3 = Int3(2, 4, 6)
        assertEquals(Int3(1, 3, 5), a3 - 1); assertEquals(Int3(4, 8, 12), a3 * 2); assertEquals(Int3(1, 2, 3), a3 / 2)
        assertEquals(Int3(3, 6, 9), a3 + Int3(1, 2, 3))
        val a4 = Int4(2, 4, 6, 8)
        assertEquals(Int4(3, 5, 7, 9), a4 + 1)
        assertEquals(a4, Int4(a4))
    }

    // Several indices read a swizzle in the order given, and write one value to each of them.
    @Test
    fun multiComponentIndexing() {
        val x = VectorComponent.X; val g = VectorComponent.G; val p = VectorComponent.P; val q = VectorComponent.Q

        val h = half4(1f, 2f, 3f, 4f)
        assertEquals(Half2(Half(4f), Half(1f)), h[3, 0]); assertEquals(half3(3f, 2f, 1f), h[2, 1, 0])
        assertEquals(Half2(Half(4f), Half(1f)), h[q, x]); assertEquals(half3(3f, 2f, 1f), h[p, g, x]); assertEquals(half4(4f, 3f, 2f, 1f), h[q, p, g, x])
        h[0, 1] = Half(5f); assertEquals(half4(5f, 5f, 3f, 4f), h)
        h[1, 2, 3] = Half(6f); assertEquals(half4(5f, 6f, 6f, 6f), h)
        h[x, q] = Half(7f); assertEquals(half4(7f, 6f, 6f, 7f), h)
        h[x, g, p] = Half(8f); assertEquals(half4(8f, 8f, 8f, 7f), h)
        h[x, g, p, q] = Half(9f); assertEquals(half4(9f, 9f, 9f, 9f), h)

        val i = Int4(1, 2, 3, 4)
        assertEquals(Int2(4, 1), i[3, 0]); assertEquals(Int3(3, 2, 1), i[2, 1, 0])
        assertEquals(Int2(4, 1), i[q, x]); assertEquals(Int4(4, 3, 2, 1), i[q, p, g, x])
        i[0, 1] = 5; assertEquals(Int4(5, 5, 3, 4), i)
        i[1, 2, 3] = 6; assertEquals(Int4(5, 6, 6, 6), i)
        i[x, q] = 7; assertEquals(Int4(7, 6, 6, 7), i)
        i[x, g, p] = 8; assertEquals(Int4(8, 8, 8, 7), i)
        i[x, g, p, q] = 9; assertEquals(Int4(9, 9, 9, 9), i)

        val b = Bool4(true, false, false, true)
        assertEquals(b, Bool4(b)); assertEquals(true, b(1)); assertEquals(false, b(2))
        assertEquals(Bool2(true, false), b[3, 1]); assertEquals(Bool3(false, false, true), b[2, 1, 0]); assertEquals(Bool4(true, false, false, true), b[3, 2, 1, 0])
        assertEquals(Bool3(false, false, true), b[p, g, x]); assertEquals(Bool4(true, false, false, true), b[q, p, g, x])
        b[1, 2] = true; assertEquals(Bool4(true, true, true, true), b)
        b[0, 1, 2] = false; assertEquals(Bool4(false, false, false, true), b)
        b[0, 1, 2, 3] = true; assertEquals(Bool4(true, true, true, true), b)
        b[x, q] = false; assertEquals(Bool4(false, true, true, false), b)
        b[x, g, p] = false; assertEquals(Bool4(false, false, false, false), b)
        b[x, g, p, q] = true; assertEquals(Bool4(true, true, true, true), b)
    }

    @Test
    fun conversions() {
        assertEquals(Half(1f), Half.fromBits(0x3c00))
        assertEquals(1f / 1024f, Half.EPSILON.toFloat())
        assertEquals(Half(1.5f), 1.5.toHalf())
        assertEquals(0, Half(1.5f).compareTo(1.5.toHalf()))

        // Omitted angles are zero and the order defaults to ZYX.
        assertEquals(Quaternion(0f, 0f, 0f, 1f), Quaternion.fromEuler())
        assertEquals(Quaternion.fromEuler(0f, 0f, 0.5f, RotationsOrder.ZYX), Quaternion.fromEuler(roll = 0.5f))
        assertEquals(Quaternion.fromEuler(Float3(10f, 20f, 30f), RotationsOrder.ZYX), Quaternion.fromEuler(Float3(10f, 20f, 30f)))
    }
}
