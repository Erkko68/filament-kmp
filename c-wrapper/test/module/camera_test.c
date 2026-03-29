#include "filament/Camera.h"
#include "filament/Engine.h"
#include "filament/EntityManager.h"

// Verifies Camera API is consumable from C and composes with Engine camera lifecycle.
void fila_camera_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaEntity entity = FilaEntityManager_create();

    FilaCamera* camera = FilaEngine_createCamera(engine, entity);
    (void)FilaEngine_getCameraComponent(engine, entity);
    (void)FilaCamera_getEntity(camera);
    FilaCamera_setExposure(camera, 16.0f, 1.0f / 60.0f, 100.0f);
    FilaCamera_setProjectionFov(camera, 45.0, 16.0 / 9.0, 0.1, 1000.0, FILA_CAMERA_FOV_VERTICAL);
    FilaCamera_lookAt(camera, 0.0, 1.0, 3.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
    FilaEngine_destroyCameraComponent(engine, entity);
    FilaEntityManager_destroy(entity);
}

