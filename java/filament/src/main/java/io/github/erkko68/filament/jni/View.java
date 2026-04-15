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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.github.erkko68.filament.jni.proguard.UsedByNative;
import java.util.EnumSet;

/**
 * Encompasses all the state needed for rendering a {@link Scene}.
 */
public class View {
    private static final AntiAliasing[] sAntiAliasingValues = AntiAliasing.values();
    private static final Dithering[] sDitheringValues = Dithering.values();

    private static final ShadowType[] sShadowTypeValues = ShadowType.values();

    private long mNativeObject;
    private String mName;
    private Scene mScene;
    private Camera mCamera;
    private Viewport mViewport = new Viewport(0, 0, 0, 0);
    private DynamicResolutionOptions mDynamicResolution;
    private RenderQuality mRenderQuality;
    private AmbientOcclusionOptions mAmbientOcclusionOptions;
    private BloomOptions mBloomOptions;
    private FogOptions mFogOptions;
    private StereoscopicOptions mStereoscopicOptions;
    private RenderTarget mRenderTarget;
    private BlendMode mBlendMode;
    private DepthOfFieldOptions mDepthOfFieldOptions;
    private VignetteOptions mVignetteOptions;
    private ColorGrading mColorGrading;
    private TemporalAntiAliasingOptions mTemporalAntiAliasingOptions;
    private ScreenSpaceReflectionsOptions mScreenSpaceReflectionsOptions;
    private MultiSampleAntiAliasingOptions mMultiSampleAntiAliasingOptions;
    private VsmShadowOptions mVsmShadowOptions;
    private SoftShadowOptions mSoftShadowOptions;
    private GuardBandOptions mGuardBandOptions;

    public enum TargetBufferFlags {
        COLOR0(0x1),
        COLOR1(0x2),
        COLOR2(0x4),
        COLOR3(0x8),
        DEPTH(0x10),
        STENCIL(0x20);

        public static EnumSet<TargetBufferFlags> NONE = EnumSet.noneOf(TargetBufferFlags.class);
        public static EnumSet<TargetBufferFlags> ALL_COLOR = EnumSet.of(COLOR0, COLOR1, COLOR2, COLOR3);
        public static EnumSet<TargetBufferFlags> DEPTH_STENCIL = EnumSet.of(DEPTH, STENCIL);
        public static EnumSet<TargetBufferFlags> ALL = EnumSet.range(COLOR0, STENCIL);

        private int mFlags;
        TargetBufferFlags(int flags) { mFlags = flags; }
        static int flags(EnumSet<TargetBufferFlags> flags) {
            int result = 0;
            for (TargetBufferFlags flag : flags) result |= flag.mFlags;
            return result;
        }
    }

    public enum QualityLevel { LOW, MEDIUM, HIGH, ULTRA }
    public enum BlendMode { OPAQUE, TRANSLUCENT }
    public enum AntiAliasing { NONE, FXAA }
    public enum Dithering { NONE, TEMPORAL }

    public enum ShadowType { PCF, VSM, DPCF }

    View(long nativeView) {
        mNativeObject = nativeView;
    }

    public void setName(@NotNull String name) {
        mName = name;
        nSetName(getNativeObject(), name);
    }

    @Nullable
    public String getName() { return mName; }

    public void setScene(@Nullable Scene scene) {
        mScene = scene;
        nSetScene(getNativeObject(), scene == null ? 0 : scene.getNativeObject());
    }

    @Nullable
    public Scene getScene() { return mScene; }

    public void setCamera(@Nullable Camera camera) {
        mCamera = camera;
        nSetCamera(getNativeObject(), camera == null ? 0 : camera.getNativeObject());
    }

    @Nullable
    public Camera getCamera() { return mCamera; }

    public boolean hasCamera() { return nHasCamera(getNativeObject()); }

    public void setViewport(@NotNull Viewport viewport) {
        mViewport = viewport;
        nSetViewport(getNativeObject(), viewport.left, viewport.bottom, viewport.width, viewport.height);
    }

    @NotNull
    public Viewport getViewport() { return mViewport; }

    public void setChannelDepthClearEnabled(int channel, boolean enabled) {
        nSetChannelDepthClearEnabled(getNativeObject(), channel, enabled);
    }

