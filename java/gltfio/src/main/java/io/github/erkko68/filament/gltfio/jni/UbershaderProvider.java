package io.github.erkko68.filament.gltfio.jni;

import io.github.erkko68.filament.jni.Engine;
import io.github.erkko68.filament.jni.Material;
import io.github.erkko68.filament.jni.MaterialInstance;
import io.github.erkko68.filament.jni.VertexBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UbershaderProvider implements MaterialProvider {
    private static final VertexBuffer.VertexAttribute[] sVertexAttributesValues =
            VertexBuffer.VertexAttribute.values();

    private long mNativeObject;

    public UbershaderProvider(@NotNull Engine engine) {
        mNativeObject = nCreateUbershaderProvider(engine.getNativeObject());
    }

    public void destroy() {
        nDestroyUbershaderProvider(mNativeObject);
        mNativeObject = 0;
    }

    @Override
    @Nullable
    public MaterialInstance createMaterialInstance(MaterialKey config, @NotNull int[] uvmap, @Nullable String label, @Nullable String extras) {
        long nativeInstance = nCreateMaterialInstance(mNativeObject, config, uvmap, label, extras);
        return nativeInstance != 0 ? new MaterialInstance(nativeInstance) : null;
    }


    @Override
    @Nullable
    public Material getMaterial(MaterialKey config, @NotNull int[] uvmap, @Nullable String label) {
        long nativeMaterial = nGetMaterial(mNativeObject, config, uvmap, label);
        return nativeMaterial != 0 ? new Material(nativeMaterial) : null;
    }

    @Override
    @NotNull
    public Material[] getMaterials() {
        final int count = nGetMaterialCount(mNativeObject);
        long[] natives = new long[count];
        nGetMaterials(mNativeObject, natives);
        // ArchiveCache pre-allocates a slot per ubershader spec but populates them
        // lazily as getMaterial() is called, so unpopulated slots come back as 0.
        // Drop them — wrapping a null pointer in Material would crash in its ctor.
        int populated = 0;
        for (int i = 0; i < count; i++) if (natives[i] != 0) populated++;
        Material[] result = new Material[populated];
        int j = 0;
        for (int i = 0; i < count; i++) {
            if (natives[i] != 0) result[j++] = new Material(natives[i]);
        }
        return result;
    }

    @Override
    public boolean needsDummyData(int attrib) {
        VertexBuffer.VertexAttribute vattrib = sVertexAttributesValues[attrib];
        switch (vattrib) {
            case UV0:
            case UV1:
            case COLOR:
                return true;
            default:
                return false;
        }
    }

    public void destroyMaterials() {
        nDestroyMaterials(mNativeObject);
    }

    public long getNativeObject() {
        return mNativeObject;
    }

    private static native long nCreateUbershaderProvider(long nativeEngine);
    private static native void nDestroyUbershaderProvider(long nativeProvider);
    private static native void nDestroyMaterials(long nativeProvider);
    private static native long nCreateMaterialInstance(long nativeProvider, MaterialKey config, int[] uvmap, String label, String extras);
    private static native long nGetMaterial(long nativeProvider, MaterialKey config, int[] uvmap, String label);
    private static native int nGetMaterialCount(long nativeProvider);
    private static native void nGetMaterials(long nativeProvider, long[] result);
}

