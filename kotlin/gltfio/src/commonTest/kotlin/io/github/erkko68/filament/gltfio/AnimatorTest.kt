package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AnimatorTest : GltfioTestFixture() {
    @Test
    fun testCrossFadeBetweenAnimations() {
        val bytes = TestGlb.getFoxGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val resourceLoader = ResourceLoader(engine)
        resourceLoader.loadResources(asset)

        val animator = asset.instance.animator
        // Fox ships with multiple animation tracks (Survey / Walk / Run).
        assertTrue(animator.animationCount > 1)

        animator.applyAnimation(0, 0.1f)
        animator.applyCrossFade(1, 0.1f, 0.5f)
        animator.updateBoneMatrices()

        resourceLoader.destroy()
        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testAnimatedAssetAnimations() {
        val bytes = TestGlb.getBoxAnimatedGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val resourceLoader = ResourceLoader(engine)
        resourceLoader.loadResources(asset)

        val animator = asset.instance.animator
        val animCount = animator.animationCount
        // BoxAnimated ships with at least one animation track.
        assertTrue(animCount > 0)

        for (i in 0 until animCount) {
            // getAnimationName is nullable: the JS/web wrapper doesn't expose names.
            animator.getAnimationName(i)
            val duration = animator.getAnimationDuration(i)
            assertTrue(duration >= 0f)
            animator.applyAnimation(i, 0f)
            animator.applyAnimation(i, duration / 2f)
        }
        animator.updateBoneMatrices()
        if (animCount > 1) {
            animator.applyCrossFade(0, 0f, 0.5f)
        }
        animator.resetBoneMatrices()

        resourceLoader.destroy()
        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }


    @Test
    fun testAnimatorMetadata() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        // Resources must be loaded before accessing the Animator's native methods.
        val resourceLoader = ResourceLoader(engine)
        resourceLoader.loadResources(asset)

        val animator = asset.instance.animator
        assertNotNull(animator)

        val animCount = animator.animationCount
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
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        // Resources must be loaded before accessing the Animator's native methods.
        val resourceLoader = ResourceLoader(engine)
        resourceLoader.loadResources(asset)

        val animator = asset.instance.animator
        val animCount = animator.animationCount

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
