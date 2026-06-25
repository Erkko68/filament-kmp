package io.github.erkko68.filament.compose.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.Scene
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Base fixture for Tier-A compose tests: a [Engine.Backend.NOOP] engine + a [Scene], created and
 * destroyed per test. NOOP runs without a GPU/display while still exercising the full binding path
 * for the CPU-side managers (Entity/Transform/Light) and `View`/`Camera`/`ColorGrading`
 * construction — everything the scene-graph composables and the `applyTo` units touch. GPU-resource
 * composables (primitives, materials, textures, glTF) panic on NOOP and belong to the gated Tier-B
 * suite instead.
 */
open class ComposeTestFixture {
    protected lateinit var engine: Engine
    protected lateinit var scene: Scene

    @BeforeTest
    fun setUp() {
        Filament.init()
        engine = Engine.create(Engine.Backend.NOOP)
        scene = engine.createScene()
    }

    @AfterTest
    fun tearDown() {
        if (::engine.isInitialized) {
            if (::scene.isInitialized) engine.destroyScene(scene)
            engine.flushAndWait()
            engine.destroy()
        }
    }
}
