package io.github.erkko68.filament.utils.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.testsupport.TestEnv
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Real-backend fixture for utils bindings that NOOP can't run (IBL prefilter
 * compute passes). [engine] is null when no backend is available — tests must
 * early-return in that case. Mirror of the filament module's RenderingTestFixture.
 */
open class UtilsRenderingTestFixture {
    protected var engine: Engine? = null

    @BeforeTest
    fun setUp() {
        Filament.init()
        if (!TestEnv.gpuBackendAvailable) return
        engine = try {
            Engine.create(Engine.Backend.DEFAULT).takeIf { it.isValid() }
        } catch (t: Throwable) {
            null
        }
    }

    @AfterTest
    fun tearDown() {
        engine?.let {
            it.flushAndWait()
            it.destroy()
        }
        engine = null
    }
}
