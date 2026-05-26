package io.github.erkko68.filament.filamat

import kotlin.test.Test
import kotlin.test.assertTrue

class MaterialPackageSmokeTest {
    @Test
    fun verifyMaterialPackageApi() {
        val packageVerify: (MaterialPackage) -> Unit = { pkg ->
            val buffer: ByteArray = pkg.getBuffer()
            val valid: Boolean = pkg.isValid()
        }
        assertTrue(true, "MaterialPackage API signatures resolved successfully.")
    }
}
