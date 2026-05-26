package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import kotlin.test.Test
import kotlin.test.assertNotNull

class HDRLoaderTest : UtilsTestFixture() {
    @Test
    fun testInternalFormatEnumLinks() {
        // Verifies the InternalFormat enum (passed through to HDRLoader.createTexture)
        // is reachable from the utils binding surface — purely a link check.
        assertNotNull(Texture.InternalFormat.RGBA8)
        assertNotNull(Texture.InternalFormat.RGB16F)
        assertNotNull(Texture.InternalFormat.R11F_G11F_B10F)
    }

    // NOTE: HDRLoader.createTexture cannot be safely exercised here — the native
    // parser aborts on malformed input rather than returning null. Real coverage
    // requires a valid .hdr asset bundled into jvmTest resources.
}
