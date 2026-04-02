#include <algorithm>
#include <new>

#include <filament/Camera.h>
#include <filament/ColorGrading.h>
#include <filament/RenderTarget.h>
#include <filament/Scene.h>
#include <filament/View.h>
#include <filament/Viewport.h>
#include <backend/CallbackHandler.h>
#include <utils/Entity.h>

#include "../../include/filament/View.h"
#include "../../include/filament/BufferDescriptor.h"

namespace {
struct PickCallbackPayload {
    FilaViewPickCallback callback = nullptr;
    FilaViewPickTokenCallback tokenCallback = nullptr;
    void* userData = nullptr;
    uintptr_t userToken = 0;
};

void toPickingQueryResult(
        const filament::View::PickingQueryResult& in,
        FilaViewPickingQueryResult* out) {
    if (!out) {
        return;
    }
    out->renderable = utils::Entity::smuggle(in.renderable);
    out->depth = in.depth;
    out->reserved1 = in.reserved1;
    out->reserved2 = in.reserved2;
    out->fragCoordsX = in.fragCoords.x;
    out->fragCoordsY = in.fragCoords.y;
    out->fragCoordsZ = in.fragCoords.z;
}

void onPickingResult(
        const filament::View::PickingQueryResult& result,
        filament::View::PickingQuery* query) {
    if (!query) {
        return;
    }
    auto* payload = static_cast<PickCallbackPayload*>(query->storage[0]);
    query->storage[0] = nullptr;
    if (!payload) {
        return;
    }
    FilaViewPickingQueryResult outResult{};
    toPickingQueryResult(result, &outResult);
    if (payload->callback) {
        payload->callback(&outResult, payload->userData);
    }
    if (payload->tokenCallback) {
        payload->tokenCallback(&outResult, payload->userToken);
    }
    delete payload;
}

bool schedulePick(
        FilaView* view,
        uint32_t x,
        uint32_t y,
        FilaCallbackHandler* handler,
        PickCallbackPayload* payload) {
    if (!view || !payload) {
        delete payload;
        return false;
    }

    auto* cppView = reinterpret_cast<filament::View*>(view);
    auto* cppHandler = handler
            ? reinterpret_cast<filament::backend::CallbackHandler*>(handler->impl)
            : nullptr;

    auto& query = cppView->pick(x, y, cppHandler, onPickingResult);
    query.storage[0] = payload;
    return true;
}

filament::BlendMode toBlendMode(FilaViewBlendMode blendMode) {
    return static_cast<filament::BlendMode>(blendMode);
}

FilaViewBlendMode fromBlendMode(filament::BlendMode blendMode) {
    return static_cast<FilaViewBlendMode>(blendMode);
}

filament::AntiAliasing toAntiAliasing(FilaViewAntiAliasing antiAliasing) {
    return static_cast<filament::AntiAliasing>(antiAliasing);
}

FilaViewAntiAliasing fromAntiAliasing(filament::AntiAliasing antiAliasing) {
    return static_cast<FilaViewAntiAliasing>(antiAliasing);
}

filament::Dithering toDithering(FilaViewDithering dithering) {
    return static_cast<filament::Dithering>(dithering);
}

FilaViewDithering fromDithering(filament::Dithering dithering) {
    return static_cast<FilaViewDithering>(dithering);
}

filament::ShadowType toShadowType(FilaViewShadowType shadowType) {
    return static_cast<filament::ShadowType>(shadowType);
}

FilaViewShadowType fromShadowType(filament::ShadowType shadowType) {
    return static_cast<FilaViewShadowType>(shadowType);
}

filament::RenderQuality toRenderQuality(const FilaRenderQuality* quality) {
    filament::RenderQuality out;
    if (!quality) {
        return out;
    }
    out.hdrColorBuffer = static_cast<filament::QualityLevel>(quality->hdrColorBuffer);
    return out;
}

void fromRenderQuality(filament::RenderQuality const& in, FilaRenderQuality* out) {
    if (!out) {
        return;
    }
    out->hdrColorBuffer = static_cast<FilaQualityLevel>(in.hdrColorBuffer);
}

filament::DynamicResolutionOptions toDynamicResolutionOptions(const FilaDynamicResolutionOptions* options) {
    filament::DynamicResolutionOptions out;
    if (!options) {
        return out;
    }
    out.minScale = { options->minScaleX, options->minScaleY };
    out.maxScale = { options->maxScaleX, options->maxScaleY };
    out.sharpness = options->sharpness;
    out.enabled = options->enabled;
    out.homogeneousScaling = options->homogeneousScaling;
    out.quality = static_cast<filament::QualityLevel>(options->quality);
    return out;
}

void fromDynamicResolutionOptions(filament::DynamicResolutionOptions const& in, FilaDynamicResolutionOptions* out) {
    if (!out) {
        return;
    }
    out->minScaleX = in.minScale.x;
    out->minScaleY = in.minScale.y;
    out->maxScaleX = in.maxScale.x;
    out->maxScaleY = in.maxScale.y;
    out->sharpness = in.sharpness;
    out->enabled = in.enabled;
    out->homogeneousScaling = in.homogeneousScaling;
    out->quality = static_cast<FilaQualityLevel>(in.quality);
}

filament::MultiSampleAntiAliasingOptions toMsaaOptions(const FilaMultiSampleAntiAliasingOptions* options) {
    filament::MultiSampleAntiAliasingOptions out;
    if (!options) {
        return out;
    }
    out.enabled = options->enabled;
    out.sampleCount = options->sampleCount;
    out.customResolve = options->customResolve;
    return out;
}

void fromMsaaOptions(filament::MultiSampleAntiAliasingOptions const& in, FilaMultiSampleAntiAliasingOptions* out) {
    if (!out) {
        return;
    }
    out->enabled = in.enabled;
    out->sampleCount = in.sampleCount;
    out->customResolve = in.customResolve;
}

filament::TemporalAntiAliasingOptions toTaaOptions(const FilaTemporalAntiAliasingOptions* options) {
    filament::TemporalAntiAliasingOptions out;
    if (!options) {
        return out;
    }
    out.filterWidth = options->filterWidth;
    out.feedback = options->feedback;
    out.lodBias = options->lodBias;
    out.sharpness = options->sharpness;
    out.enabled = options->enabled;
    out.upscaling = options->upscaling;
    out.filterHistory = options->filterHistory;
    out.filterInput = options->filterInput;
    out.useYCoCg = options->useYCoCg;
    out.hdr = options->hdr;
    out.boxType = static_cast<filament::TemporalAntiAliasingOptions::BoxType>(options->boxType);
    out.boxClipping = static_cast<filament::TemporalAntiAliasingOptions::BoxClipping>(options->boxClipping);
    out.jitterPattern = static_cast<filament::TemporalAntiAliasingOptions::JitterPattern>(options->jitterPattern);
    out.varianceGamma = options->varianceGamma;
    out.preventFlickering = options->preventFlickering;
    out.historyReprojection = options->historyReprojection;
    return out;
}

void fromTaaOptions(filament::TemporalAntiAliasingOptions const& in, FilaTemporalAntiAliasingOptions* out) {
    if (!out) {
        return;
    }
    out->filterWidth = in.filterWidth;
    out->feedback = in.feedback;
    out->lodBias = in.lodBias;
    out->sharpness = in.sharpness;
    out->enabled = in.enabled;
    out->upscaling = in.upscaling;
    out->filterHistory = in.filterHistory;
    out->filterInput = in.filterInput;
    out->useYCoCg = in.useYCoCg;
    out->hdr = in.hdr;
    out->boxType = static_cast<FilaTemporalAntiAliasingBoxType>(in.boxType);
    out->boxClipping = static_cast<FilaTemporalAntiAliasingBoxClipping>(in.boxClipping);
    out->jitterPattern = static_cast<FilaTemporalAntiAliasingJitterPattern>(in.jitterPattern);
    out->varianceGamma = in.varianceGamma;
    out->preventFlickering = in.preventFlickering;
    out->historyReprojection = in.historyReprojection;
}

filament::ScreenSpaceReflectionsOptions toSsrOptions(const FilaScreenSpaceReflectionsOptions* options) {
    filament::ScreenSpaceReflectionsOptions out;
    if (!options) {
        return out;
    }
    out.thickness = options->thickness;
    out.bias = options->bias;
    out.maxDistance = options->maxDistance;
    out.stride = options->stride;
    out.enabled = options->enabled;
    return out;
}

void fromSsrOptions(filament::ScreenSpaceReflectionsOptions const& in, FilaScreenSpaceReflectionsOptions* out) {
    if (!out) {
        return;
    }
    out->thickness = in.thickness;
    out->bias = in.bias;
    out->maxDistance = in.maxDistance;
    out->stride = in.stride;
    out->enabled = in.enabled;
}

filament::VsmShadowOptions toVsmShadowOptions(const FilaVsmShadowOptions* options) {
    filament::VsmShadowOptions out;
    if (!options) {
        return out;
    }
    out.anisotropy = options->anisotropy;
    out.mipmapping = options->mipmapping;
    out.msaaSamples = options->msaaSamples;
    out.highPrecision = options->highPrecision;
    out.minVarianceScale = options->minVarianceScale;
    out.lightBleedReduction = options->lightBleedReduction;
    return out;
}

void fromVsmShadowOptions(filament::VsmShadowOptions const& in, FilaVsmShadowOptions* out) {
    if (!out) {
        return;
    }
    out->anisotropy = in.anisotropy;
    out->mipmapping = in.mipmapping;
    out->msaaSamples = in.msaaSamples;
    out->highPrecision = in.highPrecision;
    out->minVarianceScale = in.minVarianceScale;
    out->lightBleedReduction = in.lightBleedReduction;
}

filament::SoftShadowOptions toSoftShadowOptions(const FilaSoftShadowOptions* options) {
    filament::SoftShadowOptions out;
    if (!options) {
        return out;
    }
    out.penumbraScale = options->penumbraScale;
    out.penumbraRatioScale = options->penumbraRatioScale;
    return out;
}

void fromSoftShadowOptions(filament::SoftShadowOptions const& in, FilaSoftShadowOptions* out) {
    if (!out) {
        return;
    }
    out->penumbraScale = in.penumbraScale;
    out->penumbraRatioScale = in.penumbraRatioScale;
}

filament::GuardBandOptions toGuardBandOptions(const FilaGuardBandOptions* options) {
    filament::GuardBandOptions out;
    if (!options) {
        return out;
    }
    out.enabled = options->enabled;
    return out;
}

void fromGuardBandOptions(filament::GuardBandOptions const& in, FilaGuardBandOptions* out) {
    if (!out) {
        return;
    }
    out->enabled = in.enabled;
}

filament::StereoscopicOptions toStereoscopicOptions(const FilaStereoscopicOptions* options) {
    filament::StereoscopicOptions out;
    if (!options) {
        return out;
    }
    out.enabled = options->enabled;
    return out;
}

void fromStereoscopicOptions(filament::StereoscopicOptions const& in, FilaStereoscopicOptions* out) {
    if (!out) {
        return;
    }
    out->enabled = in.enabled;
}

filament::AmbientOcclusionOptions toAmbientOcclusionOptions(const FilaAmbientOcclusionOptions* options) {
    filament::AmbientOcclusionOptions out;
    if (!options) {
        return out;
    }
    out.aoType = static_cast<filament::AmbientOcclusionOptions::AmbientOcclusionType>(options->aoType);
    out.radius = options->radius;
    out.power = options->power;
    out.bias = options->bias;
    out.resolution = options->resolution;
    out.intensity = options->intensity;
    out.bilateralThreshold = options->bilateralThreshold;
    out.quality = static_cast<filament::QualityLevel>(options->quality);
    out.lowPassFilter = static_cast<filament::QualityLevel>(options->lowPassFilter);
    out.upsampling = static_cast<filament::QualityLevel>(options->upsampling);
    out.enabled = options->enabled;
    out.bentNormals = options->bentNormals;
    out.minHorizonAngleRad = options->minHorizonAngleRad;

    out.ssct.lightConeRad = options->ssct.lightConeRad;
    out.ssct.shadowDistance = options->ssct.shadowDistance;
    out.ssct.contactDistanceMax = options->ssct.contactDistanceMax;
    out.ssct.intensity = options->ssct.intensity;
    out.ssct.lightDirection = {
        options->ssct.lightDirection[0], options->ssct.lightDirection[1], options->ssct.lightDirection[2]};
    out.ssct.depthBias = options->ssct.depthBias;
    out.ssct.depthSlopeBias = options->ssct.depthSlopeBias;
    out.ssct.sampleCount = options->ssct.sampleCount;
    out.ssct.rayCount = options->ssct.rayCount;
    out.ssct.enabled = options->ssct.enabled;

    out.gtao.sampleSliceCount = options->gtao.sampleSliceCount;
    out.gtao.sampleStepsPerSlice = options->gtao.sampleStepsPerSlice;
    out.gtao.thicknessHeuristic = options->gtao.thicknessHeuristic;
    out.gtao.useVisibilityBitmasks = options->gtao.useVisibilityBitmasks;
    out.gtao.constThickness = options->gtao.constThickness;
    out.gtao.linearThickness = options->gtao.linearThickness;
    return out;
}

void fromAmbientOcclusionOptions(filament::AmbientOcclusionOptions const& in, FilaAmbientOcclusionOptions* out) {
    if (!out) {
        return;
    }
    out->aoType = static_cast<FilaAmbientOcclusionType>(in.aoType);
    out->radius = in.radius;
    out->power = in.power;
    out->bias = in.bias;
    out->resolution = in.resolution;
    out->intensity = in.intensity;
    out->bilateralThreshold = in.bilateralThreshold;
    out->quality = static_cast<FilaQualityLevel>(in.quality);
    out->lowPassFilter = static_cast<FilaQualityLevel>(in.lowPassFilter);
    out->upsampling = static_cast<FilaQualityLevel>(in.upsampling);
    out->enabled = in.enabled;
    out->bentNormals = in.bentNormals;
    out->minHorizonAngleRad = in.minHorizonAngleRad;

    out->ssct.lightConeRad = in.ssct.lightConeRad;
    out->ssct.shadowDistance = in.ssct.shadowDistance;
    out->ssct.contactDistanceMax = in.ssct.contactDistanceMax;
    out->ssct.intensity = in.ssct.intensity;
    out->ssct.lightDirection[0] = in.ssct.lightDirection.x;
    out->ssct.lightDirection[1] = in.ssct.lightDirection.y;
    out->ssct.lightDirection[2] = in.ssct.lightDirection.z;
    out->ssct.depthBias = in.ssct.depthBias;
    out->ssct.depthSlopeBias = in.ssct.depthSlopeBias;
    out->ssct.sampleCount = in.ssct.sampleCount;
    out->ssct.rayCount = in.ssct.rayCount;
    out->ssct.enabled = in.ssct.enabled;

    out->gtao.sampleSliceCount = in.gtao.sampleSliceCount;
    out->gtao.sampleStepsPerSlice = in.gtao.sampleStepsPerSlice;
    out->gtao.thicknessHeuristic = in.gtao.thicknessHeuristic;
    out->gtao.useVisibilityBitmasks = in.gtao.useVisibilityBitmasks;
    out->gtao.constThickness = in.gtao.constThickness;
    out->gtao.linearThickness = in.gtao.linearThickness;
}

filament::BloomOptions toBloomOptions(const FilaBloomOptions* options) {
    filament::BloomOptions out;
    if (!options) {
        return out;
    }
    out.dirt = reinterpret_cast<filament::Texture*>(options->dirt);
    out.dirtStrength = options->dirtStrength;
    out.strength = options->strength;
    out.resolution = options->resolution;
    out.levels = options->levels;
    out.blendMode = static_cast<filament::BloomOptions::BlendMode>(options->blendMode);
    out.threshold = options->threshold;
    out.enabled = options->enabled;
    out.highlight = options->highlight;
    out.quality = static_cast<filament::QualityLevel>(options->quality);
    out.lensFlare = options->lensFlare;
    out.starburst = options->starburst;
    out.chromaticAberration = options->chromaticAberration;
    out.ghostCount = options->ghostCount;
    out.ghostSpacing = options->ghostSpacing;
    out.ghostThreshold = options->ghostThreshold;
    out.haloThickness = options->haloThickness;
    out.haloRadius = options->haloRadius;
    out.haloThreshold = options->haloThreshold;
    return out;
}

void fromBloomOptions(filament::BloomOptions const& in, FilaBloomOptions* out) {
    if (!out) {
        return;
    }
    out->dirt = reinterpret_cast<FilaTexture*>(in.dirt);
    out->dirtStrength = in.dirtStrength;
    out->strength = in.strength;
    out->resolution = in.resolution;
    out->levels = in.levels;
    out->blendMode = static_cast<FilaBloomBlendMode>(in.blendMode);
    out->threshold = in.threshold;
    out->enabled = in.enabled;
    out->highlight = in.highlight;
    out->quality = static_cast<FilaQualityLevel>(in.quality);
    out->lensFlare = in.lensFlare;
    out->starburst = in.starburst;
    out->chromaticAberration = in.chromaticAberration;
    out->ghostCount = in.ghostCount;
    out->ghostSpacing = in.ghostSpacing;
    out->ghostThreshold = in.ghostThreshold;
    out->haloThickness = in.haloThickness;
    out->haloRadius = in.haloRadius;
    out->haloThreshold = in.haloThreshold;
}

filament::FogOptions toFogOptions(const FilaFogOptions* options) {
    filament::FogOptions out;
    if (!options) {
        return out;
    }
    out.distance = options->distance;
    out.cutOffDistance = options->cutOffDistance;
    out.maximumOpacity = options->maximumOpacity;
    out.height = options->height;
    out.heightFalloff = options->heightFalloff;
    out.color = { options->color[0], options->color[1], options->color[2] };
    out.density = options->density;
    out.inScatteringStart = options->inScatteringStart;
    out.inScatteringSize = options->inScatteringSize;
    out.fogColorFromIbl = options->fogColorFromIbl;
    out.skyColor = reinterpret_cast<filament::Texture*>(options->skyColor);
    out.enabled = options->enabled;
    return out;
}

void fromFogOptions(filament::FogOptions const& in, FilaFogOptions* out) {
    if (!out) {
        return;
    }
    out->distance = in.distance;
    out->cutOffDistance = in.cutOffDistance;
    out->maximumOpacity = in.maximumOpacity;
    out->height = in.height;
    out->heightFalloff = in.heightFalloff;
    out->color[0] = in.color.r;
    out->color[1] = in.color.g;
    out->color[2] = in.color.b;
    out->density = in.density;
    out->inScatteringStart = in.inScatteringStart;
    out->inScatteringSize = in.inScatteringSize;
    out->fogColorFromIbl = in.fogColorFromIbl;
    out->skyColor = reinterpret_cast<FilaTexture*>(in.skyColor);
    out->enabled = in.enabled;
}

filament::DepthOfFieldOptions toDepthOfFieldOptions(const FilaDepthOfFieldOptions* options) {
    filament::DepthOfFieldOptions out;
    if (!options) {
        return out;
    }
    out.cocScale = options->cocScale;
    out.cocAspectRatio = options->cocAspectRatio;
    out.maxApertureDiameter = options->maxApertureDiameter;
    out.enabled = options->enabled;
    out.filter = static_cast<filament::DepthOfFieldOptions::Filter>(options->filter);
    out.nativeResolution = options->nativeResolution;
    out.foregroundRingCount = options->foregroundRingCount;
    out.backgroundRingCount = options->backgroundRingCount;
    out.fastGatherRingCount = options->fastGatherRingCount;
    out.maxForegroundCOC = options->maxForegroundCOC;
    out.maxBackgroundCOC = options->maxBackgroundCOC;
    return out;
}

void fromDepthOfFieldOptions(filament::DepthOfFieldOptions const& in, FilaDepthOfFieldOptions* out) {
    if (!out) {
        return;
    }
    out->cocScale = in.cocScale;
    out->cocAspectRatio = in.cocAspectRatio;
    out->maxApertureDiameter = in.maxApertureDiameter;
    out->enabled = in.enabled;
    out->filter = static_cast<FilaDepthOfFieldFilter>(in.filter);
    out->nativeResolution = in.nativeResolution;
    out->foregroundRingCount = in.foregroundRingCount;
    out->backgroundRingCount = in.backgroundRingCount;
    out->fastGatherRingCount = in.fastGatherRingCount;
    out->maxForegroundCOC = in.maxForegroundCOC;
    out->maxBackgroundCOC = in.maxBackgroundCOC;
}

filament::VignetteOptions toVignetteOptions(const FilaVignetteOptions* options) {
    filament::VignetteOptions out;
    if (!options) {
        return out;
    }
    out.midPoint = options->midPoint;
    out.roundness = options->roundness;
    out.feather = options->feather;
    out.color = { options->color[0], options->color[1], options->color[2], options->color[3] };
    out.enabled = options->enabled;
    return out;
}

void fromVignetteOptions(filament::VignetteOptions const& in, FilaVignetteOptions* out) {
    if (!out) {
        return;
    }
    out->midPoint = in.midPoint;
    out->roundness = in.roundness;
    out->feather = in.feather;
    out->color[0] = in.color.r;
    out->color[1] = in.color.g;
    out->color[2] = in.color.b;
    out->color[3] = in.color.a;
    out->enabled = in.enabled;
}

void fromMat4f(const filament::math::mat4f& in, float out[16]) {
    for (size_t c = 0; c < 4; ++c) {
        for (size_t r = 0; r < 4; ++r) {
            out[c * 4 + r] = in[c][r];
        }
    }
}
} // namespace

