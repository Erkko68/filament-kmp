package io.github.erkko68.filament;

public class View {
    private long mNativeObject;

    View(long nativeView) {
        mNativeObject = nativeView;
    }

    public void setScene(Scene scene) {
        nSetScene(getNativeObject(), scene != null ? scene.getNativeObject() : 0);
    }

    public void setCamera(Camera camera) {
        nSetCamera(getNativeObject(), camera != null ? camera.getNativeObject() : 0);
    }

    public void setRenderTarget(RenderTarget target) {
        nSetRenderTarget(getNativeObject(), target != null ? target.getNativeObject() : 0);
    }

    public void setViewport(Viewport viewport) {
        nSetViewport(getNativeObject(), viewport.left, viewport.bottom, viewport.width, viewport.height);
    }

    public void setClearColor(float r, float g, float b, float a) {
        nSetClearColor(getNativeObject(), r, g, b, a);
    }

    public void setColorGrading(ColorGrading colorGrading) {
        nSetColorGrading(getNativeObject(), colorGrading != null ? colorGrading.getNativeObject() : 0);
    }

    public void setPostProcessingEnabled(boolean enabled) {
        nSetPostProcessingEnabled(getNativeObject(), enabled);
    }

    public void setDithering(Dithering dithering) {
        nSetDithering(getNativeObject(), dithering.ordinal());
    }

    public enum Dithering {
        NONE,
        TEMPORAL
    }

    public enum QualityLevel {
        LOW,
        MEDIUM,
        HIGH,
        ULTRA
    }

    public enum BlendMode {
        OPAQUE,
        TRANSLUCENT
    }

    public enum AntiAliasing {
        NONE,
        FXAA
    }

    public enum AmbientOcclusion {
        NONE,
        SSAO
    }

    public static class DynamicResolutionOptions {
        public float minScale = 0.5f;
        public float maxScale = 1.0f;
        public float sharpness = 0.9f;
        public boolean enabled = false;
        public boolean homogeneousScaling = false;
        public QualityLevel quality = QualityLevel.LOW;
    }

    public static class BloomOptions {
        public enum BlendMode {
            ADD,
            INTERPOLATE
        }
        public Texture dirt = null;
        public float dirtStrength = 0.2f;
        public float strength = 0.10f;
        public int resolution = 384;
        public int levels = 6;
        public BlendMode blendMode = BlendMode.ADD;
        public boolean threshold = true;
        public boolean enabled = false;
        public float highlight = 1000.0f;
        public QualityLevel quality = QualityLevel.LOW;
        public boolean lensFlare = false;
        public boolean starburst = true;
        public float chromaticAberration = 0.005f;
        public int ghostCount = 4;
        public float ghostSpacing = 0.6f;
        public float ghostThreshold = 10.0f;
        public float haloThickness = 0.1f;
        public float haloRadius = 0.4f;
        public float haloThreshold = 10.0f;
    }

    public static class FogOptions {
        public float distance = 0.0f;
        public float cutOffDistance = Float.POSITIVE_INFINITY;
        public float maximumOpacity = 1.0f;
        public float height = 0.0f;
        public float heightFalloff = 1.0f;
        public float[] color = {1.0f, 1.0f, 1.0f};
        public float density = 0.1f;
        public float inScatteringStart = 0.0f;
        public float inScatteringSize = -1.0f;
        public boolean fogColorFromIbl = false;
        public Texture skyColor = null;
        public boolean enabled = false;
    }

    public static class VignetteOptions {
        public float midPoint = 0.5f;
        public float roundness = 0.5f;
        public float feather = 0.5f;
        public float[] color = {0.0f, 0.0f, 0.0f, 1.0f};
        public boolean enabled = false;
    }

    public static class RenderQuality {
        public QualityLevel hdrColorBuffer = QualityLevel.HIGH;
    }

    public static class StereoscopicOptions {
        public boolean enabled = false;
    }

    public static class GuardBandOptions {
        public boolean enabled = false;
    }

    public void setDynamicResolutionOptions(DynamicResolutionOptions options) {
        nSetDynamicResolutionOptions(getNativeObject(),
                options.enabled, options.homogeneousScaling,
                options.minScale, options.maxScale, options.sharpness,
                options.quality.ordinal());
    }

    public void setBloomOptions(BloomOptions options) {
        nSetBloomOptions(getNativeObject(), options.dirt != null ? options.dirt.getNativeObject() : 0,
                options.dirtStrength, options.strength, options.resolution,
                options.levels, options.blendMode.ordinal(),
                options.threshold, options.enabled, options.highlight,
                options.quality.ordinal(), options.lensFlare, options.starburst,
                options.chromaticAberration, options.ghostCount, options.ghostSpacing,
                options.ghostThreshold, options.haloThickness, options.haloRadius, options.haloThreshold);
    }

