/*
 * Copyright (C) 2017 Romain Guy
 * Vendored from romainguy/kotlin-math (Apache-2.0) alongside the math sources;
 * only the package was changed. Keep in sync when updating the vendored library.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.erkko68.filament.utils

import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class QuaternionTest {

    @Test
    fun fromAxisAngle() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.0093f, 0.0186f, 0.0280f, 0.9994f).toFloatArray(),
            Quaternion.fromAxisAngle(Float3(1.0f, 2.0f, 3.0f), 4.0f).toFloatArray()
        )
    }

    @Test
    fun fromEulerXYZ() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.009179f, 0.0172174f, 0.0263242f, 0.999463f).toFloatArray(),
            Quaternion.fromEuler(Float3(1.0f, 2.0f, 3.0f), RotationsOrder.XYZ).toFloatArray()
        )
    }

    @Test
    fun fromEulerXZY() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.0082654f, 0.0172174f, 0.0263242f, 0.999471f).toFloatArray(),
            Quaternion.fromEuler(Float3(1.0f, 2.0f, 3.0f), RotationsOrder.XZY).toFloatArray()
        )
    }

    @Test
    fun fromEulerYXZ() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.009179f, 0.0172174f, 0.0260197f, 0.999471f).toFloatArray(),
            Quaternion.fromEuler(Float3(1.0f, 2.0f, 3.0f), RotationsOrder.YXZ).toFloatArray()
        )
    }

    @Test
    fun fromEulerYZX() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.009179f, 0.0176742f, 0.0260197f, 0.999463f).toFloatArray(),
            Quaternion.fromEuler(Float3(1.0f, 2.0f, 3.0f), RotationsOrder.YZX).toFloatArray()
        )
    }

    @Test
    fun fromEulerZXY() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.0082654f, 0.0176742f, 0.0263242f, 0.999463f).toFloatArray(),
            Quaternion.fromEuler(Float3(1.0f, 2.0f, 3.0f), RotationsOrder.ZXY).toFloatArray()
        )
    }

    @Test
    fun fromEulerZYX() {
        MatrixTest.assertArrayEquals(
            Quaternion(0.0082654f, 0.0176742f, 0.0260197f, 0.999471f).toFloatArray(),
            Quaternion.fromEuler(Float3(1.0f, 2.0f, 3.0f), RotationsOrder.ZYX).toFloatArray()
        )
    }

    @Test
    fun toEulerXYZ() {
        MatrixTest.assertArrayEquals(
            Float3(-63.4349488f, 41.8103164f, 169.6951532f).toFloatArray(),
            eulerAngles(Quaternion(1.0f, 2.0f, 3.0f, 1.0f), RotationsOrder.XYZ).toFloatArray()
        )
    }

    @Test
    fun toEulerXZY() {
        MatrixTest.assertArrayEquals(
            Float3(109.6538245f, 137.7263108f, 7.6622561f).toFloatArray(),
            eulerAngles(Quaternion(1.0f, 2.0f, 3.0f, 1.0f), RotationsOrder.XZY).toFloatArray()
        )
    }

    @Test
    fun toEulerYXZ() {
        MatrixTest.assertArrayEquals(
            Float3(-41.8103164f, 63.4349488f, 116.5650512f).toFloatArray(),
            eulerAngles(Quaternion(1.0f, 2.0f, 3.0f, 1.0f), RotationsOrder.YXZ).toFloatArray()
        )
    }

    @Test
    fun toEulerYZX() {
        MatrixTest.assertArrayEquals(
            Float3(-116.5650512f, -169.6951532f, 41.8103164f).toFloatArray(),
            eulerAngles(Quaternion(1.0f, 2.0f, 3.0f, 1.0f), RotationsOrder.YZX).toFloatArray()
        )
    }

    @Test
    fun toEulerZXY() {
        MatrixTest.assertArrayEquals(
            Float3(68.9605309f, -21.8014099f, 158.1985901f).toFloatArray(),
            eulerAngles(Quaternion(1.0f, 2.0f, 3.0f, 1.0f), RotationsOrder.ZXY).toFloatArray()
        )
    }

    @Test
    fun toEulerZYX() {
        MatrixTest.assertArrayEquals(
            Float3(70.3461755f, -7.6622561f, 137.7263108f).toFloatArray(),
            eulerAngles(Quaternion(1.0f, 2.0f, 3.0f, 1.0f), RotationsOrder.ZYX).toFloatArray()
        )
    }

    private fun assertVec(expected: Float3, actual: Float3) = assertTrue(
        length(expected - actual) < 1e-4f, "expected $expected, got $actual",
    )
    private fun assertQuat(expected: Quaternion, actual: Quaternion) = assertTrue(
        length(expected - actual) < 1e-4f, "expected $expected, got $actual",
    )
    private val quarterTurnZ = Quaternion.fromAxisAngle(Float3(0f, 0f, 1f), 90f)

    @Test
    fun accessorsAndIndexing() {
        val q = Quaternion(1f, 2f, 3f, 4f)
        assertEquals(Float3(1f, 2f, 3f), q.xyz); assertEquals(Float3(1f, 2f, 3f), q.imaginary)
        assertEquals(4f, q.real); assertEquals(Float4(1f, 2f, 3f, 4f), q.xyzw)
        assertEquals(q, Quaternion(Float3(1f, 2f, 3f), 4f)); assertEquals(q, Quaternion(Float4(1f, 2f, 3f, 4f))); assertEquals(q, Quaternion(q))
        assertEquals(4f, q[3]); assertEquals(4f, q(4)); assertEquals(4f, q[QuaternionComponent.W])
        assertEquals(Float3(3f, 2f, 1f), q[2, 1, 0]); assertEquals(Quaternion(4f, 3f, 2f, 1f), q[3, 2, 1, 0])
        assertEquals(Quaternion(4f, 3f, 2f, 1f), q[QuaternionComponent.W, QuaternionComponent.Z, QuaternionComponent.Y, QuaternionComponent.X])
        assertEquals(Float3(4f, 3f, 2f), q[QuaternionComponent.W, QuaternionComponent.Z, QuaternionComponent.Y])
        q.xyz = Float3(5f); q.real = 6f; assertEquals(Quaternion(5f, 5f, 5f, 6f), q)
        q.imaginary = Float3(1f); q.xyzw = Float4(1f, 2f, 3f, 4f); assertEquals(Quaternion(1f, 2f, 3f, 4f), q)
        q[0] = 0f; q[QuaternionComponent.Y] = 0f; q[2, 3] = 7f; assertEquals(Quaternion(0f, 0f, 7f, 7f), q)
        q[0, 1, 2] = 1f; q[0, 1, 2, 3] = 2f; assertEquals(Quaternion(2f, 2f, 2f, 2f), q)
        q[QuaternionComponent.X, QuaternionComponent.Y] = 3f; q[QuaternionComponent.Z, QuaternionComponent.W, QuaternionComponent.X] = 4f
        assertEquals(Quaternion(4f, 3f, 4f, 4f), q)
        q[QuaternionComponent.X, QuaternionComponent.Y, QuaternionComponent.Z, QuaternionComponent.W] = 5f; assertEquals(Quaternion(5f, 5f, 5f, 5f), q)
        assertFailsWith<IllegalArgumentException> { q[4] }
        assertContentEquals(floatArrayOf(5f, 5f, 5f, 5f), q.toFloatArray())
        assertEquals(Quaternion(10f, 10f, 10f, 10f), q.transform { it * 2f })
    }

    @Test
    fun arithmeticAndComparisons() {
        val q = Quaternion(1f, 2f, 3f, 4f)
        assertEquals(Quaternion(-1f, -2f, -3f, -4f), -q)
        assertEquals(Quaternion(2f, 3f, 4f, 5f), q + 1f); assertEquals(Quaternion(0f, 1f, 2f, 3f), q - 1f)
        assertEquals(Quaternion(2f, 4f, 6f, 8f), q * 2f); assertEquals(Quaternion(0.5f, 1f, 1.5f, 2f), q / 2f)
        assertEquals(Quaternion(2f, 3f, 4f, 5f), 1f + q); assertEquals(Quaternion(0f, -1f, -2f, -3f), 1f - q)
        assertEquals(Quaternion(2f, 4f, 6f, 8f), 2f * q); assertEquals(Quaternion(12f, 6f, 4f, 3f), 12f / q)
        assertEquals(Quaternion(2f, 4f, 6f, 8f), q + q); assertEquals(Quaternion(0f, 0f, 0f, 0f), q - q)
        assertEquals(q, q * Quaternion()); assertEquals(q, Quaternion() * q)

        assertEquals(Float4(-1f, 0f, 1f, 1f), q.compareTo(2f)); assertEquals(Float4(-1f, 0f, 0f, 0f), q.compareTo(3f, delta = 1f))
        assertEquals(Float4(0f, 0f, 0f, 0f), q.compareTo(Float4(1f, 2f, 3f, 4f)))
        assertEquals(Bool4(false, true, false, false), q.equals(2f)); assertEquals(Bool4(true, true, true, true), q.equals(Float4(1f, 2f, 3f, 4f)))
        val two = Quaternion(2f, 2f, 2f, 2f)
        assertEquals(Bool4(true, false, false, false), lessThan(q, 2f)); assertEquals(Bool4(true, false, false, false), lessThan(q, two))
        assertEquals(Bool4(true, true, false, false), lessThanEqual(q, 2f)); assertEquals(Bool4(true, true, false, false), lessThanEqual(q, two))
        assertEquals(Bool4(false, false, true, true), greaterThan(q, 2f)); assertEquals(Bool4(false, false, true, true), greaterThan(q, two))
        assertEquals(Bool4(false, true, true, true), greaterThanEqual(q, 2f)); assertEquals(Bool4(false, true, true, true), greaterThanEqual(q, two))
        assertEquals(Bool4(false, true, false, false), equal(q, 2f)); assertEquals(Bool4(false, true, false, false), equal(q, two))
        assertEquals(Bool4(true, false, true, true), notEqual(q, 2f)); assertEquals(Bool4(true, false, true, true), notEqual(q, two))
        val two4 = Float4(2f)
        assertEquals(Bool4(true, false, false, false), q lt 2f); assertEquals(Bool4(true, false, false, false), q lt two4)
        assertEquals(Bool4(true, true, false, false), q lte 2f); assertEquals(Bool4(true, true, false, false), q lte two4)
        assertEquals(Bool4(false, false, true, true), q gt 2f); assertEquals(Bool4(false, false, true, true), q gt two4)
        assertEquals(Bool4(false, true, true, true), q gte 2f); assertEquals(Bool4(false, true, true, true), q gte two4)
        assertEquals(Bool4(false, true, false, false), q eq 2f); assertEquals(Bool4(false, true, false, false), q eq two4)
        assertEquals(Bool4(true, false, true, true), q neq 2f); assertEquals(Bool4(true, false, true, true), q neq two4)
    }

    @Test
    fun rotationAlgebra() {
        // A quarter turn about +Z takes +X to +Y, matching its matrix.
        assertVec(Float3(0f, 1f, 0f), quarterTurnZ * Float3(1f, 0f, 0f))
        assertVec(Float3(0f, 1f, 0f), (quarterTurnZ.toMatrix() * Float4(1f, 0f, 0f, 0f)).xyz)
        assertQuat(Quaternion(), quarterTurnZ * inverse(quarterTurnZ))
        assertEquals(conjugate(quarterTurnZ), Quaternion(-quarterTurnZ.x, -quarterTurnZ.y, -quarterTurnZ.z, quarterTurnZ.w))
        assertQuat(Quaternion(0f, 0f, 1f, 0f), cross(quarterTurnZ, quarterTurnZ)) // imaginary part of the half turn
        assertEquals(Quaternion(1f, 2f, 3f, 4f), abs(Quaternion(-1f, 2f, -3f, 4f)))
        assertEquals(30f, length2(Quaternion(1f, 2f, 3f, 4f))); assertEquals(sqrt(30f), length(Quaternion(1f, 2f, 3f, 4f)))
        assertEquals(30f, dot(Quaternion(1f, 2f, 3f, 4f), Quaternion(1f, 2f, 3f, 4f)))
        assertEquals(1f, length(normalize(Quaternion(1f, 2f, 3f, 4f))), 1e-6f)
        assertEquals(90f, degrees(angle(Quaternion(), quarterTurnZ)), 1e-3f)
        assertVec(Float3(0f, 0f, 90f), quarterTurnZ.toEulerAngles())

        // Interpolating half way gives the 45° turn, whichever sign the endpoint carries.
        val eighthTurnZ = Quaternion.fromAxisAngle(Float3(0f, 0f, 1f), 45f)
        assertQuat(eighthTurnZ, slerp(Quaternion(), quarterTurnZ, 0.5f))
        assertQuat(eighthTurnZ, slerp(Quaternion(), -quarterTurnZ, 0.5f))
        assertQuat(eighthTurnZ, nlerp(Quaternion(), quarterTurnZ, 0.5f))
        assertQuat((Quaternion() + quarterTurnZ) * 0.5f, lerp(Quaternion(), quarterTurnZ, 0.5f))
        // Near-identical endpoints fall back to nlerp.
        val tiny = Quaternion.fromAxisAngle(Float3(0f, 0f, 1f), 0.1f)
        assertQuat(nlerp(Quaternion(), tiny, 0.5f), slerp(Quaternion(), tiny, 0.5f))
    }

    @Test
    fun fromRotationCoversParallelAndOppositeVectors() {
        val x = Float3(1f, 0f, 0f)
        assertVec(Float3(0f, 1f, 0f), Quaternion.fromRotation(x, Float3(0f, 1f, 0f)) * x)
        assertEquals(Quaternion(), Quaternion.fromRotation(x, x))
        assertVec(-x, Quaternion.fromRotation(x, -x) * x)
        // Opposite along X: the X-axis cross product vanishes, so it pivots around Y instead.
        val y = Float3(0f, 1f, 0f)
        assertVec(-y, Quaternion.fromRotation(y, -y) * y)
    }
}
