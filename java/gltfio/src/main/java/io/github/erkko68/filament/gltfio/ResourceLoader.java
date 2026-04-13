package io.github.erkko68.filament.gltfio;

import io.github.erkko68.filament.Engine;
import java.nio.Buffer;

public class ResourceLoader {
    private final long mNativeObject;
    private final long mNativeStbProvider;
    private final long mNativeKtx2Provider;

    public ResourceLoader(Engine engine) {
        this(engine, false);
    }

    public ResourceLoader(Engine engine, boolean normalizeSkinningWeights) {
        long nativeEngine = engine.getNativeObject();
        mNativeObject = nCreateResourceLoader(nativeEngine, normalizeSkinningWeights);
        mNativeStbProvider = nCreateStbProvider(nativeEngine);
        mNativeKtx2Provider = nCreateKtx2Provider(nativeEngine);

        nAddTextureProvider(mNativeObject, "image/jpeg", mNativeStbProvider);
        nAddTextureProvider(mNativeObject, "image/png", mNativeStbProvider);
        nAddTextureProvider(mNativeObject, "image/ktx2", mNativeKtx2Provider);
    }

    public void destroy() {
        nDestroyResourceLoader(mNativeObject);
        nDestroyTextureProvider(mNativeStbProvider);
        nDestroyTextureProvider(mNativeKtx2Provider);
    }

    public ResourceLoader addResourceData(String uri, Buffer buffer) {
        nAddResourceData(mNativeObject, uri, buffer, buffer.remaining());
        return this;
    }

    public boolean hasResourceData(String uri) {
        return nHasResourceData(mNativeObject, uri);
    }

    public void evictResourceData() {
        nEvictResourceData(mNativeObject);
    }

    public ResourceLoader loadResources(FilamentAsset asset) {
        nLoadResources(mNativeObject, asset.getNativeObject());
        return this;
    }

    public boolean asyncBeginLoad(FilamentAsset asset) {
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
    private static native void nAddTextureProvider(long nativeLoader, String url, long nativeProvider);
    private static native void nDestroyTextureProvider(long nativeProvider);
}
