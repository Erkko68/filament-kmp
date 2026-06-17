package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import kotlin.test.Test

class MaterialProviderTest : GltfioTestFixture() {
    @Test
    fun testUbershaderProviderLifecycle() {
        val provider = UbershaderProvider(engine)

        // Don't call getMaterials() here: upstream's binding pre-allocates one
        // slot per ubershader spec and lazily fills them on getMaterial(), but
        // wraps every slot in `new Material(ptr)` unconditionally — so reading
        // materials before any are populated crashes on null pointers.
        provider.needsDummyData(0)
        provider.destroyMaterials()
        provider.destroy()
    }

    @Test
    fun testUbershaderAsMaterialProviderInterface() {
        val provider: MaterialProvider = UbershaderProvider(engine)
        provider.needsDummyData(0)
        provider.destroy()
    }

    // getMaterial / createMaterialInstance need a real backend to compile
    // ubershaders — covered in MaterialProviderRenderingTest.
}
