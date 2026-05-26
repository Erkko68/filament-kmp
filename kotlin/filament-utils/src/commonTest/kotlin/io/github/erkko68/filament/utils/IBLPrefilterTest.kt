package io.github.erkko68.filament.utils

import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import kotlin.test.Test
import kotlin.test.assertNotNull

class IBLPrefilterTest : UtilsTestFixture() {
    @Test
    fun testIBLPrefilterContextLifecycle() {
        val context = IBLPrefilterContext(engine)
        assertNotNull(context)
        context.destroy()
    }

    @Test
    fun testEquirectangularToCubemapLifecycle() {
        val context = IBLPrefilterContext(engine)
        val converter = EquirectangularToCubemap(context)
        assertNotNull(converter)
        // run() requires a valid equirectangular Texture. We only verify the lifecycle
        // (create + destroy) since creating a valid equirectangular texture on a NOOP
        // backend is not feasible without real image data.
        converter.destroy()
        context.destroy()
    }

    @Test
    fun testSpecularFilterLifecycle() {
        val context = IBLPrefilterContext(engine)
        val filter = SpecularFilter(context)
        assertNotNull(filter)
        // run() requires a valid cubemap Texture; only lifecycle is tested here.
        filter.destroy()
        context.destroy()
    }

    // TODO: Full IBLPrefilterContext tests (EquirectangularToCubemap.run,
    //       SpecularFilter.run) require valid Texture objects created from real
    //       HDR / KTX image data and a non-NOOP rendering backend.
}
