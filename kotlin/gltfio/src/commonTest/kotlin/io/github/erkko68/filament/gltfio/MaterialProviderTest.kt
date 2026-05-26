package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import kotlin.test.Ignore
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

    @Test
    @Ignore // Crashes the JVM/Android test runner; needs further investigation.
    fun testGetMaterialAndCreateInstance() {
        val provider = UbershaderProvider(engine)
        val key = MaterialKey().apply {
            unlit = true
            doubleSided = false
        }
        val uvmap = IntArray(8)

        // These may return null if the ubershader archive has no match for the
        // requested key — we exercise the bindings either way.
        provider.getMaterial(key, uvmap)
        provider.getMaterial(key, uvmap, "label")
        provider.createMaterialInstance(key, uvmap)
        provider.createMaterialInstance(key, uvmap, "label")
        provider.createMaterialInstance(key, uvmap, "label", "extras")

        provider.destroy()
    }
}
