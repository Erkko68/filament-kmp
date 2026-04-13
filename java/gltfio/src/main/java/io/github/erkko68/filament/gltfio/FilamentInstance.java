package io.github.erkko68.filament.gltfio;

import io.github.erkko68.filament.Engine;
import io.github.erkko68.filament.MaterialInstance;

public class FilamentInstance {
    private FilamentAsset mAsset;
    private long mNativeObject;
    private Animator mAnimator;

    FilamentInstance(FilamentAsset asset, long nativeObject) {
        mAsset = asset;
        mNativeObject = nativeObject;
        mAnimator = null;
    }

    long getNativeObject() {
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    public FilamentAsset getAsset() {
        return mAsset;
    }

    public int getRoot() {
        return nGetRoot(mNativeObject);
    }

    public int[] getEntities() {
        int[] result = new int[nGetEntityCount(mNativeObject)];
        nGetEntities(mNativeObject, result);
        return result;
    }

    public Animator getAnimator() {
        if (mAnimator != null) {
            return mAnimator;
        }
        mAnimator = new Animator(nGetAnimator(mNativeObject));
        return mAnimator;
    }

    public int getSkinCount() {
        return nGetSkinCount(getNativeObject());
    }

    public String[] getSkinNames() {
        String[] result = new String[getSkinCount()];
        nGetSkinNames(getNativeObject(), result);
        return result;
    }

    public void attachSkin(int skinIndex, int target) {
        nAttachSkin(getNativeObject(), skinIndex, target);
    }

    public void detachSkin(int skinIndex, int target) {
        nDetachSkin(getNativeObject(), skinIndex, target);
    }

    public int getJointCountAt(int skinIndex) {
        return nGetJointCountAt(getNativeObject(), skinIndex);
    }

    public int[] getJointsAt(int skinIndex) {
        int[] result = new int[getJointCountAt(skinIndex)];
        nGetJointsAt(getNativeObject(), skinIndex, result);
        return result;
    }

    public void applyMaterialVariant(int variantIndex) {
        nApplyMaterialVariant(mNativeObject, variantIndex);
    }

    public MaterialInstance[] getMaterialInstances() {
        final int count = nGetMaterialInstanceCount(mNativeObject);
        MaterialInstance[] result = new MaterialInstance[count];
        long[] natives = new long[count];
        nGetMaterialInstances(mNativeObject, natives);
        for (int i = 0; i < count; i++) {
            result[i] = new MaterialInstance(natives[i]);
        }
        return result;
    }

    public String[] getMaterialVariantNames() {
        String[] names = new String[nGetMaterialVariantCount(mNativeObject)];
        nGetMaterialVariantNames(mNativeObject, names);
        return names;
    }

    private static native int nGetRoot(long nativeInstance);
    private static native int nGetEntityCount(long nativeInstance);
    private static native void nGetEntities(long nativeInstance, int[] result);
    private static native long nGetAnimator(long nativeInstance);
    private static native int nGetMaterialInstanceCount(long nativeAsset);
    private static native void nGetMaterialInstances(long nativeAsset, long[] nativeResults);
    private static native void nApplyMaterialVariant(long nativeInstance, int variantIndex);
    private static native int nGetMaterialVariantCount(long nativeAsset);
    private static native void nGetMaterialVariantNames(long nativeAsset, String[] result);
    private static native void nGetJointsAt(long nativeInstance, int skinIndex, int[] result);
    private static native int nGetSkinCount(long nativeInstance);
    private static native void nGetSkinNames(long nativeInstance, String[] result);
    private static native int nGetJointCountAt(long nativeInstance, int skinIndex);
    private static native void nAttachSkin(long nativeInstance, int skinIndex, int entity);
    private static native void nDetachSkin(long nativeInstance, int skinIndex, int entity);
}