    public void setFogOptions(FogOptions options) {
        nSetFogOptions(getNativeObject(), options.distance, options.maximumOpacity, options.height,
                options.heightFalloff, options.cutOffDistance,
                options.color[0], options.color[1], options.color[2],
                options.density, options.inScatteringStart, options.inScatteringSize,
                options.fogColorFromIbl, options.skyColor != null ? options.skyColor.getNativeObject() : 0,
                options.enabled);
    }

    public void setVignetteOptions(VignetteOptions options) {
        nSetVignetteOptions(getNativeObject(), options.midPoint, options.roundness, options.feather,
                options.color[0], options.color[1], options.color[2], options.color[3], options.enabled);
    }

    public void setRenderQuality(RenderQuality quality) {
        nSetRenderQuality(getNativeObject(), quality.hdrColorBuffer.ordinal());
    }

    public void setGuardBandOptions(GuardBandOptions options) {
        nSetGuardBandOptions(getNativeObject(), options.enabled);
    }

    public void setVisibleLayers(int select, int value) {
        nSetVisibleLayers(getNativeObject(), select, value);
    }

    public int getVisibleLayers() {
        return nGetVisibleLayers(getNativeObject());
    }

    public void setShadowingEnabled(boolean enabled) {
        nSetShadowingEnabled(getNativeObject(), enabled);
    }

    public boolean isShadowingEnabled() {
        return nIsShadowingEnabled(getNativeObject());
    }

    public void setFrustumCullingEnabled(boolean enabled) {
        nSetFrustumCullingEnabled(getNativeObject(), enabled);
    }

    public boolean isFrustumCullingEnabled() {
        return nIsFrustumCullingEnabled(getNativeObject());
    }

    public void setMaterialGlobal(int index, float x, float y, float z, float w) {
        nSetMaterialGlobal(getNativeObject(), index, x, y, z, w);
    }

    public void getMaterialGlobal(int index, float[] out) {
        nGetMaterialGlobal(getNativeObject(), index, out);
    }

    public void clearFrameHistory(Engine engine) {
        nClearFrameHistory(getNativeObject(), engine.getNativeObject());
    }

    public void setStereoscopicOptions(StereoscopicOptions options) {
        nSetStereoscopicOptions(getNativeObject(), options.enabled);
    }

    public static class AmbientOcclusionOptions {
        public enum AmbientOcclusionType {
            SAO,
            GTAO
        }
        public AmbientOcclusionType aoType = AmbientOcclusionType.SAO;
        public float radius = 0.3f;
        public float power = 1.0f;
        public float bias = 0.0005f;
        public float resolution = 0.5f;
        public float intensity = 1.0f;
        public float bilateralThreshold = 0.05f;
        public QualityLevel quality = QualityLevel.LOW;
        public QualityLevel lowPassFilter = QualityLevel.MEDIUM;
        public QualityLevel upsampling = QualityLevel.LOW;
        public boolean enabled = false;
        public boolean bentNormals = false;
        public float minHorizonAngleRad = 0.0f;
        public float ssctLightConeRad = 1.0f;
        public float ssctShadowDistance = 0.3f;
        public float ssctContactDistanceMax = 1.0f;
        public float ssctIntensity = 0.8f;
        public float[] ssctLightDirection = {0f, -1f, 0f};
        public float ssctDepthBias = 0.01f;
        public float ssctDepthSlopeBias = 0.01f;
        public int ssctSampleCount = 4;
        public int ssctRayCount = 1;
        public boolean ssctEnabled = false;
        public int gtaoSampleSliceCount = 4;
        public int gtaoSampleStepsPerSlice = 3;
        public float gtaoThicknessHeuristic = 0.004f;
        public boolean gtaoUseVisibilityBitmasks = false;
        public float gtaoConstThickness = 0.5f;
        public boolean gtaoLinearThickness = false;
    }

    public static class TemporalAntiAliasingOptions {
        public enum BoxType { AABB, AABB_VARIANCE }
        public enum BoxClipping { ACCURATE, CLAMP, NONE }
        public enum JitterPattern { RGSS_X4, UNIFORM_HELIX_X4, HALTON_23_X8, HALTON_23_X16, HALTON_23_X32 }