    public boolean isChannelDepthClearEnabled(int channel) {
        return nIsChannelDepthClearEnabled(getNativeObject(), channel);
    }

    public void setBlendMode(@NotNull BlendMode blendMode) {
        mBlendMode = blendMode;
        nSetBlendMode(getNativeObject(), blendMode.ordinal());
    }

    @Nullable
    public BlendMode getBlendMode() { return mBlendMode; }

    public void setVisibleLayers(int select, int values) {
        nSetVisibleLayers(getNativeObject(), select & 0xFF, values & 0xFF);
    }

    public int getVisibleLayers() { return nGetVisibleLayers(getNativeObject()); }

    public void setLayerEnabled(int layer, boolean enabled) {
        int mask = 1 << layer;
        setVisibleLayers(mask, enabled ? mask : 0);
    }

    public void setShadowingEnabled(boolean enabled) { nSetShadowingEnabled(getNativeObject(), enabled); }

    public boolean isShadowingEnabled() { return nIsShadowingEnabled(getNativeObject()); }

    public void setFrustumCullingEnabled(boolean enabled) { nSetFrustumCullingEnabled(getNativeObject(), enabled); }

    public boolean isFrustumCullingEnabled() { return nIsFrustumCullingEnabled(getNativeObject()); }

    public void setScreenSpaceRefractionEnabled(boolean enabled) { nSetScreenSpaceRefractionEnabled(getNativeObject(), enabled); }

    public boolean isScreenSpaceRefractionEnabled() { return nIsScreenSpaceRefractionEnabled(getNativeObject()); }

    public void setRenderTarget(@Nullable RenderTarget target) {
        mRenderTarget = target;
        nSetRenderTarget(getNativeObject(), target != null ? target.getNativeObject() : 0);
    }

    @Nullable
    public RenderTarget getRenderTarget() { return mRenderTarget; }


    public void setAntiAliasing(@NotNull AntiAliasing type) { nSetAntiAliasing(getNativeObject(), type.ordinal()); }

    @NotNull
    public AntiAliasing getAntiAliasing() { return sAntiAliasingValues[nGetAntiAliasing(getNativeObject())]; }

    public void setMultiSampleAntiAliasingOptions(@NotNull MultiSampleAntiAliasingOptions options) {
        mMultiSampleAntiAliasingOptions = options;
        nSetMultiSampleAntiAliasingOptions(getNativeObject(), options.enabled, options.sampleCount, options.customResolve);
    }

    @NotNull
    public MultiSampleAntiAliasingOptions getMultiSampleAntiAliasingOptions() {
        if (mMultiSampleAntiAliasingOptions == null) mMultiSampleAntiAliasingOptions = new MultiSampleAntiAliasingOptions();
        return mMultiSampleAntiAliasingOptions;
    }

    public void setTemporalAntiAliasingOptions(@NotNull TemporalAntiAliasingOptions options) {
        mTemporalAntiAliasingOptions = options;
        nSetTemporalAntiAliasingOptions(getNativeObject(), options.filterWidth, options.feedback, options.lodBias, options.sharpness, options.enabled, options.upscaling, options.filterHistory, options.filterInput, options.useYCoCg, options.hdr, options.boxType.ordinal(), options.boxClipping.ordinal(), options.jitterPattern.ordinal(), options.varianceGamma, options.preventFlickering, options.historyReprojection);
    }

    @NotNull
    public TemporalAntiAliasingOptions getTemporalAntiAliasingOptions() {
        if (mTemporalAntiAliasingOptions == null) mTemporalAntiAliasingOptions = new TemporalAntiAliasingOptions();
        return mTemporalAntiAliasingOptions;
    }

    public void setScreenSpaceReflectionsOptions(@NotNull ScreenSpaceReflectionsOptions options) {
        mScreenSpaceReflectionsOptions = options;
        nSetScreenSpaceReflectionsOptions(getNativeObject(), options.thickness, options.bias, options.maxDistance, options.stride, options.enabled);
    }

    @NotNull
    public ScreenSpaceReflectionsOptions getScreenSpaceReflectionsOptions() {
        if (mScreenSpaceReflectionsOptions == null) mScreenSpaceReflectionsOptions = new ScreenSpaceReflectionsOptions();
        return mScreenSpaceReflectionsOptions;
    }

