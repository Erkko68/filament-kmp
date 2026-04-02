#include <filament/Options.h>
#include <filament/Renderer.h>

#include "../../include/filament/Options.h"

extern "C" {

void FilaRenderQuality_setDefaults(FilaRenderQuality* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::RenderQuality defaults;
    outOptions->hdrColorBuffer = static_cast<FilaQualityLevel>(defaults.hdrColorBuffer);
}

void FilaGuardBandOptions_setDefaults(FilaGuardBandOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::GuardBandOptions defaults;
    outOptions->enabled = defaults.enabled;
}

void FilaStereoscopicOptions_setDefaults(FilaStereoscopicOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::StereoscopicOptions defaults;
    outOptions->enabled = defaults.enabled;
}

void FilaDynamicResolutionOptions_setDefaults(FilaDynamicResolutionOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::DynamicResolutionOptions defaults;
    outOptions->minScaleX = defaults.minScale.x;
    outOptions->minScaleY = defaults.minScale.y;
    outOptions->maxScaleX = defaults.maxScale.x;
    outOptions->maxScaleY = defaults.maxScale.y;
    outOptions->sharpness = defaults.sharpness;
    outOptions->enabled = defaults.enabled;
    outOptions->homogeneousScaling = defaults.homogeneousScaling;
    outOptions->quality = static_cast<FilaQualityLevel>(defaults.quality);
}

void FilaMultiSampleAntiAliasingOptions_setDefaults(FilaMultiSampleAntiAliasingOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::MultiSampleAntiAliasingOptions defaults;
    outOptions->enabled = defaults.enabled;
    outOptions->sampleCount = defaults.sampleCount;
    outOptions->customResolve = defaults.customResolve;
}

void FilaTemporalAntiAliasingOptions_setDefaults(FilaTemporalAntiAliasingOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::TemporalAntiAliasingOptions defaults;
    outOptions->filterWidth = defaults.filterWidth;
    outOptions->feedback = defaults.feedback;
    outOptions->lodBias = defaults.lodBias;
    outOptions->sharpness = defaults.sharpness;
    outOptions->enabled = defaults.enabled;
    outOptions->upscaling = defaults.upscaling;
    outOptions->filterHistory = defaults.filterHistory;
    outOptions->filterInput = defaults.filterInput;
    outOptions->useYCoCg = defaults.useYCoCg;
    outOptions->hdr = defaults.hdr;
    outOptions->boxType = static_cast<FilaTemporalAntiAliasingBoxType>(defaults.boxType);
    outOptions->boxClipping = static_cast<FilaTemporalAntiAliasingBoxClipping>(defaults.boxClipping);
    outOptions->jitterPattern = static_cast<FilaTemporalAntiAliasingJitterPattern>(defaults.jitterPattern);
    outOptions->varianceGamma = defaults.varianceGamma;
    outOptions->preventFlickering = defaults.preventFlickering;
    outOptions->historyReprojection = defaults.historyReprojection;
}

void FilaScreenSpaceReflectionsOptions_setDefaults(FilaScreenSpaceReflectionsOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::ScreenSpaceReflectionsOptions defaults;
    outOptions->thickness = defaults.thickness;
    outOptions->bias = defaults.bias;
    outOptions->maxDistance = defaults.maxDistance;
    outOptions->stride = defaults.stride;
    outOptions->enabled = defaults.enabled;
}

void FilaVsmShadowOptions_setDefaults(FilaVsmShadowOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::VsmShadowOptions defaults;
    outOptions->anisotropy = defaults.anisotropy;
    outOptions->mipmapping = defaults.mipmapping;
    outOptions->msaaSamples = defaults.msaaSamples;
    outOptions->highPrecision = defaults.highPrecision;
    outOptions->minVarianceScale = defaults.minVarianceScale;
    outOptions->lightBleedReduction = defaults.lightBleedReduction;
}

void FilaSoftShadowOptions_setDefaults(FilaSoftShadowOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::SoftShadowOptions defaults;
    outOptions->penumbraScale = defaults.penumbraScale;
    outOptions->penumbraRatioScale = defaults.penumbraRatioScale;
}

void FilaRendererDisplayInfo_setDefaults(FilaRendererDisplayInfo* outInfo) {
    if (!outInfo) {
        return;
    }
    // Keep this independent from deprecated DisplayInfo fields.
    outInfo->refreshRate = 60.0f;
}

void FilaRendererFrameRateOptions_setDefaults(FilaRendererFrameRateOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::Renderer::FrameRateOptions defaults;
    outOptions->headRoomRatio = defaults.headRoomRatio;
    outOptions->scaleRate = defaults.scaleRate;
    outOptions->history = defaults.history;
    outOptions->interval = defaults.interval;
}

void FilaRendererClearOptions_setDefaults(FilaRendererClearOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::Renderer::ClearOptions defaults;
    outOptions->clearColor[0] = defaults.clearColor.x;
    outOptions->clearColor[1] = defaults.clearColor.y;
    outOptions->clearColor[2] = defaults.clearColor.z;
    outOptions->clearColor[3] = defaults.clearColor.w;
    outOptions->clearStencil = defaults.clearStencil;
    outOptions->clear = defaults.clear;
    outOptions->discard = defaults.discard;
}

void FilaAmbientOcclusionOptions_setDefaults(FilaAmbientOcclusionOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::AmbientOcclusionOptions defaults;
    outOptions->aoType = static_cast<FilaAmbientOcclusionType>(defaults.aoType);
    outOptions->radius = defaults.radius;
    outOptions->power = defaults.power;
    outOptions->bias = defaults.bias;
    outOptions->resolution = defaults.resolution;
    outOptions->intensity = defaults.intensity;
    outOptions->bilateralThreshold = defaults.bilateralThreshold;
    outOptions->quality = static_cast<FilaQualityLevel>(defaults.quality);
    outOptions->lowPassFilter = static_cast<FilaQualityLevel>(defaults.lowPassFilter);
    outOptions->upsampling = static_cast<FilaQualityLevel>(defaults.upsampling);
    outOptions->enabled = defaults.enabled;
    outOptions->bentNormals = defaults.bentNormals;
    outOptions->minHorizonAngleRad = defaults.minHorizonAngleRad;

    outOptions->ssct.lightConeRad = defaults.ssct.lightConeRad;
    outOptions->ssct.shadowDistance = defaults.ssct.shadowDistance;
    outOptions->ssct.contactDistanceMax = defaults.ssct.contactDistanceMax;
    outOptions->ssct.intensity = defaults.ssct.intensity;
    outOptions->ssct.lightDirection[0] = defaults.ssct.lightDirection.x;
    outOptions->ssct.lightDirection[1] = defaults.ssct.lightDirection.y;
    outOptions->ssct.lightDirection[2] = defaults.ssct.lightDirection.z;
    outOptions->ssct.depthBias = defaults.ssct.depthBias;
    outOptions->ssct.depthSlopeBias = defaults.ssct.depthSlopeBias;
    outOptions->ssct.sampleCount = defaults.ssct.sampleCount;
    outOptions->ssct.rayCount = defaults.ssct.rayCount;
    outOptions->ssct.enabled = defaults.ssct.enabled;

    outOptions->gtao.sampleSliceCount = defaults.gtao.sampleSliceCount;
    outOptions->gtao.sampleStepsPerSlice = defaults.gtao.sampleStepsPerSlice;
    outOptions->gtao.thicknessHeuristic = defaults.gtao.thicknessHeuristic;
    outOptions->gtao.useVisibilityBitmasks = defaults.gtao.useVisibilityBitmasks;
    outOptions->gtao.constThickness = defaults.gtao.constThickness;
    outOptions->gtao.linearThickness = defaults.gtao.linearThickness;
}

void FilaBloomOptions_setDefaults(FilaBloomOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::BloomOptions defaults;
    outOptions->dirt = reinterpret_cast<FilaTexture*>(defaults.dirt);
    outOptions->dirtStrength = defaults.dirtStrength;
    outOptions->strength = defaults.strength;
    outOptions->resolution = defaults.resolution;
    outOptions->levels = defaults.levels;
    outOptions->blendMode = static_cast<FilaBloomBlendMode>(defaults.blendMode);
    outOptions->threshold = defaults.threshold;
    outOptions->enabled = defaults.enabled;
    outOptions->highlight = defaults.highlight;
    outOptions->quality = static_cast<FilaQualityLevel>(defaults.quality);
    outOptions->lensFlare = defaults.lensFlare;
    outOptions->starburst = defaults.starburst;
    outOptions->chromaticAberration = defaults.chromaticAberration;
    outOptions->ghostCount = defaults.ghostCount;
    outOptions->ghostSpacing = defaults.ghostSpacing;
    outOptions->ghostThreshold = defaults.ghostThreshold;
    outOptions->haloThickness = defaults.haloThickness;
    outOptions->haloRadius = defaults.haloRadius;
    outOptions->haloThreshold = defaults.haloThreshold;
}

void FilaFogOptions_setDefaults(FilaFogOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::FogOptions defaults;
    outOptions->distance = defaults.distance;
    outOptions->cutOffDistance = defaults.cutOffDistance;
    outOptions->maximumOpacity = defaults.maximumOpacity;
    outOptions->height = defaults.height;
    outOptions->heightFalloff = defaults.heightFalloff;
    outOptions->color[0] = defaults.color.r;
    outOptions->color[1] = defaults.color.g;
    outOptions->color[2] = defaults.color.b;
    outOptions->density = defaults.density;
    outOptions->inScatteringStart = defaults.inScatteringStart;
    outOptions->inScatteringSize = defaults.inScatteringSize;
    outOptions->fogColorFromIbl = defaults.fogColorFromIbl;
    outOptions->skyColor = reinterpret_cast<FilaTexture*>(defaults.skyColor);
    outOptions->enabled = defaults.enabled;
}

void FilaDepthOfFieldOptions_setDefaults(FilaDepthOfFieldOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::DepthOfFieldOptions defaults;
    outOptions->cocScale = defaults.cocScale;
    outOptions->cocAspectRatio = defaults.cocAspectRatio;
    outOptions->maxApertureDiameter = defaults.maxApertureDiameter;
    outOptions->enabled = defaults.enabled;
    outOptions->filter = static_cast<FilaDepthOfFieldFilter>(defaults.filter);
    outOptions->nativeResolution = defaults.nativeResolution;
    outOptions->foregroundRingCount = defaults.foregroundRingCount;
    outOptions->backgroundRingCount = defaults.backgroundRingCount;
    outOptions->fastGatherRingCount = defaults.fastGatherRingCount;
    outOptions->maxForegroundCOC = defaults.maxForegroundCOC;
    outOptions->maxBackgroundCOC = defaults.maxBackgroundCOC;
}

void FilaVignetteOptions_setDefaults(FilaVignetteOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::VignetteOptions defaults;
    outOptions->midPoint = defaults.midPoint;
    outOptions->roundness = defaults.roundness;
    outOptions->feather = defaults.feather;
    outOptions->color[0] = defaults.color.r;
    outOptions->color[1] = defaults.color.g;
    outOptions->color[2] = defaults.color.b;
    outOptions->color[3] = defaults.color.a;
    outOptions->enabled = defaults.enabled;
}

} // extern "C"

