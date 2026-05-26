package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import kotlin.test.Test
import kotlin.test.assertTrue

class HDRLoaderSmokeTest {
    @Test
    fun verifyHDRLoaderApi() {
        val hdrVerify: (Engine) -> Unit = { engine ->
            try {
                val format = Texture.InternalFormat.RGBA8
                val tex: Texture? = HDRLoader.createTexture(engine, byteArrayOf(0), format)
            } catch (e: UnsupportedOperationException) {
                // Expected on unsupported runtime targets like JS
            }
        }
        assertTrue(true, "HDRLoader API signatures resolved successfully.")
    }
}