    public void setGuardBandOptions(@NotNull GuardBandOptions options) {
        mGuardBandOptions = options;
        nSetGuardBandOptions(getNativeObject(), options.enabled);
    }

    @NotNull
    public GuardBandOptions getGuardBandOptions() {
        if (mGuardBandOptions == null) mGuardBandOptions = new GuardBandOptions();
        return mGuardBandOptions;
    }

    public void setColorGrading(@Nullable ColorGrading colorGrading) {
        nSetColorGrading(getNativeObject(), colorGrading != null ? colorGrading.getNativeObject() : 0);
        mColorGrading = colorGrading;
    }

    @Nullable
    public ColorGrading getColorGrading() { return mColorGrading; }

    public void setDithering(@NotNull Dithering dithering) { nSetDithering(getNativeObject(), dithering.ordinal()); }

    @NotNull
    public Dithering getDithering() { return sDitheringValues[nGetDithering(getNativeObject())]; }

    public void setDynamicResolutionOptions(@NotNull DynamicResolutionOptions options) {
        mDynamicResolution = options;
        nSetDynamicResolutionOptions(getNativeObject(), options.enabled, options.homogeneousScaling, options.minScale, options.maxScale, options.sharpness, options.quality.ordinal());
    }

    @NotNull
    public DynamicResolutionOptions getDynamicResolutionOptions() {
        if (mDynamicResolution == null) mDynamicResolution = new DynamicResolutionOptions();
        return mDynamicResolution;
    }

    public void getLastDynamicResolutionScale(@NotNull float[] out) { nGetLastDynamicResolutionScale(getNativeObject(), out); }

    public void setRenderQuality(@NotNull RenderQuality renderQuality) {
        mRenderQuality = renderQuality;
        nSetRenderQuality(getNativeObject(), renderQuality.hdrColorBuffer.ordinal());
    }

    @NotNull
    public RenderQuality getRenderQuality() {
        if (mRenderQuality == null) mRenderQuality = new RenderQuality();
        return mRenderQuality;
    }

    public void setPostProcessingEnabled(boolean enabled) { nSetPostProcessingEnabled(getNativeObject(), enabled); }

    public boolean isPostProcessingEnabled() { return nIsPostProcessingEnabled(getNativeObject()); }

    public void setFrontFaceWindingInverted(boolean inverted) { nSetFrontFaceWindingInverted(getNativeObject(), inverted); }

    public boolean isFrontFaceWindingInverted() { return nIsFrontFaceWindingInverted(getNativeObject()); }

    public void setTransparentPickingEnabled(boolean enabled) { nSetTransparentPickingEnabled(getNativeObject(), enabled); }

    public boolean isTransparentPickingEnabled() { return nIsTransparentPickingEnabled(getNativeObject()); }

    public void setDynamicLightingOptions(float zLightNear, float zLightFar) { nSetDynamicLightingOptions(getNativeObject(), zLightNear, zLightFar); }

    public void setShadowType(@NotNull ShadowType type) { nSetShadowType(getNativeObject(), type.ordinal()); }

    @NotNull
    public ShadowType getShadowType() { return sShadowTypeValues[nGetShadowType(getNativeObject())]; }

    public void setVsmShadowOptions(@NotNull VsmShadowOptions options) {
        mVsmShadowOptions = options;
        nSetVsmShadowOptions(getNativeObject(), options.anisotropy, options.mipmapping, options.highPrecision, options.minVarianceScale, options.lightBleedReduction);
    }

    @NotNull
    public VsmShadowOptions getVsmShadowOptions() {
        if (mVsmShadowOptions == null) mVsmShadowOptions = new VsmShadowOptions();
        return mVsmShadowOptions;
    }

    public void setSoftShadowOptions(@NotNull SoftShadowOptions options) {
        mSoftShadowOptions = options;
        nSetSoftShadowOptions(getNativeObject(), options.penumbraScale, options.penumbraRatioScale);
    }

    @NotNull
    public SoftShadowOptions getSoftShadowOptions() {
        if (mSoftShadowOptions == null) mSoftShadowOptions = new SoftShadowOptions();
        return mSoftShadowOptions;
    }


