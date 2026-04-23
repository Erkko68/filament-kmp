package io.github.erkko68.filament.gltfio.jni;

import org.jetbrains.annotations.NotNull;

public class Animator {
    private long mNativeObject;

    public Animator(long nativeObject) {
        mNativeObject = nativeObject;
    }

    public void applyAnimation(int animationIndex, float time) {
        nApplyAnimation(getNativeObject(), animationIndex, time);
    }

    public void updateBoneMatrices() {
        nUpdateBoneMatrices(getNativeObject());
    }

    public void applyCrossFade(int previousAnimIndex, float previousAnimTime, float alpha) {
        nApplyCrossFade(getNativeObject(), previousAnimIndex, previousAnimTime, alpha);
    }

    public void resetBoneMatrices() {
        nResetBoneMatrices(getNativeObject());
    }

    public int getAnimationCount() {
        return nGetAnimationCount(getNativeObject());
    }

    public float getAnimationDuration(int animationIndex) {
        return nGetAnimationDuration(getNativeObject(), animationIndex);
    }

    @NotNull
    public String getAnimationName(int animationIndex) {
        return nGetAnimationName(getNativeObject(), animationIndex);
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Using Animator on destroyed asset");
        }
        return mNativeObject;
    }

    public void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native void nApplyAnimation(long nativeAnimator, int index, float time);
    private static native void nUpdateBoneMatrices(long nativeAnimator);
    private static native void nApplyCrossFade(long nativeAnimator, int animIndex, float animTime, float alpha);
    private static native void nResetBoneMatrices(long nativeAnimator);
    private static native int nGetAnimationCount(long nativeAnimator);
    private static native float nGetAnimationDuration(long nativeAnimator, int index);
    private static native String nGetAnimationName(long nativeAnimator, int index);
}
