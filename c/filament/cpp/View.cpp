#include <filament/View.h>
#include <filament/Scene.h>
#include <filament/Camera.h>
#include <filament/ColorGrading.h>
#include <filament/RenderTarget.h>
#include <filament/Texture.h>
#include <filament/Engine.h>
#include <filament/Viewport.h>

#include <math/vec2.h>
#include <math/vec3.h>
#include <math/vec4.h>

#include <algorithm>

#include "FilaCommon.h"
#include "../c/View.h"

using namespace filament;

extern "C" {

void FilaView_setName(FilaView* view, const char* name) {
    FILA_CAST(View, view)->setName(name);
}

const char* FilaView_getName(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->getName();
}

void FilaView_setScene(FilaView* view, FilaScene* scene) {
    FILA_CAST(View, view)->setScene(FILA_CAST(Scene, scene));
}

void FilaView_setCamera(FilaView* view, FilaCamera* camera) {
    FILA_CAST(View, view)->setCamera(FILA_CAST(Camera, camera));
}

bool FilaView_hasCamera(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->hasCamera();
}

void FilaView_setColorGrading(FilaView* view, FilaColorGrading* colorGrading) {
    FILA_CAST(View, view)->setColorGrading(FILA_CAST(ColorGrading, colorGrading));
}

void FilaView_setViewport(FilaView* view, int left, int bottom, uint32_t width, uint32_t height) {
    FILA_CAST(View, view)->setViewport({left, bottom, width, height});
}

void FilaView_getViewport(const FilaView* view, int* left, int* bottom, uint32_t* width, uint32_t* height) {
    const auto& vp = FILA_CONST_CAST(View, view)->getViewport();
    if (left)   *left   = vp.left;
    if (bottom) *bottom = vp.bottom;
    if (width)  *width  = vp.width;
    if (height) *height = vp.height;
}

void FilaView_setVisibleLayers(FilaView* view, uint32_t select, uint32_t value) {
    FILA_CAST(View, view)->setVisibleLayers(select, value);
}

uint8_t FilaView_getVisibleLayers(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->getVisibleLayers();
}

void FilaView_setRenderTarget(FilaView* view, FilaRenderTarget* renderTarget) {
    FILA_CAST(View, view)->setRenderTarget(FILA_CAST(RenderTarget, renderTarget));
}

void FilaView_setAntiAliasing(FilaView* view, FilaViewAntiAliasing type) {
    FILA_CAST(View, view)->setAntiAliasing(static_cast<View::AntiAliasing>(type));
}

FilaViewAntiAliasing FilaView_getAntiAliasing(const FilaView* view) {
    return static_cast<FilaViewAntiAliasing>(FILA_CONST_CAST(View, view)->getAntiAliasing());
}

void FilaView_setDithering(FilaView* view, FilaViewDithering dithering) {
    FILA_CAST(View, view)->setDithering(static_cast<View::Dithering>(dithering));
}

FilaViewDithering FilaView_getDithering(const FilaView* view) {
    return static_cast<FilaViewDithering>(FILA_CONST_CAST(View, view)->getDithering());
}

static void FilaView_setDynamicResolutionOptions_struct(FilaView* view, const FilaViewDynamicResolutionOptions* options) {
    View::DynamicResolutionOptions cppOptions;
    cppOptions.minScale = {options->minScale[0], options->minScale[1]};
    cppOptions.maxScale = {options->maxScale[0], options->maxScale[1]};
    cppOptions.sharpness = options->sharpness;
    cppOptions.enabled = options->enabled;
    cppOptions.homogeneousScaling = options->homogeneousScaling;
    cppOptions.quality = static_cast<View::QualityLevel>(options->quality);
    FILA_CAST(View, view)->setDynamicResolutionOptions(cppOptions);
}

static void FilaView_getDynamicResolutionOptions_struct(const FilaView* view, FilaViewDynamicResolutionOptions* out) {
    const View::DynamicResolutionOptions& opts = FILA_CONST_CAST(View, view)->getDynamicResolutionOptions();
    out->minScale[0] = opts.minScale.x; out->minScale[1] = opts.minScale.y;
    out->maxScale[0] = opts.maxScale.x; out->maxScale[1] = opts.maxScale.y;
    out->sharpness = opts.sharpness;
    out->enabled = opts.enabled;
    out->homogeneousScaling = opts.homogeneousScaling;
    out->quality = static_cast<FilaViewQualityLevel>(opts.quality);
}

void FilaView_getLastDynamicResolutionScale(const FilaView* view, float out[2]) {
    filament::math::float2 scale = FILA_CONST_CAST(View, view)->getLastDynamicResolutionScale();
    out[0] = scale.x; out[1] = scale.y;
}

void FilaView_setShadowType(FilaView* view, FilaViewShadowType type) {
    FILA_CAST(View, view)->setShadowType(static_cast<View::ShadowType>(type));
}

static void FilaView_setVsmShadowOptions_struct(FilaView* view, const FilaViewVsmShadowOptions* options) {
    View::VsmShadowOptions cppOptions;
    cppOptions.anisotropy = options->anisotropy;
    cppOptions.mipmapping = options->mipmapping;
    cppOptions.msaaSamples = options->msaaSamples;
    cppOptions.highPrecision = options->highPrecision;
    cppOptions.lightBleedReduction = options->lightBleedReduction;
    FILA_CAST(View, view)->setVsmShadowOptions(cppOptions);
}

static void FilaView_setSoftShadowOptions_struct(FilaView* view, const FilaViewSoftShadowOptions* options) {
    View::SoftShadowOptions cppOptions;
    cppOptions.penumbraScale = options->penumbraScale;
    cppOptions.penumbraRatioScale = options->penumbraRatioScale;
    cppOptions.maxPenumbraRatio = options->maxPenumbraRatio;
    cppOptions.maxSearchRadius = options->maxSearchRadius;
    FILA_CAST(View, view)->setSoftShadowOptions(cppOptions);
}

static void FilaView_getVsmShadowOptions_struct(const FilaView* view, FilaViewVsmShadowOptions* out) {
    const View::VsmShadowOptions& opts = FILA_CONST_CAST(View, view)->getVsmShadowOptions();
    out->anisotropy = opts.anisotropy;
    out->mipmapping = opts.mipmapping;
    out->msaaSamples = opts.msaaSamples;
    out->highPrecision = opts.highPrecision;
    out->lightBleedReduction = opts.lightBleedReduction;
}

static void FilaView_getSoftShadowOptions_struct(const FilaView* view, FilaViewSoftShadowOptions* out) {
    const View::SoftShadowOptions& opts = FILA_CONST_CAST(View, view)->getSoftShadowOptions();
    out->penumbraScale = opts.penumbraScale;
    out->penumbraRatioScale = opts.penumbraRatioScale;
    out->maxPenumbraRatio = opts.maxPenumbraRatio;
    out->maxSearchRadius = opts.maxSearchRadius;
}

void FilaView_setRenderQuality(FilaView* view, FilaViewQualityLevel hdrColorBufferQuality) {
    View::RenderQuality renderQuality;
    renderQuality.hdrColorBuffer = static_cast<View::QualityLevel>(hdrColorBufferQuality);
    FILA_CAST(View, view)->setRenderQuality(renderQuality);
}

FilaViewQualityLevel FilaView_getRenderQuality(const FilaView* view) {
    return static_cast<FilaViewQualityLevel>(FILA_CONST_CAST(View, view)->getRenderQuality().hdrColorBuffer);
}

void FilaView_setDynamicLightingOptions(FilaView* view, float zLightNear, float zLightFar) {
    FILA_CAST(View, view)->setDynamicLightingOptions(zLightNear, zLightFar);
}

void FilaView_setShadowingEnabled(FilaView* view, bool enabled) {
    FILA_CAST(View, view)->setShadowingEnabled(enabled);
}

bool FilaView_isShadowingEnabled(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->isShadowingEnabled();
}

void FilaView_setPostProcessingEnabled(FilaView* view, bool enabled) {
    FILA_CAST(View, view)->setPostProcessingEnabled(enabled);
}

bool FilaView_isPostProcessingEnabled(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->isPostProcessingEnabled();
}

void FilaView_setFrontFaceWindingInverted(FilaView* view, bool inverted) {
    FILA_CAST(View, view)->setFrontFaceWindingInverted(inverted);
}

bool FilaView_isFrontFaceWindingInverted(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->isFrontFaceWindingInverted();
}

void FilaView_setTransparentPickingEnabled(FilaView* view, bool enabled) {
    FILA_CAST(View, view)->setTransparentPickingEnabled(enabled);
}

bool FilaView_isTransparentPickingEnabled(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->isTransparentPickingEnabled();
}

static void FilaView_setAmbientOcclusionOptions_struct(FilaView* view, const FilaViewAmbientOcclusionOptions* options) {
    View::AmbientOcclusionOptions cppOptions;
    cppOptions.radius = options->radius;
    cppOptions.bias = options->bias;
    cppOptions.power = options->power;
    cppOptions.resolution = options->resolution;
    cppOptions.intensity = options->intensity;
    cppOptions.bilateralThreshold = options->bilateralThreshold;
    cppOptions.quality = static_cast<View::QualityLevel>(options->quality);
    cppOptions.lowPassFilter = static_cast<View::QualityLevel>(options->lowPassFilter);
    cppOptions.upsampling = static_cast<View::QualityLevel>(options->upsampling);
    cppOptions.enabled = options->enabled;
    cppOptions.bentNormals = options->bentNormals;
    cppOptions.minHorizonAngleRad = options->minHorizonAngleRad;
    cppOptions.ssct.lightConeRad = options->ssct.lightConeRad;
    cppOptions.ssct.shadowDistance = options->ssct.shadowDistance;
    cppOptions.ssct.contactDistanceMax = options->ssct.contactDistanceMax;
    cppOptions.ssct.intensity = options->ssct.intensity;
    cppOptions.ssct.lightDirection = {options->ssct.lightDirection[0], options->ssct.lightDirection[1], options->ssct.lightDirection[2]};
    cppOptions.ssct.depthBias = options->ssct.depthBias;
    cppOptions.ssct.depthSlopeBias = options->ssct.depthSlopeBias;
    cppOptions.ssct.sampleCount = options->ssct.sampleCount;
    cppOptions.ssct.rayCount = options->ssct.rayCount;
    cppOptions.ssct.enabled = options->ssct.enabled;
    cppOptions.gtao.sampleSliceCount = options->gtao.sampleSliceCount;
    cppOptions.gtao.sampleStepsPerSlice = options->gtao.sampleStepsPerSlice;
    cppOptions.gtao.thicknessHeuristic = options->gtao.thicknessHeuristic;
    cppOptions.gtao.useVisibilityBitmasks = options->gtao.useVisibilityBitmasks;
    cppOptions.gtao.constThickness = options->gtao.constThickness;
    cppOptions.gtao.linearThickness = options->gtao.linearThickness;
    cppOptions.aoType = static_cast<View::AmbientOcclusionOptions::AmbientOcclusionType>(options->aoType);
    FILA_CAST(View, view)->setAmbientOcclusionOptions(cppOptions);
}

static void FilaView_getAmbientOcclusionOptions_struct(const FilaView* view, FilaViewAmbientOcclusionOptions* out) {
    const View::AmbientOcclusionOptions& cppOptions = FILA_CONST_CAST(View, view)->getAmbientOcclusionOptions();
    out->radius = cppOptions.radius;
    out->bias = cppOptions.bias;
    out->power = cppOptions.power;
    out->resolution = cppOptions.resolution;
    out->intensity = cppOptions.intensity;
    out->bilateralThreshold = cppOptions.bilateralThreshold;
    out->quality = static_cast<FilaViewQualityLevel>(cppOptions.quality);
    out->lowPassFilter = static_cast<FilaViewQualityLevel>(cppOptions.lowPassFilter);
    out->upsampling = static_cast<FilaViewQualityLevel>(cppOptions.upsampling);
    out->enabled = cppOptions.enabled;
    out->bentNormals = cppOptions.bentNormals;
    out->minHorizonAngleRad = cppOptions.minHorizonAngleRad;
    out->ssct.lightConeRad = cppOptions.ssct.lightConeRad;
    out->ssct.shadowDistance = cppOptions.ssct.shadowDistance;
    out->ssct.contactDistanceMax = cppOptions.ssct.contactDistanceMax;
    out->ssct.intensity = cppOptions.ssct.intensity;
    out->ssct.lightDirection[0] = cppOptions.ssct.lightDirection.x;
    out->ssct.lightDirection[1] = cppOptions.ssct.lightDirection.y;
    out->ssct.lightDirection[2] = cppOptions.ssct.lightDirection.z;
    out->ssct.depthBias = cppOptions.ssct.depthBias;
    out->ssct.depthSlopeBias = cppOptions.ssct.depthSlopeBias;
    out->ssct.sampleCount = cppOptions.ssct.sampleCount;
    out->ssct.rayCount = cppOptions.ssct.rayCount;
    out->ssct.enabled = cppOptions.ssct.enabled;
    out->gtao.sampleSliceCount = cppOptions.gtao.sampleSliceCount;
    out->gtao.sampleStepsPerSlice = cppOptions.gtao.sampleStepsPerSlice;
    out->gtao.thicknessHeuristic = cppOptions.gtao.thicknessHeuristic;
    out->gtao.useVisibilityBitmasks = cppOptions.gtao.useVisibilityBitmasks;
    out->gtao.constThickness = cppOptions.gtao.constThickness;
    out->gtao.linearThickness = cppOptions.gtao.linearThickness;
    out->aoType = static_cast<int>(cppOptions.aoType);
}

static void FilaView_setBloomOptions_struct(FilaView* view, const FilaViewBloomOptions* options) {
    View::BloomOptions cppOptions;
    cppOptions.dirt = FILA_CAST(Texture, options->dirt);
    cppOptions.dirtStrength = options->dirtStrength;
    cppOptions.strength = options->strength;
    cppOptions.resolution = options->resolution;
    cppOptions.levels = options->levels;
    cppOptions.blendMode = static_cast<View::BloomOptions::BlendMode>(options->blendMode);
    cppOptions.threshold = options->threshold;
    cppOptions.enabled = options->enabled;
    cppOptions.highlight = options->highlight;
    cppOptions.quality = static_cast<View::QualityLevel>(options->quality);
    cppOptions.lensFlare = options->lensFlare;
    cppOptions.starburst = options->starburst;
    cppOptions.chromaticAberration = options->chromaticAberration;
    cppOptions.ghostCount = options->ghostCount;
    cppOptions.ghostSpacing = options->ghostSpacing;
    cppOptions.ghostThreshold = options->ghostThreshold;
    cppOptions.haloThickness = options->haloThickness;
    cppOptions.haloRadius = options->haloRadius;
    cppOptions.haloThreshold = options->haloThreshold;
    FILA_CAST(View, view)->setBloomOptions(cppOptions);
}

static void FilaView_getBloomOptions_struct(const FilaView* view, FilaViewBloomOptions* out) {
    const View::BloomOptions& cppOptions = FILA_CONST_CAST(View, view)->getBloomOptions();
    out->dirt = reinterpret_cast<FilaTexture*>(const_cast<Texture*>(cppOptions.dirt));
    out->dirtStrength = cppOptions.dirtStrength;
    out->strength = cppOptions.strength;
    out->resolution = cppOptions.resolution;
    out->levels = cppOptions.levels;
    out->blendMode = static_cast<int>(cppOptions.blendMode);
    out->threshold = cppOptions.threshold;
    out->enabled = cppOptions.enabled;
    out->highlight = cppOptions.highlight;
    out->quality = static_cast<FilaViewQualityLevel>(cppOptions.quality);
    out->lensFlare = cppOptions.lensFlare;
    out->starburst = cppOptions.starburst;
    out->chromaticAberration = cppOptions.chromaticAberration;
    out->ghostCount = cppOptions.ghostCount;
    out->ghostSpacing = cppOptions.ghostSpacing;
    out->ghostThreshold = cppOptions.ghostThreshold;
    out->haloThickness = cppOptions.haloThickness;
    out->haloRadius = cppOptions.haloRadius;
    out->haloThreshold = cppOptions.haloThreshold;
}

static void FilaView_setFogOptions_struct(FilaView* view, const FilaViewFogOptions* options) {
    View::FogOptions cppOptions;
    cppOptions.distance = options->distance;
    cppOptions.cutOffDistance = options->cutOffDistance;
    cppOptions.maximumOpacity = options->maximumOpacity;
    cppOptions.height = options->height;
    cppOptions.heightFalloff = options->heightFalloff;
    cppOptions.color = {options->color[0], options->color[1], options->color[2]};
    cppOptions.density = options->density;
    cppOptions.inScatteringStart = options->inScatteringStart;
    cppOptions.inScatteringSize = options->inScatteringSize;
    cppOptions.fogColorFromIbl = options->fogColorFromIbl;
    cppOptions.skyColor = FILA_CAST(Texture, options->skyColor);
    cppOptions.enabled = options->enabled;
    FILA_CAST(View, view)->setFogOptions(cppOptions);
}

static void FilaView_getFogOptions_struct(const FilaView* view, FilaViewFogOptions* out) {
    const View::FogOptions& cppOptions = FILA_CONST_CAST(View, view)->getFogOptions();
    out->distance = cppOptions.distance;
    out->cutOffDistance = cppOptions.cutOffDistance;
    out->maximumOpacity = cppOptions.maximumOpacity;
    out->height = cppOptions.height;
    out->heightFalloff = cppOptions.heightFalloff;
    out->color[0] = cppOptions.color.r; out->color[1] = cppOptions.color.g; out->color[2] = cppOptions.color.b;
    out->density = cppOptions.density;
    out->inScatteringStart = cppOptions.inScatteringStart;
    out->inScatteringSize = cppOptions.inScatteringSize;
    out->fogColorFromIbl = cppOptions.fogColorFromIbl;
    out->skyColor = reinterpret_cast<FilaTexture*>(const_cast<Texture*>(cppOptions.skyColor));
    out->enabled = cppOptions.enabled;
}

void FilaView_setBlendMode(FilaView* view, FilaViewBlendMode blendMode) {
    FILA_CAST(View, view)->setBlendMode(static_cast<View::BlendMode>(blendMode));
}

FilaViewBlendMode FilaView_getBlendMode(const FilaView* view) {
    return static_cast<FilaViewBlendMode>(FILA_CONST_CAST(View, view)->getBlendMode());
}

static void FilaView_setDepthOfFieldOptions_struct(FilaView* view, const FilaViewDepthOfFieldOptions* options) {
    View::DepthOfFieldOptions cppOptions;
    cppOptions.cocScale = options->cocScale;
    cppOptions.cocAspectRatio = options->cocAspectRatio;
    cppOptions.maxApertureDiameter = options->maxApertureDiameter;
    cppOptions.enabled = options->enabled;
    cppOptions.filter = static_cast<View::DepthOfFieldOptions::Filter>(options->filter);
    cppOptions.nativeResolution = options->nativeResolution;
    cppOptions.foregroundRingCount = options->foregroundRingCount;
    cppOptions.backgroundRingCount = options->backgroundRingCount;
    cppOptions.fastGatherRingCount = options->fastGatherRingCount;
    cppOptions.maxForegroundCOC = options->maxForegroundCOC;
    cppOptions.maxBackgroundCOC = options->maxBackgroundCOC;
    FILA_CAST(View, view)->setDepthOfFieldOptions(cppOptions);
}

static void FilaView_getDepthOfFieldOptions_struct(const FilaView* view, FilaViewDepthOfFieldOptions* out) {
    const View::DepthOfFieldOptions& cppOptions = FILA_CONST_CAST(View, view)->getDepthOfFieldOptions();
    out->cocScale = cppOptions.cocScale;
    out->cocAspectRatio = cppOptions.cocAspectRatio;
    out->maxApertureDiameter = cppOptions.maxApertureDiameter;
    out->enabled = cppOptions.enabled;
    out->filter = static_cast<int>(cppOptions.filter);
    out->nativeResolution = cppOptions.nativeResolution;
    out->foregroundRingCount = cppOptions.foregroundRingCount;
    out->backgroundRingCount = cppOptions.backgroundRingCount;
    out->fastGatherRingCount = cppOptions.fastGatherRingCount;
    out->maxForegroundCOC = cppOptions.maxForegroundCOC;
    out->maxBackgroundCOC = cppOptions.maxBackgroundCOC;
}

static void FilaView_setVignetteOptions_struct(FilaView* view, const FilaViewVignetteOptions* options) {
    View::VignetteOptions cppOptions;
    cppOptions.midPoint = options->midPoint;
    cppOptions.roundness = options->roundness;
    cppOptions.feather = options->feather;
    cppOptions.color = {options->color[0], options->color[1], options->color[2], options->color[3]};
    cppOptions.enabled = options->enabled;
    FILA_CAST(View, view)->setVignetteOptions(cppOptions);
}

static void FilaView_getVignetteOptions_struct(const FilaView* view, FilaViewVignetteOptions* out) {
    const View::VignetteOptions& cppOptions = FILA_CONST_CAST(View, view)->getVignetteOptions();
    out->midPoint = cppOptions.midPoint;
    out->roundness = cppOptions.roundness;
    out->feather = cppOptions.feather;
    out->color[0] = cppOptions.color.r; out->color[1] = cppOptions.color.g; out->color[2] = cppOptions.color.b; out->color[3] = cppOptions.color.a;
    out->enabled = cppOptions.enabled;
}

static void FilaView_setTemporalAntiAliasingOptions_struct(FilaView* view, const FilaViewTemporalAntiAliasingOptions* options) {
    View::TemporalAntiAliasingOptions cppOptions;
    cppOptions.feedback = options->feedback;
    cppOptions.lodBias = options->lodBias;
    cppOptions.sharpness = options->sharpness;
    cppOptions.enabled = options->enabled;
    cppOptions.upscaling = options->upscaling;
    cppOptions.filterHistory = options->filterHistory;
    cppOptions.filterInput = options->filterInput;
    cppOptions.useYCoCg = options->useYCoCg;
    cppOptions.hdr = options->hdr;
    cppOptions.boxType = static_cast<View::TemporalAntiAliasingOptions::BoxType>(options->boxType);
    cppOptions.boxClipping = static_cast<View::TemporalAntiAliasingOptions::BoxClipping>(options->boxClipping);
    cppOptions.jitterPattern = static_cast<View::TemporalAntiAliasingOptions::JitterPattern>(options->jitterPattern);
    cppOptions.varianceGamma = options->varianceGamma;
    cppOptions.preventFlickering = options->preventFlickering;
    cppOptions.historyReprojection = options->historyReprojection;
    FILA_CAST(View, view)->setTemporalAntiAliasingOptions(cppOptions);
}

static void FilaView_getTemporalAntiAliasingOptions_struct(const FilaView* view, FilaViewTemporalAntiAliasingOptions* out) {
    const View::TemporalAntiAliasingOptions& cppOptions = FILA_CONST_CAST(View, view)->getTemporalAntiAliasingOptions();
    out->feedback = cppOptions.feedback;
    out->lodBias = cppOptions.lodBias;
    out->sharpness = cppOptions.sharpness;
    out->enabled = cppOptions.enabled;
    out->upscaling = cppOptions.upscaling;
    out->filterHistory = cppOptions.filterHistory;
    out->filterInput = cppOptions.filterInput;
    out->useYCoCg = cppOptions.useYCoCg;
    out->hdr = cppOptions.hdr;
    out->boxType = static_cast<int>(cppOptions.boxType);
    out->boxClipping = static_cast<int>(cppOptions.boxClipping);
    out->jitterPattern = static_cast<int>(cppOptions.jitterPattern);
    out->varianceGamma = cppOptions.varianceGamma;
    out->preventFlickering = cppOptions.preventFlickering;
    out->historyReprojection = cppOptions.historyReprojection;
}

static void FilaView_setMultiSampleAntiAliasingOptions_struct(FilaView* view, const FilaViewMultiSampleAntiAliasingOptions* options) {
    View::MultiSampleAntiAliasingOptions cppOptions;
    cppOptions.enabled = options->enabled;
    cppOptions.sampleCount = options->sampleCount;
    cppOptions.customResolve = options->customResolve;
    FILA_CAST(View, view)->setMultiSampleAntiAliasingOptions(cppOptions);
}

static void FilaView_getMultiSampleAntiAliasingOptions_struct(const FilaView* view, FilaViewMultiSampleAntiAliasingOptions* out) {
    const View::MultiSampleAntiAliasingOptions& cppOptions = FILA_CONST_CAST(View, view)->getMultiSampleAntiAliasingOptions();
    out->enabled = cppOptions.enabled;
    out->sampleCount = cppOptions.sampleCount;
    out->customResolve = cppOptions.customResolve;
}

static void FilaView_setScreenSpaceReflectionsOptions_struct(FilaView* view, const FilaViewScreenSpaceReflectionsOptions* options) {
    View::ScreenSpaceReflectionsOptions cppOptions;
    cppOptions.thickness = options->thickness;
    cppOptions.bias = options->bias;
    cppOptions.maxDistance = options->maxDistance;
    cppOptions.stride = options->stride;
    cppOptions.enabled = options->enabled;
    FILA_CAST(View, view)->setScreenSpaceReflectionsOptions(cppOptions);
}

static void FilaView_getScreenSpaceReflectionsOptions_struct(const FilaView* view, FilaViewScreenSpaceReflectionsOptions* out) {
    const View::ScreenSpaceReflectionsOptions& cppOptions = FILA_CONST_CAST(View, view)->getScreenSpaceReflectionsOptions();
    out->thickness = cppOptions.thickness;
    out->bias = cppOptions.bias;
    out->maxDistance = cppOptions.maxDistance;
    out->stride = cppOptions.stride;
    out->enabled = cppOptions.enabled;
}

static void FilaView_setStereoscopicOptions_struct(FilaView* view, const FilaViewStereoscopicOptions* options) {
    View::StereoscopicOptions cppOptions;
    cppOptions.enabled = options->enabled;
    FILA_CAST(View, view)->setStereoscopicOptions(cppOptions);
}

static void FilaView_getStereoscopicOptions_struct(const FilaView* view, FilaViewStereoscopicOptions* out) {
    const View::StereoscopicOptions& cppOptions = FILA_CONST_CAST(View, view)->getStereoscopicOptions();
    out->enabled = cppOptions.enabled;
}

static void FilaView_setGuardBandOptions_struct(FilaView* view, const FilaViewGuardBandOptions* options) {
    View::GuardBandOptions cppOptions;
    cppOptions.enabled = options->enabled;
    FILA_CAST(View, view)->setGuardBandOptions(cppOptions);
}

static void FilaView_getGuardBandOptions_struct(const FilaView* view, FilaViewGuardBandOptions* out) {
    const View::GuardBandOptions& cppOptions = FILA_CONST_CAST(View, view)->getGuardBandOptions();
    out->enabled = cppOptions.enabled;
}

void FilaView_setFrustumCullingEnabled(FilaView* view, bool enabled) {
    FILA_CAST(View, view)->setFrustumCullingEnabled(enabled);
}

bool FilaView_isFrustumCullingEnabled(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->isFrustumCullingEnabled();
}

void FilaView_setScreenSpaceRefractionEnabled(FilaView* view, bool enabled) {
    FILA_CAST(View, view)->setScreenSpaceRefractionEnabled(enabled);
}

bool FilaView_isScreenSpaceRefractionEnabled(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->isScreenSpaceRefractionEnabled();
}

void FilaView_readPickingResult(const FilaViewPickingQueryResult* result, int32_t* renderable, float* depthAndFragCoords) {
    *renderable = static_cast<int32_t>(result->renderable);
    depthAndFragCoords[0] = result->depth;
    depthAndFragCoords[1] = result->fragCoords[0];
    depthAndFragCoords[2] = result->fragCoords[1];
    depthAndFragCoords[3] = result->fragCoords[2];
}

void FilaView_pick(FilaView* view, uint32_t x, uint32_t y, FilaCallbackHandler* handler, FilaViewPickingCallback callback, void* userData) {
    FILA_CAST(View, view)->pick(x, y, [callback, userData](View::PickingQueryResult const& result) {
        if (callback) {
            FilaViewPickingQueryResult cResult;
            cResult.renderable = result.renderable.getId();
            cResult.depth = result.depth;
            cResult.fragCoords[0] = result.fragCoords.x;
            cResult.fragCoords[1] = result.fragCoords.y;
            cResult.fragCoords[2] = result.fragCoords.z;
            callback(&cResult, userData);
        }
    }, reinterpret_cast<backend::CallbackHandler*>(handler));
}

void FilaView_setStencilBufferEnabled(FilaView* view, bool enabled) {
    FILA_CAST(View, view)->setStencilBufferEnabled(enabled);
}

bool FilaView_isStencilBufferEnabled(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->isStencilBufferEnabled();
}

void FilaView_setGridSize(FilaView* view, double size) {
    FILA_CAST(View, view)->setGridSize(size);
}

double FilaView_getGridSize(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->getGridSize();
}

double FilaView_getEffectiveGridSize(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->getEffectiveGridSize();
}

void FilaView_setMaterialGlobal(FilaView* view, uint32_t index, float x, float y, float z, float w) {
    FILA_CAST(View, view)->setMaterialGlobal(index, {x, y, z, w});
}

void FilaView_getMaterialGlobal(const FilaView* view, uint32_t index, float out[4]) {
    math::float4 val = FILA_CONST_CAST(View, view)->getMaterialGlobal(index);
    out[0] = val.x; out[1] = val.y; out[2] = val.z; out[3] = val.w;
}

FilaEntity FilaView_getFogEntity(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->getFogEntity().getId();
}

int32_t FilaView_getVisibleRenderableCount(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->getVisibleRenderableCount();
}

void FilaView_clearFrameHistory(FilaView* view, FilaEngine* engine) {
    FILA_CAST(View, view)->clearFrameHistory(*FILA_CAST(Engine, engine));
}

void FilaView_setChannelDepthClearEnabled(FilaView* view, uint32_t channel, bool enabled) {
    FILA_CAST(View, view)->setChannelDepthClearEnabled(channel, enabled);
}

bool FilaView_isChannelDepthClearEnabled(const FilaView* view, uint32_t channel) {
    return FILA_CONST_CAST(View, view)->isChannelDepthClearEnabled(channel);
}

// Flattened entry points: the fields as arguments / out arrays, no structs across the boundary.

void FilaView_setDynamicResolutionOptions(FilaView* view, float minScale_0, float minScale_1, float maxScale_0, float maxScale_1, float sharpness, bool enabled, bool homogeneousScaling, FilaViewQualityLevel quality) {
    FilaViewDynamicResolutionOptions o{};
    o.minScale[0] = minScale_0;
    o.minScale[1] = minScale_1;
    o.maxScale[0] = maxScale_0;
    o.maxScale[1] = maxScale_1;
    o.sharpness = sharpness;
    o.enabled = enabled;
    o.homogeneousScaling = homogeneousScaling;
    o.quality = quality;
    FilaView_setDynamicResolutionOptions_struct(view, &o);
}

void FilaView_getDynamicResolutionOptions(const FilaView* view, float* floats, int32_t* ints) {
    FilaViewDynamicResolutionOptions o{};
    FilaView_getDynamicResolutionOptions_struct(view, &o);
    floats[0] = o.minScale[0];
    floats[1] = o.minScale[1];
    floats[2] = o.maxScale[0];
    floats[3] = o.maxScale[1];
    floats[4] = o.sharpness;
    ints[0] = (int32_t) o.enabled;
    ints[1] = (int32_t) o.homogeneousScaling;
    ints[2] = (int32_t) o.quality;
}

void FilaView_setVsmShadowOptions(FilaView* view, uint32_t anisotropy, bool mipmapping, uint32_t msaaSamples, bool highPrecision, float lightBleedReduction) {
    FilaViewVsmShadowOptions o{};
    o.anisotropy = anisotropy;
    o.mipmapping = mipmapping;
    o.msaaSamples = msaaSamples;
    o.highPrecision = highPrecision;
    o.lightBleedReduction = lightBleedReduction;
    FilaView_setVsmShadowOptions_struct(view, &o);
}

void FilaView_getVsmShadowOptions(const FilaView* view, float* floats, int32_t* ints) {
    FilaViewVsmShadowOptions o{};
    FilaView_getVsmShadowOptions_struct(view, &o);
    ints[0] = (int32_t) o.anisotropy;
    ints[1] = (int32_t) o.mipmapping;
    ints[2] = (int32_t) o.msaaSamples;
    ints[3] = (int32_t) o.highPrecision;
    floats[0] = o.lightBleedReduction;
}

void FilaView_setSoftShadowOptions(FilaView* view, float penumbraScale, float penumbraRatioScale, float maxPenumbraRatio, float maxSearchRadius) {
    FilaViewSoftShadowOptions o{};
    o.penumbraScale = penumbraScale;
    o.penumbraRatioScale = penumbraRatioScale;
    o.maxPenumbraRatio = maxPenumbraRatio;
    o.maxSearchRadius = maxSearchRadius;
    FilaView_setSoftShadowOptions_struct(view, &o);
}

void FilaView_getSoftShadowOptions(const FilaView* view, float* floats) {
    FilaViewSoftShadowOptions o{};
    FilaView_getSoftShadowOptions_struct(view, &o);
    floats[0] = o.penumbraScale;
    floats[1] = o.penumbraRatioScale;
    floats[2] = o.maxPenumbraRatio;
    floats[3] = o.maxSearchRadius;
}

void FilaView_setAmbientOcclusionOptions(FilaView* view, float radius, float bias, float power, float resolution, float intensity, float bilateralThreshold, FilaViewQualityLevel quality, FilaViewQualityLevel lowPassFilter, FilaViewQualityLevel upsampling, bool enabled, bool bentNormals, float minHorizonAngleRad, float ssct_lightConeRad, float ssct_shadowDistance, float ssct_contactDistanceMax, float ssct_intensity, float ssct_lightDirection_0, float ssct_lightDirection_1, float ssct_lightDirection_2, float ssct_depthBias, float ssct_depthSlopeBias, uint32_t ssct_sampleCount, uint32_t ssct_rayCount, bool ssct_enabled, uint32_t gtao_sampleSliceCount, uint32_t gtao_sampleStepsPerSlice, float gtao_thicknessHeuristic, bool gtao_useVisibilityBitmasks, float gtao_constThickness, bool gtao_linearThickness, int aoType) {
    FilaViewAmbientOcclusionOptions o{};
    o.radius = radius;
    o.bias = bias;
    o.power = power;
    o.resolution = resolution;
    o.intensity = intensity;
    o.bilateralThreshold = bilateralThreshold;
    o.quality = quality;
    o.lowPassFilter = lowPassFilter;
    o.upsampling = upsampling;
    o.enabled = enabled;
    o.bentNormals = bentNormals;
    o.minHorizonAngleRad = minHorizonAngleRad;
    o.ssct.lightConeRad = ssct_lightConeRad;
    o.ssct.shadowDistance = ssct_shadowDistance;
    o.ssct.contactDistanceMax = ssct_contactDistanceMax;
    o.ssct.intensity = ssct_intensity;
    o.ssct.lightDirection[0] = ssct_lightDirection_0;
    o.ssct.lightDirection[1] = ssct_lightDirection_1;
    o.ssct.lightDirection[2] = ssct_lightDirection_2;
    o.ssct.depthBias = ssct_depthBias;
    o.ssct.depthSlopeBias = ssct_depthSlopeBias;
    o.ssct.sampleCount = ssct_sampleCount;
    o.ssct.rayCount = ssct_rayCount;
    o.ssct.enabled = ssct_enabled;
    o.gtao.sampleSliceCount = gtao_sampleSliceCount;
    o.gtao.sampleStepsPerSlice = gtao_sampleStepsPerSlice;
    o.gtao.thicknessHeuristic = gtao_thicknessHeuristic;
    o.gtao.useVisibilityBitmasks = gtao_useVisibilityBitmasks;
    o.gtao.constThickness = gtao_constThickness;
    o.gtao.linearThickness = gtao_linearThickness;
    o.aoType = aoType;
    FilaView_setAmbientOcclusionOptions_struct(view, &o);
}

void FilaView_getAmbientOcclusionOptions(const FilaView* view, float* floats, int32_t* ints) {
    FilaViewAmbientOcclusionOptions o{};
    FilaView_getAmbientOcclusionOptions_struct(view, &o);
    floats[0] = o.radius;
    floats[1] = o.bias;
    floats[2] = o.power;
    floats[3] = o.resolution;
    floats[4] = o.intensity;
    floats[5] = o.bilateralThreshold;
    ints[0] = (int32_t) o.quality;
    ints[1] = (int32_t) o.lowPassFilter;
    ints[2] = (int32_t) o.upsampling;
    ints[3] = (int32_t) o.enabled;
    ints[4] = (int32_t) o.bentNormals;
    floats[6] = o.minHorizonAngleRad;
    floats[7] = o.ssct.lightConeRad;
    floats[8] = o.ssct.shadowDistance;
    floats[9] = o.ssct.contactDistanceMax;
    floats[10] = o.ssct.intensity;
    floats[11] = o.ssct.lightDirection[0];
    floats[12] = o.ssct.lightDirection[1];
    floats[13] = o.ssct.lightDirection[2];
    floats[14] = o.ssct.depthBias;
    floats[15] = o.ssct.depthSlopeBias;
    ints[5] = (int32_t) o.ssct.sampleCount;
    ints[6] = (int32_t) o.ssct.rayCount;
    ints[7] = (int32_t) o.ssct.enabled;
    ints[8] = (int32_t) o.gtao.sampleSliceCount;
    ints[9] = (int32_t) o.gtao.sampleStepsPerSlice;
    floats[16] = o.gtao.thicknessHeuristic;
    ints[10] = (int32_t) o.gtao.useVisibilityBitmasks;
    floats[17] = o.gtao.constThickness;
    ints[11] = (int32_t) o.gtao.linearThickness;
    ints[12] = (int32_t) o.aoType;
}

void FilaView_setBloomOptions(FilaView* view, FilaTexture* dirt, float dirtStrength, float strength, uint32_t resolution, uint32_t levels, int blendMode, bool threshold, bool enabled, float highlight, FilaViewQualityLevel quality, bool lensFlare, bool starburst, float chromaticAberration, uint32_t ghostCount, float ghostSpacing, float ghostThreshold, float haloThickness, float haloRadius, float haloThreshold) {
    FilaViewBloomOptions o{};
    o.dirt = dirt;
    o.dirtStrength = dirtStrength;
    o.strength = strength;
    o.resolution = resolution;
    o.levels = levels;
    o.blendMode = blendMode;
    o.threshold = threshold;
    o.enabled = enabled;
    o.highlight = highlight;
    o.quality = quality;
    o.lensFlare = lensFlare;
    o.starburst = starburst;
    o.chromaticAberration = chromaticAberration;
    o.ghostCount = ghostCount;
    o.ghostSpacing = ghostSpacing;
    o.ghostThreshold = ghostThreshold;
    o.haloThickness = haloThickness;
    o.haloRadius = haloRadius;
    o.haloThreshold = haloThreshold;
    FilaView_setBloomOptions_struct(view, &o);
}

void FilaView_getBloomOptions(const FilaView* view, float* floats, int32_t* ints) {
    FilaViewBloomOptions o{};
    FilaView_getBloomOptions_struct(view, &o);
    floats[0] = o.dirtStrength;
    floats[1] = o.strength;
    ints[0] = (int32_t) o.resolution;
    ints[1] = (int32_t) o.levels;
    ints[2] = (int32_t) o.blendMode;
    ints[3] = (int32_t) o.threshold;
    ints[4] = (int32_t) o.enabled;
    floats[2] = o.highlight;
    ints[5] = (int32_t) o.quality;
    ints[6] = (int32_t) o.lensFlare;
    ints[7] = (int32_t) o.starburst;
    floats[3] = o.chromaticAberration;
    ints[8] = (int32_t) o.ghostCount;
    floats[4] = o.ghostSpacing;
    floats[5] = o.ghostThreshold;
    floats[6] = o.haloThickness;
    floats[7] = o.haloRadius;
    floats[8] = o.haloThreshold;
}

void FilaView_setFogOptions(FilaView* view, float distance, float cutOffDistance, float maximumOpacity, float height, float heightFalloff, float color_0, float color_1, float color_2, float density, float inScatteringStart, float inScatteringSize, bool fogColorFromIbl, FilaTexture* skyColor, bool enabled) {
    FilaViewFogOptions o{};
    o.distance = distance;
    o.cutOffDistance = cutOffDistance;
    o.maximumOpacity = maximumOpacity;
    o.height = height;
    o.heightFalloff = heightFalloff;
    o.color[0] = color_0;
    o.color[1] = color_1;
    o.color[2] = color_2;
    o.density = density;
    o.inScatteringStart = inScatteringStart;
    o.inScatteringSize = inScatteringSize;
    o.fogColorFromIbl = fogColorFromIbl;
    o.skyColor = skyColor;
    o.enabled = enabled;
    FilaView_setFogOptions_struct(view, &o);
}

void FilaView_getFogOptions(const FilaView* view, float* floats, int32_t* ints) {
    FilaViewFogOptions o{};
    FilaView_getFogOptions_struct(view, &o);
    floats[0] = o.distance;
    floats[1] = o.cutOffDistance;
    floats[2] = o.maximumOpacity;
    floats[3] = o.height;
    floats[4] = o.heightFalloff;
    floats[5] = o.color[0];
    floats[6] = o.color[1];
    floats[7] = o.color[2];
    floats[8] = o.density;
    floats[9] = o.inScatteringStart;
    floats[10] = o.inScatteringSize;
    ints[0] = (int32_t) o.fogColorFromIbl;
    ints[1] = (int32_t) o.enabled;
}

void FilaView_setDepthOfFieldOptions(FilaView* view, float cocScale, float cocAspectRatio, float maxApertureDiameter, bool enabled, int filter, bool nativeResolution, uint32_t foregroundRingCount, uint32_t backgroundRingCount, uint32_t fastGatherRingCount, uint32_t maxForegroundCOC, uint32_t maxBackgroundCOC) {
    FilaViewDepthOfFieldOptions o{};
    o.cocScale = cocScale;
    o.cocAspectRatio = cocAspectRatio;
    o.maxApertureDiameter = maxApertureDiameter;
    o.enabled = enabled;
    o.filter = filter;
    o.nativeResolution = nativeResolution;
    o.foregroundRingCount = foregroundRingCount;
    o.backgroundRingCount = backgroundRingCount;
    o.fastGatherRingCount = fastGatherRingCount;
    o.maxForegroundCOC = maxForegroundCOC;
    o.maxBackgroundCOC = maxBackgroundCOC;
    FilaView_setDepthOfFieldOptions_struct(view, &o);
}

void FilaView_getDepthOfFieldOptions(const FilaView* view, float* floats, int32_t* ints) {
    FilaViewDepthOfFieldOptions o{};
    FilaView_getDepthOfFieldOptions_struct(view, &o);
    floats[0] = o.cocScale;
    floats[1] = o.cocAspectRatio;
    floats[2] = o.maxApertureDiameter;
    ints[0] = (int32_t) o.enabled;
    ints[1] = (int32_t) o.filter;
    ints[2] = (int32_t) o.nativeResolution;
    ints[3] = (int32_t) o.foregroundRingCount;
    ints[4] = (int32_t) o.backgroundRingCount;
    ints[5] = (int32_t) o.fastGatherRingCount;
    ints[6] = (int32_t) o.maxForegroundCOC;
    ints[7] = (int32_t) o.maxBackgroundCOC;
}

void FilaView_setVignetteOptions(FilaView* view, float midPoint, float roundness, float feather, float color_0, float color_1, float color_2, float color_3, bool enabled) {
    FilaViewVignetteOptions o{};
    o.midPoint = midPoint;
    o.roundness = roundness;
    o.feather = feather;
    o.color[0] = color_0;
    o.color[1] = color_1;
    o.color[2] = color_2;
    o.color[3] = color_3;
    o.enabled = enabled;
    FilaView_setVignetteOptions_struct(view, &o);
}

void FilaView_getVignetteOptions(const FilaView* view, float* floats, int32_t* ints) {
    FilaViewVignetteOptions o{};
    FilaView_getVignetteOptions_struct(view, &o);
    floats[0] = o.midPoint;
    floats[1] = o.roundness;
    floats[2] = o.feather;
    floats[3] = o.color[0];
    floats[4] = o.color[1];
    floats[5] = o.color[2];
    floats[6] = o.color[3];
    ints[0] = (int32_t) o.enabled;
}

void FilaView_setTemporalAntiAliasingOptions(FilaView* view, float feedback, float lodBias, float sharpness, bool enabled, float upscaling, bool filterHistory, bool filterInput, bool useYCoCg, bool hdr, int boxType, int boxClipping, int jitterPattern, float varianceGamma, bool preventFlickering, bool historyReprojection) {
    FilaViewTemporalAntiAliasingOptions o{};
    o.feedback = feedback;
    o.lodBias = lodBias;
    o.sharpness = sharpness;
    o.enabled = enabled;
    o.upscaling = upscaling;
    o.filterHistory = filterHistory;
    o.filterInput = filterInput;
    o.useYCoCg = useYCoCg;
    o.hdr = hdr;
    o.boxType = boxType;
    o.boxClipping = boxClipping;
    o.jitterPattern = jitterPattern;
    o.varianceGamma = varianceGamma;
    o.preventFlickering = preventFlickering;
    o.historyReprojection = historyReprojection;
    FilaView_setTemporalAntiAliasingOptions_struct(view, &o);
}

void FilaView_getTemporalAntiAliasingOptions(const FilaView* view, float* floats, int32_t* ints) {
    FilaViewTemporalAntiAliasingOptions o{};
    FilaView_getTemporalAntiAliasingOptions_struct(view, &o);
    floats[0] = o.feedback;
    floats[1] = o.lodBias;
    floats[2] = o.sharpness;
    ints[0] = (int32_t) o.enabled;
    floats[3] = o.upscaling;
    ints[1] = (int32_t) o.filterHistory;
    ints[2] = (int32_t) o.filterInput;
    ints[3] = (int32_t) o.useYCoCg;
    ints[4] = (int32_t) o.hdr;
    ints[5] = (int32_t) o.boxType;
    ints[6] = (int32_t) o.boxClipping;
    ints[7] = (int32_t) o.jitterPattern;
    floats[4] = o.varianceGamma;
    ints[8] = (int32_t) o.preventFlickering;
    ints[9] = (int32_t) o.historyReprojection;
}

void FilaView_setMultiSampleAntiAliasingOptions(FilaView* view, bool enabled, uint32_t sampleCount, bool customResolve) {
    FilaViewMultiSampleAntiAliasingOptions o{};
    o.enabled = enabled;
    o.sampleCount = sampleCount;
    o.customResolve = customResolve;
    FilaView_setMultiSampleAntiAliasingOptions_struct(view, &o);
}

void FilaView_getMultiSampleAntiAliasingOptions(const FilaView* view, int32_t* ints) {
    FilaViewMultiSampleAntiAliasingOptions o{};
    FilaView_getMultiSampleAntiAliasingOptions_struct(view, &o);
    ints[0] = (int32_t) o.enabled;
    ints[1] = (int32_t) o.sampleCount;
    ints[2] = (int32_t) o.customResolve;
}

void FilaView_setScreenSpaceReflectionsOptions(FilaView* view, float thickness, float bias, float maxDistance, float stride, bool enabled) {
    FilaViewScreenSpaceReflectionsOptions o{};
    o.thickness = thickness;
    o.bias = bias;
    o.maxDistance = maxDistance;
    o.stride = stride;
    o.enabled = enabled;
    FilaView_setScreenSpaceReflectionsOptions_struct(view, &o);
}

void FilaView_getScreenSpaceReflectionsOptions(const FilaView* view, float* floats, int32_t* ints) {
    FilaViewScreenSpaceReflectionsOptions o{};
    FilaView_getScreenSpaceReflectionsOptions_struct(view, &o);
    floats[0] = o.thickness;
    floats[1] = o.bias;
    floats[2] = o.maxDistance;
    floats[3] = o.stride;
    ints[0] = (int32_t) o.enabled;
}

void FilaView_setStereoscopicOptions(FilaView* view, bool enabled) {
    FilaViewStereoscopicOptions o{};
    o.enabled = enabled;
    FilaView_setStereoscopicOptions_struct(view, &o);
}

void FilaView_getStereoscopicOptions(const FilaView* view, int32_t* ints) {
    FilaViewStereoscopicOptions o{};
    FilaView_getStereoscopicOptions_struct(view, &o);
    ints[0] = (int32_t) o.enabled;
}

void FilaView_setGuardBandOptions(FilaView* view, bool enabled) {
    FilaViewGuardBandOptions o{};
    o.enabled = enabled;
    FilaView_setGuardBandOptions_struct(view, &o);
}

void FilaView_getGuardBandOptions(const FilaView* view, int32_t* ints) {
    FilaViewGuardBandOptions o{};
    FilaView_getGuardBandOptions_struct(view, &o);
    ints[0] = (int32_t) o.enabled;
}

} // extern "C"
