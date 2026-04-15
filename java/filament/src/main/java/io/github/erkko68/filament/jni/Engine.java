/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.erkko68.filament.jni;

import io.github.erkko68.filament.jni.internal.NativeRegistry;
import io.github.erkko68.filament.jni.proguard.UsedByReflection;
import java.lang.ref.Cleaner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Engine is filament's main entry-point.
 */
public class Engine {
    private static final Backend[] sBackendValues = Backend.values();
    private static final FeatureLevel[] sFeatureLevelValues = FeatureLevel.values();

    private long mNativeObject;
    private final Cleaner.Cleanable mCleanable;
    private Config mConfig;

    @NotNull private final TransformManager mTransformManager;
    @NotNull private final LightManager mLightManager;
    @NotNull private final RenderableManager mRenderableManager;
    @NotNull private final EntityManager mEntityManager;

    public enum Backend {
        DEFAULT,
        OPENGL,
        VULKAN,
        METAL,
        WEBGPU,
        NOOP,
    }

    public enum FeatureLevel {
        FEATURE_LEVEL_0,
        FEATURE_LEVEL_1,
        FEATURE_LEVEL_2,
        FEATURE_LEVEL_3,
    }

    public enum StereoscopicType {
        NONE,
        INSTANCED,
        MULTIVIEW,
    }

    public enum GpuContextPriority {
        DEFAULT,
        LOW,
        MEDIUM,
        HIGH,
        REALTIME,
    }

    public enum FeatureState {
        FALSE,
        TRUE,
        INDETERMINATE
    }

    public static class Builder {
        private final long mNativeBuilder;
        private Config mConfig;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeBuilder;
            BuilderCleanup(long nativeBuilder) { mNativeBuilder = nativeBuilder; }
            @Override public void run() { nDestroyBuilder(mNativeBuilder); }
        }

        public Builder backend(Backend backend) {
            nSetBuilderBackend(mNativeBuilder, backend.ordinal());
            return this;
        }

        public Builder sharedContext(Object sharedContext) {
            // For KMP we might not have a Platform class yet, so we pass the object directly to JNI
            nSetBuilderSharedContext(mNativeBuilder, sharedContext);
            return this;
        }

        public Builder config(Config config) {
            mConfig = config;
            nSetBuilderConfig(mNativeBuilder, config.commandBufferSizeMB,
                    config.perRenderPassArenaSizeMB, config.driverHandleArenaSizeMB,
                    config.minCommandBufferSizeMB, config.perFrameCommandsSizeMB,
                    config.jobSystemThreadCount, config.disableParallelShaderCompile,
                    config.stereoscopicType.ordinal(), config.stereoscopicEyeCount,
                    config.resourceAllocatorCacheSizeMB, config.resourceAllocatorCacheMaxAge,
                    config.disableHandleUseAfterFreeCheck,
                    config.preferredShaderLanguage.ordinal(),
                    config.forceGLES2Context, config.assertNativeWindowIsValid,
                    config.gpuContextPriority.ordinal(),
                    config.sharedUboInitialSizeInBytes);
            return this;
        }

        public Builder featureLevel(FeatureLevel featureLevel) {
            nSetBuilderFeatureLevel(mNativeBuilder, featureLevel.ordinal());
            return this;
        }

        public Builder paused(boolean paused) {
            nSetBuilderPaused(mNativeBuilder, paused);
            return this;
        }

        public Builder feature(@NotNull String name, boolean value) {
            nSetBuilderFeature(mNativeBuilder, name, value);
            return this;
        }

