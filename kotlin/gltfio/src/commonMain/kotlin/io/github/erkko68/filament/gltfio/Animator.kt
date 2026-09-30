package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.capi.*
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.InternalFilamentApi

/**
 * Animator updates matrices according to glTF animation and skin definitions.
 *
 * Animator handles two primary tasks:
 * 1. Updating matrices in TransformManager components based on glTF animation definitions
 * 2. Updating bone matrices in RenderableManager components based on glTF skin definitions
 *
 * **Usage pattern for animated skeletal meshes:**
 * ```
 * animator.applyAnimation(currentIndex, time)      // Apply current animation
 * animator.applyCrossFade(previousAnimIndex, previousAnimTime, alpha)  // Blend with previous (optional)
 * animator.updateBoneMatrices()  // Propagate bone transforms to renderables
 * ```
 *
 * @see FilamentInstance
 * @see FilamentAsset
 */
class Animator @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Apply a glTF animation to the transform hierarchy.
     *
     * Applies rotation, translation, and scale to entities targeted by the animation using
     * TransformManager. The animation is sampled at the given time.
     *
     * @param animationIndex Zero-based animation index.
     * @param time Elapsed time in seconds.
     */
    fun applyAnimation(animationIndex: Int, time: Float) {
        FilaGltfioAnimator_applyAnimation(nativeHandle, animationIndex, time)
    }

    /**
     * Cross-fade from a previous animation with alpha blending.
     *
     * Blends transforms from two animations by:
     * 1. Stashing current transform hierarchy
     * 2. Applying the previous animation
     * 3. Lerping stashed transforms with current, then pushing results
     *
     * Useful for smooth animation transitions. For skeletal meshes, typically call in order:
     * applyAnimation() → applyCrossFade() → updateBoneMatrices().
     *
     * @param previousAnimIndex Zero-based index of the previous animation (alpha=0).
     * @param previousAnimTime Elapsed time for previous animation in seconds.
     * @param alpha Blend factor [0, 1]; 0 = previous, 1 = current.
     */
    fun applyCrossFade(previousAnimIndex: Int, previousAnimTime: Float, alpha: Float) {
        FilaGltfioAnimator_applyCrossFade(nativeHandle, previousAnimIndex, previousAnimTime, alpha)
    }

    /**
     * Compute root-to-node transforms for all bone nodes and push to RenderableManager.
     *
     * Updates bone matrices based on the current transform hierarchy. Independent of animations—
     * call after applyAnimation() to propagate skeletal transforms to renderables.
     */
    fun updateBoneMatrices() {
        FilaGltfioAnimator_updateBoneMatrices(nativeHandle)
    }

    /**
     * Reset all bone matrices to identity (T-pose).
     *
     * Independent of animations; useful for returning to the rest pose.
     */
    fun resetBoneMatrices() {
        FilaGltfioAnimator_resetBoneMatrices(nativeHandle)
    }

    /**
     * Get the number of animations in the glTF asset.
     *
     * @return Animation count.
     */
    val animationCount: Int get() = FilaGltfioAnimator_getAnimationCount(nativeHandle)

    /**
     * Get the duration of a glTF animation.
     *
     * @param animationIndex Zero-based animation index.
     * @return Duration in seconds.
     */
    fun getAnimationDuration(animationIndex: Int): Float = FilaGltfioAnimator_getAnimationDuration(nativeHandle, animationIndex)

    /**
     * Get the name of a glTF animation.
     *
     * @param animationIndex Zero-based animation index.
     * @return Animation name, or null if unnamed.
     */
    fun getAnimationName(animationIndex: Int): String? = stringFromInterop(FilaGltfioAnimator_getAnimationName(nativeHandle, animationIndex))
}
