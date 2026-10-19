package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FrameHistoryStreamTest : FilamentTestFixture() {
    @Test
    fun testYieldsEachFrameOnce() {
        val swapChain = engine.createSwapChain(16, 16, 0L)
        val renderer = engine.createRenderer()
        val scene = engine.createScene()
        val camera = engine.createCamera(engine.entityManager.create())
        val view = engine.createView().apply {
            this.scene = scene
            this.camera = camera
            viewport = Viewport(0, 0, 16, 16)
        }
        val stream = FrameHistoryStream(renderer)

        // Timing lands some frames after a frame renders, later on a slow runner: render until two calls returned frames.
        val seen = mutableListOf<Int>()
        var batches = 0
        val deadline = Engine.steadyClockTimeNano + 10_000_000_000L
        while (batches < 2 && Engine.steadyClockTimeNano < deadline) {
            repeat(4) {
                if (renderer.beginFrame(swapChain, 0L)) {
                    renderer.render(view)
                    renderer.endFrame()
                }
                engine.flushAndWait()
            }
            val batch = stream.getNewFrames().onEach { r -> r.frameInfo?.let { assertTrue(it.frameId == r.frameId) } }.map { it.frameId }
            if (batch.isNotEmpty()) batches++
            seen += batch
        }
        assertEquals(2, batches, "frames came back in $batches calls: $seen")
        // Oldest first, never repeated across calls (a frame still awaiting presentation timing comes later).
        assertTrue(seen.zipWithNext().all { (a, b) -> b > a }, "frame IDs not strictly increasing: $seen")

        stream.close()
        engine.destroy(view)
        engine.destroyCameraComponent(camera.entity)
        engine.destroy(scene)
        engine.destroy(renderer)
        engine.destroy(swapChain)
    }
}
