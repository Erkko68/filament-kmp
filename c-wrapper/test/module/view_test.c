#include "filament/Engine.h"
#include "filament/View.h"
#include "filament/ColorGrading.h"
#include "filament/RenderTarget.h"
#include "filament/Texture.h"

// Verifies View API is consumable from C and composes with Engine + Scene handles.
void fila_view_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaView* view = FilaEngine_createView(engine);
    FilaScene* scene = (FilaScene*)0;
    FilaEntity entity = 2;
    FilaCamera* camera = FilaEngine_createCamera(engine, entity);
    FilaViewport viewport = {0, 0, 640u, 480u};

    FilaView_setScene(view, scene);
    (void)FilaView_getScene(view);
    FilaView_setViewport(view, viewport);
    (void)FilaView_getViewport(view);
    FilaView_setCamera(view, camera);
    (void)FilaView_hasCamera(view);
    (void)FilaView_getCamera(view);

    // Test ColorGrading binding
    FilaColorGrading* colorGrading = (FilaColorGrading*)0;
    FilaView_setColorGrading(view, colorGrading);
    (void)FilaView_getColorGrading(view);

    // Test RenderTarget binding
    FilaRenderTarget* renderTarget = (FilaRenderTarget*)0;
    FilaView_setRenderTarget(view, renderTarget);
    (void)FilaView_getRenderTarget(view);

    FilaView_setName(view, "main-view");
    (void)FilaView_getName(view);
    FilaView_setVisibleLayers(view, 0xFFu, 0x03u);
    (void)FilaView_getVisibleLayers(view);
    FilaView_setBlendMode(view, FILA_VIEW_BLEND_MODE_TRANSLUCENT);
    (void)FilaView_getBlendMode(view);
    FilaView_setAntiAliasing(view, FILA_VIEW_ANTI_ALIASING_FXAA);
    (void)FilaView_getAntiAliasing(view);
    FilaView_setDithering(view, FILA_VIEW_DITHERING_TEMPORAL);
    (void)FilaView_getDithering(view);
    FilaView_setShadowType(view, FILA_VIEW_SHADOW_TYPE_VSM);
    (void)FilaView_getShadowType(view);
    FilaView_setShadowingEnabled(view, true);
    (void)FilaView_isShadowingEnabled(view);
    FilaView_setScreenSpaceRefractionEnabled(view, false);
    (void)FilaView_isScreenSpaceRefractionEnabled(view);
    FilaView_setPostProcessingEnabled(view, true);
    (void)FilaView_isPostProcessingEnabled(view);
    FilaView_setFrontFaceWindingInverted(view, false);
    (void)FilaView_isFrontFaceWindingInverted(view);
    FilaView_setTransparentPickingEnabled(view, false);
    (void)FilaView_isTransparentPickingEnabled(view);
    FilaView_setStencilBufferEnabled(view, false);
    (void)FilaView_isStencilBufferEnabled(view);

    FilaEngine_destroyCameraComponent(engine, entity);
    FilaEngine_destroyView(engine, view);
}
