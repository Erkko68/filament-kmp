package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
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

    @Test
    fun testDecodesMinimalRadianceImage() {
        // 1x1 flat (non-RLE) Radiance file: header, blank line, resolution, then one RGBE pixel.
        val header = "#?RADIANCE\nFORMAT=32-bit_rle_rgbe\n\n-Y 1 +X 1\n".encodeToByteArray()
        val hdr = header + byteArrayOf(128.toByte(), 64, 32, 129.toByte())
        val texture = HDRLoader.createTexture(engine, hdr, Texture.InternalFormat.RGB16F)
        assertNotNull(texture)
        assertEquals(1, texture.getWidth(0))
        engine.destroyTexture(texture)
    }
}
