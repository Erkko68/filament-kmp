package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class AnimatorTest : GltfioTestFixture() {
    /** Every node's local transform, in entity order. */
    private fun FilamentAsset.pose(): List<Float> {
        val tm = engine.transformManager
        return entities.filter(tm::hasComponent).flatMap { tm.getTransform(tm.getInstance(it)).toList() }
    }

    private fun assertPose(expected: List<Float>, actual: List<Float>, message: String) =
        assertTrue(expected.zip(actual).all { (e, a) -> abs(e - a) < 1e-4f }, message)

    @Test
    fun foxHasItsThreeClips() {
        val animator = load(TestGlb.getFoxGlbBytes()).instance.animator
        assertEquals(3, animator.animationCount)
        assertEquals(listOf("Survey", "Walk", "Run"), List(3) { animator.getAnimationName(it) })
        val durations = List(3) { animator.getAnimationDuration(it) }
        listOf(3.4166667f, 0.7083333f, 1.1583333f).zip(durations).forEach { (e, a) -> assertEquals(e, a, 1e-4f) }
        assertEquals(0, load(TestGlb.getDuckGlbBytes()).instance.animator.animationCount)
    }

    @Test
    fun applyingAnAnimationMovesItsNodes() {
        val box = load(TestGlb.getBoxAnimatedGlbBytes())
        val animator = box.instance.animator
        val half = animator.getAnimationDuration(0) / 2
        animator.applyAnimation(0, 0f)
        val start = box.pose()
        animator.applyAnimation(0, half)
        val middle = box.pose()
        animator.applyAnimation(0, 0f)

        assertNotEquals(start, middle, "half way through, the boxes have moved")
        assertPose(start, box.pose(), "the same time gives the same pose")
    }

    /** applyAnimation's clip is alpha = 1, applyCrossFade's is alpha = 0 (Animator.h). */
    @Test
    fun crossFadeBlendsFromThePreviousClip() {
        val fox = load(TestGlb.getFoxGlbBytes())
        val animator = fox.instance.animator
        fun pose(alpha: Float?): List<Float> {
            animator.applyAnimation(WALK, 0.3f)
            if (alpha != null) animator.applyCrossFade(SURVEY, 1f, alpha)
            return fox.pose()
        }
        val walk = pose(null)
        animator.applyAnimation(SURVEY, 1f)
        val survey = fox.pose()

        assertNotEquals(walk, survey)
        assertPose(walk, pose(1f), "alpha 1 is the current clip")
        assertPose(survey, pose(0f), "alpha 0 is the previous clip")
        val blend = pose(0.5f)
        assertTrue(blend != walk && blend != survey, "half way is neither")
    }

    private companion object {
        const val SURVEY = 0
        const val WALK = 1
    }
}
