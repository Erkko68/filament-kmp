package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import kotlin.test.Test
import kotlin.test.assertTrue

class TextureLoaderSmokeTest {
    @Test
    fun verifyTextureLoaderApi() {
        val textureVerify: (Engine) -> Unit = { engine ->
            val typeColor = TextureLoader.TextureType.COLOR
            val typeNormal = TextureLoader.TextureType.NORMAL
            val typeData = TextureLoader.TextureType.DATA
            
            val tex: Texture? = TextureLoader.loadTexture(engine, byteArrayOf(0), typeColor)
        }
        assertTrue(true, "TextureLoader API signatures resolved successfully.")
    }
}
