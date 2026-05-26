package io.github.erkko68.filament.gltfio.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.gltfio.Gltfio
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class GltfioTestFixture {
    protected lateinit var engine: Engine

    @BeforeTest
    fun setUp() {
        Filament.init()
        Gltfio.init()
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
