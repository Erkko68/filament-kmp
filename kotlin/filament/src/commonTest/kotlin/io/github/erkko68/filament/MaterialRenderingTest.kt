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
        assertTrue(mat.name.isNotEmpty())
        assertNotNull(mat.shading)
        assertNotNull(mat.interpolation)
        assertNotNull(mat.blendingMode)
        assertNotNull(mat.transparencyMode)
        assertNotNull(mat.refractionMode)
        assertNotNull(mat.refractionType)
        assertNotNull(mat.reflectionMode)
        assertNotNull(mat.vertexDomain)
        assertNotNull(mat.cullingMode)
        assertNotNull(mat.featureLevel)
        assertTrue(mat.maskThreshold >= 0f)
        assertTrue(mat.specularAntiAliasingVariance >= 0f)
        assertTrue(mat.specularAntiAliasingThreshold >= 0f)
        assertTrue(mat.parameterCount >= 0)
        val params = mat.parameters
        assertEquals(mat.parameterCount, params.size)
        params.forEach { assertTrue(it.name.isNotEmpty()) }
        assertNotNull(mat.requiredAttributes)

        // Instances
        val inst1 = mat.createInstance()
        assertNotNull(inst1)
        val inst2 = mat.createInstance("named_instance")
        assertNotNull(inst2)
        val defInst = mat.defaultInstance
        assertNotNull(defInst)

        engine.destroyMaterialInstance(inst1)
        engine.destroyMaterialInstance(inst2)
        engine.destroyMaterial(mat)
    }
}
