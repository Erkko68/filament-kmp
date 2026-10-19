package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Viewport
import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import kotlin.test.Test
import kotlin.test.assertEquals

/** The consumer loop: models in a scene, animated and re-skinned through frames, then taken out. */
class GltfSceneTest : GltfioTestFixture() {
    @Test
    fun animatedModelsRenderThroughFramesAndLeaveTheScene() {
        val models = listOf(TestGlb.getFoxGlbBytes(), TestGlb.getDuckGlbBytes(), TestGlb.getMaterialVariantsGlbBytes()).map { load(it) }
        val (fox, _, variants) = models
        val scene = engine.createScene()
        val cameraEntity = engine.entityManager.create()
        val camera = engine.createCamera(cameraEntity).apply {
            // Fox is ~80 units tall around the origin; the duck and the triangle sit inside it.
            lookAt(0.0, 40.0, 300.0, 0.0, 40.0, 0.0)
            setProjection(45.0, 1.0, 0.1, 1000.0)
        }
        val view = engine.createView().also {
            it.scene = scene
            it.camera = camera
            it.viewport = Viewport(0, 0, 64, 64)
        }
        val renderer = engine.createRenderer()
        val swapChain = engine.createSwapChain(64, 64)

        models.forEach { scene.addEntities(it.entities) }
        val animator = fox.instance.animator
        val joint = fox.instance.getJointsAt(0)[0]
        val visible = mutableListOf<Int>()
        repeat(12) { frame ->
            val time = frame / 60f
            animator.applyAnimation(1, time)
            animator.applyCrossFade(0, time, 0.5f)
            animator.updateBoneMatrices()
            variants.instance.applyMaterialVariant(frame % 2)
            if (frame == 6) fox.instance.detachSkin(0, joint)
            if (frame == 8) fox.instance.attachSkin(0, joint)
            if (renderer.beginFrame(swapChain, 0L)) {
                renderer.render(view)
                renderer.endFrame()
                visible += view.visibleRenderableCount
            }
        }
        animator.resetBoneMatrices()
        val drawn = scene.renderableCount
        models.forEach { scene.removeEntities(it.entities) }
        val left = scene.entityCount

        engine.destroy(renderer)
        engine.destroy(swapChain)
        engine.destroy(view)
        engine.destroyCameraComponent(cameraEntity)
        engine.entityManager.destroy(cameraEntity)
        engine.destroy(scene)

        assertEquals(3, drawn)
        assertEquals(3, visible.lastOrNull(), "every model is in view; frames rendered: ${visible.size}")
        assertEquals(0, left)
    }
}
