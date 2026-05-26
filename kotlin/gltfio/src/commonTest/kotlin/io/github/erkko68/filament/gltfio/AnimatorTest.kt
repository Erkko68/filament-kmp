package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AnimatorTest : GltfioTestFixture() {
    @Test
    fun testAnimatorMetadata() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        // Resources must be loaded before accessing the Animator's native methods.
        val resourceLoader = ResourceLoader(engine)
        resourceLoader.loadResources(asset)

        val animator = asset.getInstance().getAnimator()
        assertNotNull(animator)

        val animCount = animator.getAnimationCount()
        assertTrue(animCount >= 0)

        for (i in 0 until animCount) {
            animator.getAnimationDuration(i)
            animator.getAnimationName(i)
        }

        resourceLoader.destroy()
        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testApplyAnimationAndBoneMatrices() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        // Resources must be loaded before accessing the Animator's native methods.
        val resourceLoader = ResourceLoader(engine)
        resourceLoader.loadResources(asset)

        val animator = asset.getInstance().getAnimator()
        val animCount = animator.getAnimationCount()

        if (animCount > 0) {
            val duration = animator.getAnimationDuration(0)
            animator.applyAnimation(0, 0f)
            animator.applyAnimation(0, duration / 2f)
            animator.updateBoneMatrices()
            if (animCount > 1) {
                animator.applyCrossFade(0, 0f, 0.5f)
            }
        }

        animator.resetBoneMatrices()

        resourceLoader.destroy()
        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }
}
