package io.github.erkko68.filament.gltfio

import kotlin.test.Test
import kotlin.test.assertTrue

class AnimatorSmokeTest {
    @Test
    fun verifyAnimatorApi() {
        val animatorVerify: (Animator) -> Unit = { animator ->
            animator.applyAnimation(0, 0f)
            animator.applyCrossFade(0, 0f, 0.5f)
            animator.updateBoneMatrices()
            animator.resetBoneMatrices()
            val count: Int = animator.getAnimationCount()
            val duration: Float = animator.getAnimationDuration(0)
            val name: String? = animator.getAnimationName(0)
        }
        assertTrue(true, "Animator API signatures resolved successfully.")
    }
}
