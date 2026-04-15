package io.github.erkko68.filament.gltfio.jni;

import io.github.erkko68.filament.jni.Engine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;

public class ResourceLoader {
    private final long mNativeObject;
    private final long mNativeStbProvider;
    private final long mNativeKtx2Provider;
    private final long mNativeWebpProvider;

    public ResourceLoader(@NotNull Engine engine) {
        this(engine, false);
    }

    public ResourceLoader(@NotNull Engine engine, boolean normalizeSkinningWeights) {
        long nativeEngine = engine.getNativeObject();
        mNativeObject = nCreateResourceLoader(nativeEngine, normalizeSkinningWeights);
        mNativeStbProvider = nCreateStbProvider(nativeEngine);
        mNativeKtx2Provider = nCreateKtx2Provider(nativeEngine);

        nAddTextureProvider(mNativeObject, "image/jpeg", mNativeStbProvider);
        nAddTextureProvider(mNativeObject, "image/png", mNativeStbProvider);
        nAddTextureProvider(mNativeObject, "image/ktx2", mNativeKtx2Provider);
        if (nIsWebpSupported()) {
            mNativeWebpProvider = nCreateWebpProvider(nativeEngine);
            nAddTextureProvider(mNativeObject, "image/webp", mNativeWebpProvider);
        } else {
            mNativeWebpProvider = 0;
        }
    }

    public void destroy() {
        nDestroyResourceLoader(mNativeObject);
        nDestroyTextureProvider(mNativeStbProvider);
        nDestroyTextureProvider(mNativeKtx2Provider);
        if (nIsWebpSupported()) {
            nDestroyTextureProvider(mNativeWebpProvider);
        }
    }

    @NotNull
    public ResourceLoader addResourceData(@NotNull String uri, @NotNull Buffer buffer) {
        nAddResourceData(mNativeObject, uri, buffer, buffer.remaining());
        return this;
    }

    public boolean hasResourceData(@NotNull String uri) {
        return nHasResourceData(mNativeObject, uri);
    }

    public void evictResourceData() {
        nEvictResourceData(mNativeObject);
    }

    @NotNull
    public ResourceLoader loadResources(@NotNull FilamentAsset asset) {
        nLoadResources(mNativeObject, asset.getNativeObject());
        return this;
    }

    public boolean asyncBeginLoad(@NotNull FilamentAsset asset) {
        return nAsyncBeginLoad(mNativeObject, asset.getNativeObject());
    }

    public float asyncGetLoadProgress() {
        return nAsyncGetLoadProgress(mNativeObject);
    }

    public void asyncUpdateLoad() {
        nAsyncUpdateLoad(mNativeObject);
    }

    public void asyncCancelLoad() {
        nAsyncCancelLoad(mNativeObject);
    }

    private static native long nCreateResourceLoader(long nativeEngine, boolean normalizeSkinningWeights);
    private static native void nDestroyResourceLoader(long nativeLoader);
    private static native void nAddResourceData(long nativeLoader, String url, Buffer buffer, int remaining);
    private static native void nEvictResourceData(long nativeLoader);
    private static native boolean nHasResourceData(long nativeLoader, String url);
    private static native void nLoadResources(long nativeLoader, long nativeAsset);
    private static native boolean nAsyncBeginLoad(long nativeLoader, long nativeAsset);
    private static native float nAsyncGetLoadProgress(long nativeLoader);
    private static native void nAsyncUpdateLoad(long nativeLoader);
    private static native void nAsyncCancelLoad(long nativeLoader);
    private static native long nCreateStbProvider(long nativeEngine);
    private static native long nCreateKtx2Provider(long nativeEngine);
    private static native boolean nIsWebpSupported();
    private static native long nCreateWebpProvider(long nativeEngine);
    private static native void nAddTextureProvider(long nativeLoader, String url, long nativeProvider);
    private static native void nDestroyTextureProvider(long nativeProvider);
}

