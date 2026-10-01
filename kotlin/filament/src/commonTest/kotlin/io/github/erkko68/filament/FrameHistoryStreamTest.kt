package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.RenderingTestFixture
import kotlin.test.Test
import kotlin.test.assertTrue

class FrameHistoryStreamTest : RenderingTestFixture() {
    @Test
    fun testYieldsEachFrameOnce() {
        val engine = engine ?: return
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

        val seen = mutableListOf<Int>()
        repeat(2) {
            repeat(4) {
                if (renderer.beginFrame(swapChain, 0L)) {
                    renderer.render(view)
                    renderer.endFrame()
                }
                engine.flushAndWait()
            }
            seen += stream.getNewFrames().onEach { r -> r.frameInfo?.let { assertTrue(it.frameId == r.frameId) } }.map { it.frameId }
        }
        assertTrue(seen.isNotEmpty(), "no frames came back")
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
