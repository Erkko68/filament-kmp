#ifndef FILAMENT_C_OPTIONS_H
#define FILAMENT_C_OPTIONS_H

#include <stdbool.h>
#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaQualityLevel {
    FILA_QUALITY_LEVEL_LOW = 0,
    FILA_QUALITY_LEVEL_MEDIUM = 1,
    FILA_QUALITY_LEVEL_HIGH = 2,
    FILA_QUALITY_LEVEL_ULTRA = 3,
} FilaQualityLevel;

typedef enum FilaBlendMode {
    FILA_BLEND_MODE_OPAQUE = 0,
    FILA_BLEND_MODE_TRANSLUCENT = 1,
} FilaBlendMode;

typedef enum FilaAntiAliasing {
    FILA_ANTI_ALIASING_NONE = 0,
    FILA_ANTI_ALIASING_FXAA = 1,
} FilaAntiAliasing;

typedef enum FilaDithering {
    FILA_DITHERING_NONE = 0,
    FILA_DITHERING_TEMPORAL = 1,
} FilaDithering;

typedef enum FilaShadowType {
    FILA_SHADOW_TYPE_PCF = 0,
    FILA_SHADOW_TYPE_VSM = 1,
    FILA_SHADOW_TYPE_DPCF = 2,
    FILA_SHADOW_TYPE_PCSS = 3,
    FILA_SHADOW_TYPE_PCFD = 4,
} FilaShadowType;

typedef struct FilaRenderQuality {
    FilaQualityLevel hdrColorBuffer;
} FilaRenderQuality;

typedef struct FilaGuardBandOptions {
    bool enabled;
} FilaGuardBandOptions;

typedef struct FilaStereoscopicOptions {
    bool enabled;
} FilaStereoscopicOptions;

typedef struct FilaDynamicResolutionOptions {
    float minScaleX;
    float minScaleY;
    float maxScaleX;
    float maxScaleY;
    float sharpness;
    bool enabled;
    bool homogeneousScaling;
    FilaQualityLevel quality;
} FilaDynamicResolutionOptions;

typedef struct FilaMultiSampleAntiAliasingOptions {
    bool enabled;
    uint8_t sampleCount;
    bool customResolve;
} FilaMultiSampleAntiAliasingOptions;

typedef enum FilaTemporalAntiAliasingBoxType {
    FILA_TEMPORAL_AA_BOX_TYPE_AABB = 0,
    FILA_TEMPORAL_AA_BOX_TYPE_AABB_VARIANCE = 1,
} FilaTemporalAntiAliasingBoxType;

typedef enum FilaTemporalAntiAliasingBoxClipping {
    FILA_TEMPORAL_AA_BOX_CLIPPING_ACCURATE = 0,
    FILA_TEMPORAL_AA_BOX_CLIPPING_CLAMP = 1,
    FILA_TEMPORAL_AA_BOX_CLIPPING_NONE = 2,
} FilaTemporalAntiAliasingBoxClipping;

typedef enum FilaTemporalAntiAliasingJitterPattern {
    FILA_TEMPORAL_AA_JITTER_PATTERN_RGSS_X4 = 0,
    FILA_TEMPORAL_AA_JITTER_PATTERN_UNIFORM_HELIX_X4 = 1,
    FILA_TEMPORAL_AA_JITTER_PATTERN_HALTON_23_X8 = 2,
    FILA_TEMPORAL_AA_JITTER_PATTERN_HALTON_23_X16 = 3,
    FILA_TEMPORAL_AA_JITTER_PATTERN_HALTON_23_X32 = 4,
} FilaTemporalAntiAliasingJitterPattern;

typedef struct FilaTemporalAntiAliasingOptions {
    float filterWidth;
    float feedback;
    float lodBias;
    float sharpness;
    bool enabled;
    float upscaling;
    bool filterHistory;
    bool filterInput;
    bool useYCoCg;
    bool hdr;
    FilaTemporalAntiAliasingBoxType boxType;
    FilaTemporalAntiAliasingBoxClipping boxClipping;
    FilaTemporalAntiAliasingJitterPattern jitterPattern;
    float varianceGamma;
    bool preventFlickering;
    bool historyReprojection;
} FilaTemporalAntiAliasingOptions;

typedef struct FilaScreenSpaceReflectionsOptions {
    float thickness;
    float bias;
    float maxDistance;
    float stride;
    bool enabled;
} FilaScreenSpaceReflectionsOptions;

typedef struct FilaVsmShadowOptions {
    uint8_t anisotropy;
    bool mipmapping;
    uint8_t msaaSamples;
    bool highPrecision;
    float minVarianceScale;
    float lightBleedReduction;
} FilaVsmShadowOptions;

typedef struct FilaSoftShadowOptions {
    float penumbraScale;
    float penumbraRatioScale;
} FilaSoftShadowOptions;

typedef enum FilaAmbientOcclusionType {
    FILA_AMBIENT_OCCLUSION_TYPE_SAO = 0,
    FILA_AMBIENT_OCCLUSION_TYPE_GTAO = 1,
} FilaAmbientOcclusionType;

typedef struct FilaAmbientOcclusionSsctOptions {
    float lightConeRad;
    float shadowDistance;
    float contactDistanceMax;
    float intensity;
    float lightDirection[3];
    float depthBias;
    float depthSlopeBias;
    uint8_t sampleCount;
    uint8_t rayCount;
    bool enabled;
} FilaAmbientOcclusionSsctOptions;

