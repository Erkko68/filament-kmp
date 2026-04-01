#include "filament/Engine.h"
#include "utils/EntityManager.h"
#include "filament/TransformManager.h"

static void transform_instance_callback(FilaTransformManagerInstance instance, void* userData) {
    (void)instance;
    (void)userData;
}

// Verifies TransformManager API is consumable from C and composes with Engine + EntityManager.
void test_headers_transform_manager(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaTransformManager* manager = FilaEngine_getTransformManager(engine);
    FilaEntity parent = FilaEntityManager_create();
    FilaEntity child = FilaEntityManager_create();
    float identity[16] = {
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f,
    };
    double identity64[16] = {
        1.0, 0.0, 0.0, 0.0,
        0.0, 1.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 1.0,
    };
    float outLocal[16];
    float outWorld[16];
    double outLocal64[16];
    double outWorld64[16];
    FilaEntity children[4];
    FilaTransformManagerInstance childInstances[4];
    FilaEntity listedEntities[4];

    FilaTransformManager_createWithTransformMat4f(manager, parent, 0u, identity);
    FilaTransformManager_createWithTransformMat4(manager, child,
            FilaTransformManager_getInstance(manager, parent),
            identity64);
    (void)FilaTransformManager_hasComponent(manager, parent);
    FilaTransformManagerInstance parentInstance = FilaTransformManager_getInstance(manager, parent);
    FilaTransformManagerInstance childInstance = FilaTransformManager_getInstance(manager, child);
    (void)FilaTransformManager_getEntity(manager, childInstance);
    FilaTransformManager_setAccurateTranslationsEnabled(manager, true);
    (void)FilaTransformManager_isAccurateTranslationsEnabled(manager);
    (void)FilaTransformManager_getEntities(manager, listedEntities, 4u);
    FilaTransformManager_setParent(manager, childInstance, parentInstance);
    (void)FilaTransformManager_getParent(manager, childInstance);
    (void)FilaTransformManager_getChildCount(manager, parentInstance);
    (void)FilaTransformManager_getChildren(manager, parentInstance, children, 4u);
    (void)FilaTransformManager_getChildInstances(manager, parentInstance, childInstances, 4u);
    FilaTransformManager_forEachChildInstance(manager, parentInstance, transform_instance_callback, (void*)0);
    FilaTransformManager_openLocalTransformTransaction(manager);
    FilaTransformManager_setTransformMat4f(manager, childInstance, identity);
    FilaTransformManager_setTransformMat4(manager, childInstance, identity64);
    FilaTransformManager_commitLocalTransformTransaction(manager);
    (void)FilaTransformManager_getTransformMat4f(manager, childInstance, outLocal);
    (void)FilaTransformManager_getWorldTransformMat4f(manager, childInstance, outWorld);
    (void)FilaTransformManager_getTransformMat4(manager, childInstance, outLocal64);
    (void)FilaTransformManager_getWorldTransformMat4(manager, childInstance, outWorld64);
    (void)FilaTransformManager_getComponentCount(manager);
    (void)FilaTransformManager_empty(manager);
    FilaTransformManager_destroy(manager, child);
    FilaTransformManager_destroy(manager, parent);
    FilaEntityManager_destroy(child);
    FilaEntityManager_destroy(parent);
}
