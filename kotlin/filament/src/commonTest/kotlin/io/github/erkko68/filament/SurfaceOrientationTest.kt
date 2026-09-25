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
            .tangents(floatArrayOf(1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f), 0)
            .uvs(floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f), 0)
            .positions(floatArrayOf(-1f, -1f, 0f, 1f, -1f, 0f, -1f, 1f, 0f, 1f, 1f, 0f), 0)
            .triangleCount(2)
            .triangles16(shortArrayOf(0, 1, 2, 2, 1, 3))
        
        val orientation = builder.build()
        assertNotNull(orientation)
        assertEquals(4, orientation.vertexCount)

        val quats = FloatArray(16)
        orientation.getQuatsAsFloat(quats, 4)
        
        val halfQuats = ShortArray(16)
        orientation.getQuatsAsHalf(halfQuats, 4)

        val shortQuats = ShortArray(16)
        orientation.getQuatsAsShort(shortQuats, 4)

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
            .triangles32(intArrayOf(0, 1, 2, 2, 1, 3))
            .build()
        val quats = FloatArray(16)
        orientation.getQuatsAsFloat(quats, 4)
        orientation.destroy()
        for (v in 0 until 4) assertEquals(1f, kotlin.math.abs(quats[v * 4 + 3]), 1e-4f, "vertex $v: ${quats.toList()}")
    }

    @Test
    fun testBuilderWithTriangles32() {
        val builder = SurfaceOrientation.Builder()
            .vertexCount(4)
            .normals(floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f), 0)
            .tangents(floatArrayOf(1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f), 0)
            .uvs(floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f), 0)
            .positions(floatArrayOf(-1f, -1f, 0f, 1f, -1f, 0f, -1f, 1f, 0f, 1f, 1f, 0f), 0)
            .triangleCount(2)
            .triangles32(intArrayOf(0, 1, 2, 2, 1, 3))
        
        val orientation = builder.build()
        assertNotNull(orientation)
        orientation.destroy()
    }
}
