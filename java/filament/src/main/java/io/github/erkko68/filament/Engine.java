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

    public void destroyIndirectLight(IndirectLight indirectLight) {
        nDestroyIndirectLight(getNativeObject(), indirectLight.getNativeObject());
        indirectLight.clearNativeObject();
    }

    public void destroySkybox(Skybox skybox) {
        nDestroySkybox(getNativeObject(), skybox.getNativeObject());
        skybox.clearNativeObject();
    }

    public void destroyColorGrading(ColorGrading colorGrading) {
        nDestroyColorGrading(getNativeObject(), colorGrading.getNativeObject());
        colorGrading.clearNativeObject();
    }

    public void destroyBufferObject(BufferObject bufferObject) {
        nDestroyBufferObject(getNativeObject(), bufferObject.getNativeObject());
        bufferObject.clearNativeObject();
    }

    public void destroyRenderTarget(RenderTarget renderTarget) {
        nDestroyRenderTarget(getNativeObject(), renderTarget.getNativeObject());
        renderTarget.clearNativeObject();
    }

    public void destroyStream(Stream stream) {
        nDestroyStream(getNativeObject(), stream.getNativeObject());
        stream.clearNativeObject();
    }

    public void destroyMorphTargetBuffer(MorphTargetBuffer buffer) {
        nDestroyMorphTargetBuffer(getNativeObject(), buffer.getNativeObject());
        buffer.clearNativeObject();
    }

    public void destroySkinningBuffer(SkinningBuffer buffer) {
        nDestroySkinningBuffer(getNativeObject(), buffer.getNativeObject());
        buffer.clearNativeObject();
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
    private static native void nDestroyIndirectLight(long nativeEngine, long nativeIndirectLight);
    private static native void nDestroySkybox(long nativeEngine, long nativeSkybox);
    private static native void nDestroyColorGrading(long nativeEngine, long nativeColorGrading);
    private static native void nDestroyBufferObject(long nativeEngine, long nativeBufferObject);
    private static native void nDestroyRenderTarget(long nativeEngine, long nativeRenderTarget);
    private static native void nDestroyStream(long nativeEngine, long nativeStream);
    private static native void nDestroyMorphTargetBuffer(long nativeEngine, long nativeBuffer);
    private static native void nDestroySkinningBuffer(long nativeEngine, long nativeBuffer);
    private static native void nDestroyEntity(long nativeEngine, int entity);

    private static native long nGetRenderableManager(long nativeEngine);
    private static native long nGetLightManager(long nativeEngine);
    private static native long nGetTransformManager(long nativeEngine);
}