extern "C" {

void FilaView_setScene(FilaView* view, FilaScene* scene) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    cppView->setScene(cppScene);
}

FilaScene* FilaView_getScene(FilaView* view) {
    if (!view) {
        return nullptr;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    return reinterpret_cast<FilaScene*>(cppView->getScene());
}

void FilaView_setViewport(FilaView* view, FilaViewport viewport) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setViewport(filament::Viewport(viewport.left, viewport.bottom, viewport.width, viewport.height));
}

FilaViewport FilaView_getViewport(const FilaView* view) {
    FilaViewport viewport = {0, 0, 0, 0};
    if (!view) {
        return viewport;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    const auto& cppViewport = cppView->getViewport();
    viewport.left = cppViewport.left;
    viewport.bottom = cppViewport.bottom;
    viewport.width = cppViewport.width;
    viewport.height = cppViewport.height;
    return viewport;
}

void FilaView_setCamera(FilaView* view, FilaCamera* camera) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppView->setCamera(cppCamera);
}

bool FilaView_hasCamera(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->hasCamera();
}

FilaCamera* FilaView_getCamera(FilaView* view) {
    if (!view) {
        return nullptr;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    return reinterpret_cast<FilaCamera*>(&cppView->getCamera());
}

void FilaView_setColorGrading(FilaView* view, FilaColorGrading* colorGrading) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    auto cppColorGrading = reinterpret_cast<filament::ColorGrading*>(colorGrading);
    cppView->setColorGrading(cppColorGrading);
}

