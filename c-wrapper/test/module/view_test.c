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

    FilaEngine_destroyCameraComponent(engine, entity);
    FilaEngine_destroyView(engine, view);
}
