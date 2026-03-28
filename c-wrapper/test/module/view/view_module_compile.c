#include "filament/Engine.h"
#include "filament/View.h"

// Verifies View API is consumable from C and composes with Engine + Scene handles.
void fila_view_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaView* view = FilaEngine_createView(engine);
    FilaScene* scene = (FilaScene*)0;

    FilaView_setScene(view, scene);
    (void)FilaView_getScene(view);
    FilaEngine_destroyView(engine, view);
}

