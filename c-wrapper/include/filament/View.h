#ifndef FILAMENT_C_VIEW_H
#define FILAMENT_C_VIEW_H

#include <stdbool.h>
#include <stdint.h>

#include "Types.h"
#include "Viewport.h"

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
void FilaView_setAntiAliasing(FilaView* view, FilaViewAntiAliasing antiAliasing);
FilaViewAntiAliasing FilaView_getAntiAliasing(const FilaView* view);
void FilaView_setDithering(FilaView* view, FilaViewDithering dithering);
FilaViewDithering FilaView_getDithering(const FilaView* view);

// Configures shadowing strategy.
void FilaView_setShadowType(FilaView* view, FilaViewShadowType shadowType);
FilaViewShadowType FilaView_getShadowType(const FilaView* view);

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

// Debug/runtime culling toggle.
void FilaView_setFrustumCullingEnabled(FilaView* view, bool cullingEnabled);
bool FilaView_isFrustumCullingEnabled(const FilaView* view);

// Returns the fog entity associated with this view.
FilaEntity FilaView_getFogEntity(const FilaView* view);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_VIEW_H

