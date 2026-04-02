#ifndef FILAMENT_C_VIEW_H
#define FILAMENT_C_VIEW_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "Types.h"
#include "Options.h"
#include "Viewport.h"
#include "../backend/CallbackHandler.h"

#ifdef __cplusplus
extern "C" {
#endif

// Sets the scene used when rendering this view.
void FilaView_setScene(FilaView* view, FilaScene* scene);

// Returns the scene currently attached to this view.
FilaScene* FilaView_getScene(FilaView* view);

// Sets the rectangular region rendered by this view.
void FilaView_setViewport(FilaView* view, FilaViewport viewport);

// Gets the rectangular region rendered by this view.
FilaViewport FilaView_getViewport(const FilaView* view);

// Sets the camera used when rendering this view.
void FilaView_setCamera(FilaView* view, FilaCamera* camera);

// Returns true when a camera is currently attached to this view.
bool FilaView_hasCamera(const FilaView* view);

// Returns the camera attached to this view, or NULL if none is set.
FilaCamera* FilaView_getCamera(FilaView* view);

// Sets the color grading transform for this view.
void FilaView_setColorGrading(FilaView* view, FilaColorGrading* colorGrading);

// Returns the color grading transform bound to this view.
FilaColorGrading* FilaView_getColorGrading(FilaView* view);

// Sets the render target for this view.
void FilaView_setRenderTarget(FilaView* view, FilaRenderTarget* renderTarget);

// Returns the render target bound to this view.
FilaRenderTarget* FilaView_getRenderTarget(FilaView* view);

typedef enum FilaViewBlendMode {
    FILA_VIEW_BLEND_MODE_OPAQUE = 0,
    FILA_VIEW_BLEND_MODE_TRANSLUCENT = 1,
} FilaViewBlendMode;

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
    FILA_VIEW_SHADOW_TYPE_DPCF = 2,
    FILA_VIEW_SHADOW_TYPE_PCSS = 3,
    FILA_VIEW_SHADOW_TYPE_PCFD = 4,
} FilaViewShadowType;

// Sets/gets an optional debug name for this view.
void FilaView_setName(FilaView* view, const char* name);
const char* FilaView_getName(const FilaView* view);

// Configures visibility layer mask.
void FilaView_setVisibleLayers(FilaView* view, uint8_t select, uint8_t values);
uint8_t FilaView_getVisibleLayers(const FilaView* view);

// Configures blend mode for rendering into swap chain.
void FilaView_setBlendMode(FilaView* view, FilaViewBlendMode blendMode);
FilaViewBlendMode FilaView_getBlendMode(const FilaView* view);

// Configures post-process anti-aliasing and dithering.

// Configures post-process anti-aliasing and dithering.
void FilaView_setAntiAliasing(FilaView* view, FilaViewAntiAliasing antiAliasing);
FilaViewAntiAliasing FilaView_getAntiAliasing(const FilaView* view);
void FilaView_setDithering(FilaView* view, FilaViewDithering dithering);
FilaViewDithering FilaView_getDithering(const FilaView* view);

// Configures shadowing strategy.
void FilaView_setShadowType(FilaView* view, FilaViewShadowType shadowType);
FilaViewShadowType FilaView_getShadowType(const FilaView* view);
void FilaView_setVsmShadowOptions(FilaView* view, const FilaVsmShadowOptions* options);
bool FilaView_getVsmShadowOptions(const FilaView* view, FilaVsmShadowOptions* outOptions);
void FilaView_setSoftShadowOptions(FilaView* view, const FilaSoftShadowOptions* options);
bool FilaView_getSoftShadowOptions(const FilaView* view, FilaSoftShadowOptions* outOptions);

// Toggles key render pipeline options.
void FilaView_setShadowingEnabled(FilaView* view, bool enabled);
bool FilaView_isShadowingEnabled(const FilaView* view);
void FilaView_setScreenSpaceRefractionEnabled(FilaView* view, bool enabled);
bool FilaView_isScreenSpaceRefractionEnabled(const FilaView* view);
void FilaView_setPostProcessingEnabled(FilaView* view, bool enabled);
bool FilaView_isPostProcessingEnabled(const FilaView* view);
void FilaView_setFrontFaceWindingInverted(FilaView* view, bool inverted);
bool FilaView_isFrontFaceWindingInverted(const FilaView* view);
void FilaView_setTransparentPickingEnabled(FilaView* view, bool enabled);
bool FilaView_isTransparentPickingEnabled(const FilaView* view);
void FilaView_setStencilBufferEnabled(FilaView* view, bool enabled);
bool FilaView_isStencilBufferEnabled(const FilaView* view);

// Per-channel depth clear control (channel index 0..7).
void FilaView_setChannelDepthClearEnabled(FilaView* view, uint8_t channel, bool enabled);
bool FilaView_isChannelDepthClearEnabled(const FilaView* view, uint8_t channel);

// Configures light clustering depth range heuristics.
void FilaView_setDynamicLightingOptions(FilaView* view, float zLightNear, float zLightFar);

// Configures dynamic resolution for this view.
void FilaView_setDynamicResolutionOptions(FilaView* view, const FilaDynamicResolutionOptions* options);
bool FilaView_getDynamicResolutionOptions(const FilaView* view, FilaDynamicResolutionOptions* outOptions);
bool FilaView_getLastDynamicResolutionScale(const FilaView* view, float outScale2[2]);

// Configures render-quality controls.
void FilaView_setRenderQuality(FilaView* view, const FilaRenderQuality* quality);
bool FilaView_getRenderQuality(const FilaView* view, FilaRenderQuality* outQuality);

