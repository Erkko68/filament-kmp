package io.github.erkko68.filament.utils

import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KTX1LoaderTest : UtilsTestFixture() {
    @Test
    fun testOptionsDefaultsAndRoundTrip() {
        val options = KTX1Loader.Options()
        assertNotNull(options)
        options.srgb = true
        assertTrue(options.srgb)
        options.srgb = false
        assertFalse(options.srgb)
    }

    @Test
    fun testIndirectLightBundleFields() {
        val bundle = KTX1Loader.IndirectLightBundle(null, null)
        assertNull(bundle.indirectLight)
        assertNull(bundle.cubemap)
    }

    @Test
    fun testSkyboxBundleFields() {
        val bundle = KTX1Loader.SkyboxBundle(null, null)
        assertNull(bundle.skybox)
        assertNull(bundle.cubemap)
    }

    // NOTE: KTX1Loader.{createTexture, createIndirectLight, createSkybox,
    // getSphericalHarmonics} cannot be safely exercised with synthetic / invalid
    // data — the underlying native parser asserts on malformed input and aborts
    // the process rather than returning null. Exercising those paths requires a
    // real .ktx1 asset bundled into commonTest resources.
}