FilaColorGrading* FilaView_getColorGrading(FilaView* view) {
    if (!view) {
        return nullptr;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    return reinterpret_cast<FilaColorGrading*>(const_cast<filament::ColorGrading*>(cppView->getColorGrading()));
}

void FilaView_setRenderTarget(FilaView* view, FilaRenderTarget* renderTarget) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    auto cppRenderTarget = reinterpret_cast<filament::RenderTarget*>(renderTarget);
    cppView->setRenderTarget(cppRenderTarget);
}

FilaRenderTarget* FilaView_getRenderTarget(FilaView* view) {
    if (!view) {
        return nullptr;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    return reinterpret_cast<FilaRenderTarget*>(cppView->getRenderTarget());
}

void FilaView_setName(FilaView* view, const char* name) {
    if (!view || !name) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setName(name);
}

const char* FilaView_getName(const FilaView* view) {
    if (!view) {
        return nullptr;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->getName();
}

void FilaView_setVisibleLayers(FilaView* view, uint8_t select, uint8_t values) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setVisibleLayers(select, values);
}

uint8_t FilaView_getVisibleLayers(const FilaView* view) {
    if (!view) {
        return 0u;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->getVisibleLayers();
}

void FilaView_setBlendMode(FilaView* view, FilaViewBlendMode blendMode) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setBlendMode(toBlendMode(blendMode));
}

FilaViewBlendMode FilaView_getBlendMode(const FilaView* view) {
    if (!view) {
        return FILA_VIEW_BLEND_MODE_OPAQUE;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return fromBlendMode(cppView->getBlendMode());
}

void FilaView_setAntiAliasing(FilaView* view, FilaViewAntiAliasing antiAliasing) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setAntiAliasing(toAntiAliasing(antiAliasing));
}


FilaViewAntiAliasing FilaView_getAntiAliasing(const FilaView* view) {
    if (!view) {
        return FILA_VIEW_ANTI_ALIASING_NONE;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return fromAntiAliasing(cppView->getAntiAliasing());
}

void FilaView_setDithering(FilaView* view, FilaViewDithering dithering) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setDithering(toDithering(dithering));
}

FilaViewDithering FilaView_getDithering(const FilaView* view) {
    if (!view) {
        return FILA_VIEW_DITHERING_NONE;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return fromDithering(cppView->getDithering());
}

void FilaView_setShadowType(FilaView* view, FilaViewShadowType shadowType) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setShadowType(toShadowType(shadowType));
}

FilaViewShadowType FilaView_getShadowType(const FilaView* view) {
    if (!view) {
        return FILA_VIEW_SHADOW_TYPE_PCF;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return fromShadowType(cppView->getShadowType());
}


void FilaView_setVsmShadowOptions(FilaView* view, const FilaVsmShadowOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setVsmShadowOptions(toVsmShadowOptions(options));
}

bool FilaView_getVsmShadowOptions(const FilaView* view, FilaVsmShadowOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromVsmShadowOptions(cppView->getVsmShadowOptions(), outOptions);
    return true;
}

void FilaView_setSoftShadowOptions(FilaView* view, const FilaSoftShadowOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setSoftShadowOptions(toSoftShadowOptions(options));
}

bool FilaView_getSoftShadowOptions(const FilaView* view, FilaSoftShadowOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromSoftShadowOptions(cppView->getSoftShadowOptions(), outOptions);
    return true;
}

void FilaView_setShadowingEnabled(FilaView* view, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setShadowingEnabled(enabled);
}

bool FilaView_isShadowingEnabled(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isShadowingEnabled();
}

void FilaView_setScreenSpaceRefractionEnabled(FilaView* view, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setScreenSpaceRefractionEnabled(enabled);
}

bool FilaView_isScreenSpaceRefractionEnabled(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isScreenSpaceRefractionEnabled();
}

void FilaView_setPostProcessingEnabled(FilaView* view, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setPostProcessingEnabled(enabled);
}

bool FilaView_isPostProcessingEnabled(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isPostProcessingEnabled();
}

void FilaView_setFrontFaceWindingInverted(FilaView* view, bool inverted) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setFrontFaceWindingInverted(inverted);
}

bool FilaView_isFrontFaceWindingInverted(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isFrontFaceWindingInverted();
}

void FilaView_setTransparentPickingEnabled(FilaView* view, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setTransparentPickingEnabled(enabled);
}

bool FilaView_isTransparentPickingEnabled(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isTransparentPickingEnabled();
}

void FilaView_setStencilBufferEnabled(FilaView* view, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setStencilBufferEnabled(enabled);
}

bool FilaView_isStencilBufferEnabled(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isStencilBufferEnabled();
}

void FilaView_setChannelDepthClearEnabled(FilaView* view, uint8_t channel, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setChannelDepthClearEnabled(channel, enabled);
}

bool FilaView_isChannelDepthClearEnabled(const FilaView* view, uint8_t channel) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isChannelDepthClearEnabled(channel);
}

