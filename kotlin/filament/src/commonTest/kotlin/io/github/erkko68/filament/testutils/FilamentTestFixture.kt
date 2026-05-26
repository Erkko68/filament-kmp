package io.github.erkko68.filament.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Base test fixture that creates and destroys a Filament [Engine] for each test.
 *
 * Uses [Engine.Backend.NOOP] so tests run without a GPU or display server while
 * still exercising every JNI / WASM / C-interop binding call.
 */
open class FilamentTestFixture {
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