        public float filterWidth = 1.0f;
        public float feedback = 0.12f;
        public float edgeThreshold = 1.0f;
        public float filterThreshold = 1.0f;
        public float lodBias = -1.0f;
        public float sharpness = 0.0f;
        public boolean enabled = false;
        public float upscaling = 1.0f;
        public boolean filterHistory = true;
        public boolean filterInput = true;
        public boolean useYCoCg = false;
        public boolean hdr = true;
        public BoxType boxType = BoxType.AABB;
        public BoxClipping boxClipping = BoxClipping.ACCURATE;
        public JitterPattern jitterPattern = JitterPattern.HALTON_23_X16;
        public float varianceGamma = 1.0f;
        public boolean preventFlickering = false;
        public boolean historyReprojection = true;
    }

    public static class MultiSampleAntiAliasingOptions {
        public boolean enabled = false;
        public int sampleCount = 4;
        public boolean customResolve = false;
    }

    public static class ScreenSpaceReflectionsOptions {
        public float thickness = 0.1f;
        public float bias = 0.01f;
        public float maxDistance = 3.0f;
        public float stride = 2.0f;
        public boolean enabled = false;
    }

    public static class DepthOfFieldOptions {
        public enum Filter { NONE, UNUSED, MEDIAN }
        public float cocScale = 1.0f;
        public float cocAspectRatio = 1.0f;
        public float maxApertureDiameter = 0.01f;
        public boolean enabled = false;
        public Filter filter = Filter.MEDIAN;
        public boolean nativeResolution = false;
        public int foregroundRingCount = 0;
        public int backgroundRingCount = 0;
        public int fastGatherRingCount = 0;
        public int maxForegroundCOC = 0;
        public int maxBackgroundCOC = 0;
    }

    public static class PickingQueryResult {
        public int renderable;
        public float depth;
        public float[] fragCoords = new float[3];
    }

    public interface OnPickCallback {
        void onPick(PickingQueryResult result);
    }

    public void setAmbientOcclusionOptions(AmbientOcclusionOptions options) {
        nSetAmbientOcclusionOptions(getNativeObject(), options.radius, options.power, options.bias, options.resolution, options.intensity, options.bilateralThreshold, options.quality.ordinal(), options.lowPassFilter.ordinal(), options.upsampling.ordinal(), options.enabled, options.bentNormals, options.minHorizonAngleRad, options.ssctLightConeRad, options.ssctShadowDistance, options.ssctContactDistanceMax, options.ssctIntensity, options.ssctLightDirection[0], options.ssctLightDirection[1], options.ssctLightDirection[2], options.ssctDepthBias, options.ssctDepthSlopeBias, options.ssctSampleCount, options.ssctRayCount, options.ssctEnabled, options.aoType.ordinal(), options.gtaoSampleSliceCount, options.gtaoSampleStepsPerSlice, options.gtaoThicknessHeuristic, options.gtaoUseVisibilityBitmasks, options.gtaoConstThickness, options.gtaoLinearThickness);
    }

    public void setTemporalAntiAliasingOptions(TemporalAntiAliasingOptions options) {
        nSetTemporalAntiAliasingOptions(getNativeObject(), options.filterWidth, options.feedback, options.edgeThreshold, options.filterThreshold, options.lodBias, options.sharpness, options.enabled, options.upscaling, options.filterHistory, options.filterInput, options.useYCoCg, options.hdr, options.boxType.ordinal(), options.boxClipping.ordinal(), options.jitterPattern.ordinal(), options.varianceGamma, options.preventFlickering, options.historyReprojection);
    }

    public void setMultiSampleAntiAliasingOptions(MultiSampleAntiAliasingOptions options) {
        nSetMultiSampleAntiAliasingOptions(getNativeObject(), options.enabled, options.sampleCount, options.customResolve);
    }

    public void setScreenSpaceReflectionsOptions(ScreenSpaceReflectionsOptions options) {
        nSetScreenSpaceReflectionsOptions(getNativeObject(), options.thickness, options.bias, options.maxDistance, options.stride, options.enabled);
    }

    public void setDepthOfFieldOptions(DepthOfFieldOptions options) {
        nSetDepthOfFieldOptions(getNativeObject(), options.cocScale, options.cocAspectRatio, options.maxApertureDiameter, options.enabled, options.filter.ordinal(), options.nativeResolution, options.foregroundRingCount, options.backgroundRingCount, options.fastGatherRingCount, options.maxForegroundCOC, options.maxBackgroundCOC);
    }

