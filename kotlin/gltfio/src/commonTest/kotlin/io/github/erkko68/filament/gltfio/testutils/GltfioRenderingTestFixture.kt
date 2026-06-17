package io.github.erkko68.filament.gltfio.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.gltfio.Gltfio
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Real-backend gltfio fixture for bindings that compile shaders (ubershader
 * material providers). [engine] is null when no backend is available — tests
 * must early-return. Mirror of the filament module's RenderingTestFixture.
 */
open class GltfioRenderingTestFixture {
    protected var engine: Engine? = null

    @BeforeTest
    fun setUp() {
        Filament.init()
        Gltfio.init()
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
