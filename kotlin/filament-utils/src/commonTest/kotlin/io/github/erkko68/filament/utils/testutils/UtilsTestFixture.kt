package io.github.erkko68.filament.utils.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class UtilsTestFixture {
    protected lateinit var engine: Engine

    @BeforeTest
    fun setUp() {
        Filament.init()
        engine = Engine.create(Engine.Backend.NOOP)
    }

    @AfterTest
    fun tearDown() {
        if (::engine.isInitialized) {
            engine.flushAndWait()
            engine.destroy()
        }
    }
}