// Configures MSAA/TAA/SSR options.
void FilaView_setMultiSampleAntiAliasingOptions(FilaView* view, const FilaMultiSampleAntiAliasingOptions* options);
bool FilaView_getMultiSampleAntiAliasingOptions(const FilaView* view, FilaMultiSampleAntiAliasingOptions* outOptions);
void FilaView_setTemporalAntiAliasingOptions(FilaView* view, const FilaTemporalAntiAliasingOptions* options);
bool FilaView_getTemporalAntiAliasingOptions(const FilaView* view, FilaTemporalAntiAliasingOptions* outOptions);
void FilaView_setScreenSpaceReflectionsOptions(FilaView* view, const FilaScreenSpaceReflectionsOptions* options);
bool FilaView_getScreenSpaceReflectionsOptions(const FilaView* view, FilaScreenSpaceReflectionsOptions* outOptions);

// Configures advanced post-process options.
void FilaView_setAmbientOcclusionOptions(FilaView* view, const FilaAmbientOcclusionOptions* options);
bool FilaView_getAmbientOcclusionOptions(const FilaView* view, FilaAmbientOcclusionOptions* outOptions);
void FilaView_setBloomOptions(FilaView* view, const FilaBloomOptions* options);
bool FilaView_getBloomOptions(const FilaView* view, FilaBloomOptions* outOptions);
void FilaView_setFogOptions(FilaView* view, const FilaFogOptions* options);
bool FilaView_getFogOptions(const FilaView* view, FilaFogOptions* outOptions);
void FilaView_setDepthOfFieldOptions(FilaView* view, const FilaDepthOfFieldOptions* options);
bool FilaView_getDepthOfFieldOptions(const FilaView* view, FilaDepthOfFieldOptions* outOptions);
void FilaView_setVignetteOptions(FilaView* view, const FilaVignetteOptions* options);
bool FilaView_getVignetteOptions(const FilaView* view, FilaVignetteOptions* outOptions);

// Configures guard band and stereoscopic options.
void FilaView_setGuardBandOptions(FilaView* view, const FilaGuardBandOptions* options);
bool FilaView_getGuardBandOptions(const FilaView* view, FilaGuardBandOptions* outOptions);
void FilaView_setStereoscopicOptions(FilaView* view, const FilaStereoscopicOptions* options);
bool FilaView_getStereoscopicOptions(const FilaView* view, FilaStereoscopicOptions* outOptions);

// Clears temporal history used by this view.
void FilaView_clearFrameHistory(FilaView* view, FilaEngine* engine);

// Debug/runtime culling toggle.
void FilaView_setFrustumCullingEnabled(FilaView* view, bool cullingEnabled);
bool FilaView_isFrustumCullingEnabled(const FilaView* view);

// Returns the fog entity associated with this view.
FilaEntity FilaView_getFogEntity(const FilaView* view);

// Debug camera and directional-shadow camera inspection helpers.
void FilaView_setDebugCamera(FilaView* view, FilaCamera* camera);
size_t FilaView_getDirectionalShadowCameraCount(const FilaView* view);
size_t FilaView_getDirectionalShadowCameras(
    const FilaView* view,
    const FilaCamera** outCameras,
    size_t outCameraCount);

// Debug froxel visualizer controls.
void FilaView_setFroxelVizEnabled(FilaView* view, bool enabled);

typedef struct FilaViewFroxelConfigurationInfo {
    uint16_t width;
    uint16_t height;
    uint16_t depth;
    uint32_t viewportWidth;
    uint32_t viewportHeight;
    uint32_t froxelDimension[2];
    float zLightFar;
    float linearizer;
    float projection[16];
    float clipTransform[4];
} FilaViewFroxelConfigurationInfo;

typedef struct FilaViewFroxelConfigurationInfoWithAge {
    FilaViewFroxelConfigurationInfo info;
    uint32_t age;
} FilaViewFroxelConfigurationInfoWithAge;

bool FilaView_getFroxelConfigurationInfo(
    const FilaView* view,
    FilaViewFroxelConfigurationInfoWithAge* outInfo);

typedef struct FilaViewPickingQueryResult {
    FilaEntity renderable;
    float depth;
    uint32_t reserved1;
    uint32_t reserved2;
    float fragCoordsX;
    float fragCoordsY;
    float fragCoordsZ;
} FilaViewPickingQueryResult;

typedef void (*FilaViewPickCallback)(
    const FilaViewPickingQueryResult* result,
    void* userData);
typedef void (*FilaViewPickTokenCallback)(
    const FilaViewPickingQueryResult* result,
    uintptr_t userToken);

// Schedules an asynchronous picking query for this view.
bool FilaView_pick(
    FilaView* view,
    uint32_t x,
    uint32_t y,
    FilaViewPickCallback callback,
    void* userData);

bool FilaView_pickWithHandler(
    FilaView* view,
    uint32_t x,
    uint32_t y,
    FilaCallbackHandler* handler,
    FilaViewPickCallback callback,
    void* userData);

bool FilaView_pickWithToken(
    FilaView* view,
    uint32_t x,
    uint32_t y,
    FilaViewPickTokenCallback callback,
    uintptr_t userToken);

bool FilaView_pickWithHandlerAndToken(
    FilaView* view,
    uint32_t x,
    uint32_t y,
    FilaCallbackHandler* handler,
    FilaViewPickTokenCallback callback,
    uintptr_t userToken);

// Material global variables for user materials (index range: 0..3).
void FilaView_setMaterialGlobal(FilaView* view, uint32_t index, const float value4[4]);
bool FilaView_getMaterialGlobal(const FilaView* view, uint32_t index, float outValue4[4]);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_VIEW_H

