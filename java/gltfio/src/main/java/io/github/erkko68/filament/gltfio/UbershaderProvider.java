package io.github.erkko68.filament.gltfio;

import io.github.erkko68.filament.Engine;
import io.github.erkko68.filament.Material;
import io.github.erkko68.filament.MaterialInstance;

public class UbershaderProvider implements MaterialProvider {
    private long mNativeObject;

    public UbershaderProvider(Engine engine) {
        mNativeObject = nCreateUbershaderProvider(engine.getNativeObject());
    }

    @Override
    public MaterialInstance createMaterialInstance(MaterialKey config, int[] uvmap, String label, String extras) {
        long nativeInstance = nCreateMaterialInstance(mNativeObject, config, uvmap, label, extras);
        return nativeInstance != 0 ? new MaterialInstance(nativeInstance) : null;
    }

    @Override
    public Material getMaterial(MaterialKey config, int[] uvmap, String label) {
        long nativeMaterial = nGetMaterial(mNativeObject, config, uvmap, label);
        return nativeMaterial != 0 ? new Material(nativeMaterial) : null;
    }

    @Override
    public Material[] getMaterials() {
        long[] nativeMaterials = new long[nGetMaterialCount(mNativeObject)];
        nGetMaterials(mNativeObject, nativeMaterials);
        Material[] materials = new Material[nativeMaterials.length];
        for (int i = 0; i < nativeMaterials.length; i++) {
            materials[i] = new Material(nativeMaterials[i]);
        }
        return materials;
    }

    @Override
    public boolean needsDummyData(int attrib) {
        return nNeedsDummyData(mNativeObject, attrib);
    }

    @Override
    public void destroyMaterials() {
        nDestroyMaterials(mNativeObject);
    }

    @Override
    public void destroy() {
        nDestroy(mNativeObject);
        mNativeObject = 0;
    }

    public long getNativeObject() {
        return mNativeObject;
    }

    private static native long nCreateUbershaderProvider(long nativeEngine);
    private static native long nCreateMaterialInstance(long nativeProvider, MaterialKey config, int[] uvmap, String label, String extras);
    private static native long nGetMaterial(long nativeProvider, MaterialKey config, int[] uvmap, String label);
    private static native int nGetMaterialCount(long nativeProvider);
    private static native void nGetMaterials(long nativeProvider, long[] result);
    private static native boolean nNeedsDummyData(long nativeProvider, int attrib);
    private static native void nDestroyMaterials(long nativeProvider);
    private static native void nDestroy(long nativeProvider);
}
