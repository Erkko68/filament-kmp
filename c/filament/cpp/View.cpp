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

void FilaView_setVisibleLayers(FilaView* view, uint8_t select, uint8_t value) {
    FILA_CAST(View, view)->setVisibleLayers(select, value);
}

uint8_t FilaView_getVisibleLayers(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->getVisibleLayers();
}

void FilaView_setShadowingEnabled(FilaView* view, bool enabled) {
    FILA_CAST(View, view)->setShadowingEnabled(enabled);
}

bool FilaView_isShadowingEnabled(const FilaView* view) {
    return FILA_CONST_CAST(View, view)->isShadowingEnabled();
}

void FilaView_setRenderTarget(FilaView* view, FilaRenderTarget* renderTarget) {
    FILA_CAST(View, view)->setRenderTarget(FILA_CAST(RenderTarget, renderTarget));
}

void FilaView_setSampleCount(FilaView* view, uint8_t count) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    FILA_CAST(View, view)->setSampleCount(count);
#pragma clang diagnostic pop
}

uint8_t FilaView_getSampleCount(const FilaView* view) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    return FILA_CONST_CAST(View, view)->getSampleCount();
#pragma clang diagnostic pop
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

void FilaView_setDynamicResolutionOptions(FilaView* view, const FilaViewDynamicResolutionOptions* options) {
    View::DynamicResolutionOptions cppOptions;
    cppOptions.minScale = {options->minScale[0], options->minScale[1]};
    cppOptions.maxScale = {options->maxScale[0], options->maxScale[1]};
    cppOptions.sharpness = options->sharpness;
    cppOptions.enabled = options->enabled;
    cppOptions.homogeneousScaling = options->homogeneousScaling;
    cppOptions.quality = static_cast<View::QualityLevel>(options->quality);
    FILA_CAST(View, view)->setDynamicResolutionOptions(cppOptions);
}

void FilaView_getLastDynamicResolutionScale(const FilaView* view, float out[2]) {
    filament::math::float2 scale = FILA_CONST_CAST(View, view)->getLastDynamicResolutionScale();
    out[0] = scale.x; out[1] = scale.y;
}

void FilaView_setShadowType(FilaView* view, FilaViewShadowType type) {
    FILA_CAST(View, view)->setShadowType(static_cast<View::ShadowType>(type));
}

void FilaView_setVsmShadowOptions(FilaView* view, const FilaViewVsmShadowOptions* options) {
    View::VsmShadowOptions cppOptions;
    cppOptions.anisotropy = options->anisotropy;
    cppOptions.mipmapping = options->mipmapping;
    cppOptions.highPrecision = options->highPrecision;
    cppOptions.minVarianceScale = options->minVarianceScale;
    cppOptions.lightBleedReduction = options->lightBleedReduction;
    FILA_CAST(View, view)->setVsmShadowOptions(cppOptions);
}

void FilaView_setSoftShadowOptions(FilaView* view, const FilaViewSoftShadowOptions* options) {
    View::SoftShadowOptions cppOptions;
    cppOptions.penumbraScale = options->penumbraScale;
    cppOptions.penumbraRatioScale = options->penumbraRatioScale;
    FILA_CAST(View, view)->setSoftShadowOptions(cppOptions);
}

void FilaView_setRenderQuality(FilaView* view, FilaViewQualityLevel hdrColorBufferQuality) {
    View::RenderQuality renderQuality;
    renderQuality.hdrColorBuffer = static_cast<View::QualityLevel>(hdrColorBufferQuality);
    FILA_CAST(View, view)->setRenderQuality(renderQuality);
}

void FilaView_setDynamicLightingOptions(FilaView* view, float zLightNear, float zLightFar) {
    FILA_CAST(View, view)->setDynamicLightingOptions(zLightNear, zLightFar);
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

void FilaView_setAmbientOcclusionOptions(FilaView* view, const FilaViewAmbientOcclusionOptions* options) {
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
    FILA_CAST(View, view)->setAmbientOcclusionOptions(cppOptions);
}

void FilaView_setBloomOptions(FilaView* view, const FilaViewBloomOptions* options) {
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

void FilaView_setFogOptions(FilaView* view, const FilaViewFogOptions* options) {
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

void FilaView_setBlendMode(FilaView* view, FilaViewBlendMode blendMode) {
    FILA_CAST(View, view)->setBlendMode(static_cast<View::BlendMode>(blendMode));
}

void FilaView_setDepthOfFieldOptions(FilaView* view, const FilaViewDepthOfFieldOptions* options) {
    View::DepthOfFieldOptions cppOptions;
    cppOptions.cocScale = options->cocScale;
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

void FilaView_setVignetteOptions(FilaView* view, const FilaViewVignetteOptions* options) {
    View::VignetteOptions cppOptions;
    cppOptions.midPoint = options->midPoint;
    cppOptions.roundness = options->roundness;
    cppOptions.feather = options->feather;
    cppOptions.color = {options->color[0], options->color[1], options->color[2], options->color[3]};
    cppOptions.enabled = options->enabled;
    FILA_CAST(View, view)->setVignetteOptions(cppOptions);
}

void FilaView_setTemporalAntiAliasingOptions(FilaView* view, const FilaViewTemporalAntiAliasingOptions* options) {
    View::TemporalAntiAliasingOptions cppOptions;
    cppOptions.filterWidth = options->filterWidth;
    cppOptions.feedback = options->feedback;
    cppOptions.enabled = options->enabled;
    FILA_CAST(View, view)->setTemporalAntiAliasingOptions(cppOptions);
}

void FilaView_setMultiSampleAntiAliasingOptions(FilaView* view, const FilaViewMultiSampleAntiAliasingOptions* options) {
    View::MultiSampleAntiAliasingOptions cppOptions;
    cppOptions.enabled = options->enabled;
    cppOptions.sampleCount = options->sampleCount;
    cppOptions.customResolve = options->customResolve;
    FILA_CAST(View, view)->setMultiSampleAntiAliasingOptions(cppOptions);
}

void FilaView_setScreenSpaceReflectionsOptions(FilaView* view, const FilaViewScreenSpaceReflectionsOptions* options) {
    View::ScreenSpaceReflectionsOptions cppOptions;
    cppOptions.thickness = options->thickness;
    cppOptions.bias = options->bias;
    cppOptions.maxDistance = options->maxDistance;
    cppOptions.stride = options->stride;
    cppOptions.enabled = options->enabled;
    FILA_CAST(View, view)->setScreenSpaceReflectionsOptions(cppOptions);
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

void FilaView_clearFrameHistory(FilaView* view, FilaEngine* engine) {
    FILA_CAST(View, view)->clearFrameHistory(*FILA_CAST(Engine, engine));
}

void FilaView_setChannelDepthClearEnabled(FilaView* view, uint8_t channel, bool enabled) {
    FILA_CAST(View, view)->setChannelDepthClearEnabled(channel, enabled);
}

bool FilaView_isChannelDepthClearEnabled(const FilaView* view, uint8_t channel) {
    return FILA_CONST_CAST(View, view)->isChannelDepthClearEnabled(channel);
}

} // extern "C"