    public void setAmbientOcclusionOptions(@NotNull AmbientOcclusionOptions options) {
        mAmbientOcclusionOptions = options;
        nSetAmbientOcclusionOptions(getNativeObject(), options.radius, options.bias, options.power, options.resolution, options.intensity, options.bilateralThreshold, options.quality.ordinal(), options.lowPassFilter.ordinal(), options.upsampling.ordinal(), options.enabled, options.bentNormals, options.minHorizonAngleRad);
        nSetSSCTOptions(getNativeObject(), options.ssctLightConeRad, options.ssctShadowDistance, options.ssctContactDistanceMax, options.ssctIntensity, options.ssctLightDirection[0], options.ssctLightDirection[1], options.ssctLightDirection[2], options.ssctDepthBias, options.ssctDepthSlopeBias, options.ssctSampleCount, options.ssctRayCount, options.ssctEnabled);
    }

    @NotNull
    public AmbientOcclusionOptions getAmbientOcclusionOptions() {
        if (mAmbientOcclusionOptions == null) mAmbientOcclusionOptions = new AmbientOcclusionOptions();
        return mAmbientOcclusionOptions;
    }

    public void setBloomOptions(@NotNull BloomOptions options) {
        mBloomOptions = options;
        nSetBloomOptions(getNativeObject(), options.dirt != null ? options.dirt.getNativeObject() : 0, options.dirtStrength, options.strength, options.resolution, options.levels, options.blendMode.ordinal(), options.threshold, options.enabled, options.highlight, options.lensFlare, options.starburst, options.chromaticAberration, options.ghostCount, options.ghostSpacing, options.ghostThreshold, options.haloThickness, options.haloRadius, options.haloThreshold);
    }

    @NotNull
    public BloomOptions getBloomOptions() {
        if (mBloomOptions == null) mBloomOptions = new BloomOptions();
        return mBloomOptions;
    }

    public void setVignetteOptions(@NotNull VignetteOptions options) {
        mVignetteOptions = options;
        nSetVignetteOptions(getNativeObject(), options.midPoint, options.roundness, options.feather, options.color[0], options.color[1], options.color[2], options.color[3], options.enabled);
    }

    @NotNull
    public VignetteOptions getVignetteOptions() {
        if (mVignetteOptions == null) mVignetteOptions = new VignetteOptions();
        return mVignetteOptions;
    }

    public void setFogOptions(@NotNull FogOptions options) {
        mFogOptions = options;
        nSetFogOptions(getNativeObject(), options.distance, options.maximumOpacity, options.height, options.heightFalloff, options.cutOffDistance, options.color[0], options.color[1], options.color[2], options.density, options.inScatteringStart, options.inScatteringSize, options.fogColorFromIbl, options.skyColor == null ? 0 : options.skyColor.getNativeObject(), options.enabled);
    }

    @NotNull
    public FogOptions getFogOptions() {
        if (mFogOptions == null) mFogOptions = new FogOptions();
        return mFogOptions;
    }

    public void setDepthOfFieldOptions(@NotNull DepthOfFieldOptions options) {
        mDepthOfFieldOptions = options;
        nSetDepthOfFieldOptions(getNativeObject(), options.cocScale, options.maxApertureDiameter, options.enabled, options.filter.ordinal(), options.nativeResolution, options.foregroundRingCount, options.backgroundRingCount, options.fastGatherRingCount, options.maxForegroundCOC, options.maxBackgroundCOC);
    }

    @NotNull
    public DepthOfFieldOptions getDepthOfFieldOptions() {
        if (mDepthOfFieldOptions == null) mDepthOfFieldOptions = new DepthOfFieldOptions();
        return mDepthOfFieldOptions;
    }

    public void setStencilBufferEnabled(boolean enabled) { nSetStencilBufferEnabled(getNativeObject(), enabled); }

    public boolean isStencilBufferEnabled() { return nIsStencilBufferEnabled(getNativeObject()); }

    public void setStereoscopicOptions(@NotNull StereoscopicOptions options) {
        mStereoscopicOptions = options;
        nSetStereoscopicOptions(getNativeObject(), options.enabled);
    }

    @NotNull
    public StereoscopicOptions getStereoscopicOptions() {
        if (mStereoscopicOptions == null) mStereoscopicOptions = new StereoscopicOptions();
        return mStereoscopicOptions;
    }

    public void setMaterialGlobal(int index, @NotNull float[] value) {
        nSetMaterialGlobal(getNativeObject(), index, value[0], value[1], value[2], value[3]);
    }