void FilaView_setDynamicLightingOptions(FilaView* view, float zLightNear, float zLightFar) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setDynamicLightingOptions(zLightNear, zLightFar);
}

void FilaView_setDynamicResolutionOptions(FilaView* view, const FilaDynamicResolutionOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setDynamicResolutionOptions(toDynamicResolutionOptions(options));
}

bool FilaView_getDynamicResolutionOptions(const FilaView* view, FilaDynamicResolutionOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromDynamicResolutionOptions(cppView->getDynamicResolutionOptions(), outOptions);
    return true;
}

bool FilaView_getLastDynamicResolutionScale(const FilaView* view, float outScale2[2]) {
    if (!view || !outScale2) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    const auto scale = cppView->getLastDynamicResolutionScale();
    outScale2[0] = scale.x;
    outScale2[1] = scale.y;
    return true;
}

void FilaView_setRenderQuality(FilaView* view, const FilaRenderQuality* quality) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setRenderQuality(toRenderQuality(quality));
}

bool FilaView_getRenderQuality(const FilaView* view, FilaRenderQuality* outQuality) {
    if (!view || !outQuality) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromRenderQuality(cppView->getRenderQuality(), outQuality);
    return true;
}

void FilaView_setMultiSampleAntiAliasingOptions(FilaView* view, const FilaMultiSampleAntiAliasingOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setMultiSampleAntiAliasingOptions(toMsaaOptions(options));
}

