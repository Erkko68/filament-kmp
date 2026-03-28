#include "filament/Engine.h"
#include "filament/View.h"

// Verifies View API is consumable from C and composes with Engine + Scene handles.
void fila_view_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaView* view = FilaEngine_createView(engine);
    FilaScene* scene = (FilaScene*)0;
    FilaEntity entity = 2;
    FilaCamera* camera = FilaEngine_createCamera(engine, entity);

    FilaView_setScene(view, scene);
    (void)FilaView_getScene(view);
    FilaView_setCamera(view, camera);
    (void)FilaView_hasCamera(view);
    (void)FilaView_getCamera(view);
    FilaEngine_destroyCameraComponent(engine, entity);
    FilaEngine_destroyView(engine, view);
}

