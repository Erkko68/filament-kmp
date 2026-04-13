package io.github.erkko68.filament.gltfio;

import io.github.erkko68.filament.Engine;
import io.github.erkko68.filament.Material;
import io.github.erkko68.filament.MaterialInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Generates materials at run time using the filamat library.
 *
 * <p>This provider produces streamlined shaders but requires filamat to be linked in.</p>
 */
public class JitShaderProvider implements MaterialProvider {
    private long mNativeObject;

    public JitShaderProvider(@NotNull Engine engine) {
        mNativeObject = nCreateJitShaderProvider(engine.getNativeObject());
    }

    @Override
    public void destroy() {
        nDestroyJitShaderProvider(mNativeObject);
        mNativeObject = 0;
    }

    @Override
    @Nullable
    public MaterialInstance createMaterialInstance(MaterialKey config, @NotNull int[] uvmap, @Nullable String label, @Nullable String extras) {
        long nativeMaterialInstance = nCreateMaterialInstance(mNativeObject, config, uvmap, label, extras);
        return nativeMaterialInstance == 0 ? null : new MaterialInstance(nativeMaterialInstance);
    }

    @Override
    @Nullable
    public Material getMaterial(MaterialKey config, @NotNull int[] uvmap, @Nullable String label) {
        long nativeMaterial = nGetMaterial(mNativeObject, config, uvmap, label);
        return nativeMaterial == 0 ? null : new Material(nativeMaterial);
    }

    @Override
    @NotNull
    public Material[] getMaterials() {
        int count = nGetMaterialCount(mNativeObject);
        long[] natives = new long[count];
        nGetMaterials(mNativeObject, natives);
        Material[] result = new Material[count];
        for (int i = 0; i < count; i++) {
            result[i] = new Material(natives[i]);
        }
        return result;
    }

    @Override
    public void destroyMaterials() {
        nDestroyMaterials(mNativeObject);
    }

    @Override
    public boolean needsDummyData(int attrib) {
        return nNeedsDummyData(mNativeObject, attrib);
    }

    @Override
    public long getNativeObject() {
        return mNativeObject;
    }

    private static native long nCreateJitShaderProvider(long nativeEngine);
    private static native void nDestroyJitShaderProvider(long nativeProvider);
    private static native void nDestroyMaterials(long nativeProvider);
    private static native long nCreateMaterialInstance(long nativeProvider, MaterialKey config, int[] uvmap, String label, String extras);
    private static native long nGetMaterial(long nativeProvider, MaterialKey config, int[] uvmap, String label);
    private static native int nGetMaterialCount(long nativeProvider);
    private static native void nGetMaterials(long nativeProvider, long[] result);
    private static native boolean nNeedsDummyData(long nativeProvider, int attrib);
}
