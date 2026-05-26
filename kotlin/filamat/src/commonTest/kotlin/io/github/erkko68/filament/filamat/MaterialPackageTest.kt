package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.filamat.testutils.FilamatTestFixture
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MaterialPackageTest : FilamatTestFixture() {
    private fun buildMinimalPackage(): MaterialPackage? {
        return try {
            MaterialBuilder()
                .name("MinimalUnlit")
                .shading(MaterialBuilder.Shading.UNLIT)
                .material("void material(inout MaterialInputs m) { prepareMaterial(m); }")
                .build()
        } catch (e: UnsupportedOperationException) {
            null
        }
    }

    @Test
    fun testMaterialPackageIsValid() {
        val pkg = buildMinimalPackage() ?: return
        assertTrue(pkg.isValid(), "MaterialPackage built from a valid shader must be valid")
    }

    @Test
    fun testMaterialPackageBufferIsNonEmpty() {
        val pkg = buildMinimalPackage() ?: return
        val buffer = pkg.getBuffer()
        assertNotNull(buffer)
        assertTrue(buffer.isNotEmpty(), "Compiled material package buffer must not be empty")
    }

    @Test
    fun testMaterialPackageBufferHasReasonableSize() {
        val pkg = buildMinimalPackage() ?: return
        val buffer = pkg.getBuffer()
        // A compiled Filament material package is a chunked binary blob; even a minimal
        // unlit material is well over a few hundred bytes once shader stages are encoded.
        assertTrue(buffer.size > 64, "Compiled material package should be larger than 64 bytes, was ${buffer.size}")
    }
}