    public void getMaterialGlobal(int index, @NotNull float[] out) {
        nGetMaterialGlobal(getNativeObject(), index, out);
    }

    public int getFogEntity() { return nGetFogEntity(getNativeObject()); }

    public void clearFrameHistory(@NotNull Engine engine) { nClearFrameHistory(getNativeObject(), engine.getNativeObject()); }

    public void pick(int x, int y, @Nullable Object handler, @Nullable OnPickCallback callback) {
        InternalOnPickCallback internalCallback = new InternalOnPickCallback(callback);
        nPick(getNativeObject(), x, y, handler, internalCallback);
    }

    public static class DynamicResolutionOptions {
        public float minScale = 0.5f;
        public float maxScale = 1.0f;
        public float sharpness = 0.9f;
        public boolean enabled = false;
        public boolean homogeneousScaling = false;
        @NotNull public QualityLevel quality = QualityLevel.LOW;
    }

    public static class BloomOptions {
        public enum BlendMode { ADD, INTERPOLATE }
        @Nullable public Texture dirt = null;
        public float dirtStrength = 0.2f;
        public float strength = 0.10f;
        public int resolution = 384;
        public int levels = 6;
        @NotNull public BlendMode blendMode = BlendMode.ADD;
        public boolean threshold = true;
        public boolean enabled = false;
        public float highlight = 1000.0f;
        @NotNull public QualityLevel quality = QualityLevel.LOW;
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
        @NotNull public float[] color = {1.0f, 1.0f, 1.0f};
        public float density = 0.1f;
        public float inScatteringStart = 0.0f;
        public float inScatteringSize = -1.0f;
        public boolean fogColorFromIbl = false;
        @Nullable public Texture skyColor = null;
        public boolean enabled = false;
    }

    public static class VignetteOptions {
        public float midPoint = 0.5f;
        public float roundness = 0.5f;
        public float feather = 0.5f;
        @NotNull public float[] color = {0.0f, 0.0f, 0.0f, 1.0f};
        public boolean enabled = false;
    }

    public static class RenderQuality {
        @NotNull public QualityLevel hdrColorBuffer = QualityLevel.HIGH;
    }

    public static class StereoscopicOptions { public boolean enabled = false; }

    public static class GuardBandOptions { public boolean enabled = false; }

    public static class AmbientOcclusionOptions {
        public enum AmbientOcclusionType { SAO, GTAO }
        @NotNull public AmbientOcclusionType aoType = AmbientOcclusionType.SAO;
        public float radius = 0.3f;
        public float power = 1.0f;
        public float bias = 0.0005f;
        public float resolution = 0.5f;
        public float intensity = 1.0f;
        public float bilateralThreshold = 0.05f;
        @NotNull public QualityLevel quality = QualityLevel.LOW;
        @NotNull public QualityLevel lowPassFilter = QualityLevel.MEDIUM;
        @NotNull public QualityLevel upsampling = QualityLevel.LOW;
        public boolean enabled = false;
        public boolean bentNormals = false;
        public float minHorizonAngleRad = 0.0f;
        public float ssctLightConeRad = 1.0f;
        public float ssctShadowDistance = 0.3f;
        public float ssctContactDistanceMax = 1.0f;
        public float ssctIntensity = 0.8f;
        @NotNull public float[] ssctLightDirection = {0f, -1f, 0f};
        public float ssctDepthBias = 0.01f;
        public float ssctDepthSlopeBias = 0.01f;
        public int ssctSampleCount = 4;
        public int ssctRayCount = 1;
        public boolean ssctEnabled = false;
    }

    public static class TemporalAntiAliasingOptions {
        public enum BoxType { AABB, AABB_VARIANCE }
        public enum BoxClipping { ACCURATE, CLAMP, NONE }
        public enum JitterPattern { RGSS_X4, UNIFORM_HELIX_X4, HALTON_23_X8, HALTON_23_X16, HALTON_23_X32 }
        public float filterWidth = 1.0f;
        public float feedback = 0.12f;
        public float lodBias = -1.0f;
        public float sharpness = 0.0f;
        public boolean enabled = false;
        public float upscaling = 1.0f;
        public boolean filterHistory = true;
        public boolean filterInput = true;
        public boolean useYCoCg = false;
        public boolean hdr = true;
        @NotNull public BoxType boxType = BoxType.AABB;
        @NotNull public BoxClipping boxClipping = BoxClipping.ACCURATE;
        @NotNull public JitterPattern jitterPattern = JitterPattern.HALTON_23_X16;
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
        @NotNull public Filter filter = Filter.MEDIAN;
        public boolean nativeResolution = false;
        public int foregroundRingCount = 0;
        public int backgroundRingCount = 0;
        public int fastGatherRingCount = 0;
        public int maxForegroundCOC = 0;
        public int maxBackgroundCOC = 0;
    }

