package io.github.erkko68.filament.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Test fixture backed by a real (DEFAULT) backend, for binding calls that the
 * NOOP driver panics on (GPU-resource creation: materials, textures, readPixels).
 *
 * [engine] is null when no backend is available in the current environment;
 * tests must early-return in that case (`val engine = engine ?: return`) so they
 * skip gracefully instead of failing. Assertions stay binding-level — handles
 * non-null, getters round-trip — never pixel/golden comparisons.
 */
open class RenderingTestFixture {
    protected var engine: Engine? = null

    @BeforeTest
    fun setUp() {
        Filament.init()
        engine = try {
            Engine.create(Engine.Backend.DEFAULT).takeIf { it.isValid() }
        } catch (t: Throwable) {
            null // ponytail: no GPU backend here -> tests guard on null and skip
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
