package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NativeRegistry;
import java.lang.ref.Cleaner;

public class Engine {
    private long mNativeObject;
    private final Cleaner.Cleanable mCleanable;

    private Engine(long nativeEngine) {
        mNativeObject = nativeEngine;
        mCleanable = NativeRegistry.registerForCleanup(this, new EngineCleanup(mNativeObject));
    }

    public static Engine create() {
        long nativeEngine = nCreateEngine(0, 0);
        if (nativeEngine == 0) throw new IllegalStateException("Couldn't create Engine");
        return new Engine(nativeEngine);
    }

    public void destroy() {
        mCleanable.clean();
        mNativeObject = 0;
    }

    public Renderer createRenderer() {
        return new Renderer(nCreateRenderer(getNativeObject()));
    }

    public void destroyRenderer(Renderer renderer) {
        nDestroyRenderer(getNativeObject(), renderer.getNativeObject());
        renderer.clearNativeObject();
    }

    public SwapChain createSwapChain(int width, int height, long flags) {
        return new SwapChain(nCreateSwapChain(getNativeObject(), width, height, flags));
    }

    public void destroySwapChain(SwapChain swapChain) {
        nDestroySwapChain(getNativeObject(), swapChain.getNativeObject());
        swapChain.clearNativeObject();
    }

    public View createView() {
        return new View(nCreateView(getNativeObject()));
    }

    public void destroyView(View view) {
        nDestroyView(getNativeObject(), view.getNativeObject());
        view.clearNativeObject();
    }

    public Scene createScene() {
        return new Scene(nCreateScene(getNativeObject()));
    }

    public void destroyScene(Scene scene) {
        nDestroyScene(getNativeObject(), scene.getNativeObject());
        scene.clearNativeObject();
    }

    public Camera createCamera() {
        return new Camera(nCreateCamera(getNativeObject()));
    }

    public void destroyCamera(Camera camera) {
        nDestroyCamera(getNativeObject(), camera.getNativeObject());
        camera.clearNativeObject();
    }

    public void destroyVertexBuffer(VertexBuffer vb) {
        nDestroyVertexBuffer(getNativeObject(), vb.getNativeObject());
        vb.clearNativeObject();
    }

    public void destroyIndexBuffer(IndexBuffer ib) {
        nDestroyIndexBuffer(getNativeObject(), ib.getNativeObject());
        ib.clearNativeObject();
    }

    public void destroyMaterial(Material material) {
        nDestroyMaterial(getNativeObject(), material.getNativeObject());
        material.clearNativeObject();
    }

    public void destroyTexture(Texture texture) {
        nDestroyTexture(getNativeObject(), texture.getNativeObject());
        texture.clearNativeObject();
    }

    public void destroyEntity(int entity) {
        nDestroyEntity(getNativeObject(), entity);
    }

    public RenderableManager getRenderableManager() {
        return new RenderableManager(nGetRenderableManager(getNativeObject()));
    }

    public LightManager getLightManager() {
        return new LightManager(nGetLightManager(getNativeObject()));
    }

    public TransformManager getTransformManager() {
        return new TransformManager(nGetTransformManager(getNativeObject()));
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed Engine");
        }
        return mNativeObject;
    }

    private static class EngineCleanup implements Runnable {
        private final long mNativeObject;
        EngineCleanup(long nativeObject) { mNativeObject = nativeObject; }
        @Override public void run() { nDestroyEngine(mNativeObject); }
    }

    private static native long nCreateEngine(long nativeBackend, long nativeSharedContext);
    private static native void nDestroyEngine(long nativeEngine);
    private static native long nCreateRenderer(long nativeEngine);
    private static native void nDestroyRenderer(long nativeEngine, long nativeRenderer);
    private static native long nCreateSwapChain(long nativeEngine, int width, int height, long flags);
    private static native void nDestroySwapChain(long nativeEngine, long nativeSwapChain);
    private static native long nCreateView(long nativeEngine);
    private static native void nDestroyView(long nativeEngine, long nativeView);
    private static native long nCreateScene(long nativeEngine);
    private static native void nDestroyScene(long nativeEngine, long nativeScene);
    private static native long nCreateCamera(long nativeEngine);
    private static native void nDestroyCamera(long nativeEngine, long nativeCamera);
    private static native void nDestroyVertexBuffer(long nativeEngine, long nativeVertexBuffer);
    private static native void nDestroyIndexBuffer(long nativeEngine, long nativeIndexBuffer);
    private static native void nDestroyMaterial(long nativeEngine, long nativeMaterial);
    private static native void nDestroyTexture(long nativeEngine, long nativeTexture);
    private static native void nDestroyEntity(long nativeEngine, int entity);

    private static native long nGetRenderableManager(long nativeEngine);
    private static native long nGetLightManager(long nativeEngine);
    private static native long nGetTransformManager(long nativeEngine);
}
