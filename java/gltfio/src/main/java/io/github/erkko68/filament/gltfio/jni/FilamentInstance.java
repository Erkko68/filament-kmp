package io.github.erkko68.filament.gltfio.jni;

import io.github.erkko68.filament.jni.MaterialInstance;
import io.github.erkko68.filament.jni.Engine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FilamentInstance {
    private FilamentAsset mAsset;
    private long mNativeObject;
    private Animator mAnimator;

    public FilamentInstance(@NotNull FilamentAsset asset, long nativeInstance) {
        mAsset = asset;
        mNativeObject = nativeInstance;
    }

    public long getNativeObject() {
        return mNativeObject;
    }

    public void clearNativeObject() {
        mNativeObject = 0;
    }

    @NotNull
    public FilamentAsset getAsset() {
        return mAsset;
    }

    public int getRoot() {
        return nGetRoot(mNativeObject);
    }

    @NotNull
    public int[] getEntities() {
        int[] result = new int[nGetEntityCount(mNativeObject)];
        nGetEntities(mNativeObject, result);
        return result;
    }

    @NotNull
    public Animator getAnimator() {
        if (mAnimator == null) {
            mAnimator = new Animator(nGetAnimator(mNativeObject));
        }
        return mAnimator;
    }

    public int getSkinCount() {
        return nGetSkinCount(getNativeObject());
    }

    @NotNull
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

    @NotNull
    public int[] getJointsAt(int skinIndex) {
        int[] result = new int[getJointCountAt(skinIndex)];
        nGetJointsAt(getNativeObject(), skinIndex, result);
        return result;
    }

    public void applyMaterialVariant(int variantIndex) {
        nApplyMaterialVariant(mNativeObject, variantIndex);
    }

    @NotNull
    public MaterialInstance[] getMaterialInstances() {
        final int count = nGetMaterialInstanceCount(mNativeObject);
        MaterialInstance[] result = new MaterialInstance[count];
        long[] natives = new long[count];
        nGetMaterialInstances(mNativeObject, natives);
        Engine engine = mAsset.getEngine();
        for (int i = 0; i < count; i++) {
            result[i] = new MaterialInstance(engine, natives[i]);
        }
        return result;
    }

    @NotNull
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