bool FilaView_getMultiSampleAntiAliasingOptions(const FilaView* view, FilaMultiSampleAntiAliasingOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromMsaaOptions(cppView->getMultiSampleAntiAliasingOptions(), outOptions);
    return true;
}

void FilaView_setTemporalAntiAliasingOptions(FilaView* view, const FilaTemporalAntiAliasingOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setTemporalAntiAliasingOptions(toTaaOptions(options));
}

bool FilaView_getTemporalAntiAliasingOptions(const FilaView* view, FilaTemporalAntiAliasingOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromTaaOptions(cppView->getTemporalAntiAliasingOptions(), outOptions);
    return true;
}

void FilaView_setScreenSpaceReflectionsOptions(FilaView* view, const FilaScreenSpaceReflectionsOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setScreenSpaceReflectionsOptions(toSsrOptions(options));
}

bool FilaView_getScreenSpaceReflectionsOptions(const FilaView* view, FilaScreenSpaceReflectionsOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromSsrOptions(cppView->getScreenSpaceReflectionsOptions(), outOptions);
    return true;
}

void FilaView_setAmbientOcclusionOptions(FilaView* view, const FilaAmbientOcclusionOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setAmbientOcclusionOptions(toAmbientOcclusionOptions(options));
}

bool FilaView_getAmbientOcclusionOptions(const FilaView* view, FilaAmbientOcclusionOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromAmbientOcclusionOptions(cppView->getAmbientOcclusionOptions(), outOptions);
    return true;
}

