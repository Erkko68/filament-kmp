#ifndef FILAMENT_C_VIEW_H
#define FILAMENT_C_VIEW_H

#include "FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif



typedef enum FilaViewAntiAliasing {
    FILA_VIEW_ANTI_ALIASING_NONE = 0,
    FILA_VIEW_ANTI_ALIASING_FXAA = 1,
} FilaViewAntiAliasing;

typedef enum FilaViewDithering {
    FILA_VIEW_DITHERING_NONE = 0,
    FILA_VIEW_DITHERING_TEMPORAL = 1,
} FilaViewDithering;

typedef enum FilaViewShadowType {
    FILA_VIEW_SHADOW_TYPE_PCF = 0,
    FILA_VIEW_SHADOW_TYPE_VSM = 1,
} FilaViewShadowType;

typedef enum FilaViewBlendMode {
    FILA_VIEW_BLEND_MODE_OPAQUE = 0,
    FILA_VIEW_BLEND_MODE_TRANSLUCENT = 1,
} FilaViewBlendMode;

typedef enum FilaViewQualityLevel {
    FILA_VIEW_QUALITY_LEVEL_LOW = 0,
    FILA_VIEW_QUALITY_LEVEL_MEDIUM = 1,
    FILA_VIEW_QUALITY_LEVEL_HIGH = 2,
    FILA_VIEW_QUALITY_LEVEL_ULTRA = 3,
} FilaViewQualityLevel;

typedef struct FilaViewDynamicResolutionOptions {
    float minScale[2];
    float maxScale[2];
    float sharpness;
    bool enabled;
    bool homogeneousScaling;
    FilaViewQualityLevel quality;
} FilaViewDynamicResolutionOptions;

typedef struct FilaViewBloomOptions {
    FilaTexture* dirt;
    float dirtStrength;
    float strength;
    uint32_t resolution;
    uint8_t levels;
    int blendMode;
    bool threshold;
    bool enabled;
    float highlight;
    FilaViewQualityLevel quality;
    bool lensFlare;
    bool starburst;
    float chromaticAberration;
    uint8_t ghostCount;
    float ghostSpacing;
    float ghostThreshold;
    float haloThickness;
    float haloRadius;
    float haloThreshold;
} FilaViewBloomOptions;

typedef struct FilaViewFogOptions {
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
} FilaViewFogOptions;

typedef struct FilaViewDepthOfFieldOptions {
    float cocScale;
    float cocAspectRatio;
    float maxApertureDiameter;
    bool enabled;
    int filter;
    bool nativeResolution;
    uint8_t foregroundRingCount;
    uint8_t backgroundRingCount;
    uint8_t fastGatherRingCount;
    uint16_t maxForegroundCOC;
    uint16_t maxBackgroundCOC;
} FilaViewDepthOfFieldOptions;

typedef struct FilaViewVignetteOptions {
    float midPoint;
    float roundness;
    float feather;
    float color[4];
    bool enabled;
} FilaViewVignetteOptions;

typedef struct FilaViewAmbientOcclusionOptions {
    float radius;
    float bias;
    float power;
    float resolution;
    float intensity;
    float bilateralThreshold;
    FilaViewQualityLevel quality;
    FilaViewQualityLevel lowPassFilter;
    FilaViewQualityLevel upsampling;
    bool enabled;
    bool bentNormals;
    float minHorizonAngleRad;
    struct {
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
    } ssct;
    struct {
        uint8_t sampleSliceCount;
        uint8_t sampleStepsPerSlice;
        float thicknessHeuristic;
        bool useVisibilityBitmasks;
        float constThickness;
        bool linearThickness;
    } gtao;
    int aoType;
} FilaViewAmbientOcclusionOptions;

typedef struct FilaViewTemporalAntiAliasingOptions {
    float feedback;
    float lodBias;
    float sharpness;
    bool enabled;
    float upscaling;
    bool filterHistory;
    bool filterInput;
    bool useYCoCg;
    bool hdr;
    int boxType;
    int boxClipping;
    int jitterPattern;
    float varianceGamma;
    bool preventFlickering;
    bool historyReprojection;
} FilaViewTemporalAntiAliasingOptions;

typedef struct FilaViewMultiSampleAntiAliasingOptions {
    bool enabled;
    uint8_t sampleCount;
    bool customResolve;
} FilaViewMultiSampleAntiAliasingOptions;

typedef struct FilaViewScreenSpaceReflectionsOptions {
    float thickness;
    float bias;
    float maxDistance;
    float stride;
    bool enabled;
} FilaViewScreenSpaceReflectionsOptions;

typedef struct FilaViewVsmShadowOptions {
    uint8_t anisotropy;
    bool mipmapping;
    uint8_t msaaSamples;
    bool highPrecision;
    float lightBleedReduction;
} FilaViewVsmShadowOptions;

