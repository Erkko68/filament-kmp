package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SurfaceOrientationTest : FilamentTestFixture() {

    @Test
    fun testBuilderAndMethods() {
        val builder = SurfaceOrientation.Builder()
            .vertexCount(4)
            .normals(floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f), 0)
            .tangents(FloatArray(16) { if (it % 4 == 0 || it % 4 == 3) 1f else 0f }, 0) // float4 per vertex: +X, w = 1
            .uvs(floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f), 0)
            .positions(floatArrayOf(-1f, -1f, 0f, 1f, -1f, 0f, -1f, 1f, 0f, 1f, 1f, 0f), 0)
            .triangleCount(2)
            .triangles(shortArrayOf(0, 1, 2, 2, 1, 3))
        
        val orientation = assertNotNull(builder.build())
        assertEquals(4, orientation.vertexCount)
        // A flat quad facing +Z with +X tangents is the identity frame, in every output format.
        val quats = FloatArray(16).also { orientation.getQuats(it, 4) }
        val shortQuats = ShortArray(16).also { orientation.getQuats(it, 4) }
        val halfQuats = ShortArray(16).also { orientation.getHalfQuats(it, 4) }
        for (v in 0 until 4) {
            assertEquals(1f, kotlin.math.abs(quats[v * 4 + 3]), 1e-4f, "float w, vertex $v")
            assertEquals(Short.MAX_VALUE.toInt(), kotlin.math.abs(shortQuats[v * 4 + 3].toInt()), "snorm16 w, vertex $v")
            assertEquals(0x3C00, halfQuats[v * 4 + 3].toInt() and 0x7FFF, "half w, vertex $v")
        }
        orientation.destroy()
    }

    // Builder inputs are read at build(), so they must stay valid until then (web heap copies didn't).
    @Test
    fun flatQuadYieldsIdentityQuats() {
        val orientation = SurfaceOrientation.Builder()
            .vertexCount(4)
            .positions(floatArrayOf(-1f, -1f, 0f, 1f, -1f, 0f, -1f, 1f, 0f, 1f, 1f, 0f), 0)
            .normals(floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f), 0)
            .uvs(floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f), 0)
            .triangleCount(2)
            .triangles(intArrayOf(0, 1, 2, 2, 1, 3))
            .build()!!
        val quats = FloatArray(16)
        orientation.getQuats(quats, 4)
        orientation.destroy()
        for (v in 0 until 4) assertEquals(1f, kotlin.math.abs(quats[v * 4 + 3]), 1e-4f, "vertex $v: ${quats.toList()}")
    }

    @Test
    fun testBuilderWithTriangles32() {
        val builder = SurfaceOrientation.Builder()
            .vertexCount(4)
            .normals(floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f), 0)
            .tangents(FloatArray(16) { if (it % 4 == 0 || it % 4 == 3) 1f else 0f }, 0) // float4 per vertex: +X, w = 1
            .uvs(floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f), 0)
            .positions(floatArrayOf(-1f, -1f, 0f, 1f, -1f, 0f, -1f, 1f, 0f, 1f, 1f, 0f), 0)
            .triangleCount(2)
            .triangles(intArrayOf(0, 1, 2, 2, 1, 3))
        
        assertNotNull(builder.build()).use { assertEquals(4, it.vertexCount) }
    }
}
