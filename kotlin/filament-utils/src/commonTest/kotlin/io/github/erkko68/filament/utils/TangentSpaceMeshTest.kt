package io.github.erkko68.filament.utils

import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

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
}