typedef struct FilaViewSoftShadowOptions {
    float penumbraScale;
    float penumbraRatioScale;
    float maxPenumbraRatio;
    float maxSearchRadius;
} FilaViewSoftShadowOptions;

typedef struct FilaViewGuardBandOptions {
    bool enabled;
} FilaViewGuardBandOptions;

typedef struct FilaViewStereoscopicOptions {
    bool enabled;
} FilaViewStereoscopicOptions;

typedef struct FilaViewPickingQueryResult {
    FilaEntity renderable;
    float depth;
    float fragCoords[3];
} FilaViewPickingQueryResult;

typedef void (*FilaViewPickingCallback)(const FilaViewPickingQueryResult* result, void* userData);

// View
void FilaView_setName(FilaView* view, const char* name);
const char* FilaView_getName(const FilaView* view);
void FilaView_setScene(FilaView* view, FilaScene* scene);
void FilaView_setCamera(FilaView* view, FilaCamera* camera);
bool FilaView_hasCamera(const FilaView* view);

void FilaView_setColorGrading(FilaView* view, FilaColorGrading* colorGrading);
void FilaView_setViewport(FilaView* view, int left, int bottom, uint32_t width, uint32_t height);
void FilaView_getViewport(const FilaView* view, int* left, int* bottom, uint32_t* width, uint32_t* height);
void FilaView_setVisibleLayers(FilaView* view, uint32_t select, uint32_t value);
uint8_t FilaView_getVisibleLayers(const FilaView* view);

void FilaView_setRenderTarget(FilaView* view, FilaRenderTarget* renderTarget);

void FilaView_setAntiAliasing(FilaView* view, FilaViewAntiAliasing type);
FilaViewAntiAliasing FilaView_getAntiAliasing(const FilaView* view);

void FilaView_setDithering(FilaView* view, FilaViewDithering dithering);
FilaViewDithering FilaView_getDithering(const FilaView* view);

void FilaView_setDynamicResolutionOptions(FilaView* view, float minScale_0, float minScale_1, float maxScale_0, float maxScale_1, float sharpness, bool enabled, bool homogeneousScaling, FilaViewQualityLevel quality);
void FilaView_getDynamicResolutionOptions(const FilaView* view, float* floats, int32_t* ints);
void FilaView_getLastDynamicResolutionScale(const FilaView* view, float out[2]);

void FilaView_setShadowType(FilaView* view, FilaViewShadowType type);
void FilaView_setVsmShadowOptions(FilaView* view, uint32_t anisotropy, bool mipmapping, uint32_t msaaSamples, bool highPrecision, float lightBleedReduction);
void FilaView_getVsmShadowOptions(const FilaView* view, float* floats, int32_t* ints);
void FilaView_setSoftShadowOptions(FilaView* view, float penumbraScale, float penumbraRatioScale, float maxPenumbraRatio, float maxSearchRadius);
void FilaView_getSoftShadowOptions(const FilaView* view, float* floats);

void FilaView_setRenderQuality(FilaView* view, FilaViewQualityLevel hdrColorBufferQuality);
FilaViewQualityLevel FilaView_getRenderQuality(const FilaView* view);
void FilaView_setDynamicLightingOptions(FilaView* view, float zLightNear, float zLightFar);

void FilaView_setShadowingEnabled(FilaView* view, bool enabled);
bool FilaView_isShadowingEnabled(const FilaView* view);

void FilaView_setPostProcessingEnabled(FilaView* view, bool enabled);
bool FilaView_isPostProcessingEnabled(const FilaView* view);

void FilaView_setFrontFaceWindingInverted(FilaView* view, bool inverted);
bool FilaView_isFrontFaceWindingInverted(const FilaView* view);

void FilaView_setTransparentPickingEnabled(FilaView* view, bool enabled);
bool FilaView_isTransparentPickingEnabled(const FilaView* view);