void FilaView_setBloomOptions(FilaView* view, const FilaBloomOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setBloomOptions(toBloomOptions(options));
}

bool FilaView_getBloomOptions(const FilaView* view, FilaBloomOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromBloomOptions(cppView->getBloomOptions(), outOptions);
    return true;
}

void FilaView_setFogOptions(FilaView* view, const FilaFogOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setFogOptions(toFogOptions(options));
}

bool FilaView_getFogOptions(const FilaView* view, FilaFogOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromFogOptions(cppView->getFogOptions(), outOptions);
    return true;
}

void FilaView_setDepthOfFieldOptions(FilaView* view, const FilaDepthOfFieldOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setDepthOfFieldOptions(toDepthOfFieldOptions(options));
}

bool FilaView_getDepthOfFieldOptions(const FilaView* view, FilaDepthOfFieldOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromDepthOfFieldOptions(cppView->getDepthOfFieldOptions(), outOptions);
    return true;
}

void FilaView_setVignetteOptions(FilaView* view, const FilaVignetteOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setVignetteOptions(toVignetteOptions(options));
}

bool FilaView_getVignetteOptions(const FilaView* view, FilaVignetteOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromVignetteOptions(cppView->getVignetteOptions(), outOptions);
    return true;
}

void FilaView_setGuardBandOptions(FilaView* view, const FilaGuardBandOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setGuardBandOptions(toGuardBandOptions(options));
}

