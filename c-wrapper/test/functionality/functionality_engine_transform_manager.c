#include <stdio.h>

#include <math.h>

#include "filament/Engine.h"
#include "utils/EntityManager.h"
#include "filament/TransformManager.h"

typedef struct {
    FilaTransformManagerInstance expected;
    size_t count;
    bool foundExpected;
} ChildInstanceVisitorContext;

static void count_child_instance(FilaTransformManagerInstance instance, void* userData) {
    ChildInstanceVisitorContext* context = (ChildInstanceVisitorContext*)userData;
    if (!context) {
        return;
    }
    context->count += 1u;
    if (instance == context->expected) {
        context->foundExpected = true;
    }
}

int main(void) {
    printf("Running engine+transform_manager functionality program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaTransformManager* manager = FilaEngine_getTransformManager(engine);
    if (!manager) {
        printf("TransformManager retrieval failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity parent = FilaEntityManager_create();
    FilaEntity child = FilaEntityManager_create();
    if (parent == 0 || child == 0) {
        printf("Entity creation failed\n");
        if (parent != 0) {
            FilaEntityManager_destroy(parent);
        }
        if (child != 0) {
            FilaEntityManager_destroy(child);
        }
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaTransformManager_hasComponent(manager, parent) ||
            FilaTransformManager_hasComponent(manager, child)) {
        printf("Entity unexpectedly has transform component\n");
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTransformManager_setAccurateTranslationsEnabled(manager, true);
    if (!FilaTransformManager_isAccurateTranslationsEnabled(manager)) {
        printf("Accurate translation mode failed to enable\n");
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    const float parentInit[16] = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
    };
    FilaTransformManager_createWithTransformMat4f(manager, parent, 0u, parentInit);
    FilaTransformManagerInstance parentInstance = FilaTransformManager_getInstance(manager, parent);

    const double childInit[16] = {
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.5, 0.0, 0.0, 1.0,
    };
    FilaTransformManager_createWithTransformMat4(manager, child, parentInstance, childInit);

    if (!FilaTransformManager_hasComponent(manager, parent) ||
            !FilaTransformManager_hasComponent(manager, child)) {
        printf("Transform component creation failed\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity listed[4] = {0};
    const size_t listedCount = FilaTransformManager_getEntities(manager, listed, 4u);
    if (listedCount < 2u) {
        printf("Transform manager entity listing failed\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTransformManagerInstance childInstance = FilaTransformManager_getInstance(manager, child);
    if (parentInstance == 0u || childInstance == 0u) {
        printf("Transform instance retrieval failed\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaTransformManager_getEntity(manager, childInstance) != child) {
        printf("Transform instance entity mismatch\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaTransformManager_getParent(manager, childInstance) != parent) {
        printf("Transform parent mismatch\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity children[8] = {0};
    const size_t childCount = FilaTransformManager_getChildren(manager, parentInstance, children, 8u);
    if (childCount == 0u) {
        printf("Transform child listing failed\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTransformManagerInstance childInstances[8] = {0u};
    const size_t childInstanceCount =
            FilaTransformManager_getChildInstances(manager, parentInstance, childInstances, 8u);
    bool foundChildInstance = false;
    for (size_t i = 0; i < childInstanceCount; ++i) {
        if (childInstances[i] == childInstance) {
            foundChildInstance = true;
            break;
        }
    }
    if (childInstanceCount == 0u || !foundChildInstance) {
        printf("Transform child-instance listing failed\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    ChildInstanceVisitorContext visitorContext = { childInstance, 0u, false };
    FilaTransformManager_forEachChildInstance(manager, parentInstance, count_child_instance, &visitorContext);
    if (visitorContext.count == 0u || !visitorContext.foundExpected) {
        printf("Transform child-instance iteration failed\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    const float childLocal[16] = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            1.5f, 0.0f, 0.0f, 1.0f,
    };
    const double childLocal64[16] = {
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            2.0, 0.0, 0.0, 1.0,
    };
    float localOut[16] = {0};
    float worldOut[16] = {0};
    double localOut64[16] = {0};
    double worldOut64[16] = {0};

    FilaTransformManager_openLocalTransformTransaction(manager);
    FilaTransformManager_setTransformMat4f(manager, childInstance, childLocal);
    FilaTransformManager_setTransformMat4(manager, childInstance, childLocal64);
    FilaTransformManager_commitLocalTransformTransaction(manager);

    if (!FilaTransformManager_getTransformMat4f(manager, childInstance, localOut)) {
        printf("Transform local matrix get failed\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }
    if (!FilaTransformManager_getWorldTransformMat4f(manager, childInstance, worldOut)) {
        printf("Transform world matrix get failed\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }
    if (fabsf(localOut[12] - 2.0f) > 1e-5f || fabsf(worldOut[12] - 2.0f) > 1e-5f) {
        printf("Transform matrix translation mismatch\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (!FilaTransformManager_getTransformMat4(manager, childInstance, localOut64) ||
            !FilaTransformManager_getWorldTransformMat4(manager, childInstance, worldOut64)) {
        printf("Accurate transform matrix get failed\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }
    if (fabs(localOut64[12] - 2.0) > 1e-8 || fabs(worldOut64[12] - 2.0) > 1e-8) {
        printf("Accurate transform matrix translation mismatch\n");
        FilaTransformManager_destroy(manager, child);
        FilaTransformManager_destroy(manager, parent);
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTransformManager_destroy(manager, child);
    FilaTransformManager_destroy(manager, parent);
    if (FilaTransformManager_hasComponent(manager, child) ||
            FilaTransformManager_hasComponent(manager, parent)) {
        printf("Transform component still exists after destroy\n");
        FilaEntityManager_destroy(child);
        FilaEntityManager_destroy(parent);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntityManager_destroy(child);
    FilaEntityManager_destroy(parent);
    FilaEngine_destroy(&engine);

    printf("Engine+transform_manager functionality program completed\n");
    return 0;
}