void FilaView_setAmbientOcclusionOptions(FilaView* view, float radius, float bias, float power, float resolution, float intensity, float bilateralThreshold, FilaViewQualityLevel quality, FilaViewQualityLevel lowPassFilter, FilaViewQualityLevel upsampling, bool enabled, bool bentNormals, float minHorizonAngleRad, float ssct_lightConeRad, float ssct_shadowDistance, float ssct_contactDistanceMax, float ssct_intensity, float ssct_lightDirection_0, float ssct_lightDirection_1, float ssct_lightDirection_2, float ssct_depthBias, float ssct_depthSlopeBias, uint32_t ssct_sampleCount, uint32_t ssct_rayCount, bool ssct_enabled, uint32_t gtao_sampleSliceCount, uint32_t gtao_sampleStepsPerSlice, float gtao_thicknessHeuristic, bool gtao_useVisibilityBitmasks, float gtao_constThickness, bool gtao_linearThickness, int aoType);
void FilaView_getAmbientOcclusionOptions(const FilaView* view, float* floats, int32_t* ints);
void FilaView_setBloomOptions(FilaView* view, FilaTexture* dirt, float dirtStrength, float strength, uint32_t resolution, uint32_t levels, int blendMode, bool threshold, bool enabled, float highlight, FilaViewQualityLevel quality, bool lensFlare, bool starburst, float chromaticAberration, uint32_t ghostCount, float ghostSpacing, float ghostThreshold, float haloThickness, float haloRadius, float haloThreshold);
void FilaView_getBloomOptions(const FilaView* view, float* floats, int32_t* ints);
void FilaView_setFogOptions(FilaView* view, float distance, float cutOffDistance, float maximumOpacity, float height, float heightFalloff, float color_0, float color_1, float color_2, float density, float inScatteringStart, float inScatteringSize, bool fogColorFromIbl, FilaTexture* skyColor, bool enabled);
void FilaView_getFogOptions(const FilaView* view, float* floats, int32_t* ints);
void FilaView_setBlendMode(FilaView* view, FilaViewBlendMode blendMode);
FilaViewBlendMode FilaView_getBlendMode(const FilaView* view);
void FilaView_setDepthOfFieldOptions(FilaView* view, float cocScale, float cocAspectRatio, float maxApertureDiameter, bool enabled, int filter, bool nativeResolution, uint32_t foregroundRingCount, uint32_t backgroundRingCount, uint32_t fastGatherRingCount, uint32_t maxForegroundCOC, uint32_t maxBackgroundCOC);
void FilaView_getDepthOfFieldOptions(const FilaView* view, float* floats, int32_t* ints);
void FilaView_setVignetteOptions(FilaView* view, float midPoint, float roundness, float feather, float color_0, float color_1, float color_2, float color_3, bool enabled);
void FilaView_getVignetteOptions(const FilaView* view, float* floats, int32_t* ints);
void FilaView_setTemporalAntiAliasingOptions(FilaView* view, float feedback, float lodBias, float sharpness, bool enabled, float upscaling, bool filterHistory, bool filterInput, bool useYCoCg, bool hdr, int boxType, int boxClipping, int jitterPattern, float varianceGamma, bool preventFlickering, bool historyReprojection);
void FilaView_getTemporalAntiAliasingOptions(const FilaView* view, float* floats, int32_t* ints);
void FilaView_setMultiSampleAntiAliasingOptions(FilaView* view, bool enabled, uint32_t sampleCount, bool customResolve);
void FilaView_getMultiSampleAntiAliasingOptions(const FilaView* view, int32_t* ints);
void FilaView_setScreenSpaceReflectionsOptions(FilaView* view, float thickness, float bias, float maxDistance, float stride, bool enabled);
void FilaView_getScreenSpaceReflectionsOptions(const FilaView* view, float* floats, int32_t* ints);

void FilaView_setStereoscopicOptions(FilaView* view, bool enabled);
void FilaView_getStereoscopicOptions(const FilaView* view, int32_t* ints);

void FilaView_setGuardBandOptions(FilaView* view, bool enabled);
void FilaView_getGuardBandOptions(const FilaView* view, int32_t* ints);

void FilaView_setFrustumCullingEnabled(FilaView* view, bool enabled);
bool FilaView_isFrustumCullingEnabled(const FilaView* view);

void FilaView_setScreenSpaceRefractionEnabled(FilaView* view, bool enabled);
bool FilaView_isScreenSpaceRefractionEnabled(const FilaView* view);

// Copies a picking callback's result out: renderable entity, then depth and fragCoords[3].
void FilaView_readPickingResult(const FilaViewPickingQueryResult* result, int32_t* renderable, float* depthAndFragCoords);
void FilaView_pick(FilaView* view, uint32_t x, uint32_t y, FilaCallbackHandler* handler, FilaViewPickingCallback callback, void* userData);

void FilaView_setStencilBufferEnabled(FilaView* view, bool enabled);
bool FilaView_isStencilBufferEnabled(const FilaView* view);

void FilaView_setGridSize(FilaView* view, double size);
double FilaView_getGridSize(const FilaView* view);
double FilaView_getEffectiveGridSize(const FilaView* view);
void FilaView_setMaterialGlobal(FilaView* view, uint32_t index, float x, float y, float z, float w);
void FilaView_getMaterialGlobal(const FilaView* view, uint32_t index, float out[4]);

FilaEntity FilaView_getFogEntity(const FilaView* view);
int32_t FilaView_getVisibleRenderableCount(const FilaView* view);
void FilaView_clearFrameHistory(FilaView* view, FilaEngine* engine);

void FilaView_setChannelDepthClearEnabled(FilaView* view, uint32_t channel, bool enabled);
bool FilaView_isChannelDepthClearEnabled(const FilaView* view, uint32_t channel);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_VIEW_H
