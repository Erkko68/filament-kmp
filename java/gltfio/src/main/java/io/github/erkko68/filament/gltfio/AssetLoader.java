package io.github.erkko68.filament.gltfio;

import io.github.erkko68.filament.Engine;
import io.github.erkko68.filament.EntityManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;

public class AssetLoader {
    private long mNativeObject;
    private Engine mEngine;
    private MaterialProvider mMaterialCache;

    public AssetLoader(@NotNull Engine engine, @NotNull MaterialProvider provider,
            @NotNull EntityManager entities) {
        long nativeEngine = engine.getNativeObject();
        long nativeEntities = entities.getNativeObject();
        mNativeObject = nCreateAssetLoader(nativeEngine, provider, nativeEntities);
        if (mNativeObject == 0) {
            throw new IllegalStateException("Unable to parse glTF asset.");
        }
        mEngine = engine;
        mMaterialCache = provider;
    }

    public void destroy() {
        nDestroyAssetLoader(mNativeObject);
        mNativeObject = 0;
    }

    @Nullable
    public FilamentAsset createAsset(@NotNull Buffer buffer) {
        long nativeAsset = nCreateAsset(mNativeObject, buffer, buffer.remaining());
        return nativeAsset != 0 ? new FilamentAsset(mEngine, nativeAsset) : null;
    }

    @Nullable
    @SuppressWarnings("unused")
    public FilamentAsset createInstancedAsset(@NotNull Buffer buffer,
            @NotNull FilamentInstance[] instances) {
        long[] nativeInstances = new long[instances.length];
        long nativeAsset = nCreateInstancedAsset(mNativeObject, buffer, buffer.remaining(),
                nativeInstances);
        if (nativeAsset == 0) {
            return null;
        }
        FilamentAsset asset = new FilamentAsset(mEngine, nativeAsset);
        for (int i = 0; i < nativeInstances.length; i++) {
            instances[i] = new FilamentInstance(asset, nativeInstances[i]);
        }
        return asset;
    }

    @Nullable
    @SuppressWarnings("unused")
    public FilamentInstance createInstance(@NotNull FilamentAsset asset) {
        long nativeInstance = nCreateInstance(mNativeObject, asset.getNativeObject());
        if (nativeInstance == 0) {
            return null;
        }
        return new FilamentInstance(asset, nativeInstance);
    }

    @SuppressWarnings("unused")
    public void enableDiagnostics(boolean enable) {
        nEnableDiagnostics(mNativeObject, enable);
    }

    public void destroyAsset(@NotNull FilamentAsset asset) {
        nDestroyAsset(mNativeObject, asset.getNativeObject());
        asset.clearNativeObject();
    }

    private static native long nCreateAssetLoader(long nativeEngine, Object provider,
            long nativeEntities);
    private static native void nDestroyAssetLoader(long nativeLoader);
    private static native long nCreateAsset(long nativeLoader, Buffer buffer, int remaining);
    private static native long nCreateInstancedAsset(long nativeLoader, Buffer buffer, int remaining,
            long[] nativeInstances);
    private static native long nCreateInstance(long nativeLoader, long nativeAsset);
    private static native void nEnableDiagnostics(long nativeLoader, boolean enable);
    private static native void nDestroyAsset(long nativeLoader, long nativeAsset);
}


