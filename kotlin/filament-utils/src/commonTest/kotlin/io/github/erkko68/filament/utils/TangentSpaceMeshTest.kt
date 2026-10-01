package io.github.erkko68.filament.utils

import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TangentSpaceMeshTest : UtilsTestFixture() {
    @Test
    fun testNormalsOnly() {
        val normals = floatArrayOf(0f, 0f, 1f, 0f, 1f, 0f, 1f, 0f, 0f)
        TangentSpaceMesh.Builder()
            .vertexCount(3)
            .normals(normals)
            .algorithm(TangentSpaceMesh.Algorithm.FRISVAD)
            .build()
            .use { mesh ->
                assertEquals(3, mesh.vertexCount)
                assertFalse(mesh.remeshed)
                val quats = FloatArray(3 * 4)
                mesh.getQuats(quats)
                for (v in 0 until 3) {
                    val q = quats.copyOfRange(v * 4, v * 4 + 4)
                    assertEquals(1f, sqrt(q.sumOf { (it * it).toDouble() }).toFloat(), 1e-4f)
                }
                val shorts = ShortArray(3 * 4)
                mesh.getQuats(shorts)
                assertEquals((quats[0] * 32767).toInt().toShort().toFloat(), shorts[0].toFloat(), 1f)
            }
    }

    @Test
    fun testAuxFollowsRemesh() {
        val positions = floatArrayOf(0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f, 1f, 1f, 0f)
        val normals = FloatArray(12) { if (it % 3 == 2) 1f else 0f }
        val uvs = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f)
        val uv1 = FloatArray(8) { it * 0.5f }
        val joints = ShortArray(16) { (it * 3).toShort() }
        TangentSpaceMesh.Builder()
            .vertexCount(4)
            .positions(positions)
            .normals(normals)
            .uvs(uvs)
            .aux(TangentSpaceMesh.AuxAttribute.UV1, uv1, 2)
            .aux(TangentSpaceMesh.AuxAttribute.JOINTS, joints, 4)
            .triangleCount(2)
            .triangles(intArrayOf(0, 1, 2, 2, 1, 3))
            .algorithm(TangentSpaceMesh.Algorithm.MIKKTSPACE)
            .build()
            .use { mesh ->
                val n = mesh.vertexCount
                val outPositions = FloatArray(n * 3).also { mesh.getPositions(it) }
                val outUv1 = FloatArray(n * 2).also { mesh.getAux(TangentSpaceMesh.AuxAttribute.UV1, it, 2) }
                val outJoints = ShortArray(n * 4).also { mesh.getAux(TangentSpaceMesh.AuxAttribute.JOINTS, it, 4) }
                // Each output vertex carries the aux of the input vertex at its position.
                for (v in 0 until n) {
                    val src = (0 until 4).single { i -> (0 until 3).all { positions[i * 3 + it] == outPositions[v * 3 + it] } }
                    assertContentEquals(uv1.copyOfRange(src * 2, src * 2 + 2), outUv1.copyOfRange(v * 2, v * 2 + 2))
                    assertContentEquals(joints.copyOfRange(src * 4, src * 4 + 4), outJoints.copyOfRange(v * 4, v * 4 + 4))
                }
            }
    }

    @Test
    fun testShortTrianglesAndOutputs() {
        val positions = floatArrayOf(0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f)
        val normals = FloatArray(9) { if (it % 3 == 2) 1f else 0f }
        val tangents = floatArrayOf(1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f)
        val uvs = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f)
        TangentSpaceMesh.Builder()
            .vertexCount(3)
            .positions(positions)
            .normals(normals)
            .tangents(tangents)
            .uvs(uvs)
            .triangleCount(1)
            .triangles(shortArrayOf(0, 1, 2))
            .build()
            .use { mesh ->
                assertEquals(1, mesh.triangleCount)
                val ints = IntArray(3).also { mesh.getTriangles(it) }
                val shorts = ShortArray(3).also { mesh.getTriangles(it) }
                assertEquals(ints.toList(), shorts.map { it.toInt() })
                assertEquals(setOf(0, 1, 2), ints.toSet())
                val outUvs = FloatArray(mesh.vertexCount * 2).also { mesh.getUVs(it) }
                assertEquals(uvs.toSet(), outUvs.toSet())
                val halfQuats = ShortArray(mesh.vertexCount * 4).also { mesh.getHalfQuats(it) }
                assertTrue(halfQuats.any { it != 0.toShort() })
            }
    }
}
