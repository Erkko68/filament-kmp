package io.github.erkko68.filament.utils

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Element-wise equality and comparison of matrices, column aliases, and the degenerate cases of the conversions
 * and vector helpers. The inline ones run over a table so that every element decides the result once.
 */
class MatrixEqualityTest {
    private fun fill2(v: Float) = Mat2(Float2(v), Float2(v))
    private fun fill3(v: Float) = Mat3(Float3(v), Float3(v), Float3(v))
    private fun fill4(v: Float) = Mat4(Float4(v), Float4(v), Float4(v), Float4(v))

    @Test
    fun mat2() {
        val m = Mat2(Float2(1f, 2f), Float2(3f, 4f))
        val nudged = { d: Float -> Mat2(m).also { for (i in 0 until 4) it[i / 2, i % 2] += d } }
        val others = listOf(nudged(0.05f) to true) + (0 until 4).map { i -> Mat2(m).also { it[i / 2, i % 2] += 1f } to false }
        for ((other, expected) in others) assertEquals(expected, m.equals(other, 0.1f), "$other")
        val uniform = listOf(fill2(2.05f) to true) + (0 until 4).map { i -> fill2(2f).also { it[i / 2, i % 2] = 3f } to false }
        for ((u, expected) in uniform) assertEquals(expected, u.equals(2f, 0.1f), "$u")

        for ((d, sign) in listOf(0.05f to 0f, 1f to -1f, -1f to 1f)) {
            assertEquals(fill2(sign), m.compareTo(nudged(d), 0.1f))
            assertEquals(fill2(sign), fill2(2f).compareTo(2f + d, 0.1f))
        }
        for (c in MatrixColumn.entries) {
            if (c.ordinal < 2) assertEquals(m[c.ordinal], m[c]) else assertFailsWith<IllegalArgumentException> { m[c] }
        }
        assertFailsWith<IllegalArgumentException> { Mat2.of(1f, 2f, 3f) }
    }

    @Test
    fun mat3() {
        val m = Mat3(Float3(1f, 2f, 3f), Float3(4f, 5f, 6f), Float3(7f, 8f, 9f))
        val nudged = { d: Float -> Mat3(m).also { for (i in 0 until 9) it[i / 3, i % 3] += d } }
        val others = listOf(nudged(0.05f) to true) + (0 until 9).map { i -> Mat3(m).also { it[i / 3, i % 3] += 1f } to false }
        for ((other, expected) in others) assertEquals(expected, m.equals(other, 0.1f), "$other")
        val uniform = listOf(fill3(2.05f) to true) + (0 until 9).map { i -> fill3(2f).also { it[i / 3, i % 3] = 3f } to false }
        for ((u, expected) in uniform) assertEquals(expected, u.equals(2f, 0.1f), "$u")

        for ((d, sign) in listOf(0.05f to 0f, 1f to -1f, -1f to 1f)) {
            assertEquals(fill3(sign), m.compareTo(nudged(d), 0.1f))
            assertEquals(fill3(sign), fill3(2f).compareTo(2f + d, 0.1f))
        }
        for (c in MatrixColumn.entries) {
            if (c.ordinal < 3) assertEquals(m[c.ordinal], m[c]) else assertFailsWith<IllegalArgumentException> { m[c] }
        }
    }

    @Test
    fun mat4() {
        val m = Mat4(Float4(1f, 2f, 3f, 4f), Float4(5f, 6f, 7f, 8f), Float4(9f, 10f, 11f, 12f), Float4(13f, 14f, 15f, 16f))
        val nudged = { d: Float -> Mat4(m).also { for (i in 0 until 16) it[i / 4, i % 4] += d } }
        val others = listOf(nudged(0.05f) to true) + (0 until 16).map { i -> Mat4(m).also { it[i / 4, i % 4] += 1f } to false }
        for ((other, expected) in others) assertEquals(expected, m.equals(other, 0.1f), "$other")
        val uniform = listOf(fill4(2.05f) to true) + (0 until 16).map { i -> fill4(2f).also { it[i / 4, i % 4] = 3f } to false }
        for ((u, expected) in uniform) assertEquals(expected, u.equals(2f, 0.1f), "$u")

        for ((d, sign) in listOf(0.05f to 0f, 1f to -1f, -1f to 1f)) {
            assertEquals(fill4(sign), m.compareTo(nudged(d), 0.1f))
            assertEquals(fill4(sign), fill4(2f).compareTo(2f + d, 0.1f))
        }
        for (c in MatrixColumn.entries) assertEquals(m[c.ordinal], m[c])
    }

