package io.github.erkko68.filament.gltfio.jni;

import io.github.erkko68.filament.jni.Box;
import io.github.erkko68.filament.jni.Engine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FilamentAsset {
    private long mNativeObject;
    private FilamentInstance mPrimaryInstance;
    private Engine mEngine;
    private Animator mAnimator;

    public FilamentAsset(@NotNull Engine engine, long nativeAsset) {
        mEngine = engine;
        mNativeObject = nativeAsset;
    }

    public FilamentInstance getInstance() {
        if (mPrimaryInstance != null) {
            return mPrimaryInstance;
        }
        long nativeInstance = nGetInstance(getNativeObject());
        mPrimaryInstance = new FilamentInstance(this, nativeInstance);
        return mPrimaryInstance;
    }

    public long getNativeObject() {
        return mNativeObject;
    }

    public int getRoot() {
        return nGetRoot(mNativeObject);
    }

    public int popRenderable() {
        return nPopRenderable(mNativeObject);
    }

    public int popRenderables(@Nullable int[] entities) {
        return nPopRenderables(mNativeObject, entities);
    }

    @NotNull
    public int[] getEntities() {
        int[] result = new int[nGetEntityCount(mNativeObject)];
        nGetEntities(mNativeObject, result);
        return result;
    }

    @NotNull
    public int[] getLightEntities() {
        int[] result = new int[nGetLightEntityCount(mNativeObject)];
        nGetLightEntities(mNativeObject, result);
        return result;
    }

    @NotNull
    public int[] getRenderableEntities() {
        int[] result = new int[nGetRenderableEntityCount(mNativeObject)];
        nGetRenderableEntities(mNativeObject, result);
        return result;
    }

    @NotNull
    public int[] getCameraEntities() {
        int[] result = new int[nGetCameraEntityCount(mNativeObject)];
        nGetCameraEntities(mNativeObject, result);
        return result;
    }

    public int getFirstEntityByName(String name) {
        return nGetFirstEntityByName(mNativeObject, name);
    }

    @NotNull
    public int[] getEntitiesByName(String name) {
        int[] result = new int[nGetEntitiesByName(mNativeObject, name, null)];
        nGetEntitiesByName(mNativeObject, name, result);
        return result;
    }

    @NotNull
    public int[] getEntitiesByPrefix(String prefix) {
        int[] result = new int[nGetEntitiesByPrefix(mNativeObject, prefix, null)];
        nGetEntitiesByPrefix(mNativeObject, prefix, result);
        return result;
    }

    @NotNull
    public Box getBoundingBox() {
        float[] box = new float[6];
        nGetBoundingBox(mNativeObject, box);
        return new Box(box[0], box[1], box[2], box[3], box[4], box[5]);
    }

    @Nullable
    public String getName(int entity) {
        return nGetName(getNativeObject(), entity);
    }

    @Nullable
    public String getExtras(int entity) {
        return nGetExtras(mNativeObject, entity);
    }

    @NotNull
    public String[] getMorphTargetNames(int entity) {
        String[] names = new String[nGetMorphTargetCount(mNativeObject, entity)];
        nGetMorphTargetNames(mNativeObject, entity, names);
        return names;
    }

    @NotNull
    public String[] getResourceUris() {
        int count = nGetResourceUriCount(mNativeObject);
        String[] uris = new String[count];
        nGetResourceUris(mNativeObject, uris);
        return uris;
    }

    public void releaseSourceData() {
        nReleaseSourceData(mNativeObject);
    }

    public Engine getEngine() { return mEngine; }

    public void clearNativeObject() {
        mPrimaryInstance = null;
        mNativeObject = 0;
        if (mAnimator != null) {
            mAnimator.clearNativeObject();
        }
    }

    private static native int nGetRoot(long nativeAsset);
    private static native int nPopRenderable(long nativeAsset);
    private static native int nPopRenderables(long nativeAsset, int[] result);
    private static native int nGetEntityCount(long nativeAsset);
    private static native void nGetEntities(long nativeAsset, int[] result);
    private static native int nGetFirstEntityByName(long nativeAsset, String name);
    private static native int nGetEntitiesByName(long nativeAsset, String name, int[] result);
    private static native int nGetEntitiesByPrefix(long nativeAsset, String prefix, int[] result);
    private static native int nGetLightEntityCount(long nativeAsset);
    private static native void nGetLightEntities(long nativeAsset, int[] result);
    private static native int nGetRenderableEntityCount(long nativeAsset);
    private static native void nGetRenderableEntities(long nativeAsset, int[] result);
    private static native int nGetCameraEntityCount(long nativeAsset);
    private static native void nGetCameraEntities(long nativeAsset, int[] result);
    private static native int nGetMorphTargetCount(long nativeAsset, int entity);
    private static native void nGetMorphTargetNames(long nativeAsset, int entity, String[] result);
    private static native void nGetBoundingBox(long nativeAsset, float[] box);
    private static native String nGetName(long nativeAsset, int entity);
    private static native String nGetExtras(long nativeAsset, int entity);
    private static native long nGetInstance(long nativeAsset);
    private static native int nGetResourceUriCount(long nativeAsset);
    private static native void nGetResourceUris(long nativeAsset, String[] result);
    private static native void nReleaseSourceData(long nativeAsset);
}
