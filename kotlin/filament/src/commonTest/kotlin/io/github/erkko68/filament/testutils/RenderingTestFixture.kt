package io.github.erkko68.filament.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.testsupport.TestEnv
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
        // Don't even attempt Engine.create on hosts with no GPU/display — Filament
        // aborts on its driver thread there, which the try/catch below can't catch.
        if (!TestEnv.gpuBackendAvailable) return
        engine = try {
            Engine.create(Engine.Backend.DEFAULT)
                .takeIf { it.isValid() }
                // DEFAULT can silently resolve to NOOP when the real backend fails to
                // initialize (e.g. an iOS simulator session without Metal). A NOOP engine
                // is "valid" but rasterizes nothing, so Tier C frame assertions would
                // fail on garbage readbacks — treat it as no backend and skip.
                ?.let { e ->
                    if (e.backend == Engine.Backend.NOOP) {
                        e.destroy()
                        null
                    } else e
                }
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