    public static class VsmShadowOptions {
        public int anisotropy = 0;
        public boolean mipmapping = false;
        public boolean highPrecision = false;
        public float minVarianceScale = 0.5f;
        public float lightBleedReduction = 0.35f;
    }

    public static class SoftShadowOptions {
        public float penumbraScale = 1.0f;
        public float penumbraRatioScale = 1.0f;
    }

    public static class PickingQueryResult {
        @io.github.erkko68.filament.jni.Entity public int renderable;
        public float depth;
        @NotNull public float[] fragCoords = new float[3];
    }

    public interface OnPickCallback {
        void onPick(@NotNull PickingQueryResult result);
    }

    @UsedByNative("View.cpp")
    private static class InternalOnPickCallback implements Runnable {
        private final OnPickCallback mUserCallback;
        private final PickingQueryResult mPickingQueryResult = new PickingQueryResult();
        @UsedByNative("View.cpp") @io.github.erkko68.filament.jni.Entity int mRenderable;
        @UsedByNative("View.cpp") float mDepth;
        @UsedByNative("View.cpp") float mFragCoordsX;
        @UsedByNative("View.cpp") float mFragCoordsY;
        @UsedByNative("View.cpp") float mFragCoordsZ;
        public InternalOnPickCallback(OnPickCallback mUserCallback) { this.mUserCallback = mUserCallback; }
        @Override public void run() {
            if (mUserCallback == null) return;
            mPickingQueryResult.renderable = mRenderable;
            mPickingQueryResult.depth = mDepth;
            mPickingQueryResult.fragCoords[0] = mFragCoordsX;
            mPickingQueryResult.fragCoords[1] = mFragCoordsY;
            mPickingQueryResult.fragCoords[2] = mFragCoordsZ;
            mUserCallback.onPick(mPickingQueryResult);
        }
    }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Calling method on destroyed View");
        return mNativeObject;
    }

    void clearNativeObject() { mNativeObject = 0; }

    private static native void nSetName(long nativeView, String name);
    private static native void nSetScene(long nativeView, long nativeScene);
    private static native void nSetCamera(long nativeView, long nativeCamera);
    private static native boolean nHasCamera(long nativeView);
    private static native void nSetViewport(long nativeView, int left, int bottom, int width, int height);
    private static native void nSetVisibleLayers(long nativeView, int select, int value);
    private static native int nGetVisibleLayers(long nativeView);
    private static native void nSetFrustumCullingEnabled(long nativeView, boolean enabled);
    private static native boolean nIsFrustumCullingEnabled(long nativeView);
    private static native void nSetShadowingEnabled(long nativeView, boolean enabled);
    private static native boolean nIsShadowingEnabled(long nativeView);
    private static native void nSetRenderTarget(long nativeView, long nativeRenderTarget);
    private static native void nSetAntiAliasing(long nativeView, int type);
    private static native int nGetAntiAliasing(long nativeView);
    private static native void nSetDithering(long nativeView, int dithering);
    private static native int nGetDithering(long nativeView);
    private static native void nSetDynamicResolutionOptions(long nativeView, boolean enabled, boolean homogeneousScaling, float minScale, float maxScale, float sharpness, int quality);
    private static native void nGetLastDynamicResolutionScale(long nativeView, float[] out);
    private static native void nSetRenderQuality(long nativeView, int hdrColorBufferQuality);
    private static native void nSetDynamicLightingOptions(long nativeView, float zLightNear, float zLightFar);
    private static native void nSetShadowType(long nativeView, int type);
    private static native int nGetShadowType(long nativeView);
    private static native void nSetVsmShadowOptions(long nativeView, int anisotropy, boolean mipmapping, boolean highPrecision, float minVarianceScale, float lightBleedReduction);
    private static native void nSetSoftShadowOptions(long nativeView, float penumbraScale, float penumbraRatioScale);
    private static native void nSetColorGrading(long nativeView, long nativeColorGrading);
    private static native void nSetPostProcessingEnabled(long nativeView, boolean enabled);
    private static native boolean nIsPostProcessingEnabled(long nativeView);
    private static native void nSetFrontFaceWindingInverted(long nativeView, boolean inverted);
    private static native boolean nIsFrontFaceWindingInverted(long nativeView);
    private static native void nSetTransparentPickingEnabled(long nativeView, boolean enabled);
    private static native boolean nIsTransparentPickingEnabled(long nativeView);
    private static native void nSetAmbientOcclusionOptions(long nativeView, float radius, float bias, float power, float resolution, float intensity, float bilateralThreshold, int quality, int lowPassFilter, int upsampling, boolean enabled, boolean bentNormals, float minHorizonAngleRad);
    private static native void nSetSSCTOptions(long nativeView, float ssctLightConeRad, float ssctStartTraceDistance, float ssctContactDistanceMax, float ssctIntensity, float v, float v1, float v2, float ssctDepthBias, float ssctDepthSlopeBias, int ssctSampleCount, int ssctRayCount, boolean ssctEnabled);
    private static native void nSetBloomOptions(long nativeView, long dirtNativeObject, float dirtStrength, float strength, int resolution, int levels, int blendMode, boolean threshold, boolean enabled, float highlight, boolean lensFlare, boolean starburst, float chromaticAberration, int ghostCount, float ghostSpacing, float ghostThreshold, float haloThickness, float haloRadius, float haloThreshold);
    private static native void nSetFogOptions(long nativeView, float distance, float maximumOpacity, float height, float heightFalloff, float cutOffDistance, float v, float v1, float v2, float density, float inScatteringStart, float inScatteringSize, boolean fogColorFromIbl, long skyColorNativeObject, boolean enabled);
    private static native void nSetStereoscopicOptions(long nativeView, boolean enabled);
    private static native void nSetChannelDepthClearEnabled(long nativeView, int channel, boolean enabled);
    private static native boolean nIsChannelDepthClearEnabled(long nativeView, int channel);
    private static native void nSetBlendMode(long nativeView, int blendMode);
    private static native void nSetDepthOfFieldOptions(long nativeView, float cocScale, float maxApertureDiameter, boolean enabled, int filter, boolean nativeResolution, int foregroundRingCount, int backgroundRingCount, int fastGatherRingCount, int maxForegroundCOC, int maxBackgroundCOC);
    private static native void nSetVignetteOptions(long nativeView, float midPoint, float roundness, float feather, float r, float g, float b, float a, boolean enabled);
    private static native void nSetTemporalAntiAliasingOptions(long nativeView, float filterWidth, float feedback, float lodBias, float sharpness, boolean enabled, float upscaling, boolean filterHistory, boolean filterInput, boolean useYCoCg, boolean hdr, int boxType, int boxClipping, int jitterPattern, float varianceGamma, boolean preventFlickering, boolean historyReprojection);
    private static native void nSetScreenSpaceReflectionsOptions(long nativeView, float thickness, float bias, float maxDistance, float stride, boolean enabled);
    private static native void nSetMultiSampleAntiAliasingOptions(long nativeView, boolean enabled, int sampleCount, boolean customResolve);
    private static native void nSetScreenSpaceRefractionEnabled(long nativeView, boolean enabled);
    private static native void nSetGuardBandOptions(long nativeView, boolean enabled);
    private static native boolean nIsScreenSpaceRefractionEnabled(long nativeView);
    private static native void nPick(long nativeView, int x, int y, Object handler, InternalOnPickCallback internalCallback);
    private static native void nSetStencilBufferEnabled(long nativeView, boolean enabled);
    private static native boolean nIsStencilBufferEnabled(long nativeView);
    private static native void nSetMaterialGlobal(long nativeView, int index, float x, float y, float z, float w);
    private static native void nGetMaterialGlobal(long nativeView, int index, float[] out);
    private static native int nGetFogEntity(long nativeView);
    private static native void nClearFrameHistory(long nativeView, long nativeEngine);
}
