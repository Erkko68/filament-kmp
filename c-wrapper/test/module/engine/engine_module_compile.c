#include "filament/Engine.h"

// Verifies Engine header can be consumed from plain C.
void fila_engine_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaRenderer* renderer = (FilaRenderer*)0;
    FilaScene* scene = (FilaScene*)0;
    FilaView* view = (FilaView*)0;

    FilaEngine_destroy(&engine);
    FilaEngine_destroyRenderer(engine, renderer);
    FilaEngine_destroyScene(engine, scene);
    FilaEngine_destroyView(engine, view);
}

