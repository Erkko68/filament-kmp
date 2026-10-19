package io.github.erkko68.filament.testsupport

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

/**
 * Tier 2 fixture: an engine on a real backend, for pixels and async calls. Skipped (reported as such) on a
 * host without a rasterizer; where there is one, failing to get a real backend fails the test.
 */
open class RealBackendFixture {
    protected lateinit var engine: Engine

    @BeforeTest
    fun setUp() {
        assumeRealBackend()
        Filament.init()
        engine = assertNotNull(createEngine(), "no engine on a host with a rasterizer")
        assertNotEquals(Engine.Backend.NOOP, engine.backend, "DEFAULT resolved to NOOP")
    }

    protected open fun createEngine(): Engine? = Engine.create(Engine.Backend.DEFAULT)

    @AfterTest
    fun tearDown() {
        if (::engine.isInitialized) {
            engine.flushAndWait()
            Engine.destroy(engine)
        }
    }
}
