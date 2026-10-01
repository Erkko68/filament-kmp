package io.github.erkko68.filament.testutils

import io.github.erkko68.filament.Engine
import kotlin.test.assertTrue

/**
 * Pumps the engine until [done], for up to 10 s (shader compiles take a while). Callbacks run on the main thread once
 * the backend has served them; without threads (web) the backend only advances as frames tick, so this renders empty ones.
 */
fun Engine.pumpUntil(done: () -> Boolean) {
    val swapChain = createSwapChain(1, 1, 0L)
    val renderer = createRenderer()
    val deadline = Engine.steadyClockTimeNano + 10_000_000_000L
    while (!done() && Engine.steadyClockTimeNano < deadline) {
        if (renderer.beginFrame(swapChain, 0L)) renderer.endFrame()
        flushAndWait()
        pumpMessageQueues()
    }
    destroy(renderer)
    destroy(swapChain)
    assertTrue(done(), "callback never arrived")
}
