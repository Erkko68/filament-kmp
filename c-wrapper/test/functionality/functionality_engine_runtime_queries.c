#include <limits.h>
#include <stdio.h>

#include "filament/Engine.h"
#include "filament/DebugRegistry.h"
#include "filament/TransformManager.h"
#include "utils/EntityManager.h"

int main(void) {
    printf("Running functionality_engine_runtime_queries...\n");

    // Null-safety checks for query and control APIs.
    if (FilaEngine_getSupportedFeatureLevel((const FilaEngine*)0) != FILA_BACKEND_FEATURE_LEVEL_0) {
        printf("Unexpected supported feature level for null engine\n");
        return 1;
    }
    if (FilaEngine_getActiveFeatureLevel((const FilaEngine*)0) != FILA_BACKEND_FEATURE_LEVEL_0) {
        printf("Unexpected active feature level for null engine\n");
        return 1;
    }
    if (FilaEngine_getMaxAutomaticInstances((const FilaEngine*)0) != 0u) {
        printf("Unexpected automatic instance limit for null engine\n");
        return 1;
    }

    FilaBackendFeatureLevel outActive = FILA_BACKEND_FEATURE_LEVEL_0;
    if (FilaEngine_setActiveFeatureLevel((FilaEngine*)0, FILA_BACKEND_FEATURE_LEVEL_1, &outActive)) {
        printf("setActiveFeatureLevel should fail on null engine\n");
        return 1;
    }
    if (FilaEngine_flush((FilaEngine*)0)) {
        printf("flush should fail on null engine\n");
        return 1;
    }
    if (FilaEngine_flushAndWait((FilaEngine*)0)) {
        printf("flushAndWait should fail on null engine\n");
        return 1;
    }
    if (FilaEngine_flushAndWaitWithTimeout((FilaEngine*)0, 1u, (bool*)0)) {
        printf("flushAndWaitWithTimeout should fail on null args\n");
        return 1;
    }

    if (FilaEngine_isValidRenderer((const FilaEngine*)0, (const FilaRenderer*)0)) {
        printf("isValidRenderer should fail on null args\n");
        return 1;
    }
    if (FilaEngine_setPaused((FilaEngine*)0, true)) {
        printf("setPaused should fail on null engine\n");
        return 1;
    }
    if (FilaEngine_getDefaultMaterial((const FilaEngine*)0) != (const FilaMaterial*)0) {
        printf("Default material should be null for null engine\n");
        return 1;
    }
    if (FilaEngine_hasFeatureFlag((const FilaEngine*)0, "x")) {
        printf("hasFeatureFlag should fail on null engine\n");
        return 1;
    }
    bool featureValue = false;
    if (FilaEngine_getFeatureFlag((const FilaEngine*)0, "x", &featureValue)) {
        printf("getFeatureFlag should fail on null engine\n");
        return 1;
    }
    if (FilaEngine_setFeatureFlag((FilaEngine*)0, "x", false)) {
        printf("setFeatureFlag should fail on null engine\n");
        return 1;
    }
    if (FilaEngine_getFeatureFlagCount((const FilaEngine*)0) != 0u) {
        printf("Feature flag count should be zero for null engine\n");
        return 1;
    }
    if (FilaEngine_getFeatureFlagInfo((const FilaEngine*)0, 0u, (const char**)0, (const char**)0, (bool*)0, (bool*)0)) {
        printf("getFeatureFlagInfo should fail on null engine\n");
        return 1;
    }
    if (FilaEngine_execute((FilaEngine*)0)) {
        printf("execute should fail on null engine\n");
        return 1;
    }

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    const FilaBackendFeatureLevel supported = FilaEngine_getSupportedFeatureLevel(engine);
    const FilaBackendFeatureLevel active = FilaEngine_getActiveFeatureLevel(engine);
    if ((int)active > (int)supported) {
        printf("Active feature level higher than supported level\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    outActive = FILA_BACKEND_FEATURE_LEVEL_0;
    if (!FilaEngine_setActiveFeatureLevel(engine, active, &outActive)) {
        printf("setActiveFeatureLevel failed for current level\n");
        FilaEngine_destroy(&engine);
        return 1;
    }
    if ((int)outActive < (int)active || (int)outActive > (int)supported) {
        printf("setActiveFeatureLevel returned unexpected active level\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEngine_setAutomaticInstancingEnabled(engine, true);
    if (!FilaEngine_isAutomaticInstancingEnabled(engine)) {
        printf("Automatic instancing should be enabled\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEngine_setAutomaticInstancingEnabled(engine, false);
    if (FilaEngine_isAutomaticInstancingEnabled(engine)) {
        printf("Automatic instancing should be disabled\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    // Validate object validity transitions for a representative set.
    FilaRenderer* renderer = FilaEngine_createRenderer(engine);
    FilaScene* scene = FilaEngine_createScene(engine);
    FilaView* view = FilaEngine_createView(engine);
    FilaFence* fence = FilaEngine_createFence(engine);

    if ((renderer && !FilaEngine_isValidRenderer(engine, renderer)) ||
            (scene && !FilaEngine_isValidScene(engine, scene)) ||
            (view && !FilaEngine_isValidView(engine, view)) ||
            (fence && !FilaEngine_isValidFence(engine, fence))) {
        printf("isValid returned false for newly created object\n");
        if (fence) FilaEngine_destroyFence(engine, fence);
        if (view) FilaEngine_destroyView(engine, view);
        if (scene) FilaEngine_destroyScene(engine, scene);
        if (renderer) FilaEngine_destroyRenderer(engine, renderer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (fence) FilaEngine_destroyFence(engine, fence);
    if (view) FilaEngine_destroyView(engine, view);
    if (scene) FilaEngine_destroyScene(engine, scene);
    if (renderer) FilaEngine_destroyRenderer(engine, renderer);

    if ((renderer && FilaEngine_isValidRenderer(engine, renderer)) ||
            (scene && FilaEngine_isValidScene(engine, scene)) ||
            (view && FilaEngine_isValidView(engine, view)) ||
            (fence && FilaEngine_isValidFence(engine, fence))) {
        printf("isValid returned true after destroy\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    // Validate destroyEntityComponents with camera + transform.
    const FilaEntity e = FilaEntityManager_create();
    if (e == 0) {
        printf("Entity creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaCamera* camera = FilaEngine_createCamera(engine, e);
    FilaTransformManager* tm = FilaEngine_getTransformManager(engine);
    if (!tm) {
        printf("TransformManager unavailable\n");
        FilaEntityManager_destroy(e);
        FilaEngine_destroy(&engine);
        return 1;
    }
    FilaTransformManager_create(tm, e, 0u);

    if (!camera || !FilaTransformManager_hasComponent(tm, e)) {
        printf("Failed to initialize entity components\n");
        FilaEntityManager_destroy(e);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEngine_destroyEntityComponents(engine, e);
    if (FilaEngine_getCameraComponent(engine, e) != (FilaCamera*)0 || FilaTransformManager_hasComponent(tm, e)) {
        printf("destroyEntityComponents did not clear known components\n");
        FilaEntityManager_destroy(e);
        FilaEngine_destroy(&engine);
        return 1;
    }
    FilaEntityManager_destroy(e);

    const uint64_t t0 = FilaEngine_getSteadyClockTimeNano();
    const uint64_t t1 = FilaEngine_getSteadyClockTimeNano();
    if (t1 < t0) {
        printf("Steady clock regression\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (!FilaEngine_flush(engine)) {
        printf("flush failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }
    if (!FilaEngine_flushAndWait(engine)) {
        printf("flushAndWait failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    bool completed = false;
    if (!FilaEngine_flushAndWaitWithTimeout(engine, UINT64_MAX, &completed) || !completed) {
        printf("flushAndWaitWithTimeout failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (!FilaEngine_setPaused(engine, true) || !FilaEngine_isPaused(engine)) {
        printf("Failed to pause engine\n");
        FilaEngine_destroy(&engine);
        return 1;
    }
    if (!FilaEngine_setPaused(engine, false) || FilaEngine_isPaused(engine)) {
        printf("Failed to resume engine\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (!FilaEngine_pumpMessageQueues(engine) || !FilaEngine_unprotected(engine)) {
        printf("Engine runtime control helper failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (!FilaEngine_enableAccurateTranslations(engine) ||
            !FilaTransformManager_isAccurateTranslationsEnabled(FilaEngine_getTransformManager(engine))) {
        printf("enableAccurateTranslations failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    {
        FilaDebugRegistry* debugRegistry = FilaEngine_getDebugRegistry(engine);
        if (!debugRegistry) {
            printf("DebugRegistry unavailable\n");
            FilaEngine_destroy(&engine);
            return 1;
        }
        const char* missingProperty = "fila.nonexistent.debug.property";
        bool b = false;
        int i = 0;
        float f = 0.0f;
        float f2[2] = {0.0f, 0.0f};
        float f3[3] = {0.0f, 0.0f, 0.0f};
        float f4[4] = {0.0f, 0.0f, 0.0f, 0.0f};
        const void* data = NULL;
        size_t count = 0u;

        if (FilaDebugRegistry_hasProperty(debugRegistry, missingProperty)) {
            printf("Unexpected debug property presence\n");
            FilaEngine_destroy(&engine);
            return 1;
        }
        if (FilaDebugRegistry_getPropertyAddress(debugRegistry, missingProperty) != NULL ||
                FilaDebugRegistry_getPropertyAddressConst(debugRegistry, missingProperty) != NULL) {
            printf("Unexpected debug property address\n");
            FilaEngine_destroy(&engine);
            return 1;
        }
        if (FilaDebugRegistry_setPropertyBool(debugRegistry, missingProperty, true) ||
                FilaDebugRegistry_setPropertyInt(debugRegistry, missingProperty, 1) ||
                FilaDebugRegistry_setPropertyFloat(debugRegistry, missingProperty, 1.0f) ||
                FilaDebugRegistry_setPropertyFloat2(debugRegistry, missingProperty, f2) ||
                FilaDebugRegistry_setPropertyFloat3(debugRegistry, missingProperty, f3) ||
                FilaDebugRegistry_setPropertyFloat4(debugRegistry, missingProperty, f4)) {
            printf("Unexpected ability to set missing debug property\n");
            FilaEngine_destroy(&engine);
            return 1;
        }
        if (FilaDebugRegistry_getPropertyBool(debugRegistry, missingProperty, &b) ||
                FilaDebugRegistry_getPropertyInt(debugRegistry, missingProperty, &i) ||
                FilaDebugRegistry_getPropertyFloat(debugRegistry, missingProperty, &f) ||
                FilaDebugRegistry_getPropertyFloat2(debugRegistry, missingProperty, f2) ||
                FilaDebugRegistry_getPropertyFloat3(debugRegistry, missingProperty, f3) ||
                FilaDebugRegistry_getPropertyFloat4(debugRegistry, missingProperty, f4)) {
            printf("Unexpected value retrieval for missing debug property\n");
            FilaEngine_destroy(&engine);
            return 1;
        }
        if (FilaDebugRegistry_getDataSource(debugRegistry, missingProperty, &data, &count)) {
            printf("Unexpected data source for missing debug property\n");
            FilaEngine_destroy(&engine);
            return 1;
        }
    }

    const FilaMaterial* defaultMaterial = FilaEngine_getDefaultMaterial(engine);
    if (!defaultMaterial || !FilaEngine_isValidMaterial(engine, defaultMaterial)) {
        printf("Default material should be valid\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaSync* sync = FilaEngine_createSync(engine);
    if (!sync || !FilaEngine_isValidSync(engine, sync)) {
        printf("Sync creation or validity check failed\n");
        FilaEngine_destroySync(engine, sync);
        FilaEngine_destroy(&engine);
        return 1;
    }
    FilaEngine_destroySync(engine, sync);
    if (FilaEngine_isValidSync(engine, sync)) {
        printf("Sync should be invalid after destroy\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    const char* missingFlag = "fila.nonexistent.flag";
    if (FilaEngine_hasFeatureFlag(engine, missingFlag)) {
        printf("Unexpected missing feature flag presence\n");
        FilaEngine_destroy(&engine);
        return 1;
    }
    if (FilaEngine_getFeatureFlag(engine, missingFlag, &featureValue)) {
        printf("Unexpected missing feature flag value\n");
        FilaEngine_destroy(&engine);
        return 1;
    }
    if (FilaEngine_setFeatureFlag(engine, missingFlag, true)) {
        printf("Unexpected ability to set missing feature flag\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    const size_t featureFlagCount = FilaEngine_getFeatureFlagCount(engine);
    if (featureFlagCount > 0u) {
        const char* flagName = NULL;
        const char* flagDescription = NULL;
        bool flagCurrentValue = false;
        bool flagIsConstant = false;
        if (!FilaEngine_getFeatureFlagInfo(
                engine,
                0u,
                &flagName,
                &flagDescription,
                &flagCurrentValue,
                &flagIsConstant)) {
            printf("Failed to read first feature flag info\n");
            FilaEngine_destroy(&engine);
            return 1;
        }
        if (!flagName || !flagDescription) {
            printf("Feature flag metadata is unexpectedly null\n");
            FilaEngine_destroy(&engine);
            return 1;
        }
        (void)flagCurrentValue;
        (void)flagIsConstant;
    }

    // execute() is intended for single-threaded platforms; on threaded platforms
    // our C wrapper returns false instead of propagating a precondition exception.
    (void)FilaEngine_execute(engine);

    (void)FilaEngine_getBackend(engine);
    (void)FilaEngine_isAsynchronousModeEnabled(engine);
    (void)FilaEngine_getBufferObjectCount(engine);
    (void)FilaEngine_getSceneCount(engine);
    (void)FilaEngine_getSwapChainCount(engine);
    (void)FilaEngine_getStreamCount(engine);
    (void)FilaEngine_getIndexBufferCount(engine);
    (void)FilaEngine_getSkinningBufferCount(engine);
    (void)FilaEngine_getMorphTargetBufferCount(engine);
    (void)FilaEngine_getInstanceBufferCount(engine);
    (void)FilaEngine_getVertexBufferCount(engine);
    (void)FilaEngine_getIndirectLightCount(engine);
    (void)FilaEngine_getMaterialCount(engine);
    (void)FilaEngine_getTextureCount(engine);
    (void)FilaEngine_getSkyboxCount(engine);
    (void)FilaEngine_getColorGradingCount(engine);
    (void)FilaEngine_getRenderTargetCount(engine);

    FilaEngine_destroy(&engine);

    printf("functionality_engine_runtime_queries completed\n");
    return 0;
}