bool FilaView_getGuardBandOptions(const FilaView* view, FilaGuardBandOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromGuardBandOptions(cppView->getGuardBandOptions(), outOptions);
    return true;
}

void FilaView_setStereoscopicOptions(FilaView* view, const FilaStereoscopicOptions* options) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setStereoscopicOptions(toStereoscopicOptions(options));
}

bool FilaView_getStereoscopicOptions(const FilaView* view, FilaStereoscopicOptions* outOptions) {
    if (!view || !outOptions) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    fromStereoscopicOptions(cppView->getStereoscopicOptions(), outOptions);
    return true;
}

void FilaView_clearFrameHistory(FilaView* view, FilaEngine* engine) {
    if (!view || !engine) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppView->clearFrameHistory(*cppEngine);
}

void FilaView_setFrustumCullingEnabled(FilaView* view, bool cullingEnabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setFrustumCullingEnabled(cullingEnabled);
}

bool FilaView_isFrustumCullingEnabled(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isFrustumCullingEnabled();
}

FilaEntity FilaView_getFogEntity(const FilaView* view) {
    if (!view) {
        return 0;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return utils::Entity::smuggle(cppView->getFogEntity());
}


void FilaView_setDebugCamera(FilaView* view, FilaCamera* camera) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppView->setDebugCamera(cppCamera);
}

