package io.github.erkko68.filament.utils

import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import kotlin.test.Test

// The filters' invoke needs a real backend: see IBLPrefilterRenderingTest.
class IBLPrefilterTest : UtilsTestFixture() {
    @Test
    fun testFiltersLifecycle() {
        IBLPrefilterContext(engine).use { context ->
            IBLPrefilterContext.EquirectangularToCubemap(context).destroy()
            IBLPrefilterContext.EquirectangularToCubemap(context, IBLPrefilterContext.EquirectangularToCubemap.Config(mirror = false)).destroy()
            IBLPrefilterContext.IrradianceFilter(context, IBLPrefilterContext.IrradianceFilter.Config(sampleCount = 64)).destroy()
            IBLPrefilterContext.SpecularFilter(context, IBLPrefilterContext.SpecularFilter.Config(sampleCount = 64, levelCount = 3)).destroy()
        }
    }
}