    public void pick(int x, int y, java.util.concurrent.Executor executor, OnPickCallback callback) {
        nPick(getNativeObject(), x, y, executor, result -> {
            if (executor != null) executor.execute(() -> callback.onPick(result));
            else callback.onPick(result);
        });
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed View");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native void nSetScene(long nativeView, long nativeScene);
    private static native void nSetCamera(long nativeView, long nativeCamera);
    private static native void nSetViewport(long nativeView, int left, int bottom, int width, int height);
    private static native void nSetClearColor(long nativeView, float r, float g, float b, float a);
    private static native void nSetColorGrading(long nativeView, long nativeColorGrading);
    private static native void nSetRenderTarget(long nativeView, long nativeTarget);
    private static native void nSetPostProcessingEnabled(long nativeView, boolean enabled);
    private static native void nSetDithering(long nativeView, int dithering);
    private static native void nSetDynamicResolutionOptions(long nativeView, boolean enabled, boolean homogeneousScaling, float minScale, float maxScale, float sharpness, int quality);
    private static native void nSetBloomOptions(long nativeView, long nativeTexture, float dirtStrength, float strength, int resolution, int levels, int blendMode, boolean threshold, boolean enabled, float highlight, int quality, boolean lensFlare, boolean starburst, float chromaticAberration, int ghostCount, float ghostSpacing, float ghostThreshold, float haloThickness, float haloRadius, float haloThreshold);
    private static native void nSetFogOptions(long nativeView, float distance, float opacity, float height, float falloff, float cutOff, float r, float g, float b, float density, float inStart, float inSize, boolean colorFromIbl, long nativeSky, boolean enabled);
    private static native void nSetVignetteOptions(long nativeView, float midPoint, float roundness, float feather, float r, float g, float b, float a, boolean enabled);
    private static native void nSetRenderQuality(long nativeView, int hdrColorBuffer);
    private static native void nSetStereoscopicOptions(long nativeView, boolean enabled);
    private static native void nSetGuardBandOptions(long nativeView, boolean enabled);
    private static native void nSetVisibleLayers(long nativeView, int select, int value);
    private static native int nGetVisibleLayers(long nativeView);
    private static native void nSetShadowingEnabled(long nativeView, boolean enabled);
    private static native boolean nIsShadowingEnabled(long nativeView);
    private static native void nSetFrustumCullingEnabled(long nativeView, boolean enabled);
    private static native boolean nIsFrustumCullingEnabled(long nativeView);
    private static native void nSetMaterialGlobal(long nativeView, int index, float x, float y, float z, float w);
    private static native void nGetMaterialGlobal(long nativeView, int index, float[] out);
    private static native void nClearFrameHistory(long nativeView, long nativeEngine);
    private static native void nSetAmbientOcclusionOptions(long nativeView, float radius, float power, float bias, float resolution, float intensity, float bilateralThreshold, int quality, int lowPassFilter, int upsampling, boolean enabled, boolean bentNormals, float minHorizonAngleRad, float ssctLightConeRad, float ssctShadowDistance, float ssctContactDistanceMax, float ssctIntensity, float ssctLightDirectionX, float ssctLightDirectionY, float ssctLightDirectionZ, float ssctDepthBias, float ssctDepthSlopeBias, int ssctSampleCount, int ssctRayCount, boolean ssctEnabled, int aoType, int gtaoSampleSliceCount, int gtaoSampleStepsPerSlice, float gtaoThicknessHeuristic, boolean gtaoUseVisibilityBitmasks, float gtaoConstThickness, boolean gtaoLinearThickness);
    private static native void nSetTemporalAntiAliasingOptions(long nativeView, float filterWidth, float feedback, float edgeThreshold, float filterThreshold, float lodBias, float sharpness, boolean enabled, float upscaling, boolean filterHistory, boolean filterInput, boolean useYCoCg, boolean hdr, int boxType, int boxClipping, int jitterPattern, float varianceGamma, boolean preventFlickering, boolean historyReprojection);
    private static native void nSetMultiSampleAntiAliasingOptions(long nativeView, boolean enabled, int sampleCount, boolean customResolve);
    private static native void nSetScreenSpaceReflectionsOptions(long nativeView, float thickness, float bias, float maxDistance, float stride, boolean enabled);
    private static native void nSetDepthOfFieldOptions(long nativeView, float cocScale, float cocAspectRatio, float maxApertureDiameter, boolean enabled, int filter, boolean nativeResolution, int foregroundRingCount, int backgroundRingCount, int fastGatherRingCount, int maxForegroundCOC, int maxBackgroundCOC);
    private static native void nPick(long nativeView, int x, int y, Object executor, PickingCallback callback);

    private interface PickingCallback {
        void onPick(PickingQueryResult result);
    }
}
