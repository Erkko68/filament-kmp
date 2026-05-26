package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.IndirectLight
import io.github.erkko68.filament.Skybox
import io.github.erkko68.filament.Texture
import kotlin.test.Test
import kotlin.test.assertTrue

class KTX1LoaderSmokeTest {
    @Test
    fun verifyKTX1LoaderApi() {
        val ktx1Verify: (Engine) -> Unit = { engine ->
            val options = KTX1Loader.Options()
            options.srgb = true
            
            val tex: Texture? = KTX1Loader.createTexture(engine, byteArrayOf(0), options)
            
            val iblBundle = KTX1Loader.createIndirectLight(engine, byteArrayOf(0), options)
            val ibl: IndirectLight? = iblBundle.indirectLight
            val iblCube: Texture? = iblBundle.cubemap
            
            val skyBundle = KTX1Loader.createSkybox(engine, byteArrayOf(0), options)
            val sky: Skybox? = skyBundle.skybox
            val skyCube: Texture? = skyBundle.cubemap
            
            val sh: FloatArray? = KTX1Loader.getSphericalHarmonics(byteArrayOf(0))
        }
        assertTrue(true, "KTX1Loader API signatures resolved successfully.")
    }
}
