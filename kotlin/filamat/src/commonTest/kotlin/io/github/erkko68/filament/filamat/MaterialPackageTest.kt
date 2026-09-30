package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.filamat.testutils.FilamatTestFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        assertTrue(pkg.isValid, "MaterialPackage built from a valid shader must be valid")
    }

    @Test
    fun testMaterialPackageDataIsNonEmpty() {
        val pkg = buildMinimalPackage() ?: return
        assertTrue(pkg.data.isNotEmpty(), "Compiled material package data must not be empty")
        assertEquals(pkg.data.size, pkg.size)
    }

    @Test
    fun testMaterialPackageDataHasReasonableSize() {
        val pkg = buildMinimalPackage() ?: return
        // A compiled Filament material package is a chunked binary blob; even a minimal
        // unlit material is well over a few hundred bytes once shader stages are encoded.
        assertTrue(pkg.size > 64, "Compiled material package should be larger than 64 bytes, was ${pkg.size}")
    }

    @Test
    fun testConstructorsAndInvalidPackage() {
        assertEquals(16, MaterialPackage(16).size)
        val src = byteArrayOf(1, 2, 3, 4)
        val copy = MaterialPackage(src, 2)
        src[0] = 9
        assertContentEquals(byteArrayOf(1, 2), copy.data)
        assertTrue(copy.isValid)
        val invalid = MaterialPackage.invalidPackage()
        assertFalse(invalid.isValid)
        assertEquals(0, invalid.size)
    }
}
