#include "filament/Camera.h"
#include "filament/Engine.h"

// Verifies Camera API is consumable from C and composes with Engine camera lifecycle.
void fila_camera_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaEntity entity = 1;

    FilaCamera* camera = FilaEngine_createCamera(engine, entity);
    (void)FilaEngine_getCameraComponent(engine, entity);
    (void)FilaCamera_getEntity(camera);
    FilaCamera_setExposure(camera, 16.0f, 1.0f / 60.0f, 100.0f);
    FilaEngine_destroyCameraComponent(engine, entity);
}

