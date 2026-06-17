package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.RenderingTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Real-backend coverage for [Material] bindings that NOOP can't build.
 * See [RenderingTestFixture] — skips when no backend is available.
 */
class MaterialRenderingTest : RenderingTestFixture() {
    @Test
    fun testMaterialLifecycleAndGetters() {
        val engine = engine ?: return
        val bytes = TestMaterials.getEmissiveMaterialBytes()
        if (bytes.isEmpty()) return

        val mat = Material.Builder()
            .payload(bytes)
            .build(engine)
        assertNotNull(mat)
        assertTrue(engine.isValidMaterial(mat))

        // Getters: just exercise the binding path and round-trip what we can.
        assertTrue(mat.getName().isNotEmpty())
        assertNotNull(mat.getShading())
        assertNotNull(mat.getInterpolation())
        assertNotNull(mat.getBlendingMode())
        assertNotNull(mat.getTransparencyMode())
        assertNotNull(mat.getRefractionMode())
        assertNotNull(mat.getRefractionType())
        assertNotNull(mat.getReflectionMode())
        assertNotNull(mat.getVertexDomain())
        assertNotNull(mat.getCullingMode())
        assertNotNull(mat.getFeatureLevel())
        assertTrue(mat.getMaskThreshold() >= 0f)
        assertTrue(mat.getSpecularAntiAliasingVariance() >= 0f)
        assertTrue(mat.getSpecularAntiAliasingThreshold() >= 0f)
        assertTrue(mat.getParameterCount() >= 0)
        val params = mat.getParameters()
        assertEquals(mat.getParameterCount(), params.size)
        params.forEach { assertTrue(it.name.isNotEmpty()) }
        assertNotNull(mat.getRequiredAttributes())

        // Instances
        val inst1 = mat.createInstance()
        assertNotNull(inst1)
        val inst2 = mat.createInstance("named_instance")
        assertNotNull(inst2)
        val defInst = mat.getDefaultInstance()
        assertNotNull(defInst)

        engine.destroyMaterialInstance(inst1)
        engine.destroyMaterialInstance(inst2)
        engine.destroyMaterial(mat)
    }
}
