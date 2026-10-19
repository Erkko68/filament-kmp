package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * An array shorter than what C reads (or writes, for out arrays) would be accessed out of bounds in native
 * memory; every API that hands one over must turn it away first.
 */
class ArrayLengthTest : FilamentTestFixture() {
    private fun rejected(what: String, call: () -> Unit) = assertFailsWith<IllegalArgumentException>(what) { call() }

    @Test
    fun fixedSizeInputsAndOutputs() {
        val entity = EntityManager.get().create()
        val camera = engine.createCamera(entity)
        val tm = engine.transformManager
        tm.create(entity)
        val inst = tm.getInstance(entity)

        rejected("3x3 model matrix") { camera.setModelMatrix(FloatArray(9)) }
        rejected("short projection out") { camera.getProjectionMatrix(out = DoubleArray(4)) }
        rejected("short transform") { tm.setTransform(inst, FloatArray(12)) }
        rejected("short world transform out") { tm.getWorldTransform(inst, FloatArray(4)) }
        rejected("3x3 aabb transform") { Aabb().transform(FloatArray(9)) }

        tm.destroy(entity)
        engine.destroyCameraComponent(entity)
        EntityManager.get().destroy(entity)
    }

    @Test
    fun countDrivenInputs() {
        val vb = VertexBuffer.Builder().vertexCount(3).bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .build(engine)
        rejected("upload count past the array") { vb.setBufferAt(engine, 0, ByteArray(12), 0, 36) }
        val skinning = SkinningBuffer.Builder().boneCount(4).build(engine)
        rejected("fewer bones than counted") { skinning.setBones(engine, FloatArray(16), count = 2) }
        rejected("SH for fewer bands") { IndirectLight.Builder().irradiance(3, FloatArray(12)) }
        rejected("LUT smaller than its dimension") { ColorGrading.Builder().customLut(FloatArray(3), 16) }
        rejected("AABB indices past the vertices") { RenderableManager.computeAABB(FloatArray(6), intArrayOf(0, 5)) }

        val material = Material.Builder().payload(TestMaterials.getEmissiveMaterialBytes()).build(engine)!!
        val mi = material.createInstance()
        rejected("float3 array shorter than its count") {
            mi.setParameter("color", MaterialInstance.FloatElement.FLOAT3, FloatArray(3), 0, 2)
        }
        engine.destroy(mi)
        engine.destroy(material)
        engine.destroy(skinning)
        engine.destroy(vb)
    }

    @Test
    fun meshBuildersCheckAtBuild() {
        // 4 float4 tangents need 16 floats: the old SurfaceOrientation test passed 12.
        rejected("short tangents") {
            SurfaceOrientation.Builder().vertexCount(4)
                .normals(FloatArray(12)).tangents(FloatArray(12))
                .triangleCount(2).triangles(intArrayOf(0, 1, 2, 2, 1, 3))
                .build()
        }
        rejected("triangles short of their count") {
            SurfaceOrientation.Builder().vertexCount(3).normals(FloatArray(9))
                .triangleCount(2).triangles(intArrayOf(0, 1, 2))
                .build()
        }
        SurfaceOrientation.Builder().vertexCount(1).normals(floatArrayOf(0f, 0f, 1f)).build()!!.use { orientation ->
            rejected("more quaternions than vertices") { orientation.getQuats(FloatArray(8), 2) }
            rejected("out array too short") { orientation.getQuats(FloatArray(2), 1) }
        }
    }
}