        public Engine build() {
            long nativeEngine = nBuilderBuild(mNativeBuilder);
            if (nativeEngine == 0) throw new IllegalStateException("Couldn't create Engine");
            return new Engine(nativeEngine, mConfig);
        }
    }

    public static class Config {
        private static final long FILAMENT_PER_RENDER_PASS_ARENA_SIZE_IN_MB = 3;
        private static final long FILAMENT_PER_FRAME_COMMANDS_SIZE_IN_MB = 2;
        private static final long FILAMENT_MIN_COMMAND_BUFFERS_SIZE_IN_MB = 1;
        private static final long FILAMENT_COMMAND_BUFFER_SIZE_IN_MB = FILAMENT_MIN_COMMAND_BUFFERS_SIZE_IN_MB * 3;

        public long commandBufferSizeMB = FILAMENT_COMMAND_BUFFER_SIZE_IN_MB;
        public long perRenderPassArenaSizeMB = FILAMENT_PER_RENDER_PASS_ARENA_SIZE_IN_MB;
        public long driverHandleArenaSizeMB = 0;
        public long minCommandBufferSizeMB = FILAMENT_MIN_COMMAND_BUFFERS_SIZE_IN_MB;
        public long perFrameCommandsSizeMB = FILAMENT_PER_FRAME_COMMANDS_SIZE_IN_MB;
        public long jobSystemThreadCount = 0;
        public long textureUseAfterFreePoolSize = 0;
        public boolean disableParallelShaderCompile = false;
        public StereoscopicType stereoscopicType = StereoscopicType.NONE;
        public long stereoscopicEyeCount = 2;
        public long resourceAllocatorCacheSizeMB = 64;
        public long resourceAllocatorCacheMaxAge = 1;
        public boolean disableHandleUseAfterFreeCheck = false;

        public enum ShaderLanguage {
            DEFAULT,
            MSL,
            METAL_LIBRARY,
        }
        public ShaderLanguage preferredShaderLanguage = ShaderLanguage.DEFAULT;
        public boolean forceGLES2Context = false;
        public boolean assertNativeWindowIsValid = false;
        public GpuContextPriority gpuContextPriority = GpuContextPriority.DEFAULT;
        public long sharedUboInitialSizeInBytes = 256 * 64;
    }

    private Engine(long nativeEngine, Config config) {
        mNativeObject = nativeEngine;
        mTransformManager = new TransformManager(nGetTransformManager(nativeEngine));
        mLightManager = new LightManager(nGetLightManager(nativeEngine));
        mRenderableManager = new RenderableManager(nGetRenderableManager(nativeEngine));
        mEntityManager = new EntityManager(nGetEntityManager(nativeEngine));
        mConfig = config;
        mCleanable = NativeRegistry.registerForCleanup(this, new EngineCleanup(mNativeObject));
    }

    @NotNull
    public static Engine create() {
        return new Builder().build();
    }

    @NotNull
    public static Engine create(@NotNull Backend backend) {
        return new Builder().backend(backend).build();
    }

    @NotNull
    public static Engine create(@NotNull Object sharedContext) {
        return new Builder().sharedContext(sharedContext).build();
    }

    public boolean isValid() {
        return mNativeObject != 0;
    }


    public void destroy() {
        mCleanable.clean();
        mNativeObject = 0;
    }

    @NotNull
    public Backend getBackend() {
        return sBackendValues[(int) nGetBackend(getNativeObject())];
    }

    public void enableAccurateTranslations() {
        getTransformManager().setAccurateTranslationsEnabled(true);
    }

    @NotNull
    public FeatureLevel getSupportedFeatureLevel() {
        return sFeatureLevelValues[nGetSupportedFeatureLevel(getNativeObject())];
    }

    @NotNull
    public FeatureLevel setActiveFeatureLevel(@NotNull FeatureLevel featureLevel) {
        return sFeatureLevelValues[nSetActiveFeatureLevel(getNativeObject(), featureLevel.ordinal())];
    }

    @NotNull
    public FeatureLevel getActiveFeatureLevel() {
        return sFeatureLevelValues[nGetActiveFeatureLevel(getNativeObject())];
    }

    public void setAutomaticInstancingEnabled(boolean enable) {
        nSetAutomaticInstancingEnabled(getNativeObject(), enable);
    }

    public boolean isAutomaticInstancingEnabled() {
        return nIsAutomaticInstancingEnabled(getNativeObject());
    }

    @NotNull
    public Config getConfig() {
        if (mConfig == null) mConfig = new Config();
        return mConfig;
    }

    public long getMaxStereoscopicEyes() {
        return nGetMaxStereoscopicEyes(getNativeObject());
    }

    @NotNull
    public SwapChain createSwapChain(@NotNull Object surface) {
        return createSwapChain(surface, 0); // CONFIG_DEFAULT
    }

    @NotNull
    public SwapChain createSwapChain(@NotNull Object surface, long flags) {
        long nativeSwapChain = nCreateSwapChain(getNativeObject(), surface, flags);
        if (nativeSwapChain == 0) throw new IllegalStateException("Couldn't create SwapChain");
        return new SwapChain(nativeSwapChain, surface);
    }

    @NotNull
    public SwapChain createSwapChain(int width, int height, long flags) {
        long nativeSwapChain = nCreateSwapChainHeadless(getNativeObject(), width, height, flags);
        if (nativeSwapChain == 0) throw new IllegalStateException("Couldn't create SwapChain");
        return new SwapChain(nativeSwapChain, null);
    }


    public void destroySwapChain(@NotNull SwapChain swapChain) {
        nDestroySwapChain(getNativeObject(), swapChain.getNativeObject());
        swapChain.clearNativeObject();
    }

    public boolean isValidRenderer(@NotNull Renderer object) { return nIsValidRenderer(getNativeObject(), object.getNativeObject()); }
    public boolean isValidView(@NotNull View object) { return nIsValidView(getNativeObject(), object.getNativeObject()); }
    public boolean isValidScene(@NotNull Scene object) { return nIsValidScene(getNativeObject(), object.getNativeObject()); }
    public boolean isValidFence(@NotNull Fence object) { return nIsValidFence(getNativeObject(), object.getNativeObject()); }
    public boolean isValidStream(@NotNull Stream object) { return nIsValidStream(getNativeObject(), object.getNativeObject()); }
    public boolean isValidIndexBuffer(@NotNull IndexBuffer object) { return nIsValidIndexBuffer(getNativeObject(), object.getNativeObject()); }
    public boolean isValidVertexBuffer(@NotNull VertexBuffer object) { return nIsValidVertexBuffer(getNativeObject(), object.getNativeObject()); }
    public boolean isValidSkinningBuffer(@NotNull SkinningBuffer object) { return nIsValidSkinningBuffer(getNativeObject(), object.getNativeObject()); }
    public boolean isValidMorphTargetBuffer(@NotNull MorphTargetBuffer object) { return nIsValidMorphTargetBuffer(getNativeObject(), object.getNativeObject()); }
    public boolean isValidIndirectLight(@NotNull IndirectLight object) { return nIsValidIndirectLight(getNativeObject(), object.getNativeObject()); }
    public boolean isValidMaterial(@NotNull Material object) { return nIsValidMaterial(getNativeObject(), object.getNativeObject()); }
    public boolean isValidMaterialInstance(@NotNull Material ma, MaterialInstance mi) { return nIsValidMaterialInstance(getNativeObject(), ma.getNativeObject(), mi.getNativeObject()); }
    public boolean isValidExpensiveMaterialInstance(@NotNull MaterialInstance object) { return nIsValidExpensiveMaterialInstance(getNativeObject(), object.getNativeObject()); }
    public boolean isValidSkybox(@NotNull Skybox object) { return nIsValidSkybox(getNativeObject(), object.getNativeObject()); }
    public boolean isValidColorGrading(@NotNull ColorGrading object) { return nIsValidColorGrading(getNativeObject(), object.getNativeObject()); }
    public boolean isValidTexture(@NotNull Texture object) { return nIsValidTexture(getNativeObject(), object.getNativeObject()); }
    public boolean isValidRenderTarget(@NotNull RenderTarget object) { return nIsValidRenderTarget(getNativeObject(), object.getNativeObject()); }
    public boolean isValidSwapChain(@NotNull SwapChain object) { return nIsValidSwapChain(getNativeObject(), object.getNativeObject()); }

    @NotNull
    public View createView() {
        long nativeView = nCreateView(getNativeObject());
        if (nativeView == 0) throw new IllegalStateException("Couldn't create View");
        return new View(nativeView);
    }

    public void destroyView(@NotNull View view) {
        nDestroyView(getNativeObject(), view.getNativeObject());
        view.clearNativeObject();
    }

    @NotNull
    public Renderer createRenderer() {
        long nativeRenderer = nCreateRenderer(getNativeObject());
        if (nativeRenderer == 0) throw new IllegalStateException("Couldn't create Renderer");
        return new Renderer(this, nativeRenderer);
    }

    public void destroyRenderer(@NotNull Renderer renderer) {
        nDestroyRenderer(getNativeObject(), renderer.getNativeObject());
        renderer.clearNativeObject();
    }

    @NotNull
    public Camera createCamera(@Entity int entity) {
        long nativeCamera = nCreateCamera(getNativeObject(), entity);
        if (nativeCamera == 0) throw new IllegalStateException("Couldn't create Camera");
        return new Camera(nativeCamera, entity);
    }

    @Nullable
    public Camera getCameraComponent(@Entity int entity) {
        long nativeCamera = nGetCameraComponent(getNativeObject(), entity);
        if (nativeCamera == 0) return null;
        return new Camera(nativeCamera, entity);
    }

    public void destroyCameraComponent(@Entity int entity) {
        nDestroyCameraComponent(getNativeObject(), entity);
    }

    @NotNull
    public Scene createScene() {
        long nativeScene = nCreateScene(getNativeObject());
        if (nativeScene == 0) throw new IllegalStateException("Couldn't create Scene");
        return new Scene(nativeScene);
    }

    public void destroyScene(@NotNull Scene scene) {
        nDestroyScene(getNativeObject(), scene.getNativeObject());
        scene.clearNativeObject();
    }

    public void destroyStream(@NotNull Stream stream) {
        nDestroyStream(getNativeObject(), stream.getNativeObject());
        stream.clearNativeObject();
    }

    @NotNull
    public Fence createFence() {
        long nativeFence = nCreateFence(getNativeObject());
        if (nativeFence == 0) throw new IllegalStateException("Couldn't create Fence");
        return new Fence(nativeFence);
    }

    public void destroyFence(@NotNull Fence fence) {
        nDestroyFence(getNativeObject(), fence.getNativeObject());
        fence.clearNativeObject();
    }

    public void destroyIndexBuffer(@NotNull IndexBuffer indexBuffer) {
        nDestroyIndexBuffer(getNativeObject(), indexBuffer.getNativeObject());
        indexBuffer.clearNativeObject();
    }

    public void destroyVertexBuffer(@NotNull VertexBuffer vertexBuffer) {
        nDestroyVertexBuffer(getNativeObject(), vertexBuffer.getNativeObject());
        vertexBuffer.clearNativeObject();
    }

    public void destroySkinningBuffer(@NotNull SkinningBuffer skinningBuffer) {
        nDestroySkinningBuffer(getNativeObject(), skinningBuffer.getNativeObject());
        skinningBuffer.clearNativeObject();
    }

    public void destroyMorphTargetBuffer(@NotNull MorphTargetBuffer morphTargetBuffer) {
        nDestroyMorphTargetBuffer(getNativeObject(), morphTargetBuffer.getNativeObject());
        morphTargetBuffer.clearNativeObject();
    }

    public void destroyIndirectLight(@NotNull IndirectLight ibl) {
        nDestroyIndirectLight(getNativeObject(), ibl.getNativeObject());
        ibl.clearNativeObject();
    }

    public void destroyMaterial(@NotNull Material material) {
        nDestroyMaterial(getNativeObject(), material.getNativeObject());
        material.clearNativeObject();
    }

    public void destroyMaterialInstance(@NotNull MaterialInstance materialInstance) {
        nDestroyMaterialInstance(getNativeObject(), materialInstance.getNativeObject());
        materialInstance.clearNativeObject();
    }

    public void destroySkybox(@NotNull Skybox skybox) {
        nDestroySkybox(getNativeObject(), skybox.getNativeObject());
        skybox.clearNativeObject();
    }

    public void destroyColorGrading(@NotNull ColorGrading colorGrading) {
        nDestroyColorGrading(getNativeObject(), colorGrading.getNativeObject());
        colorGrading.clearNativeObject();
    }

    public void destroyTexture(@NotNull Texture texture) {
        nDestroyTexture(getNativeObject(), texture.getNativeObject());
        texture.clearNativeObject();
    }

    public void destroyRenderTarget(@NotNull RenderTarget target) {
        nDestroyRenderTarget(getNativeObject(), target.getNativeObject());
        target.clearNativeObject();
    }

    public void destroyEntity(@Entity int entity) {
        nDestroyEntity(getNativeObject(), entity);
    }

    public void compile(@NotNull Material.CompilerPriorityQueue priority,
                        @NotNull Material material,
                        @NotNull View view,
                        @NotNull FeatureState shadowReceiver,
                        @NotNull FeatureState skinning,
                        @Nullable Object handler,
                        @Nullable Runnable callback) {
        nCompile(getNativeObject(), priority.ordinal(), material.getNativeObject(), view.getNativeObject(), shadowReceiver.ordinal(), skinning.ordinal(), handler, callback);
    }

    @NotNull
    public TransformManager getTransformManager() { return mTransformManager; }

    @NotNull
    public LightManager getLightManager() { return mLightManager; }

    @NotNull
    public RenderableManager getRenderableManager() { return mRenderableManager; }

    @NotNull
    public EntityManager getEntityManager() { return mEntityManager; }

    public void flushAndWait() { flushAndWait(Long.MAX_VALUE); }

    public boolean flushAndWait(long timeout) {
        return nFlushAndWait(getNativeObject(), timeout);
    }

    public void flush() {
        nFlush(getNativeObject());
    }

    public boolean isPaused() {
        return nIsPaused(getNativeObject());
    }

    public void setPaused(boolean paused) {
        nSetPaused(getNativeObject(), paused);
    }

    public void unprotected() {
        nUnprotected(getNativeObject());
    }

    public static native long getSteadyClockTimeNano();

    public boolean hasFeatureFlag(@NotNull String name) {
        return nHasFeatureFlag(getNativeObject(), name);
    }

    public boolean setFeatureFlag(@NotNull String name, boolean value) {
        return nSetFeatureFlag(getNativeObject(), name, value);
    }

    public boolean getFeatureFlag(@NotNull String name) {
        return nGetFeatureFlag(getNativeObject(), name);
    }

    @UsedByReflection("TextureHelper.java")
    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed Engine");
        }
        return mNativeObject;
    }

    @UsedByReflection("MaterialBuilder.java")
    public long getNativeJobSystem() {
        return nGetJobSystem(getNativeObject());
    }

    private static class EngineCleanup implements Runnable {
        private final long mNativeObject;
        EngineCleanup(long nativeObject) { mNativeObject = nativeObject; }
        @Override public void run() { nDestroyEngine(mNativeObject); }
    }

    private static native void nDestroyEngine(long nativeEngine);
    private static native long nGetBackend(long nativeEngine);
    private static native long nCreateSwapChain(long nativeEngine, Object nativeWindow, long flags);
    private static native long nCreateSwapChainHeadless(long nativeEngine, int width, int height, long flags);
    private static native long nCreateSwapChainFromRawPointer(long nativeEngine, long pointer, long flags);
    private static native long nCreateView(long nativeEngine);
    private static native long nCreateRenderer(long nativeEngine);
    private static native long nCreateCamera(long nativeEngine, int entity);
    private static native long nGetCameraComponent(long nativeEngine, int entity);
    private static native void nDestroyCameraComponent(long nativeEngine, int entity);
    private static native long nCreateScene(long nativeEngine);
    private static native long nCreateFence(long nativeEngine);

    private static native void nDestroyRenderer(long nativeEngine, long nativeRenderer);
    private static native void nDestroyView(long nativeEngine, long nativeView);
    private static native void nDestroyScene(long nativeEngine, long nativeScene);
    private static native void nDestroyFence(long nativeEngine, long nativeFence);
    private static native void nDestroyStream(long nativeEngine, long nativeStream);
    private static native void nDestroyIndexBuffer(long nativeEngine, long nativeIndexBuffer);
    private static native void nDestroyVertexBuffer(long nativeEngine, long nativeVertexBuffer);
    private static native void nDestroySkinningBuffer(long nativeEngine, long nativeSkinningBuffer);
    private static native void nDestroyMorphTargetBuffer(long nativeEngine, long nativeMorphTargetBuffer);
    private static native void nDestroyIndirectLight(long nativeEngine, long nativeIndirectLight);
    private static native void nDestroyMaterial(long nativeEngine, long nativeMaterial);
    private static native void nDestroyMaterialInstance(long nativeEngine, long nativeMaterialInstance);
    private static native void nDestroySkybox(long nativeEngine, long nativeSkybox);
    private static native void nDestroyColorGrading(long nativeEngine, long nativeColorGrading);
    private static native void nDestroyTexture(long nativeEngine, long nativeTexture);
    private static native void nDestroyRenderTarget(long nativeEngine, long nativeTarget);
    private static native void nDestroySwapChain(long nativeEngine, long nativeSwapChain);
    private static native boolean nIsValidRenderer(long nativeEngine, long nativeRenderer);
    private static native boolean nIsValidView(long nativeEngine, long nativeView);
    private static native boolean nIsValidScene(long nativeEngine, long nativeScene);
    private static native boolean nIsValidFence(long nativeEngine, long nativeFence);
    private static native boolean nIsValidStream(long nativeEngine, long nativeStream);
    private static native boolean nIsValidIndexBuffer(long nativeEngine, long nativeIndexBuffer);
    private static native boolean nIsValidVertexBuffer(long nativeEngine, long nativeVertexBuffer);
    private static native boolean nIsValidSkinningBuffer(long nativeEngine, long nativeSkinningBuffer);
    private static native boolean nIsValidMorphTargetBuffer(long nativeEngine, long nativeMorphTargetBuffer);
    private static native boolean nIsValidIndirectLight(long nativeEngine, long nativeIndirectLight);
    private static native boolean nIsValidMaterial(long nativeEngine, long nativeMaterial);
    private static native boolean nIsValidMaterialInstance(long nativeEngine, long nativeMaterial, long nativeMaterialInstance);
    private static native boolean nIsValidExpensiveMaterialInstance(long nativeEngine, long nativeMaterialInstance);
    private static native boolean nIsValidSkybox(long nativeEngine, long nativeSkybox);
    private static native boolean nIsValidColorGrading(long nativeEngine, long nativeColorGrading);
    private static native boolean nIsValidTexture(long nativeEngine, long nativeTexture);
    private static native boolean nIsValidRenderTarget(long nativeEngine, long nativeTarget);
    private static native boolean nIsValidSwapChain(long nativeEngine, long nativeSwapChain);
    private static native void nDestroyEntity(long nativeEngine, int entity);
    private static native boolean nFlushAndWait(long nativeEngine, long timeout);
    private static native void nFlush(long nativeEngine);
    private static native boolean nIsPaused(long nativeEngine);
    private static native void nSetPaused(long nativeEngine, boolean paused);
    private static native void nUnprotected(long nativeEngine);
    private static native long nGetTransformManager(long nativeEngine);
    private static native long nGetLightManager(long nativeEngine);
    private static native long nGetRenderableManager(long nativeEngine);
    private static native long nGetJobSystem(long nativeEngine);
    private static native long nGetEntityManager(long nativeEngine);
    private static native void nSetAutomaticInstancingEnabled(long nativeEngine, boolean enable);
    private static native boolean nIsAutomaticInstancingEnabled(long nativeEngine);
    private static native long nGetMaxStereoscopicEyes(long nativeEngine);
    private static native int nGetSupportedFeatureLevel(long nativeEngine);
    private static native int nSetActiveFeatureLevel(long nativeEngine, int ordinal);
    private static native int nGetActiveFeatureLevel(long nativeEngine);
    private static native boolean nHasFeatureFlag(long nativeEngine, String name);
    private static native boolean nSetFeatureFlag(long nativeEngine, String name, boolean value);
    private static native boolean nGetFeatureFlag(long nativeEngine, String name);

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nSetBuilderBackend(long nativeBuilder, int backend);
    private static native void nSetBuilderSharedContext(long nativeBuilder, Object sharedContext);
    private static native void nSetBuilderConfig(long nativeBuilder, long commandBufferSizeMB,
            long perRenderPassArenaSizeMB, long driverHandleArenaSizeMB,
            long minCommandBufferSizeMB, long perFrameCommandsSizeMB, long jobSystemThreadCount,
            boolean disableParallelShaderCompile, int stereoscopicType, long stereoscopicEyeCount,
            long resourceAllocatorCacheSizeMB, long resourceAllocatorCacheMaxAge,
            boolean disableHandleUseAfterFreeCheck,
            int preferredShaderLanguage,
            boolean forceGLES2Context, boolean assertNativeWindowIsValid,
            int gpuContextPriority,
            long sharedUboInitialSizeInBytes);
    private static native void nSetBuilderFeatureLevel(long nativeBuilder, int featureLevel);
    private static native void nSetBuilderPaused(long nativeBuilder, boolean paused);
    private static native void nSetBuilderFeature(long nativeBuilder, String name, boolean value);
    private static native void nCompile(long nativeEngine, int priority, long nativeMaterial, long nativeView, int shadowReceiver, int skinning, Object handler, Runnable callback);
    private static native long nBuilderBuild(long nativeBuilder);
}