size_t FilaView_getDirectionalShadowCameraCount(const FilaView* view) {
    if (!view) {
        return 0u;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->getDirectionalShadowCameras().size();
}

size_t FilaView_getDirectionalShadowCameras(
        const FilaView* view,
        const FilaCamera** outCameras,
        size_t outCameraCount) {
    if (!view || !outCameras || outCameraCount == 0u) {
        return 0u;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    const auto cameras = cppView->getDirectionalShadowCameras();
    const size_t copied = std::min<size_t>(cameras.size(), outCameraCount);
    for (size_t i = 0; i < copied; ++i) {
        outCameras[i] = reinterpret_cast<const FilaCamera*>(cameras[i]);
    }
    return copied;
}

void FilaView_setFroxelVizEnabled(FilaView* view, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setFroxelVizEnabled(enabled);
}

bool FilaView_getFroxelConfigurationInfo(
        const FilaView* view,
        FilaViewFroxelConfigurationInfoWithAge* outInfo) {
    if (!view || !outInfo) {
        return false;
    }

    auto cppView = reinterpret_cast<const filament::View*>(view);
    const auto infoWithAge = cppView->getFroxelConfigurationInfo();

    outInfo->info.width = infoWithAge.info.width;
    outInfo->info.height = infoWithAge.info.height;
    outInfo->info.depth = infoWithAge.info.depth;
    outInfo->info.viewportWidth = infoWithAge.info.viewportWidth;
    outInfo->info.viewportHeight = infoWithAge.info.viewportHeight;
    outInfo->info.froxelDimension[0] = infoWithAge.info.froxelDimension.x;
    outInfo->info.froxelDimension[1] = infoWithAge.info.froxelDimension.y;
    outInfo->info.zLightFar = infoWithAge.info.zLightFar;
    outInfo->info.linearizer = infoWithAge.info.linearizer;
    fromMat4f(infoWithAge.info.p, outInfo->info.projection);
    outInfo->info.clipTransform[0] = infoWithAge.info.clipTransform.x;
    outInfo->info.clipTransform[1] = infoWithAge.info.clipTransform.y;
    outInfo->info.clipTransform[2] = infoWithAge.info.clipTransform.z;
    outInfo->info.clipTransform[3] = infoWithAge.info.clipTransform.w;
    outInfo->age = infoWithAge.age;
    return true;
}

bool FilaView_pick(
        FilaView* view,
        uint32_t x,
        uint32_t y,
        FilaViewPickCallback callback,
        void* userData) {
    if (!callback) {
        return false;
    }
    auto* payload = new (std::nothrow) PickCallbackPayload;
    if (!payload) {
        return false;
    }
    payload->callback = callback;
    payload->userData = userData;
    return schedulePick(view, x, y, nullptr, payload);
}

bool FilaView_pickWithHandler(
        FilaView* view,
        uint32_t x,
        uint32_t y,
        FilaCallbackHandler* handler,
        FilaViewPickCallback callback,
        void* userData) {
    if (!callback) {
        return false;
    }
    auto* payload = new (std::nothrow) PickCallbackPayload;
    if (!payload) {
        return false;
    }
    payload->callback = callback;
    payload->userData = userData;
    return schedulePick(view, x, y, handler, payload);
}

bool FilaView_pickWithToken(
        FilaView* view,
        uint32_t x,
        uint32_t y,
        FilaViewPickTokenCallback callback,
        uintptr_t userToken) {
    if (!callback) {
        return false;
    }
    auto* payload = new (std::nothrow) PickCallbackPayload;
    if (!payload) {
        return false;
    }
    payload->tokenCallback = callback;
    payload->userToken = userToken;
    return schedulePick(view, x, y, nullptr, payload);
}

bool FilaView_pickWithHandlerAndToken(
        FilaView* view,
        uint32_t x,
        uint32_t y,
        FilaCallbackHandler* handler,
        FilaViewPickTokenCallback callback,
        uintptr_t userToken) {
    if (!callback) {
        return false;
    }
    auto* payload = new (std::nothrow) PickCallbackPayload;
    if (!payload) {
        return false;
    }
    payload->tokenCallback = callback;
    payload->userToken = userToken;
    return schedulePick(view, x, y, handler, payload);
}

void FilaView_setMaterialGlobal(FilaView* view, uint32_t index, const float value4[4]) {
    if (!view || !value4) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setMaterialGlobal(index,
            filament::math::float4(value4[0], value4[1], value4[2], value4[3]));
}

bool FilaView_getMaterialGlobal(const FilaView* view, uint32_t index, float outValue4[4]) {
    if (!view || !outValue4) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    const auto value = cppView->getMaterialGlobal(index);
    outValue4[0] = value.x;
    outValue4[1] = value.y;
    outValue4[2] = value.z;
    outValue4[3] = value.w;
    return true;
}

} // extern "C"

