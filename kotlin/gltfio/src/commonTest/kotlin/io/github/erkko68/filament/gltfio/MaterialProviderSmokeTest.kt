package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import kotlin.test.Test
import kotlin.test.assertTrue

class MaterialProviderSmokeTest {
    @Test
    fun verifyMaterialProviderApi() {
        val providerVerify: (MaterialProvider, MaterialKey) -> Unit = { provider, key ->
            val matInst: MaterialInstance? = provider.createMaterialInstance(key, intArrayOf(0), "label", "extras")
            val mat: Material? = provider.getMaterial(key, intArrayOf(0), "label")
            val mats: Array<Material> = provider.getMaterials()
            val needsDummy: Boolean = provider.needsDummyData(0)
            provider.destroyMaterials()
            provider.destroy()
        }
        
        val ubershaderVerify: (Engine) -> Unit = { engine ->
            val provider = UbershaderProvider(engine)
            val isProvider: MaterialProvider = provider
        }
        
        assertTrue(true, "MaterialProvider API signatures resolved successfully.")
    }
}