typedef struct FilaAmbientOcclusionGtaoOptions {
    uint8_t sampleSliceCount;
    uint8_t sampleStepsPerSlice;
    float thicknessHeuristic;
    bool useVisibilityBitmasks;
    float constThickness;
    bool linearThickness;
} FilaAmbientOcclusionGtaoOptions;

typedef struct FilaAmbientOcclusionOptions {
    FilaAmbientOcclusionType aoType;
    float radius;
    float power;
    float bias;
    float resolution;
    float intensity;
    float bilateralThreshold;
    FilaQualityLevel quality;
    FilaQualityLevel lowPassFilter;
    FilaQualityLevel upsampling;
    bool enabled;
    bool bentNormals;
    float minHorizonAngleRad;
    FilaAmbientOcclusionSsctOptions ssct;
    FilaAmbientOcclusionGtaoOptions gtao;
} FilaAmbientOcclusionOptions;

typedef enum FilaBloomBlendMode {
    FILA_BLOOM_BLEND_MODE_ADD = 0,
    FILA_BLOOM_BLEND_MODE_INTERPOLATE = 1,
} FilaBloomBlendMode;

typedef struct FilaBloomOptions {
    FilaTexture* dirt;
    float dirtStrength;
    float strength;
    uint32_t resolution;
    uint8_t levels;
    FilaBloomBlendMode blendMode;
    bool threshold;
    bool enabled;
    float highlight;
    FilaQualityLevel quality;
    bool lensFlare;
    bool starburst;
    float chromaticAberration;
    uint8_t ghostCount;
    float ghostSpacing;
    float ghostThreshold;
    float haloThickness;
    float haloRadius;
    float haloThreshold;
} FilaBloomOptions;

typedef struct FilaFogOptions {
    float distance;
    float cutOffDistance;
    float maximumOpacity;
    float height;
    float heightFalloff;
    float color[3];
    float density;
    float inScatteringStart;
    float inScatteringSize;
    bool fogColorFromIbl;
    FilaTexture* skyColor;
    bool enabled;
} FilaFogOptions;

typedef enum FilaDepthOfFieldFilter {
    FILA_DEPTH_OF_FIELD_FILTER_NONE = 0,
    FILA_DEPTH_OF_FIELD_FILTER_UNUSED = 1,
    FILA_DEPTH_OF_FIELD_FILTER_MEDIAN = 2,
} FilaDepthOfFieldFilter;

typedef struct FilaDepthOfFieldOptions {
    float cocScale;
    float cocAspectRatio;
    float maxApertureDiameter;
    bool enabled;
    FilaDepthOfFieldFilter filter;
    bool nativeResolution;
    uint8_t foregroundRingCount;
    uint8_t backgroundRingCount;
    uint8_t fastGatherRingCount;
    uint16_t maxForegroundCOC;
    uint16_t maxBackgroundCOC;
} FilaDepthOfFieldOptions;

typedef struct FilaVignetteOptions {
    float midPoint;
    float roundness;
    float feather;
    float color[4];
    bool enabled;
} FilaVignetteOptions;

typedef struct FilaRendererDisplayInfo {
    float refreshRate;
} FilaRendererDisplayInfo;

typedef struct FilaRendererFrameRateOptions {
    float headRoomRatio;
    float scaleRate;
    uint8_t history;
    uint8_t interval;
} FilaRendererFrameRateOptions;

typedef struct FilaRendererClearOptions {
    float clearColor[4];
    uint8_t clearStencil;
    bool clear;
    bool discard;
} FilaRendererClearOptions;

void FilaRenderQuality_setDefaults(FilaRenderQuality* outOptions);
void FilaGuardBandOptions_setDefaults(FilaGuardBandOptions* outOptions);
void FilaStereoscopicOptions_setDefaults(FilaStereoscopicOptions* outOptions);
void FilaDynamicResolutionOptions_setDefaults(FilaDynamicResolutionOptions* outOptions);
void FilaMultiSampleAntiAliasingOptions_setDefaults(FilaMultiSampleAntiAliasingOptions* outOptions);
void FilaTemporalAntiAliasingOptions_setDefaults(FilaTemporalAntiAliasingOptions* outOptions);
void FilaScreenSpaceReflectionsOptions_setDefaults(FilaScreenSpaceReflectionsOptions* outOptions);
void FilaVsmShadowOptions_setDefaults(FilaVsmShadowOptions* outOptions);
void FilaSoftShadowOptions_setDefaults(FilaSoftShadowOptions* outOptions);
void FilaRendererDisplayInfo_setDefaults(FilaRendererDisplayInfo* outInfo);
void FilaRendererFrameRateOptions_setDefaults(FilaRendererFrameRateOptions* outOptions);
void FilaRendererClearOptions_setDefaults(FilaRendererClearOptions* outOptions);
void FilaAmbientOcclusionOptions_setDefaults(FilaAmbientOcclusionOptions* outOptions);
void FilaBloomOptions_setDefaults(FilaBloomOptions* outOptions);
void FilaFogOptions_setDefaults(FilaFogOptions* outOptions);
void FilaDepthOfFieldOptions_setDefaults(FilaDepthOfFieldOptions* outOptions);
void FilaVignetteOptions_setDefaults(FilaVignetteOptions* outOptions);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_OPTIONS_H

