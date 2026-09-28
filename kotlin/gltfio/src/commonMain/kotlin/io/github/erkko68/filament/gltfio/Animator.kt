package io.github.erkko68.filament.gltfio

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
 * animator.applyCrossFade(previousIndex, prevTime, alpha)  // Blend with previous (optional)
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
     * @param index Zero-based animation index.
     * @param time Elapsed time in seconds.
     */
    fun applyAnimation(index: Int, time: Float) {
        FilaAnimator_applyAnimation(nativeHandle, index, time)
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
     * @param previousIndex Zero-based index of the previous animation (alpha=0).
     * @param previousTime Elapsed time for previous animation in seconds.
     * @param alpha Blend factor [0, 1]; 0 = previous, 1 = current.
     */
    fun applyCrossFade(previousIndex: Int, previousTime: Float, alpha: Float) {
        FilaAnimator_applyCrossFade(nativeHandle, previousIndex, previousTime, alpha)
    }

    /**
     * Compute root-to-node transforms for all bone nodes and push to RenderableManager.
     *
     * Updates bone matrices based on the current transform hierarchy. Independent of animations—
     * call after applyAnimation() to propagate skeletal transforms to renderables.
     */
    fun updateBoneMatrices() {
        FilaAnimator_updateBoneMatrices(nativeHandle)
    }

    /**
     * Reset all bone matrices to identity (T-pose).
     *
     * Independent of animations; useful for returning to the rest pose.
     */
    fun resetBoneMatrices() {
        FilaAnimator_resetBoneMatrices(nativeHandle)
    }

    /**
     * Get the number of animations in the glTF asset.
     *
     * @return Animation count.
     */
    val animationCount: Int get() = FilaAnimator_getAnimationCount(nativeHandle)

    /**
     * Get the duration of a glTF animation.
     *
     * @param index Zero-based animation index.
     * @return Duration in seconds.
     */
    fun getAnimationDuration(index: Int): Float = FilaAnimator_getAnimationDuration(nativeHandle, index)

    /**
     * Get the name of a glTF animation.
     *
     * @param index Zero-based animation index.
     * @return Animation name, or null if unnamed.
     */
    fun getAnimationName(index: Int): String? = stringFromInterop(FilaAnimator_getAnimationName(nativeHandle, index))
}

@ExternalSymbolName("FilaAnimator_applyAnimation")
private external fun FilaAnimator_applyAnimation(animator: NativePointer, animationIndex: Int, time: Float)

@ExternalSymbolName("FilaAnimator_applyCrossFade")
private external fun FilaAnimator_applyCrossFade(animator: NativePointer, previousAnimationIndex: Int, previousAnimationTime: Float, alpha: Float)

@ExternalSymbolName("FilaAnimator_updateBoneMatrices")
private external fun FilaAnimator_updateBoneMatrices(animator: NativePointer)

@ExternalSymbolName("FilaAnimator_resetBoneMatrices")
private external fun FilaAnimator_resetBoneMatrices(animator: NativePointer)

@ExternalSymbolName("FilaAnimator_getAnimationCount")
private external fun FilaAnimator_getAnimationCount(animator: NativePointer): Int

@ExternalSymbolName("FilaAnimator_getAnimationDuration")
private external fun FilaAnimator_getAnimationDuration(animator: NativePointer, animationIndex: Int): Float

@ExternalSymbolName("FilaAnimator_getAnimationName")
private external fun FilaAnimator_getAnimationName(animator: NativePointer, animationIndex: Int): NativePointer
