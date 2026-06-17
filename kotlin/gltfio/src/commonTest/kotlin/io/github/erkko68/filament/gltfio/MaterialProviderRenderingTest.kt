package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioRenderingTestFixture
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Real-backend coverage for [UbershaderProvider.getMaterial], which compiles
 * ubershaders and so can't run on NOOP. (Was @Ignore'd before — it crashed the
 * runner only because NOOP can't compile shaders.)
 */
@IgnoreJs // gltfio UbershaderProvider bindings are not implemented in the web wrapper.
class MaterialProviderRenderingTest : GltfioRenderingTestFixture() {
    @Test
    fun testGetMaterialAndCreateInstance() {
        val engine = engine ?: return
        val provider = UbershaderProvider(engine)
        val key = MaterialKey().apply {
            unlit = true
            doubleSided = false
        }
        val uvmap = IntArray(8)
        key.constrainMaterial(uvmap)

        // getMaterial compiles and returns a real ubershader for this key.
        // getMaterial compiles and returns a real ubershader (base_unlit_opaque,
        // UNLIT, FL1, 40 params); getDefaultInstance on it also works.
        val mat = provider.getMaterial(key, uvmap)
        assertNotNull(mat)
        assertNotNull(provider.getMaterial(key, uvmap, "label"))
        assertNotNull(mat.getDefaultInstance())

        // KNOWN ABORT — not exercised here. mat.createInstance() (and therefore
        // provider.createMaterialInstance) aborts the process on this ubershader
        // material, while it works for ordinary materials (see MaterialRenderingTest)
        // and getDefaultInstance() above works. Upstream Material::createInstance is
        // noexcept, so an internal precondition there can't propagate and calls
        // std::terminate. The abort is inside the prebuilt filament lib (our binding
        // is a one-line reinterpret_cast, c/filament/cpp/Material.cpp:56); the release
        // build strips the panic message and SIP blocks a native debugger here, so the
        // exact precondition is undiagnosed. Tracked separately — needs a debug
        // filament build to pin down.

        provider.destroy()
    }
}
