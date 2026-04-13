package io.github.erkko68.filament;

import java.util.concurrent.Executor;

/**
 * Engine is filament's main entry-point.
 */
public class Engine {
    private long mNativeObject;
    private Config mConfig;

    public enum Backend {
        DEFAULT,
        VULKAN,
        OPENGL,
        METAL,
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

    public static class Config {
        public long commandBufferSizeMB = 3 * 1;
        public long perRenderPassArenaSizeMB = 3;
        public long driverHandleArenaSizeMB = 0;
        public long minCommandBufferSizeMB = 1;
        public long perFrameCommandsSizeMB = 2;
        public long jobSystemThreadCount = 0;
        public boolean disableParallelShaderCompile = false;
        public StereoscopicType stereoscopicType = StereoscopicType.NONE;
        public long stereoscopicEyeCount = 2;
        public long resourceAllocatorCacheSizeMB = 64;
        public long resourceAllocatorCacheMaxAge = 1;
        public boolean disableHandleUseAfterFreeCheck = false;
        public enum ShaderLanguage { DEFAULT, MSL, METAL_LIBRARY }
        public ShaderLanguage preferredShaderLanguage = ShaderLanguage.DEFAULT;
        public boolean forceGLES2Context = false;
        public boolean assertNativeWindowIsValid = false;
        public GpuContextPriority gpuContextPriority = GpuContextPriority.DEFAULT;
        public long sharedUboInitialSizeInBytes = 256 * 64;
    }

    public static class Builder {
        private final long mNativeBuilder;
        private Config mConfig;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
        }

        public Builder backend(Backend backend) {
            nSetBuilderBackend(mNativeBuilder, (long) backend.ordinal());
            return this;
        }

        public Builder sharedContext(long sharedContext) {
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

        public Builder feature(String name, boolean value) {
            nSetBuilderFeature(mNativeBuilder, name, value);
            return this;
        }

        public Engine build() {
            long nativeEngine = nBuilderBuild(mNativeBuilder);
            if (nativeEngine == 0) throw new IllegalStateException("Couldn't create Engine");
            return new Engine(nativeEngine, mConfig);
        }

        @Override
        protected void finalize() throws Throwable {
            try {
                nDestroyBuilder(mNativeBuilder);
            } finally {
                super.finalize();
            }
        }
    }

    private Engine(long nativeEngine, Config config) {
        mNativeObject = nativeEngine;
        mConfig = config;
    }

    public static Engine create() {
        return new Builder().build();
    }

    public static Engine create(Backend backend) {
        return new Builder().backend(backend).build();
    }

    public boolean isValid() {
        return mNativeObject != 0;
    }

    public void destroy() {
        nDestroyEngine(getNativeObject());
        mNativeObject = 0;
    }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Engine already destroyed");
        return mNativeObject;
    }

    public Backend getBackend() {
        return Backend.values()[(int) nGetBackend(getNativeObject())];
    }

    public void flush() {
        nFlush(getNativeObject());
    }

    public boolean flushAndWait(long timeout) {
        return nFlushAndWait(getNativeObject(), timeout);
    }

    // SwpChain management
    public SwapChain createSwapChain(long nativeWindow) {
        return createSwapChain(nativeWindow, 0);
    }

    public SwapChain createSwapChain(long nativeWindow, long flags) {
        long nativeSwapChain = nCreateSwapChain(getNativeObject(), nativeWindow, flags);
        if (nativeSwapChain == 0) return null;
        return new SwapChain(nativeSwapChain);
    }

    public void destroySwapChain(SwapChain swapChain) {
        if (nDestroySwapChain(getNativeObject(), swapChain.getNativeObject())) {
            swapChain.clearNativeObject();
        }
    }

    // View management
    public View createView() {
        long nativeView = nCreateView(getNativeObject());
        if (nativeView == 0) return null;
        return new View(nativeView);
    }

    public void destroyView(View view) {
        if (nDestroyView(getNativeObject(), view.getNativeObject())) {
            view.clearNativeObject();
        }
    }

    // Renderer management
    public Renderer createRenderer() {
        long nativeRenderer = nCreateRenderer(getNativeObject());
        if (nativeRenderer == 0) return null;
        return new Renderer(nativeRenderer);
    }

    public void destroyRenderer(Renderer renderer) {
        if (nDestroyRenderer(getNativeObject(), renderer.getNativeObject())) {
            renderer.clearNativeObject();
        }
    }

    // Native methods
    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nSetBuilderBackend(long nativeBuilder, long backend);
    private static native void nSetBuilderSharedContext(long nativeBuilder, long sharedContext);
    private static native void nSetBuilderConfig(long nativeBuilder, long commandBufferSizeMB,
            long perRenderPassArenaSizeMB, long driverHandleArenaSizeMB, long minCommandBufferSizeMB,
            long perFrameCommandsSizeMB, long jobSystemThreadCount, boolean disableParallelShaderCompile,
            int stereoscopicType, long stereoscopicEyeCount, long resourceAllocatorCacheSizeMB,
            long resourceAllocatorCacheMaxAge, boolean disableHandleUseAfterFreeCheck,
            int preferredShaderLanguage, boolean forceGLES2Context, boolean assertNativeWindowIsValid,
            int gpuContextPriority, long sharedUboInitialSizeInBytes);
    private static native void nSetBuilderFeatureLevel(long nativeBuilder, int featureLevel);
    private static native void nSetBuilderPaused(long nativeBuilder, boolean paused);
    private static native void nSetBuilderFeature(long nativeBuilder, String name, boolean value);
    private static native long nBuilderBuild(long nativeBuilder);

    private static native void nDestroyEngine(long nativeEngine);
    private static native long nGetBackend(long nativeEngine);
    private static native void nFlush(long nativeEngine);
    private static native boolean nFlushAndWait(long nativeEngine, long timeout);

    private static native long nCreateSwapChain(long nativeEngine, long nativeWindow, long flags);
    private static native boolean nDestroySwapChain(long nativeEngine, long nativeSwapChain);
    private static native long nCreateView(long nativeEngine);
    private static native boolean nDestroyView(long nativeEngine, long nativeView);
    private static native long nCreateRenderer(long nativeEngine);
    private static native boolean nDestroyRenderer(long nativeEngine, long nativeRenderer);

    static {
        System.loadLibrary("filament-jni");
    }
}