    // Looking straight up or down the pitch is ±90° and yaw is folded into roll.
    @Test
    fun rotationAtThePoles() {
        val up = Mat4(right = Float3(1f, 0f, 0f), up = Float3(0f, 0f, -1f), forward = Float3(0f, 1f, 0f)).rotation
        val down = Mat4(right = Float3(1f, 0f, 0f), up = Float3(0f, 0f, 1f), forward = Float3(0f, -1f, 0f)).rotation
        for ((expected, actual) in listOf(Float3(90f, 0f, 0f) to up, Float3(-90f, 0f, 0f) to down)) {
            for (i in 0 until 3) assertEquals(expected[i], actual[i], 1e-4f, "$actual")
        }
    }

    // Half turns have a negative trace, so the largest diagonal element picks the formula: one case per axis.
    @Test
    fun quaternionOfHalfTurns() {
        val turns = listOf(
            Float3(1f, -1f, -1f) to Quaternion(1f, 0f, 0f, 0f),
            Float3(-1f, 1f, -1f) to Quaternion(0f, 1f, 0f, 0f),
            Float3(-1f, -1f, 1f) to Quaternion(0f, 0f, 1f, 0f),
            Float3(1f, 1f, 1f) to Quaternion(0f, 0f, 0f, 1f),
        )
        for ((diagonal, expected) in turns) {
            val m = Mat4(Float4(diagonal.x, 0f, 0f, 0f), Float4(0f, diagonal.y, 0f, 0f), Float4(0f, 0f, diagonal.z, 0f), Float4(0f, 0f, 0f, 1f))
            assertEquals(expected, quaternion(m), "diagonal $diagonal")
        }
    }

    @Test
    fun angleAndRefractionEdges() {
        // No angle is defined against a zero vector; it reads as 0.
        for ((a, b, expected) in listOf(Triple(Float2(1f, 0f), Float2(0f, 1f), PI.toFloat() / 2), Triple(Float2(), Float2(0f, 1f), 0f))) {
            assertEquals(expected, angle(a, b), 1e-6f)
        }
        for ((a, b, expected) in listOf(Triple(Float3(1f, 0f, 0f), Float3(0f, 1f, 0f), PI.toFloat() / 2), Triple(Float3(), Float3(0f, 1f, 0f), 0f))) {
            assertEquals(expected, angle(a, b), 1e-6f)
        }
        // Grazing incidence into a much less dense medium reflects totally: no refracted ray.
        val h = { f: Float -> Half(f) }
        assertEquals(Half3(), refract(Half3(h(1f), h(0f), h(0f)), Half3(h(0f), h(1f), h(0f)), h(2f)))
        assertEquals(Half3(h(0f), h(-1f), h(0f)), refract(Half3(h(0f), h(-1f), h(0f)), Half3(h(0f), h(1f), h(0f)), h(1f)))
    }

    @Test
    fun halfEdges() {
        assertFailsWith<IllegalArgumentException> { Half.NaN.roundToInt() }
        assertFailsWith<IllegalArgumentException> { Half.NaN.roundToLong() }
        assertEquals(3, Half(2.5f).roundToInt()); assertEquals(3L, Half(2.5f).roundToLong())
        // Of two zeros, min is the negative one and max the positive one, whichever comes first.
        assertEquals(Half.NEGATIVE_ZERO, min(Half.NEGATIVE_ZERO, Half.POSITIVE_ZERO))
        assertEquals(Half.NEGATIVE_ZERO, min(Half.POSITIVE_ZERO, Half.NEGATIVE_ZERO))
        assertEquals(Half.POSITIVE_ZERO, max(Half.NEGATIVE_ZERO, Half.POSITIVE_ZERO))
        assertEquals(Half.POSITIVE_ZERO, max(Half.POSITIVE_ZERO, Half.NEGATIVE_ZERO))
    }
}
